package com.nick.hangman;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.Space;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.nick.hangman.Objects.Category;
import com.nick.hangman.Objects.TaleOverall;
import com.nick.hangman.Objects.TaleScoreCategory;
import com.nick.hangman.Views.CategoryView;
import com.nick.hangman.data.HangmanContract;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class TaleActivityFragment extends Fragment {

    static final String DETAIL_URI = "URI";

    private static final int LANGUAGE_LAST_USED_FLAG = 1;
    private static final int TALE_PLAYER_FLAG = 1;
    private static final int LEVEL_FLAG = 1; //Just one level
    /*
    private static final int LEVEL_EASY = 1;
    private static final int LEVEL_MEDIUM = 2;
    private static final int LEVEL_HARD = 3;
    private static final int ALL_LEVELS = 0;
*/
    private static final int WORD_NOT_USED_FLAG = 0;
    private static final int BUTTON_DISABLED_FLAG = -1;

    private static final int ALL_CATEGORIES_BUTTON_ID = 995;

    private static final int ALL_CATEGORIES_SCORE = 0;
    private static final String ALL_CATEGORIES_DESCR = "All Categories";
    private static final int ALL_CATEGORIES_ENABLED = 1;
    private static final int ALL_CATEGORIES_ENABLE_SCORE = 0;
    private static final int ALL_CATEGORIES_ID = 0;
    private static final int ALL_CATEGORIES_PATH_ORDER = 0;
    private static final int ALL_CATEGORIES_PERC_LEVEL_COMPLETED = 100;

    private static final String WORD_COUNT_USAGE_SORT_ORDER_SELECTION =
            HangmanContract.WordEntry.TABLE_NAME +
                    "." + HangmanContract.WordEntry.COLUMN_LOC_KEY_LANGUAGE + " = ? AND " +
                    HangmanContract.WordEntry.TABLE_NAME +
                    "." + HangmanContract.WordEntry.COLUMN_LOC_KEY_CATEGORY + " = ? AND " +
                    HangmanContract.WordEntry.TABLE_NAME +
                    "." + HangmanContract.WordEntry.COLUMN_LOC_KEY_LEVEL + " = ?";

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

    private static final String[] TALE_OVERALL_COLUMNS = {
            HangmanContract.TaleOverallEntry.TABLE_NAME + "." + HangmanContract.TaleOverallEntry.COLUMN_LOC_KEY_CATEGORY,
            "round(sum(" + HangmanContract.TaleOverallEntry.TABLE_NAME + "." + HangmanContract.TaleOverallEntry.COLUMN_NUM_STARS + ")/count(*),3)"
    };

    private static final String[] WORD_COLUMNS = {
            HangmanContract.WordEntry.TABLE_NAME + "." + HangmanContract.WordEntry._ID,
            HangmanContract.WordEntry.COLUMN_WORD
    };

    private static final String sWordLanguageCategorySelection =
            HangmanContract.WordEntry.TABLE_NAME +
                    "." + HangmanContract.WordEntry.COLUMN_LOC_KEY_LANGUAGE + " = ? AND " +
                    HangmanContract.WordEntry.TABLE_NAME +
                    "." + HangmanContract.WordEntry.COLUMN_WORD_USED + " = ?";

    //WORD columns
    public static final int COL_WORD_ID = 0;
    public static final int COL_WORD_WORD = 1;

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

    //TALE_OVERALL_COLUMNS columns
    public static final int COL_TALE_OVERALL_CATEGORY_ID = 0;
    public static final int COL_TALE_OVERALL_NUM_STARS_AVERAGE = 1;

    public static final int WIDTH_ICON_STARS_PERCENTAGE = 45;
    public static final int PADDING_TEXT_LEFT_PERCENTAGE = 0;

    private View rootView;
    private Uri mUri;
    private Cursor mCursor;

    private ArrayList<TaleScoreCategory> mListTaleScoreCategory;
    private ArrayList<TaleOverall> mListTaleOverall;
    private Integer[][] mPath;

    private LinearLayout mTaleLayout;
    private LinearLayout mHorizontalLayout;

    private Utils mUtils;
    private int mPercentage;
    private int mWidthPx;
    private ImageView mBtn;
    private ImageView mBtnPressed;
    private ImageView mIconStars;
    private ParametersSelected paramsSel;

    private MediaPlayer buttonSound;

    //////////////////////
    private int spaceIds;





    public TaleActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        buttonSound = MediaPlayer.create(getContext(), R.raw.button_sound);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(SelectionActivityFragment.DETAIL_URI);
        }

        rootView = inflater.inflate(R.layout.fragment_tale, container, false);

        paramsSel = new ParametersSelected();

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("paramsSel")) {
            paramsSel = (ParametersSelected) intent.getSerializableExtra("paramsSel");
        }

        return rootView;

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
        loadQtdStarsCategoryOverall();

        if(firstLoad) {
            mPath = new Integer[mListTaleScoreCategory.size()][3];
        }

        if(mListTaleScoreCategory != null) {

            spaceIds = mListTaleScoreCategory.size()+1;

            mTaleLayout = (LinearLayout) rootView.findViewById(R.id.taleLayout);
            mTaleLayout.setBackgroundColor(getResources().getColor(R.color.colorBackground));

            mUtils = new Utils();

            mWidthPx = getContext().getResources().getDisplayMetrics().widthPixels;
            mPercentage = 0;

            for (int i = 1; i <= mListTaleScoreCategory.size(); i++) {
                if(firstLoad) {
                    firstLoad(i);
                }else {
                    afterFirstLoad(i);
                }
            }

            if(firstLoad) {

                int buttonAllCategoriesWidth = 0;
                int buttonAllCategoriesHeight = 0;
                float buttonAllCategoriesRatio = 0;
                try {
                    buttonAllCategoriesWidth = getResources().getDrawable(R.drawable.button_all_categories).getIntrinsicWidth();
                    buttonAllCategoriesHeight = getResources().getDrawable(R.drawable.button_all_categories).getIntrinsicHeight();
                }catch(NullPointerException e) {
                    e.printStackTrace();
                }
                if(buttonAllCategoriesHeight > 0) {
                    buttonAllCategoriesRatio = (float)buttonAllCategoriesWidth / buttonAllCategoriesHeight;
                }

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                        (int)((mWidthPx / 3)),
                        (int)((mWidthPx / 3) / buttonAllCategoriesRatio)
                );
                LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
                        (int)((mWidthPx / 3) - getResources().getDimension(R.dimen.category_button_pressed_layout_reduction)),
                        (int)(((mWidthPx / 3) / buttonAllCategoriesRatio) - getResources().getDimension(R.dimen.category_button_pressed_layout_reduction))
                );
                params3.gravity = Gravity.CENTER;

                mHorizontalLayout = new LinearLayout(getContext());
                mHorizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
                mHorizontalLayout.setGravity(Gravity.CENTER_VERTICAL);

                ImageView allCategoriesButtonPressed = new ImageView(getContext());
                allCategoriesButtonPressed.setImageDrawable(getResources().getDrawable(R.drawable.button_all_categories_pressed));

                ImageView allCategoriesButton = new ImageView(getContext());
                allCategoriesButton.setImageDrawable(getResources().getDrawable(R.drawable.button_all_categories));
                allCategoriesButton.setId(ALL_CATEGORIES_BUTTON_ID);

                FrameLayout r = new FrameLayout(getContext());
                r.setMinimumWidth(0);
                //r.setGravity(Gravity.CENTER);
                r.addView(allCategoriesButtonPressed, params3);
                r.addView(allCategoriesButton, params2);

                mHorizontalLayout.setRotation(180);

                mHorizontalLayout.addView(r);
                mTaleLayout.addView(mHorizontalLayout, params);

                final ImageView allCategoriesButtonListener = (ImageView) rootView.findViewById(ALL_CATEGORIES_BUTTON_ID);
                allCategoriesButtonListener.setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                allCategoriesButtonListener.setVisibility(View.INVISIBLE);
                                break;
                            case MotionEvent.ACTION_UP:
                                allCategoriesButtonListener.setVisibility(View.VISIBLE);
                                buttonSound.start();
                                callGameScreen(mListTaleScoreCategory.size());
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });

            }

            TaleScoreCategory taleScoreCategory = new TaleScoreCategory();
            taleScoreCategory.setId(mListTaleScoreCategory.size() + 1);
            taleScoreCategory.setScore(ALL_CATEGORIES_SCORE);
            taleScoreCategory.setDescrCategory(ALL_CATEGORIES_DESCR);
            taleScoreCategory.setEnabled(ALL_CATEGORIES_ENABLED);
            taleScoreCategory.setEnableScore(ALL_CATEGORIES_ENABLE_SCORE);
            taleScoreCategory.setCategoryId(ALL_CATEGORIES_ID);
            taleScoreCategory.setPathOrder(ALL_CATEGORIES_PATH_ORDER);

            mListTaleScoreCategory.add(taleScoreCategory);

        }

    }

    private void firstLoad(int i) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        mHorizontalLayout = new LinearLayout(getContext());
        mHorizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        mHorizontalLayout.setGravity(Gravity.CENTER_VERTICAL);

        //Category image button
        mBtn = new ImageView(getContext());
        mBtn.setId(i);
        mBtnPressed = new ImageView(getContext());

        //Icon stars image
        mIconStars = new ImageView(getContext());
        mIconStars.setId(i+5000);

        loadImagesOnScreen(i);

        //Category name text
        TextView categoryName = new TextView(getContext());
        categoryName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        String text = mListTaleScoreCategory.get(i - 1).getDescrCategory();
        text = text.replace(" ", "\r\n");
        categoryName.setText(text);
        categoryName.setPadding(
                (int)(((mWidthPx/3)*PADDING_TEXT_LEFT_PERCENTAGE)/100),
                0,
                0,
                0
        );  //0% of a third of the screen width
        categoryName.setGravity(Gravity.LEFT);

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                mWidthPx/3,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        RelativeLayout r = new RelativeLayout(getContext());
        r.setMinimumWidth(0);
        r.setGravity(Gravity.CENTER);
        r.addView(categoryName, params2);
        mHorizontalLayout.addView(r);

        FrameLayout frame = new FrameLayout(getContext());
        FrameLayout.LayoutParams paramsFrame = new FrameLayout.LayoutParams(
                mWidthPx/4,
                mWidthPx/4
        );
        FrameLayout.LayoutParams params3 = new FrameLayout.LayoutParams(
                (int)((mWidthPx/4)-getResources().getDimensionPixelSize((R.dimen.category_button_pressed_layout_reduction))),
                (int)((mWidthPx/4)-getResources().getDimensionPixelSize((R.dimen.category_button_pressed_layout_reduction)))
        );
        params3.gravity = Gravity.CENTER;
        frame.addView(mBtnPressed, params3);
        frame.addView(mBtn);

        mHorizontalLayout.addView(frame, paramsFrame);

        r = new RelativeLayout(getContext());
        r.setMinimumWidth(0);
        r.setGravity(Gravity.CENTER);
        r.addView(mIconStars, params2);
        mHorizontalLayout.addView(r);

        mHorizontalLayout.setRotation(180);
        mTaleLayout.addView(mHorizontalLayout, params);

        int percCompleted = (int)((getResources().getDimensionPixelSize(R.dimen.path_layout_base_height) * mPercentage) / 100);
        if(percCompleted > getResources().getDimensionPixelSize(R.dimen.path_layout_base_height)) {
            percCompleted = getResources().getDimensionPixelSize(R.dimen.path_layout_base_height);
        }

        FrameLayout pathLayout = new FrameLayout(getContext());
        pathLayout.setId(spaceIds);
        LinearLayout.LayoutParams pathLayoutParamsBase = new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.path_layout_width),
                getResources().getDimensionPixelSize(R.dimen.path_layout_base_height)
        );

        LinearLayout.LayoutParams pathLayoutParamsCompleted = new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.path_layout_width),
                percCompleted
        );

        mPath[i - 1][0] = i;
        //Layout
        mPath[i - 1][1] = spaceIds;
        spaceIds++;

        ImageView space;
        space = new ImageView(getContext());
        //space.setId(spaceIds);
        space.setBackgroundColor(getResources().getColor(R.color.tale_path_uncompleted));

        pathLayout.addView(space, pathLayoutParamsBase);

        space = new ImageView(getContext());
        space.setId(spaceIds);
        space.setBackgroundColor(getResources().getColor(R.color.tale_path_completed));

        pathLayout.addView(space, pathLayoutParamsCompleted);

        //ImageView
        mPath[i - 1][2] = spaceIds;
        spaceIds++;

        mTaleLayout.addView(pathLayout, params);

        //Tale list listener
        final int id_ = mBtn.getId();
        final ImageView btn1 = ((ImageView) rootView.findViewById(id_));
        btn1.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        btn1.setVisibility(View.INVISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:
                        btn1.setVisibility(View.VISIBLE);
                        buttonSound.start();
                        if (mListTaleScoreCategory.get(id_ - 1).getEnabled() == 0) {
                            Toast.makeText(
                                    getContext(),
                                    getResources().getString(R.string.category_unlocked),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }else {
                            callGameScreen(id_);
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

    }

    private void afterFirstLoad(int i) {
        mBtn = (ImageView) rootView.findViewById(i);
        if (mListTaleScoreCategory.get(i - 1).getEnabled() != 0) {

            mBtn.setEnabled(true);
            mIconStars = (ImageView) rootView.findViewById(i+5000);
            loadImagesOnScreen(i);

            int newPercCompleted = (int)((getResources().getDimensionPixelSize(R.dimen.path_layout_base_height) * mPercentage) / 100);
            FrameLayout pathLayout = (FrameLayout) rootView.findViewById(mPath[i - 1][1]);
            LinearLayout.LayoutParams pathLayoutParamsBase = new LinearLayout.LayoutParams(
                    getResources().getDimensionPixelSize(R.dimen.path_layout_width),
                    newPercCompleted
            );

            ImageView spaceToUpdate = (ImageView) pathLayout.getChildAt(1);
            pathLayout.removeViewAt(1);
            pathLayout.addView(spaceToUpdate, pathLayoutParamsBase);

        }
    }

    private void loadImagesOnScreen(int i) {
        if (mListTaleScoreCategory.get(i - 1).getEnabled() == 0) {
            mBtn.setEnabled(true);
            mBtn.setImageDrawable(getResources().getDrawable(mUtils.getButtonCode(BUTTON_DISABLED_FLAG)));
            mPercentage = 0;
        } else {
            mBtn.setEnabled(true);
            mPercentage = (int)(mListTaleScoreCategory.get(i-1).getScore() * 100) / mListTaleScoreCategory.get(i-1).getEnableScore();
            mBtn.setImageDrawable(getResources().getDrawable(mUtils.getButtonCode(mPercentage)));
        }

        mBtn.setPadding(0, 0, 0, 0);

        mBtnPressed.setPadding(0, 0, 0, 0);
        if(mListTaleScoreCategory.get(i - 1).getEnabled() == 0) {
            mBtnPressed.setImageDrawable(getResources().getDrawable(R.drawable.button_pressed));
        }else {
            mBtnPressed.setImageDrawable(getResources().getDrawable(R.drawable.button_pressed_enabled));
        }

        //Category activated and there is at least one entry in the table TALE_OVERALL
        if(mListTaleScoreCategory.get(i - 1).getEnabled() == 1 && mListTaleOverall != null) {
            int numStars = 0;
            if(mListTaleOverall.size() > 0) {
                boolean isFind = false;
                for (int j = 0; j < mListTaleOverall.size(); j++) {
                    if (mListTaleOverall.get(j).getCategryId() == mListTaleScoreCategory.get(i - 1).getCategoryId()) {
                        numStars = mUtils.getArrowIconStars(Math.round(mListTaleOverall.get(j).getNumStarsAverage()));
                        mIconStars.setImageDrawable(getResources().getDrawable(numStars));
                        isFind = true;
                    }
                }
                if(!isFind) {
                    //First time in this category
                    numStars = mUtils.getArrowIconStars(0);
                    mIconStars.setImageDrawable(getResources().getDrawable(numStars));
                }
            }else {
                //First time playing the game
                numStars = mUtils.getArrowIconStars(0);
                mIconStars.setImageDrawable(getResources().getDrawable(numStars));
            }
            mIconStars.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mIconStars.setAdjustViewBounds(true);
            mIconStars.setPadding(0, 0, 0, 0);
            mIconStars.setMaxWidth((int)(((mWidthPx/3)*WIDTH_ICON_STARS_PERCENTAGE)/100)); //45% of a third of the screen width
            mIconStars.setMaxHeight((int)((((mWidthPx/3)*WIDTH_ICON_STARS_PERCENTAGE)/100)/2)); //Half of 45% of a third of the screen width
        }else {
            mIconStars.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mIconStars.setAdjustViewBounds(true);
            mIconStars.setPadding(0, 0, 0, 0);
            mIconStars.setMinimumWidth((int)(((mWidthPx/3)*WIDTH_ICON_STARS_PERCENTAGE)/100)); //45% of a third of the screen width
            mIconStars.setMinimumHeight((int)((((mWidthPx/3)*WIDTH_ICON_STARS_PERCENTAGE)/100)/2)); //Half of 45% of a third of the screen width
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

    public void callGameScreen(int position) {

        //paramsSel = new ParametersSelected();
        paramsSel.setTaleScoreCategoryScore(mListTaleScoreCategory.get(position-1).getScore());
        paramsSel.setTaleScoreCategoryEnableScore(mListTaleScoreCategory.get(position-1).getEnableScore());
        paramsSel.setCategoryDescrCategory(mListTaleScoreCategory.get(position-1).getDescrCategory());
        paramsSel.setCategoryId(mListTaleScoreCategory.get(position-1).getCategoryId());
        paramsSel.setPlayer1Id(TALE_PLAYER_FLAG);

        //% of level completed
        if(paramsSel.getTaleScoreCategoryEnableScore() > 0) {
            int percLevel = (int) (paramsSel.getTaleScoreCategoryScore() * 100) / paramsSel.getTaleScoreCategoryEnableScore();
            paramsSel.setLevelPercCompleted(percLevel);
        }else {
            paramsSel.setLevelPercCompleted(ALL_CATEGORIES_PERC_LEVEL_COMPLETED);
        }
/*
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
*/
        paramsSel.setLevelId(LEVEL_FLAG);

        paramsSel.setLanguageId(LANGUAGE_LAST_USED_FLAG);
        paramsSel.setTaleScoreCategoryId(mListTaleScoreCategory.get(position-1).getId());
        paramsSel.setTaleScoreCategoryPathOrder(mListTaleScoreCategory.get(position-1).getPathOrder());

        if(paramsSel.getCategoryId() != 0) {
            if (mListTaleScoreCategory.get(position) != null) {
                paramsSel.setTaleScoreCategoryIdNextCategory(mListTaleScoreCategory.get(position).getId());
            } else {
                paramsSel.setTaleScoreCategoryIdNextCategory(paramsSel.getTaleScoreCategoryId() + 1);
            }
        }
//        if(paramsSel.getLevelId() == ALL_LEVELS) {
//            validateQtdWordsAvailable(paramsSel, LEVEL_EASY);
//            validateQtdWordsAvailable(paramsSel, LEVEL_MEDIUM);
//            validateQtdWordsAvailable(paramsSel, LEVEL_HARD);
//        }else {
        validateQtdWordsAvailable(paramsSel, paramsSel.getLevelId());
//        }

        Intent intent = new Intent(getActivity(), GameMainActivity.class);

        intent.putExtra("paramsSel", paramsSel);

        startActivity(intent);
    }

    private void validateQtdWordsAvailable(ParametersSelected params, int level) {

        if(params.getCategoryId() != 0) {
            mUri = HangmanContract.WordEntry.buildCountWordsUsageWithLanguageCategoryLevel(params.getLanguageId(),
                    params.getCategoryId(), level);

            mCursor = getContext().getContentResolver().query(mUri, WORD_COUNT_USAGE_COLUMNS, null, null, WORD_COUNT_USAGE_SORT_ORDER);
        }else {
            mCursor = getContext().getContentResolver().query(HangmanContract.WordEntry.CONTENT_URI,
                    WORD_COUNT_USAGE_COLUMNS,
                    "language_id = ? group by word_used",
                    new String[]{Integer.toString(params.getLanguageId())},
                    WORD_COUNT_USAGE_SORT_ORDER
            );
        }
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

                        int qtdRws = 0;
                        if(params.getCategoryId() > 0) {
                            qtdRws = getContext().getContentResolver().update(
                                    HangmanContract.WordEntry.CONTENT_URI,
                                    values,
                                    "language_id = ? and category_id = ? and level_id = ?",
                                    new String[]{Integer.toString(params.getLanguageId())
                                            , Integer.toString(params.getCategoryId())
                                            , Integer.toString(level)}
                            );
                        }else {
                            qtdRws = getContext().getContentResolver().update(
                                    HangmanContract.WordEntry.CONTENT_URI,
                                    values,
                                    "language_id = ? and level_id = ?",
                                    new String[]{Integer.toString(params.getLanguageId())
                                            , Integer.toString(level)}
                            );
                        }
                    }
                }
            }
        }
    }

    private void loadQtdStarsCategoryOverall() {
        mUri = HangmanContract.TaleOverallEntry.buildTaleOverallWithPlayer(TALE_PLAYER_FLAG);

        mCursor = getContext().getContentResolver().query(mUri, TALE_OVERALL_COLUMNS, null, null, null);

        mListTaleOverall = new ArrayList<TaleOverall>();
        TaleOverall taleOverall;

        if(mCursor != null && mCursor.moveToFirst()) {

            do {
                taleOverall = new TaleOverall();
                taleOverall.setCategryId(mCursor.getInt(COL_TALE_OVERALL_CATEGORY_ID));
                taleOverall.setNumStarsAverage(mCursor.getFloat(COL_TALE_OVERALL_NUM_STARS_AVERAGE));

                mListTaleOverall.add(taleOverall);

            }while(mCursor.moveToNext());

            mCursor.close();

        }
    }

}
