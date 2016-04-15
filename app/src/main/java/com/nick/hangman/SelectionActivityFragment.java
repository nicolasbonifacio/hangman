package com.nick.hangman;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.nick.hangman.data.HangmanContract;
import com.nick.hangman.data.HangmanProvider;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class SelectionActivityFragment extends Fragment {

    static final String DETAIL_URI = "URI";

    private static final int PLAYER_LAST_USED_FLAG = 1;
    private static final int LANGUAGE_LAST_USED_FLAG = 1;

    private static final String[] PLAYER_COLUMNS = {
            HangmanContract.PlayerEntry.TABLE_NAME + "." + HangmanContract.PlayerEntry._ID,
            HangmanContract.PlayerEntry.COLUMN_DESCR_NAME,
            HangmanContract.PlayerEntry.COLUMN_SCORE
    };

    private static final String[] LANGUAGE_COLUMNS = {
            HangmanContract.LanguageEntry.TABLE_NAME + "." + HangmanContract.LanguageEntry._ID,
            HangmanContract.LanguageEntry.COLUMN_DESCR_LANGUAGE,
            HangmanContract.LanguageEntry.COLUMN_LAST_USED
    };

    private static final String[] CATEGORY_COLUMNS = {
            HangmanContract.CategoryEntry.TABLE_NAME + "." + HangmanContract.CategoryEntry._ID,
            HangmanContract.CategoryEntry.COLUMN_DESCR_CATEGORY
    };

    //PLAYER columns
    public static final int COL_PLAYER_ID = 0;
    public static final int COL_PLAYER_DESCR_NAME = 1;
    public static final int COL_PLAYER_SCORE = 2;

    //LANGUAGE columns
    public static final int COL_LANGUAGE_ID = 0;
    public static final int COL_LANGUAGE_DESCR_LANGUAGE = 1;

    //CATEGORY columns
    public static final int COL_CATEGORY_ID = 0;
    public static final int COL_CATEGORY_DESCR_CATEGORY = 1;

    private Uri mUri;
    private Cursor mCursor;

    private TextView mPlayer1View;
    private ListView mCategoryListView;
    private TextView categorySelectedView;

    private Player mPlayer1;
    private Language mLanguage;
    private Category mCategory;

    private ArrayList<Category> mListCategory;

    private int mLevelChosen;

    public SelectionActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(SelectionActivityFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_selection, container, false);

        mPlayer1View = (TextView) rootView.findViewById(R.id.player1TextView);

        mCategoryListView = (ListView) rootView.findViewById(R.id.categoryListView);

        categorySelectedView = (TextView) rootView.findViewById(R.id.categorySelectedTextView);

        loadPlayerLastUsed();
        loadLanguageLastUsed();
        loadCategoryList();

        mCategoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                setDescrCategory(position);

            }
        });

        final ToggleButton easyButton = (ToggleButton) rootView.findViewById(R.id.easyToggleButton);
        final ToggleButton mediumButton = (ToggleButton) rootView.findViewById(R.id.mediumToggleButton);
        final ToggleButton hardButton = (ToggleButton) rootView.findViewById(R.id.hardToggleButton);
        final Button goButton = (Button) rootView.findViewById(R.id.goButton);

        easyButton.setText("Easy");
        easyButton.setTextOn("Easy");
        easyButton.setTextOff("Easy");
        mediumButton.setText("Medium");
        mediumButton.setTextOn("Medium");
        mediumButton.setTextOff("Medium");
        hardButton.setText("Hard");
        hardButton.setTextOn("Hard");
        hardButton.setTextOff("Hard");

        easyButton.setChecked(true);
        mLevelChosen = 1;

        easyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mediumButton.setChecked(false);
                hardButton.setChecked(false);
                mLevelChosen = 1;
            }
        });

        mediumButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                easyButton.setChecked(false);
                hardButton.setChecked(false);
                mLevelChosen = 2;
            }
        });

        hardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                easyButton.setChecked(false);
                mediumButton.setChecked(false);
                mLevelChosen = 3;
            }
        });

        goButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void loadPlayerLastUsed() {
        mUri = HangmanContract.PlayerEntry.buildPlayerLastUsed(PLAYER_LAST_USED_FLAG);
        mCursor = getContext().getContentResolver().query(mUri, PLAYER_COLUMNS, null, null, null);

        if(mCursor != null && mCursor.moveToFirst()) {

            mPlayer1 = new Player();
            mPlayer1.setId(mCursor.getInt(COL_PLAYER_ID));
            mPlayer1.setDescrName(mCursor.getString(COL_PLAYER_DESCR_NAME));
            mPlayer1.setScore(mCursor.getInt(COL_PLAYER_SCORE));
            mPlayer1.setLastUsed(1);

            mPlayer1View.setText(mPlayer1.getDescrName());

            mCursor.close();
        }
    }

    private void loadLanguageLastUsed() {
        mUri = HangmanContract.LanguageEntry.buildLanguageLastUsed(LANGUAGE_LAST_USED_FLAG);
        mCursor = getContext().getContentResolver().query(mUri, LANGUAGE_COLUMNS, null, null, null);

        if(mCursor != null && mCursor.moveToFirst()) {

            mLanguage = new Language();
            mLanguage.setId(mCursor.getInt(COL_LANGUAGE_ID));
            mLanguage.setDescrLanguage(mCursor.getString(COL_LANGUAGE_DESCR_LANGUAGE));
            mLanguage.setLastUsed(1);

            mCursor.close();
        }
    }

    private void loadCategoryList() {
        mUri = HangmanContract.CategoryEntry.buildCategoryLanguage(mLanguage.getId());
        mCursor = getContext().getContentResolver().query(mUri, CATEGORY_COLUMNS, null, null, null);

        mListCategory = new ArrayList<Category>();
        ArrayList<String> listDescrCategory = new ArrayList<String>();

        if(mCursor != null && mCursor.moveToFirst()) {

            do {
                mCategory = new Category();
                mCategory.setId(mCursor.getInt(COL_CATEGORY_ID));
                mCategory.setDescrCategory(mCursor.getString(COL_CATEGORY_DESCR_CATEGORY));
                mCategory.setLanguageId(mLanguage.getId());

                mListCategory.add(mCategory);
                listDescrCategory.add(mCategory.getDescrCategory());

            }while(mCursor.moveToNext());

            mCursor.close();

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    getContext(),
                    android.R.layout.simple_list_item_1,
                    listDescrCategory );

            mCategoryListView.setAdapter(arrayAdapter);

        }
    }

    private void setDescrCategory(int position) {

        categorySelectedView.setText(mListCategory.get(position).getDescrCategory());

    }

}
