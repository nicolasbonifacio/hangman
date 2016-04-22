package com.nick.hangman;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.Space;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.nick.hangman.Objects.Category;
import com.nick.hangman.Objects.TaleScoreCategory;
import com.nick.hangman.data.HangmanContract;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class TaleActivityFragment extends Fragment {

    static final String DETAIL_URI = "URI";

    private static final int LANGUAGE_LAST_USED_FLAG = 1;
    private static final int TALE_PLAYER_FLAG = 1;
    private static final int LEVEL_EASY = 1;
    private static final int LEVEL_MEDIUM = 2;
    private static final int LEVEL_HARD = 3;
    private static final int ALL_LEVELS = 0;

    private static final int WORD_NOT_USED_FLAG = 0;

    private static final String TALE_SCORE_CATEGORY_SORT_ORDER = HangmanContract.TaleScoreCategoryEntry.TABLE_NAME + ".PATH_ORDER";

    private static final String WORD_COUNT_USAGE_SORT_ORDER = HangmanContract.WordEntry.TABLE_NAME +
            "." + HangmanContract.WordEntry.COLUMN_WORD_USED;

    private static final String[] TALE_SCORE_CATEGORY_COLUMNS = {
            HangmanContract.TaleScoreCategoryEntry.TABLE_NAME + "." + HangmanContract.TaleScoreCategoryEntry._ID,
            HangmanContract.TaleScoreCategoryEntry.TABLE_NAME + "." + HangmanContract.TaleScoreCategoryEntry.COLUMN_PLAYER_SCORE,
            HangmanContract.CategoryEntry.TABLE_NAME + "." + HangmanContract.CategoryEntry.COLUMN_DESCR_CATEGORY,
            HangmanContract.TaleScoreCategoryEntry.TABLE_NAME + "." + HangmanContract.TaleScoreCategoryEntry.COLUMN_CATEGORY_ENABLED,
            HangmanContract.TaleScoreCategoryEntry.TABLE_NAME + "." + HangmanContract.TaleScoreCategoryEntry.COLUMN_CATEGORY_ENABLE_SCORE,
            HangmanContract.TaleScoreCategoryEntry.TABLE_NAME + "." + HangmanContract.TaleScoreCategoryEntry.COLUMN_LOC_KEY_CATEGORY,
            HangmanContract.TaleScoreCategoryEntry.TABLE_NAME + "." + HangmanContract.TaleScoreCategoryEntry.COLUMN_PATH_ORDER
    };

    private static final String[] WORD_COUNT_USAGE_COLUMNS = {
            HangmanContract.WordEntry.TABLE_NAME + "." + HangmanContract.WordEntry.COLUMN_WORD_USED,
            "COUNT(*)"
    };

    //TALE_SCORE_CATEGORY_COLUMNS columns
    public static final int COL_TALE_SCORE_CATEGORY_ID = 0;
    public static final int COL_TALE_SCORE_CATEGORY_PLAYER_SCORE = 1;
    public static final int COL_CATEGORY_DESCR_CATEGORY = 2;
    public static final int COL_TALE_SCORE_CATEGORY_CATEGORY_ENABLED = 3;
    public static final int COL_TALE_SCORE_CATEGORY_CATEGORY_ENABLE_SCORE = 4;
    public static final int COL_TALE_SCORE_CATEGORY_CATEGORY_ID = 5;
    public static final int COL_TALE_SCORE_CATEGORY_PATH_ORDER = 6;

    //WORD_COUNT_USAGE_COLUMNS columns
    public static final int COL_WORD_WORD_USED = 0;
    public static final int COL_WORD_COUNT = 1;

    private View rootView;
    private ScrollView scroller;
    private Uri mUri;
    private Cursor mCursor;

    private ArrayList<TaleScoreCategory> mListTaleScoreCategory;
    private Integer[][] mPath;

    public TaleActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(SelectionActivityFragment.DETAIL_URI);
        }

        rootView = inflater.inflate(R.layout.fragment_tale, container, false);

        scroller = new ScrollView(getActivity());
        scroller.setRotation(180);
        scroller.addView(rootView);

        return scroller;


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        //Verify if it's the first time passing by that screen. If it is, creates the structures.
        //Otherwise, just reload them.
        boolean firstLoad;
        if(mListTaleScoreCategory == null) {
            firstLoad = true;
        }else {
            firstLoad = false;
        }

        loadTaleScoreCategoryList();

        if(firstLoad) {
            mPath = new Integer[mListTaleScoreCategory.size()][3];
        }

        if(mListTaleScoreCategory != null) {

            int spaceIds = mListTaleScoreCategory.size()+1;

            LinearLayout taleLayout = (LinearLayout) rootView.findViewById(R.id.taleLayout);
            for (int i = 1; i <= mListTaleScoreCategory.size(); i++) {
                if(firstLoad) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);

                    Button btn = new Button(getContext());
                    btn.setId(i);
                    final int id_ = btn.getId();
                    btn.setText(mListTaleScoreCategory.get(i - 1).getDescrCategory());
                    btn.setRotation(180);
                    if (mListTaleScoreCategory.get(i - 1).getEnabled() == 0) {
                        btn.setEnabled(false);
                    } else {
                        btn.setEnabled(true);
                    }
                    taleLayout.addView(btn, params);

                    mPath[i - 1][0] = i;
                    mPath[i - 1][1] = spaceIds;
                    mPath[i - 1][2] = 5;

                    ImageView space;
                    for (int j = 0; j < 5; j++) {
                        space = new ImageView(getContext());
                        space.setId(spaceIds);
                        space.setMinimumWidth(60);
                        space.setMinimumHeight(100);
                        space.setBackgroundColor(Color.parseColor("#b2acac"));
                        taleLayout.addView(space, params);

                        spaceIds++;
                    }

                    //Tale list listener
                    final Button btn1 = ((Button) rootView.findViewById(id_));
                    btn1.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {

                            callGameScreen(id_);
                        }
                    });

                }else {
                    Button btn = (Button) scroller.findViewById(i);
                    if (mListTaleScoreCategory.get(i - 1).getEnabled() == 0) {
                        btn.setEnabled(false);
                    } else {
                        btn.setEnabled(true);
                    }
                }
            }

            ImageView teste;
            teste = (ImageView) scroller.findViewById(13);
            teste.setBackgroundColor(Color.parseColor("#83db63"));
            teste = (ImageView) scroller.findViewById(14);
            teste.setBackgroundColor(Color.parseColor("#83db63"));
            teste = (ImageView) scroller.findViewById(15);
            teste.setBackgroundColor(Color.parseColor("#83db63"));


        }

    }

    private void loadTaleScoreCategoryList() {
        mUri = HangmanContract.TaleScoreCategoryEntry.buildTaleScoreCategoryWithPlayerCategory(TALE_PLAYER_FLAG,
                LANGUAGE_LAST_USED_FLAG);

        mCursor = getContext().getContentResolver().query(mUri, TALE_SCORE_CATEGORY_COLUMNS, null, null, TALE_SCORE_CATEGORY_SORT_ORDER);

        mListTaleScoreCategory = new ArrayList<TaleScoreCategory>();
        TaleScoreCategory taleScoreCategory;

        if(mCursor != null && mCursor.moveToFirst()) {

            do {
                taleScoreCategory = new TaleScoreCategory();
                taleScoreCategory.setId(mCursor.getInt(COL_TALE_SCORE_CATEGORY_ID));
                taleScoreCategory.setScore(mCursor.getInt(COL_TALE_SCORE_CATEGORY_PLAYER_SCORE));
                taleScoreCategory.setDescrCategory(mCursor.getString(COL_CATEGORY_DESCR_CATEGORY));
                taleScoreCategory.setEnabled(mCursor.getInt(COL_TALE_SCORE_CATEGORY_CATEGORY_ENABLED));
                taleScoreCategory.setEnableScore(mCursor.getInt(COL_TALE_SCORE_CATEGORY_CATEGORY_ENABLE_SCORE));
                taleScoreCategory.setCategoryId(mCursor.getInt(COL_TALE_SCORE_CATEGORY_CATEGORY_ID));
                taleScoreCategory.setPathOrder(mCursor.getInt(COL_TALE_SCORE_CATEGORY_PATH_ORDER));

                mListTaleScoreCategory.add(taleScoreCategory);

            }while(mCursor.moveToNext());

            mCursor.close();

        }
    }

    private void callGameScreen(int position) {

        ParametersSelected paramsSel = new ParametersSelected();
        paramsSel.setTaleScoreCategoryScore(mListTaleScoreCategory.get(position-1).getScore());
        paramsSel.setTaleScoreCategoryEnableScore(mListTaleScoreCategory.get(position-1).getEnableScore());
        paramsSel.setCategoryDescrCategory(mListTaleScoreCategory.get(position-1).getDescrCategory());
        paramsSel.setCategoryId(mListTaleScoreCategory.get(position-1).getCategoryId());

        int percLevel = (int)(paramsSel.getTaleScoreCategoryScore()*100)/paramsSel.getTaleScoreCategoryEnableScore();
        paramsSel.setLevelDescrLevel(Integer.toString(percLevel));

        int third = (int)(paramsSel.getTaleScoreCategoryEnableScore()/3);
        if(paramsSel.getTaleScoreCategoryScore() < third) {
            //Easy
            paramsSel.setLevelId(LEVEL_EASY);
        }else if(paramsSel.getTaleScoreCategoryScore() >= (third) && paramsSel.getTaleScoreCategoryScore() < (third*2)) {
            //Medium
            paramsSel.setLevelId(LEVEL_MEDIUM);
        }else if(paramsSel.getTaleScoreCategoryScore() >= (third*2) && paramsSel.getTaleScoreCategoryScore() < paramsSel.getTaleScoreCategoryEnableScore()) {
            //Hard
            paramsSel.setLevelId(LEVEL_HARD);
        }else {
            //Level already completed
            paramsSel.setLevelId(ALL_LEVELS);
        }

        paramsSel.setLanguageId(LANGUAGE_LAST_USED_FLAG);
        paramsSel.setTaleScoreCategoryId(mListTaleScoreCategory.get(position-1).getId());
        paramsSel.setTaleScoreCategoryPathOrder(mListTaleScoreCategory.get(position-1).getPathOrder());

        if(mListTaleScoreCategory.get(position) != null) {
            paramsSel.setTaleScoreCategoryIdNextCategory(mListTaleScoreCategory.get(position).getId());
        }else {
            paramsSel.setTaleScoreCategoryIdNextCategory(paramsSel.getTaleScoreCategoryId()+1);
        }

        if(paramsSel.getLevelId() == ALL_LEVELS) {
            validateQtdWordsAvailable(paramsSel, LEVEL_EASY);
            validateQtdWordsAvailable(paramsSel, LEVEL_MEDIUM);
            validateQtdWordsAvailable(paramsSel, LEVEL_HARD);
        }else {
            validateQtdWordsAvailable(paramsSel, paramsSel.getLevelId());
        }

        Intent intent = new Intent(getActivity(), GameMainActivity.class);

        intent.putExtra("paramsSel", paramsSel);

        startActivity(intent);
    }

    private void validateQtdWordsAvailable(ParametersSelected params, int level) {

        mUri = HangmanContract.WordEntry.buildCountWordsUsageWithLanguageCategoryLevel(params.getLanguageId(),
                params.getCategoryId(), level);

        mCursor = getContext().getContentResolver().query(mUri, WORD_COUNT_USAGE_COLUMNS, null, null, WORD_COUNT_USAGE_SORT_ORDER);

        Integer[][] wordUsage = new Integer[2][2];

        if(mCursor != null && mCursor.moveToFirst()) {

            int i = 0;
            do {
                wordUsage[i][0] = mCursor.getInt(COL_WORD_WORD_USED);
                wordUsage[i][1] = mCursor.getInt(COL_WORD_COUNT);

                i++;

            } while (mCursor.moveToNext());

            mCursor.close();
            if(i > 1) {
                if (wordUsage[0][0] == 0) {
                    int totalWords = wordUsage[0][1] + wordUsage[1][1];
                    int wordsAvailable = wordUsage[0][1];
                    float percAvailable = (wordsAvailable * 100) / totalWords;
                    if (percAvailable <= 10) {
                        //Sets all words as available
                        ContentValues values = new ContentValues();
                        values.put(HangmanContract.WordEntry.COLUMN_WORD_USED, WORD_NOT_USED_FLAG);

                        int qtdRws = getContext().getContentResolver().update(
                                HangmanContract.WordEntry.CONTENT_URI,
                                values,
                                "language_id = ? and category_id = ? and level_id = ?",
                                new String[]{Integer.toString(params.getLanguageId())
                                        , Integer.toString(params.getCategoryId())
                                        , Integer.toString(level)}
                        );

                        System.out.println("cccccccccccccccccccccccc");
                        System.out.println(qtdRws);

                    }
                }

            }

        }

    }

}
