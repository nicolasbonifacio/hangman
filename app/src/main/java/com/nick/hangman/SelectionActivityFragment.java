package com.nick.hangman;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.nick.hangman.data.HangmanContract;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class SelectionActivityFragment extends Fragment {

    static final String DETAIL_URI = "URI";

    private static final int PLAYER_LAST_USED_FLAG = 1;
    private static final int LANGUAGE_LAST_USED_FLAG = 1;

    private boolean isCategorySelected = false;
    private boolean isLevelSelected = false;

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

    private static final String[] LEVEL_COLUMNS = {
            HangmanContract.LevelEntry.TABLE_NAME + "." + HangmanContract.LevelEntry._ID,
            HangmanContract.LevelEntry.COLUMN_DESCR_LEVEL
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

    //LEVEL columns
    public static final int COL_LEVEL_ID = 0;
    public static final int COL_LEVEL_DESCR_LEVEL = 1;

    private Uri mUri;
    private Cursor mCursor;

    private TextView mPlayer1View;
    private ListView mCategoryListView;
    private TextView categorySelectedView;

    private Player mPlayer1;
    private Language mLanguage;
    private Category mCategory;
    private Level mLevel;

    private ArrayList<Category> mListCategory;
    private ArrayList<Level> mListLevel;

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

        final View rootView = inflater.inflate(R.layout.fragment_selection, container, false);

        mPlayer1View = (TextView) rootView.findViewById(R.id.player1TextView);

        mCategoryListView = (ListView) rootView.findViewById(R.id.categoryListView);

        categorySelectedView = (TextView) rootView.findViewById(R.id.categorySelectedTextView);

        loadPlayerLastUsed();
        loadLanguageLastUsed();
        loadCategoryList();
        loadLevelList();

        //Category listener
        mCategoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                setDescrCategory(position);

            }
        });

        LinearLayout buttonLevelLayout = (LinearLayout) rootView.findViewById(R.id.buttonLevelLayout);
        for (int i = 1; i <= mListLevel.size(); i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            ToggleButton btn = new ToggleButton(getContext());
            btn.setId(i);
            final int id_ = btn.getId();
            btn.setText(mListLevel.get(i-1).getDescrLevel());
            btn.setTextOn(mListLevel.get(i-1).getDescrLevel());
            btn.setTextOff(mListLevel.get(i-1).getDescrLevel());
            buttonLevelLayout.addView(btn, params);
            final ToggleButton btn1 = ((ToggleButton) rootView.findViewById(id_));

            //Level listener
            btn1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {

                    if(mLevelChosen != btn1.getId()) {
                        mLevelChosen = btn1.getId();
                        setLevelButtonPressed(rootView);
                    }else {
                        btn1.setChecked(true);
                    }
                }
            });
        }

        final Button goButton = (Button) rootView.findViewById(R.id.goButton);

        //Go button listener
        goButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(isCategorySelected && isLevelSelected) {

                    ParametersSelected paramsSel = new ParametersSelected();

                    paramsSel.setPlayer1Id(mPlayer1.getId());
                    paramsSel.setPlayer1DescrName(mPlayer1.getDescrName());
                    paramsSel.setPlayer1Score(mPlayer1.getScore());

                    paramsSel.setLanguageId(mLanguage.getId());
                    paramsSel.setLanguageDescrLanguage(mLanguage.getDescrLanguage());

                    paramsSel.setCategoryId(mCategory.getId());
                    paramsSel.setCategoryDescrCategory(mCategory.getDescrCategory());

                    paramsSel.setLevelId(mLevel.getId());
                    paramsSel.setLevelDescrLevel(mLevel.getDescrLevel());

                    Intent intent = new Intent(getActivity(), GameMainActivity.class);

                    intent.putExtra("paramsSel", paramsSel);


                    startActivity(intent);

                }else if(!isCategorySelected && isLevelSelected) {
                    //Category not selected and level selected
                    Toast.makeText(getContext(),(String)getResources().getString(R.string.category_not_selected),
                            Toast.LENGTH_SHORT).show();

                }else if(isCategorySelected && !isLevelSelected) {
                    //Category selected and level not selected
                    Toast.makeText(getContext(),(String)getResources().getString(R.string.level_not_selected),
                            Toast.LENGTH_SHORT).show();

                }else {
                    //Both category and level not selected
                    Toast.makeText(getContext(),(String)getResources().getString(R.string.category_and_level_not_selected),
                            Toast.LENGTH_SHORT).show();

                }
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
            mPlayer1.setLastUsed(PLAYER_LAST_USED_FLAG);

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
            mLanguage.setLastUsed(LANGUAGE_LAST_USED_FLAG);

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

    private void loadLevelList() {
        mUri = HangmanContract.LevelEntry.buildLevelLanguage(mLanguage.getId());
        mCursor = getContext().getContentResolver().query(mUri, LEVEL_COLUMNS, null, null, null);

        mListLevel = new ArrayList<Level>();

        if(mCursor != null && mCursor.moveToFirst()) {

            do {
                mLevel = new Level();
                mLevel.setId(mCursor.getInt(COL_LEVEL_ID));
                mLevel.setDescrLevel(mCursor.getString(COL_LEVEL_DESCR_LEVEL));
                mLevel.setLanguageId(mLanguage.getId());

                mListLevel.add(mLevel);

            }while(mCursor.moveToNext());

            mCursor.close();

        }
    }

    private void setDescrCategory(int position) {

        categorySelectedView.setText(mListCategory.get(position).getDescrCategory());

        mCategory = new Category();
        mCategory.setId(mListCategory.get(position).getId());
        mCategory.setDescrCategory(mListCategory.get(position).getDescrCategory());

        isCategorySelected = true;

    }

    private void setLevelButtonPressed(View view) {

        ToggleButton btn;
        for(int i = 1; i <= mListLevel.size(); i++) {
            btn = (ToggleButton) view.findViewById(i);
            if(mLevelChosen != i) {
                btn.setChecked(false);

            }
        }

        mLevel = new Level();
        mLevel.setId(mListLevel.get(mLevelChosen-1).getId());
        mLevel.setDescrLevel(mListLevel.get(mLevelChosen-1).getDescrLevel());

        isLevelSelected = true;

    }

}
