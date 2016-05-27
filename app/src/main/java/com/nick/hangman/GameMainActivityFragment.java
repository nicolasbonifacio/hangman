package com.nick.hangman;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.nick.hangman.Objects.Word;
import com.nick.hangman.data.HangmanContract;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * A placeholder fragment containing a simple view.
 */
public class GameMainActivityFragment extends Fragment {

    private static final int WORD_NOT_USED_FLAG = 0;
    private static final int WORD_USED_FLAG = 1;
    private static final int ENABLED_FLAG = 1;
    private static final int GALLOWS_IMAGE_BEGIN_FLAG = 0;
    private static final int WORD_PADDING_TIMES = 5;
    private static final String DASH_FLAG = "DASH";
    private static final int MAX_CHARACTERS_PER_LINE = 14;
    private static final int HEIGHT_DENSITY_TIMES = 20;
    private static final float GALLOWS_PERC_HEIGHT = 0.26f; //26%
    private static final float GALLOWS_PERC_HEIGHT_WITH_IMAGE = 0.35f; //35%
    private static final float GALLOWS_HEIGHT_AND_WIDTH_RATIO = 1.4434f;
    private static final float KEYPAD_FONT_SIZE = 22f;
    private static final float GAME_DETAILS_INFO_FONT_SIZE = 18f;
    private static final int IMAGE_FACE_CENTER_IN_DP = 101;
    private static final int IMAGE_FACE_BASE_HEIGHT_IN_DP = 62;
    private static final int IMAGE_FACE_Y_IN_DP = 23;
    public static final float PERC_WIDTH_DIALOG = 0.7f; //70%
    public static final float PERC_WIDTH_SHARE_DIALOG = 0.3f; //almost 50% (of dialog width)
    private static final int SHARE_ICON_DIALOG_ID = 998;
    private static final int SHARE_ICON_DIALOG_PRESSED_ID = 994;
    private static final int ALL_CATEGORIES_ID = 0;
    public static final float PERC_CHARACTER_HEIGHT_REDUCTION = 0.8f;
    private static final int IMAGE_HEAD_ID = 989;

    private static final String WORD_SORT_ORDER = "RANDOM() LIMIT 1";

    private static final int TOTAL_QTD_ERROR = 6;

    private static final String[] WORD_COLUMNS = {
            HangmanContract.WordEntry.TABLE_NAME + "." + HangmanContract.WordEntry._ID,
            HangmanContract.WordEntry.COLUMN_WORD
    };

    private static final String[] SCORE_MODEL_COLUMNS = {
            HangmanContract.ScoreModelEntry.TABLE_NAME + "." + HangmanContract.ScoreModelEntry.COLUMN_POINTS,
            HangmanContract.ScoreModelEntry.TABLE_NAME + "." + HangmanContract.ScoreModelEntry.COLUMN_QTD_STARS
    };

    private static final String[] FIRST_KEYPAD_LINE_CHARACTERS = {"A", "B", "C", "D", "E", "F",
            "G", "H", "I"};

    private static final String[] SECOND_KEYPAD_LINE_CHARACTERS = {"J", "K", "L", "M", "N", "O",
            "P", "Q", "R"};

    private static final String[] THIRD_KEYPAD_LINE_CHARACTERS = {"S", "T", "U", "V", "W", "X",
            "Y", "Z"};

    private static final String sWordLanguageAllCategoriesSelection =
            HangmanContract.WordEntry.TABLE_NAME +
                    "." + HangmanContract.WordEntry.COLUMN_LOC_KEY_LANGUAGE + " = ? AND " +
                    HangmanContract.WordEntry.TABLE_NAME +
                    "." + HangmanContract.WordEntry.COLUMN_WORD_USED + " = ?";

    //WORD columns
    public static final int COL_WORD_ID = 0;
    public static final int COL_WORD_WORD = 1;

    //SCORE_MODEL columns
    public static final int COL_SCORE_MODEL_POINTS = 0;
    public static final int COL_SCORE_MODEL_QTD_STARS = 1;

    public static final String SOUND_ON_OFF_PREFS = "soundOnOffPrefs";
    private static final int SOUND_ON_OFF_NOT_DEFINED_FLAG = -1;
    private static final int SOUND_ON_FLAG = 1;

    public static final String INTERSTITIAL_PREFS = "interstitialPrefs";
    public static final String INTERSTITIAL_KEY = "interstitialKey";
    private static final int INTERSTITIAL_FIRST_PLAY_FLAG = 0;
    private static final int INTERSTITIAL_SHOW_FLAG = 4;

    public static final String COINS_PREFS = "coinsPrefs";
    public static final String COINS_KEY = "coinsKey";
    private static final int FIRST_COINS_FILL = 100;

    public static final String DAILY_COINS_PREFS = "dailyCoinsPrefs";
    public static final String DAILY_COINS_KEY = "dailyCoinsKey";
    private static final int DAILY_COINS_QTD = 15;

    public static final String HANGY_DIALOG_PREFS = "hangyDialogPrefs";
    public static final String HANGY_DIALOG_KEY = "hangyDialogKey";

    private static final int FINGER_HINT_PRICE = 5;
    private static final int  MORE_CHANCE_HINT_PRICE = 2;

    private Uri mUri;
    private Cursor mCursor;
    private View rootView;
    private ImageView mGallowsView;

    private ParametersSelected paramsSel;
    private Word mWord;

    private ArrayList<ArrayList> mListWord;
    private ArrayList<String> mListCharacters;
    private int mQtdError;
    private int mQtdHits;
    private int lastDash;
    private boolean mGameWon;
    private int mTotalCharacters;
    private float mDensity;
    private float mScaledDensity;
    private int mCharacterHeight;
    private boolean mIsImageSelected;
    private int widthPx;
    private int heightPx;

    private Utils mUtils;

    private MediaPlayer keypadSound;
    private MediaPlayer buttonSound;

    private InterstitialAd mInterstitial;
    private boolean mIsAdLoaded;

    private int mUsableCoins;

    private boolean mFirstTimeImageHead;

    private ArrayList<Integer> mCharactersOpened;

    public GameMainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_game_main, container, false);

        keypadSound = MediaPlayer.create(getContext(), R.raw.keypad_sound);
        buttonSound = MediaPlayer.create(getContext(), R.raw.button_sound);

        mInterstitial = new InterstitialAd(getContext());

        mFirstTimeImageHead = true;

        mCharactersOpened = new ArrayList<Integer>();

        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra("paramsSel")) {
            paramsSel = (ParametersSelected) intent.getSerializableExtra("paramsSel");

            if(paramsSel.getImageImageName() != null && !paramsSel.getImageImageName().equals("")) {
                mIsImageSelected = true;
            }else {
                mIsImageSelected = false;
            }

            //Validate daily coins
            if(hasDailyCoins()) {
                setHintCoins(DAILY_COINS_QTD);
            }

            int horizontalPadding = (int)getResources().getDimension(R.dimen.activity_horizontal_margin);
            int verticalPadding = (int)getResources().getDimension(R.dimen.activity_vertical_margin);
            widthPx = getContext().getResources().getDisplayMetrics().widthPixels;
            heightPx = getContext().getResources().getDisplayMetrics().heightPixels;
            mDensity = getContext().getResources().getDisplayMetrics().density;
            mScaledDensity = getContext().getResources().getDisplayMetrics().scaledDensity;

            //TextView player1ScoreView = (TextView) rootView.findViewById(R.id.player1GameScoreValueTextView);
            TextView categoryDescrView = (TextView) rootView.findViewById(R.id.categoryGameDescrTextView);

            //player1ScoreView.setText(Integer.toString(paramsSel.getTaleScoreCategoryScore()));
            categoryDescrView.setText(paramsSel.getCategoryDescrCategory());
            categoryDescrView.setTextSize(GAME_DETAILS_INFO_FONT_SIZE);
            categoryDescrView.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));

            //Progress bar
            //Half screen width, minus two times horizontal padding
            int baseProgressBarWidth = (int)(widthPx / 2) - (horizontalPadding * 2);

            RelativeLayout baseProgressBarLayout = (RelativeLayout) rootView.findViewById(R.id.baseProgressBarLayout);
            RelativeLayout.LayoutParams baseRelativeParams = new RelativeLayout.LayoutParams(
                    baseProgressBarWidth,
                    (int)getResources().getDimension(R.dimen.base_progress_bar_height));

            ImageView baseLevelDescrView = new ImageView(getContext());
            baseLevelDescrView.setBackgroundColor(getResources().getColor(R.color.colorBaseProgressBar));

            baseProgressBarLayout.addView(baseLevelDescrView, baseRelativeParams);

            //% of level completed
            int percCompletedBar = (int)(paramsSel.getLevelPercCompleted() * baseProgressBarWidth) / 100;

            RelativeLayout progressBarLayout = (RelativeLayout) rootView.findViewById(R.id.progressBarLayout);
            RelativeLayout.LayoutParams progressRelativeParams = new RelativeLayout.LayoutParams(
                    percCompletedBar,
                    (int)getResources().getDimension(R.dimen.progress_bar_height));

            ImageView levelDescrView = new ImageView(getContext());
            levelDescrView.setImageDrawable(getResources().getDrawable(R.drawable.progress_bar));
            levelDescrView.setScaleType(ImageView.ScaleType.CENTER);
            levelDescrView.setAdjustViewBounds(false);
            levelDescrView.setPadding(0, 0, 0, 0);
            levelDescrView.setMaxWidth(percCompletedBar);
            levelDescrView.setMinimumWidth(percCompletedBar);

            progressBarLayout.addView(levelDescrView, progressRelativeParams);

            final ImageView hangyLookingForward = (ImageView) rootView.findViewById(R.id.hangyLookingForward);
            hangyLookingForward.setVisibility(View.INVISIBLE);

            final ImageView hangyLookingForwardPressed = (ImageView) rootView.findViewById(R.id.hangyLookingForwardPressed);
            hangyLookingForwardPressed.setVisibility(View.INVISIBLE);

            final ImageView hangy = (ImageView) rootView.findViewById(R.id.hangy);
            hangy.setBackgroundResource(R.drawable.hangy);

            AnimationDrawable hangyFrameAnimation = (AnimationDrawable) hangy.getBackground();
            hangyFrameAnimation.start();

            hangy.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            hangy.setVisibility(View.INVISIBLE);
                            hangyLookingForwardPressed.setVisibility(View.VISIBLE);
                            break;
                        case MotionEvent.ACTION_UP:
                            hangyLookingForwardPressed.setVisibility(View.INVISIBLE);
                            hangyLookingForward.setVisibility(View.VISIBLE);
                            if(getSoundStatus() == SOUND_ON_FLAG) {
                                buttonSound.start();
                            }

                            openHintDialog();

                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });

            TextView coinsQtdText = (TextView) rootView.findViewById(R.id.coinsQtdText);
            coinsQtdText.setText(Integer.toString(getHintCoins()));

            lastDash = 0;
            mGameWon = false;
            mUtils = new Utils();
            //Fetch DB for the word and splits it into an ArrayList, putting each character in one position
            if(loadWordNotUsed()) {

                if(mIsImageSelected) {

                    int gallowsHeight = (int) ((heightPx * GALLOWS_PERC_HEIGHT_WITH_IMAGE));
                    int gallowsWidth = (int) ((heightPx * GALLOWS_PERC_HEIGHT_WITH_IMAGE) / GALLOWS_HEIGHT_AND_WIDTH_RATIO);

                    mGallowsView = (ImageView) rootView.findViewById(R.id.imageGallowsView);
                    mGallowsView.setImageDrawable(getResources().getDrawable(mUtils.getGallowsImageWithImage(GALLOWS_IMAGE_BEGIN_FLAG)));
                    mGallowsView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    mGallowsView.setAdjustViewBounds(true);
                    mGallowsView.setPadding(0, 0, 0, 0);
                    mGallowsView.setMaxWidth(gallowsWidth);
                    mGallowsView.setMaxHeight(gallowsHeight);
                    mGallowsView.setMinimumWidth(gallowsWidth);
                    mGallowsView.setMinimumHeight(gallowsHeight);

                }else {

                    int gallowsHeight = (int) ((heightPx * GALLOWS_PERC_HEIGHT));
                    int gallowsWidth = (int) ((heightPx * GALLOWS_PERC_HEIGHT) / GALLOWS_HEIGHT_AND_WIDTH_RATIO);

                    mGallowsView = (ImageView) rootView.findViewById(R.id.imageGallowsView);
                    mGallowsView.setImageDrawable(getResources().getDrawable(mUtils.getGallowsImage(GALLOWS_IMAGE_BEGIN_FLAG)));
                    mGallowsView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    mGallowsView.setAdjustViewBounds(true);
                    mGallowsView.setPadding(0, 0, 0, 0);
                    mGallowsView.setMaxWidth(gallowsWidth);
                    mGallowsView.setMaxHeight(gallowsHeight);
                    mGallowsView.setMinimumWidth(gallowsWidth);
                    mGallowsView.setMinimumHeight(gallowsHeight);
                }

//
//                int c = getContext().getResources().getDisplayMetrics().densityDpi;
//                float xdpi = getContext().getResources().getDisplayMetrics().xdpi;
//                float ydpi = getContext().getResources().getDisplayMetrics().ydpi;
//                int keypadButtonWidth = Math.round((int)(density*35));
//                int keypadButtonHeight = Math.round((int)(density*45));

                int paddingLeftAndRight = (int)(getResources().getDimension(R.dimen.keypad_padding_horizontal) * 2);

                int widthLessPadding = widthPx - paddingLeftAndRight;

                int keypadButtonWidth = (int)(widthLessPadding / FIRST_KEYPAD_LINE_CHARACTERS.length);
                int keypadButtonHeight = (int)(keypadButtonWidth * 1.2);

                //Create game keypad line 1
                LinearLayout firstKeyPadLineLayout = (LinearLayout) rootView.findViewById(R.id.firstKeypadLineLayout);
                for (int i = 2001; i <= FIRST_KEYPAD_LINE_CHARACTERS.length + 2000; i++) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            keypadButtonWidth,
                            keypadButtonHeight);

                    Button btn = new Button(getContext());
                    btn.setId(i);
                    final int id_ = btn.getId();
                    btn.setText(FIRST_KEYPAD_LINE_CHARACTERS[i - 2001]);
                    btn.setTextSize(KEYPAD_FONT_SIZE);
                    btn.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
                    btn.setPadding(0, 0, 0, 0);

                    firstKeyPadLineLayout.addView(btn, params);

                    //First key pad line button listener
                    final Button btn1 = ((Button) rootView.findViewById(id_));
                    btn1.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {

                            btn1.setEnabled(false);
                            btn1.setBackgroundColor(getResources().getColor(R.color.colorButtonDisabled));
                            if(getSoundStatus() == SOUND_ON_FLAG) {
                                keypadSound.start();
                            }
                            handleGuess(FIRST_KEYPAD_LINE_CHARACTERS[btn1.getId() - 2001]);

                        }
                    });
                }

                //Create game keypad line 2
                LinearLayout secondKeyPadLineLayout = (LinearLayout) rootView.findViewById(R.id.secondKeypadLineLayout);
                for (int i = 3001; i <= SECOND_KEYPAD_LINE_CHARACTERS.length + 3000; i++) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            keypadButtonWidth,
                            keypadButtonHeight);
                    Button btn = new Button(getContext());
                    btn.setId(i);
                    final int id_ = btn.getId();
                    btn.setText(SECOND_KEYPAD_LINE_CHARACTERS[i - 3001]);
                    btn.setTextSize(KEYPAD_FONT_SIZE);
                    btn.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
                    btn.setPadding(0, 0, 0, 0);

                    secondKeyPadLineLayout.addView(btn, params);

                    //Second key pad line button listener
                    final Button btn2 = ((Button) rootView.findViewById(id_));
                    btn2.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {

                            btn2.setEnabled(false);
                            btn2.setBackgroundColor(getResources().getColor(R.color.colorButtonDisabled));
                            if(getSoundStatus() == SOUND_ON_FLAG) {
                                keypadSound.start();
                            }
                            handleGuess(SECOND_KEYPAD_LINE_CHARACTERS[btn2.getId() - 3001]);

                        }
                    });
                }

                //Create game keypad line 3
                LinearLayout thirdKeyPadLineLayout = (LinearLayout) rootView.findViewById(R.id.thirdKeypadLineLayout);
                for (int i = 4001; i <= THIRD_KEYPAD_LINE_CHARACTERS.length + 4000; i++) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            keypadButtonWidth,
                            keypadButtonHeight);
                    Button btn = new Button(getContext());
                    btn.setId(i);
                    final int id_ = btn.getId();
                    btn.setText(THIRD_KEYPAD_LINE_CHARACTERS[i - 4001]);
                    btn.setTextSize(KEYPAD_FONT_SIZE);
                    btn.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
                    btn.setPadding(0, 0, 0, 0);

                    thirdKeyPadLineLayout.addView(btn, params);

                    //Third key pad line button listener
                    final Button btn3 = ((Button) rootView.findViewById(id_));
                    btn3.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {

                            btn3.setEnabled(false);
                            btn3.setBackgroundColor(getResources().getColor(R.color.colorButtonDisabled));
                            if(getSoundStatus() == SOUND_ON_FLAG) {
                                keypadSound.start();
                            }
                            handleGuess(THIRD_KEYPAD_LINE_CHARACTERS[btn3.getId() - 4001]);

                        }
                    });
                }

            }else {
                Toast.makeText(getContext(),(String)getResources().getString(R.string.error_loading_word),
                        Toast.LENGTH_SHORT).show();
            }

        }else {
            Toast.makeText(getContext(),(String)getResources().getString(R.string.error_loading_word),
                    Toast.LENGTH_SHORT).show();
        }

        if(isShowHangyDialog()) {
            openHangyDialog();
        }

        return rootView;
    }

    private boolean loadWordNotUsed() {
/*
        if(paramsSel.getLevelId() == 0) {
            //All levels released
            String sLanguage = Integer.toString(paramsSel.getLanguageId());
            String sCategory = Integer.toString(paramsSel.getCategoryId());
            String sWordNotUsed = Integer.toString(WORD_NOT_USED_FLAG);

            mCursor = getContext().getContentResolver().query(HangmanContract.WordEntry.CONTENT_URI,
                    WORD_COLUMNS,
                    sWordLanguageCategorySelection, //selection
                    new String[]{sLanguage, sCategory, sWordNotUsed}, //selection args
                    null);

        }else {
            //Sets a specific level
*/
        if(paramsSel.getCategoryId() != ALL_CATEGORIES_ID) {
            mUri = HangmanContract.WordEntry.buildWordWithLanguageCategoryLevel(paramsSel.getLanguageId(),
                    paramsSel.getCategoryId(),
                    WORD_NOT_USED_FLAG,
                    paramsSel.getLevelId());

            mCursor = getContext().getContentResolver().query(mUri, WORD_COLUMNS, null, null, WORD_SORT_ORDER);
        }else {

            String sLanguage = Integer.toString(paramsSel.getLanguageId());
            String sWordNotUsed = Integer.toString(WORD_NOT_USED_FLAG);

            mCursor = getContext().getContentResolver().query(HangmanContract.WordEntry.CONTENT_URI,
                    WORD_COLUMNS,
                    sWordLanguageAllCategoriesSelection, //selection
                    new String[]{sLanguage, sWordNotUsed}, //selection args
                    WORD_SORT_ORDER);

        }
//        }

        if(mCursor != null && mCursor.moveToFirst()) {

            mWord = new Word();
            mWord.setId(mCursor.getInt(COL_WORD_ID));
            mWord.setWord(mCursor.getString(COL_WORD_WORD));
            mWord.setLanguageId(paramsSel.getLanguageId());
            mWord.setCategoryId(paramsSel.getCategoryId());
            mWord.setLevelId(paramsSel.getLevelId());
            mWord.setWordUsed(WORD_USED_FLAG);

            mCursor.close();

            mTotalCharacters = 0;

//mWord.setWord("A");
            mWord.setWord(mWord.getWord().replace("   ", " "));
            mWord.setWord(mWord.getWord().replace("  ", " "));

            //Splits the word for it's spaces
            mListWord = new ArrayList<ArrayList>();
            String[] words = mWord.getWord().split(" ");

            for(int j = 0; j < words.length; j++) {
                mListCharacters = new ArrayList<String>();
                for (int i = 0; i < words[j].length(); i++) {
                    mListCharacters.add(i, words[j].substring(i, i + 1));
                    mTotalCharacters++;
                }
                mListWord.add(j, mListCharacters);

            }

            mQtdError = 0;
            mQtdHits = 0;

            //Create word structure
            int higherNumCharactersMore = 0;
            boolean hasMoreThanMax = false;
            for(int j = 0; j < words.length; j++) {
                if(words[j].length() > MAX_CHARACTERS_PER_LINE) {
                    int numCharactersMore = words[j].length();
                    if(numCharactersMore > higherNumCharactersMore) {
                        higherNumCharactersMore = numCharactersMore;
                        mCharacterHeight = (int)(((HEIGHT_DENSITY_TIMES * mDensity) * MAX_CHARACTERS_PER_LINE) / higherNumCharactersMore);
                    }
                    hasMoreThanMax = true;
                }
            }
            if(!hasMoreThanMax) {
                mCharacterHeight = (int)(HEIGHT_DENSITY_TIMES * mDensity);
            }

            LinearLayout wordLayout = (LinearLayout) rootView.findViewById(R.id.wordLayout);
            LinearLayout wordLineLayout;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            LinearLayout.LayoutParams paramsCharacter = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    mCharacterHeight);

            LinearLayout.LayoutParams paramsSpace = new LinearLayout.LayoutParams(
                    getResources().getDimensionPixelSize(R.dimen.word_spacing_horizontal_padding),
                    (int)(mCharacterHeight / 2));

            wordLineLayout = new LinearLayout(getContext());
            wordLineLayout.setOrientation(LinearLayout.HORIZONTAL);
            wordLineLayout.setLayoutParams(params);
            wordLineLayout.setPadding(0, (int)mDensity * WORD_PADDING_TIMES, 0, (int)mDensity * WORD_PADDING_TIMES);
            int id = 1001;
            int charactersAdded = 0;
            int nextWord = 0;
            int qtdLines = 0;
            for(int j = 0; j < words.length; j++) {

                charactersAdded = charactersAdded + words[j].length();

                for (int i = 1001; i <= words[j].length() + 1000; i++) {

                    final ImageView dash = new ImageView(getContext());
                    dash.setId(id);
                    dash.setImageDrawable(getResources().getDrawable(mUtils.getCharacterImage(DASH_FLAG)));
                    dash.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    dash.setAdjustViewBounds(true);
                    dash.setPadding(0, 0, 0, 0);
                    dash.setMaxHeight(mCharacterHeight);
                    dash.setMinimumHeight(mCharacterHeight);
                    wordLineLayout.addView(dash, paramsCharacter);

                    final int _id = id;
                    dash.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            boolean isCharacterOpened = false;
                            for(int i = 0; i < mCharactersOpened.size();i++) {
                                if(mCharactersOpened.get(i) == _id) {
                                    isCharacterOpened = true;
                                }
                            }
                            if(mUsableCoins >= FINGER_HINT_PRICE && !isCharacterOpened) {
                                showCharacter(_id);
                            }
                        }
                    });

                    if (id > lastDash) {
                        lastDash = id;
                    }
                    id++;
                }

                if(j + 1 < words.length) {
                    nextWord = words[j + 1].length();
                }else {
                    nextWord = 0;
                }

                //If current word + next word is less than 13 characters, keep both words in the
                // same line and the subsequent lines too (up to 13 characters)
                if((charactersAdded + nextWord) > (MAX_CHARACTERS_PER_LINE -1) || (nextWord == 0)) {
                    wordLayout.addView(wordLineLayout, params);
                    qtdLines++;
                    wordLineLayout = new LinearLayout(getContext());
                    wordLineLayout.setOrientation(LinearLayout.HORIZONTAL);
                    wordLineLayout.setLayoutParams(params);
                    wordLineLayout.setPadding(0, (int)mDensity * WORD_PADDING_TIMES, 0, (int)mDensity * WORD_PADDING_TIMES);

                    charactersAdded = 0;
                }else {
                    ImageView space = new ImageView(getContext());

                    wordLineLayout.addView(space, paramsSpace);
                }
            }

            //If there's more than 3 lines, reduces each character line's height
            if(qtdLines >= 3) {
                LinearLayout.LayoutParams newLineParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        (int)(mCharacterHeight * PERC_CHARACTER_HEIGHT_REDUCTION));
                for(int x = 1001; x < id; x++) {
                    ImageView tempCharacters = (ImageView) rootView.findViewById(x);
                    tempCharacters.setLayoutParams(newLineParams);
                }
            }


            return true;
        }

        return false;
    }

    private void handleGuess(String character) {
        boolean hasError = true;
        ArrayList<String> listCharacters = new ArrayList<String>();
        int id = 1001;
        for(int j = 0; j < mListWord.size(); j++) {
            listCharacters = mListWord.get(j);
            for (int i = 0; i < listCharacters.size(); i++) {
                if (listCharacters.get(i).equals(character)) {
                    hasError = false;
                    drawCharacter(id, character);
                    mQtdHits++;
                    mCharactersOpened.add(id);
                    // Verify if the game was won
                    if ((mQtdHits) >= mTotalCharacters) {
                        mGameWon = true;
                    }
                }
                id++;
            }
        }

        if(mGameWon) {
            // Win game popup
            endGamePopup(true);
        }

        if(hasError) {
            mQtdError++;

            drawGallowsImage(false);

            // Verify if the game is over
            if(mQtdError >= TOTAL_QTD_ERROR) {
                endGamePopup(false);
            }
        }

    }

    private void drawGallowsImage(boolean imageHint) {
        if(mIsImageSelected) {

            int originalImageWidth = paramsSel.getImageImageWidth();
            int originalImageHeight = paramsSel.getImageImageHeight();
            float imageRatio = (float) originalImageHeight / originalImageWidth;

            int imageWidth = (int) ((IMAGE_FACE_BASE_HEIGHT_IN_DP * mDensity) / imageRatio);
            int imageHeight = (int) (IMAGE_FACE_BASE_HEIGHT_IN_DP * mDensity);

            RelativeLayout gallowsLayout = (RelativeLayout) rootView.findViewById(R.id.gallowsLayout);
            RelativeLayout.LayoutParams gallowsParams = new RelativeLayout.LayoutParams(
                    imageWidth,
                    imageHeight //61dp
            );

            mGallowsView.setImageDrawable(getResources().getDrawable(mUtils.getGallowsImageWithImage(mQtdError)));

            File image = new File(paramsSel.getImageImagePath(), paramsSel.getImageImageName());
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap imageLastUsedBitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);

            ImageView imageHead;
            if(mFirstTimeImageHead) {
                imageHead = new ImageView(getContext());
                imageHead.setId(IMAGE_HEAD_ID);
                imageHead.setImageBitmap(imageLastUsedBitmap);

                imageHead.setX((int) ((IMAGE_FACE_CENTER_IN_DP * mDensity) - (imageWidth / 2)));
                imageHead.setY((int) (IMAGE_FACE_Y_IN_DP * mDensity)); //sempre tem que ser 23dp

                gallowsLayout.addView(imageHead, gallowsParams);

                mFirstTimeImageHead = false;
            }else if(mQtdError == 1 && !imageHint) {
                imageHead = (ImageView) rootView.findViewById(IMAGE_HEAD_ID);
                imageHead.setVisibility(View.VISIBLE);
            }else if (mQtdError == 0) {
                imageHead = (ImageView) rootView.findViewById(IMAGE_HEAD_ID);
                imageHead.setVisibility(View.INVISIBLE);
            }



        }else {
            mGallowsView.setImageDrawable(getResources().getDrawable(mUtils.getGallowsImage(mQtdError)));
        }
    }

    private void drawCharacter(int position, String sCharacter) {

        ImageView characterView;

        for (int i = 1001; i <= mTotalCharacters+1000; i++) {

            if(i == position) {
                characterView = (ImageView) rootView.findViewById(i);
                characterView.setImageDrawable(getResources().getDrawable(mUtils.getCharacterImage(sCharacter)));
                characterView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                characterView.setAdjustViewBounds(true);
                characterView.setPadding(0, 0, 0, 0);
                characterView.setMaxHeight(mCharacterHeight);
                characterView.setMinimumHeight(mCharacterHeight);
            }
        }
    }

    private void setNewWord() {

        //Restore keypad
        enableKeypad();

        //Clear word
        TextView dash;
        LinearLayout wordLayout = (LinearLayout) rootView.findViewById(R.id.wordLayout);
        wordLayout.removeAllViews();

    }

    private void endGamePopup(boolean gameWon) {

        mIsAdLoaded = false;
        if(getInterstitialStatus()) {
            loadInterstitial();
        }

        final Dialog dialog = new Dialog(getContext());

        dialog.setContentView(R.layout.custom_dialog);

        //Main dialog layout
        LinearLayout dialogLayout = (LinearLayout) dialog.findViewById(R.id.dialogLayout);
        FrameLayout.LayoutParams dialogParams = new FrameLayout.LayoutParams(
                (int)(widthPx * PERC_WIDTH_DIALOG),
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialogLayout.setLayoutParams(dialogParams);

        //Dialog's 'toolbar'
        LinearLayout imageShareDialogLayout = (LinearLayout) dialog.findViewById(R.id.imageShareDialogLayout);
        LinearLayout.LayoutParams imageShareDialogParams = new LinearLayout.LayoutParams(
                (int)((widthPx * PERC_WIDTH_DIALOG) * PERC_WIDTH_SHARE_DIALOG),
                getResources().getDimensionPixelSize(R.dimen.share_dialog_bar_height)
        );

        FrameLayout imageShareDialogFrame = new FrameLayout(getContext());

        ImageView imageShareDialogView = new ImageView(getContext());
        imageShareDialogView.setImageDrawable(getResources().getDrawable(R.drawable.share_dialog));
        imageShareDialogView.setPadding(
                0,
                getResources().getDimensionPixelSize(R.dimen.share_dialog_padding_top),
                getResources().getDimensionPixelSize(R.dimen.share_dialog_padding_right),
                0);
        imageShareDialogView.setId(SHARE_ICON_DIALOG_ID);

        ImageView imageShareDialogViewPressed = new ImageView(getContext());
        imageShareDialogViewPressed.setImageDrawable(getResources().getDrawable(R.drawable.share_dialog_pressed));
        imageShareDialogViewPressed.setPadding(
                0,
                getResources().getDimensionPixelSize(R.dimen.share_dialog_padding_top),
                getResources().getDimensionPixelSize(R.dimen.share_dialog_padding_right),
                0);
        imageShareDialogViewPressed.setId(SHARE_ICON_DIALOG_PRESSED_ID);
        imageShareDialogViewPressed.setVisibility(View.INVISIBLE);

        imageShareDialogFrame.addView(imageShareDialogView);
        imageShareDialogFrame.addView(imageShareDialogViewPressed);

        imageShareDialogLayout.addView(imageShareDialogFrame, imageShareDialogParams);

        final ImageView imageShareDialogViewListenPressed = ((ImageView) dialog.findViewById(SHARE_ICON_DIALOG_PRESSED_ID));

        final ImageView imageShareDialogViewListen = ((ImageView) dialog.findViewById(SHARE_ICON_DIALOG_ID));
        imageShareDialogViewListen.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        imageShareDialogViewListen.setVisibility(View.INVISIBLE);
                        imageShareDialogViewListenPressed.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:
                        imageShareDialogViewListen.setVisibility(View.VISIBLE);
                        imageShareDialogViewListenPressed.setVisibility(View.INVISIBLE);
                        if(getSoundStatus() == SOUND_ON_FLAG) {
                            buttonSound.start();
                        }
                        shareScreen();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        int qtdStars = loadPointsAndStarsFromScoreModel(mQtdError);

        TextView endGameDialogView = (TextView) dialog.findViewById(R.id.endGameDialogView);
        endGameDialogView.setPadding(
                0,
                getResources().getDimensionPixelSize(R.dimen.end_game_dialog_msg_padding_top),
                0,
                getResources().getDimensionPixelSize(R.dimen.end_game_dialog_msg_padding_bottom));

        if(qtdStars > 0) {
            endGameDialogView.setText(getResources().getString(R.string.win_game_dialog_text));
            endGameDialogView.setTextSize(getResources().getDimension(R.dimen.win_game_dialog_text_size) / mScaledDensity);
        }else {
            endGameDialogView.setText(getResources().getString(R.string.loose_game_dialog_text));
            endGameDialogView.setTextSize(getResources().getDimension(R.dimen.loose_game_dialog_text_size) / mScaledDensity);
        }

        FrameLayout imageStarsDialogLayout = (FrameLayout) dialog.findViewById(R.id.imageStarsDialogLayout);
        LinearLayout.LayoutParams imageStarsDialogParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.image_stars_dialog_size)
        );

        ImageView imageStarsDialogView = new ImageView(getContext());
        imageStarsDialogView.setImageDrawable(getResources().getDrawable(mUtils.getDialogIconStars(qtdStars)));
        imageStarsDialogLayout.addView(imageStarsDialogView, imageStarsDialogParams);

        TextView wordSelectedDialogView = (TextView) dialog.findViewById(R.id.wordSelectedDialogView);
        wordSelectedDialogView.setText(mWord.getWord());
        wordSelectedDialogView.setTextColor(getResources().getColor(R.color.colorWordDialog));
        wordSelectedDialogView.setTypeface(wordSelectedDialogView.getTypeface(), Typeface.BOLD);
        wordSelectedDialogView.setTextSize(getResources().getDimension(R.dimen.word_dialog_text_size) / mScaledDensity);
        wordSelectedDialogView.setPadding(
                0,
                getResources().getDimensionPixelSize(R.dimen.word_dialog_msg_padding_top),
                0,
                getResources().getDimensionPixelSize(R.dimen.word_dialog_msg_padding_top)
        );

        final ImageView continueGameDialogButtonPressed = ((ImageView) dialog.findViewById(R.id.continueGameDialogButtonPressed));
        continueGameDialogButtonPressed.setImageDrawable(getResources().getDrawable(R.drawable.button_dialog_continue_blue_en_pressed));
        continueGameDialogButtonPressed.setVisibility(View.INVISIBLE);

        final ImageView continueGameDialogButton = ((ImageView) dialog.findViewById(R.id.continueGameDialogButton));
        continueGameDialogButton.setImageDrawable(getResources().getDrawable(R.drawable.button_dialog_continue_blue_en));

        continueGameDialogButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        continueGameDialogButton.setVisibility(View.INVISIBLE);
                        continueGameDialogButtonPressed.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:
                        continueGameDialogButton.setVisibility(View.VISIBLE);
                        continueGameDialogButtonPressed.setVisibility(View.INVISIBLE);
                        if(getSoundStatus() == SOUND_ON_FLAG) {
                            buttonSound.start();
                        }
                        if(mIsAdLoaded || mInterstitial.isLoaded()) {
                            mInterstitial.show();
                        }
                        dialog.cancel();
                        getActivity().finish();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        TextView dialogDefinitionIconText = (TextView) dialog.findViewById(R.id.dialogDefinitionIconText);
        dialogDefinitionIconText.setText(getResources().getString(mUtils.getDefinitionIconTextId(mWord.getCategoryId())));

        final ImageView definitionButtonPressed = ((ImageView) dialog.findViewById(R.id.dialogDefinitionIconImagePressed));
        definitionButtonPressed.setImageDrawable(getResources().getDrawable(mUtils.getDefinitionIconImageId(mWord.getCategoryId())));
        definitionButtonPressed.setVisibility(View.INVISIBLE);

        final ImageView definitionButton = (ImageView) dialog.findViewById(R.id.dialogDefinitionIconImage);
        definitionButton.setImageDrawable(getResources().getDrawable(mUtils.getDefinitionIconImageId(mWord.getCategoryId())));

        definitionButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        definitionButton.setVisibility(View.INVISIBLE);
                        definitionButtonPressed.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:
                        definitionButton.setVisibility(View.VISIBLE);
                        definitionButtonPressed.setVisibility(View.INVISIBLE);
                        if(getSoundStatus() == SOUND_ON_FLAG) {
                            buttonSound.start();
                        }
                        if(mWord.getCategoryId() == 2 || mWord.getCategoryId() == 3 || mWord.getCategoryId() == 18) {
                            startMap(mWord.getWord());
                        }else {
                            String url = mUtils.getURL(mWord.getCategoryId());
                            startGoogleBrowser(url, mWord.getWord());
                        }

                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        disableKeypad();

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        dialog.show();

    }

    private int loadPointsAndStarsFromScoreModel(int numErrors) {

        int qtdStars = 0;
        int qtdPoints = 0;

        mUri = HangmanContract.ScoreModelEntry.buildScoreModelWithCategoryNumErrors(paramsSel.getCategoryId(), numErrors);

        mCursor = getContext().getContentResolver().query(mUri, SCORE_MODEL_COLUMNS, null, null, null);

        if(mCursor != null && mCursor.moveToFirst()) {

            qtdStars = mCursor.getInt(COL_SCORE_MODEL_QTD_STARS);
            qtdPoints = mCursor.getInt(COL_SCORE_MODEL_POINTS);

            ContentValues taleOverallValues = new ContentValues();
            taleOverallValues.put(HangmanContract.TaleOverallEntry.COLUMN_LOC_KEY_PLAYER, paramsSel.getPlayer1Id());
            taleOverallValues.put(HangmanContract.TaleOverallEntry.COLUMN_LOC_KEY_CATEGORY, paramsSel.getCategoryId());
            taleOverallValues.put(HangmanContract.TaleOverallEntry.COLUMN_LOC_KEY_WORD, mWord.getId());
            taleOverallValues.put(HangmanContract.TaleOverallEntry.COLUMN_NUM_STARS, qtdStars);

            Uri insertedUri = getContext().getContentResolver().insert(
                    HangmanContract.TaleOverallEntry.CONTENT_URI,
                    taleOverallValues
            );

            mCursor.close();

            if(paramsSel.getCategoryId() != 0) {
                updatePlayerScore(qtdPoints);
            }
        }

        updateWordUsed(mWord.getId());

        return qtdStars;

    }

    private void updatePlayerScore(int points) {

        int totalPoints = points + paramsSel.getTaleScoreCategoryScore();
        String id = Integer.toString(paramsSel.getTaleScoreCategoryId());
        String idNextCategory = Integer.toString(paramsSel.getTaleScoreCategoryIdNextCategory());

        if(totalPoints >= paramsSel.getTaleScoreCategoryEnableScore()) {
            //Update player score and unlock new category
            totalPoints = paramsSel.getTaleScoreCategoryEnableScore();

            ContentValues valuesNextCategory = new ContentValues();
            valuesNextCategory.put(HangmanContract.TaleScoreCategoryEntry.COLUMN_CATEGORY_ENABLED, ENABLED_FLAG);

            int qtdRws = getContext().getContentResolver().update(
                    HangmanContract.TaleScoreCategoryEntry.CONTENT_URI,
                    valuesNextCategory,
                    "_id = ?",
                    new String[]{idNextCategory}
            );

            if(qtdRws == 0) {
                //It's the end
            }
        }

        ContentValues values = new ContentValues();
        values.put(HangmanContract.TaleScoreCategoryEntry.COLUMN_PLAYER_SCORE, totalPoints);

        int qtd = getContext().getContentResolver().update(
                HangmanContract.TaleScoreCategoryEntry.CONTENT_URI,
                values,
                "_id = ?",
                new String[]{id}
        );

    }

    public void updateWordUsed(int wordId) {

        String id = Integer.toString(wordId);

        ContentValues wordValues = new ContentValues();
        wordValues.put(HangmanContract.WordEntry.COLUMN_WORD_USED, WORD_USED_FLAG);

        int qtd = getContext().getContentResolver().update(
                HangmanContract.WordEntry.CONTENT_URI,
                wordValues,
                "_id = ?",
                new String[]{id}
        );

    }

    private void disableKeypad() {
        Button btn;

        for (int i = 2001; i <= FIRST_KEYPAD_LINE_CHARACTERS.length + 2000; i++) {
            btn = (Button) rootView.findViewById(i);
            btn.setEnabled(false);
        }

        for (int i = 3001; i <= SECOND_KEYPAD_LINE_CHARACTERS.length + 3000; i++) {
            btn = (Button) rootView.findViewById(i);
            btn.setEnabled(false);
        }

        for (int i = 4001; i <= THIRD_KEYPAD_LINE_CHARACTERS.length + 4000; i++) {
            btn = (Button) rootView.findViewById(i);
            btn.setEnabled(false);
        }
    }

    private void enableKeypad() {
        Button btn;

        for (int i = 2001; i <= FIRST_KEYPAD_LINE_CHARACTERS.length + 2000; i++) {
            btn = (Button) rootView.findViewById(i);
            btn.setEnabled(true);
            btn.setVisibility(View.VISIBLE);
        }

        for (int i = 3001; i <= SECOND_KEYPAD_LINE_CHARACTERS.length + 3000; i++) {
            btn = (Button) rootView.findViewById(i);
            btn.setEnabled(true);
            btn.setVisibility(View.VISIBLE);
        }

        for (int i = 4001; i <= THIRD_KEYPAD_LINE_CHARACTERS.length + 4000; i++) {
            btn = (Button) rootView.findViewById(i);
            btn.setEnabled(true);
            btn.setVisibility(View.VISIBLE);
        }
    }

    private void shareScreen() {

        View content = rootView.findViewById(R.id.gallowsLayout);
        boolean hasStoragePermission = true;

        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "HangmanTale");

        if (!folder.exists()) {
            folder.mkdir();
        }

        View view = content;
        View v = view.getRootView();
        v.setDrawingCacheEnabled(true);
        Bitmap b = v.getDrawingCache();
        String mPath = Environment.getExternalStorageDirectory().toString() + File.separator + "HangmanTale" + File.separator;
        File myPath = new File(mPath, "HangmanTale_temp.jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Toast.makeText(getContext(), getResources().getString(R.string.share_no_permission_storage), Toast.LENGTH_LONG).show();
            hasStoragePermission = false;
        }

        if(hasStoragePermission) {
            File image = new File(Environment.getExternalStorageDirectory() + File.separator + "HangmanTale" + File.separator, "HangmanTale_temp.jpg");
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap mBitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);

            Bitmap icon = mBitmap;
            Intent share = new Intent(Intent.ACTION_SEND);

            //share.setType("image/jpeg");

            share.setType("image/jpeg");
            //share.setType("*/*");
            share.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_text));
            share.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.share_email_title));

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "HangmanTale" + File.separator + "HangmanTale.jpg");
            try {
                f.createNewFile();
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
            share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/HangmanTale/HangmanTale.jpg"));
            startActivity(Intent.createChooser(share, getResources().getText(R.string.share_title)));
        }
    }

    private int getSoundStatus() {
        SharedPreferences.Editor editor = getContext().getSharedPreferences(SOUND_ON_OFF_PREFS, Context.MODE_PRIVATE).edit();
        SharedPreferences prefs = getContext().getSharedPreferences(SOUND_ON_OFF_PREFS, Context.MODE_PRIVATE);
        int restoredPref = prefs.getInt("soundOnOff", SOUND_ON_OFF_NOT_DEFINED_FLAG);
        if (restoredPref == SOUND_ON_OFF_NOT_DEFINED_FLAG) {
            editor.putInt("soundOnOff", SOUND_ON_FLAG);
            editor.commit();
            restoredPref = SOUND_ON_FLAG;
        }
        return restoredPref;
    }

    public void loadInterstitial() {

        mInterstitial.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id));
        mInterstitial.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mIsAdLoaded = true;
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                mIsAdLoaded = true;
            }
        });

        AdRequest ar = new AdRequest.Builder().build();
        mInterstitial.loadAd(ar);

    }

    private boolean getInterstitialStatus() {
        SharedPreferences.Editor editor = getContext().getSharedPreferences(INTERSTITIAL_PREFS, Context.MODE_PRIVATE).edit();
        SharedPreferences prefs = getContext().getSharedPreferences(INTERSTITIAL_PREFS, Context.MODE_PRIVATE);
        int restoredPref = prefs.getInt(INTERSTITIAL_KEY, INTERSTITIAL_FIRST_PLAY_FLAG);
        if (restoredPref < INTERSTITIAL_SHOW_FLAG) {
            restoredPref++;
            editor.putInt(INTERSTITIAL_KEY, restoredPref);
            editor.commit();
            return false;
        }else {
            editor.putInt(INTERSTITIAL_KEY, INTERSTITIAL_FIRST_PLAY_FLAG);
            editor.commit();
            return true;
        }

    }

    private void showCharacter(int id) {

        ArrayList<String> listCharacters = new ArrayList<String>();
        int _id = 1001;
        for(int j = 0; j < mListWord.size(); j++) {
            listCharacters = mListWord.get(j);
            for (int i = 0; i < listCharacters.size(); i++) {
                if (_id == id) {
                    mUsableCoins = mUsableCoins - FINGER_HINT_PRICE;
                    setHintCoins(-FINGER_HINT_PRICE);
                    TextView coinsQtdText = (TextView) rootView.findViewById(R.id.coinsQtdText);
                    coinsQtdText.setText(Integer.toString(getHintCoins()));

                    Button btn;
                    for(int x = 2001; x <= FIRST_KEYPAD_LINE_CHARACTERS.length + 2000; x++) {
                        btn = (Button) rootView.findViewById(x);
                        if(btn.getText().equals(listCharacters.get(i))) {
                            btn.setEnabled(false);
                            btn.setBackgroundColor(getResources().getColor(R.color.colorButtonDisabled));
                        }
                    }
                    for(int x = 3001; x <= SECOND_KEYPAD_LINE_CHARACTERS.length + 3000; x++) {
                        btn = (Button) rootView.findViewById(x);
                        if(btn.getText().equals(listCharacters.get(i))) {
                            btn.setEnabled(false);
                            btn.setBackgroundColor(getResources().getColor(R.color.colorButtonDisabled));
                        }
                    }
                    for(int x = 4001; x <= THIRD_KEYPAD_LINE_CHARACTERS.length + 4000; x++) {
                        btn = (Button) rootView.findViewById(x);
                        if(btn.getText().equals(listCharacters.get(i))) {
                            btn.setEnabled(false);
                            btn.setBackgroundColor(getResources().getColor(R.color.colorButtonDisabled));
                        }
                    }

                    handleGuess(listCharacters.get(i));
                }
                _id++;
            }
        }
    }

    private void openHintDialog() {
        final Dialog hintDialog = new Dialog(getContext(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

        Drawable d = new ColorDrawable(Color.BLACK);
        d.setAlpha(180);
        hintDialog.getWindow().setBackgroundDrawable(d);

        hintDialog.setContentView(R.layout.hint_dialog);
        hintDialog.setCanceledOnTouchOutside(false);
        hintDialog.setCancelable(false);

        TextView fingerPriceText = (TextView) hintDialog.findViewById(R.id.fingerPriceText);
        fingerPriceText.setText(" x " + Integer.toString(FINGER_HINT_PRICE));

        TextView moreChancePriceText = (TextView) hintDialog.findViewById(R.id.moreChancePriceText);
        moreChancePriceText.setText(" x " + Integer.toString(MORE_CHANCE_HINT_PRICE));

        final ImageView fingerHint = (ImageView) hintDialog.findViewById(R.id.fingerHintDialog);
        fingerHint.setBackgroundResource(R.drawable.finger_hint);
        AnimationDrawable fingerHintFrameAnimation = (AnimationDrawable) fingerHint.getBackground();
        fingerHintFrameAnimation.start();

        final ImageView fingerHintPressed = (ImageView) hintDialog.findViewById(R.id.fingerHintDialogPressed);
        fingerHintPressed.setVisibility(View.INVISIBLE);

        final ImageView moreChanceHint = (ImageView) hintDialog.findViewById(R.id.moreChanceHintDialog);
        moreChanceHint.setBackgroundResource(R.drawable.more_chance_hint);
        AnimationDrawable moreChanceFrameAnimation = (AnimationDrawable) moreChanceHint.getBackground();
        moreChanceFrameAnimation.start();

        final ImageView moreChanceHintPressed = (ImageView) hintDialog.findViewById(R.id.moreChanceHintDialogPressed);
        moreChanceHintPressed.setVisibility(View.INVISIBLE);

        ImageView moreChanceHintDialogDisabled = (ImageView) hintDialog.findViewById(R.id.moreChanceHintDialogDisabled);
        ImageView fingerHintDialogDisabled = (ImageView) hintDialog.findViewById(R.id.fingerHintDialogDisabled);

        final ImageView cancelHintDialog = (ImageView) hintDialog.findViewById(R.id.cancelHintDialog);

        // Button for finger hint option
        fingerHint.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        fingerHint.setVisibility(View.INVISIBLE);
                        fingerHintPressed.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:

                        if(getSoundStatus() == SOUND_ON_FLAG) {
                            buttonSound.start();
                        }

                        if(validateQtdHintCoins(FINGER_HINT_PRICE)) {
                            mUsableCoins = mUsableCoins + FINGER_HINT_PRICE;
                        }

                        ImageView hangy = (ImageView) rootView.findViewById(R.id.hangy);
                        hangy.setVisibility(View.VISIBLE);
                        ImageView hangyLookingForward = (ImageView) rootView.findViewById(R.id.hangyLookingForward);
                        hangyLookingForward.setVisibility(View.INVISIBLE);

                        hintDialog.cancel();

                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        // Button for more chance hint option
        moreChanceHint.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        moreChanceHint.setVisibility(View.INVISIBLE);
                        moreChanceHintPressed.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:

                        if(getSoundStatus() == SOUND_ON_FLAG) {
                            buttonSound.start();
                        }

                        ImageView hangy = (ImageView) rootView.findViewById(R.id.hangy);
                        hangy.setVisibility(View.VISIBLE);
                        ImageView hangyLookingForward = (ImageView) rootView.findViewById(R.id.hangyLookingForward);
                        hangyLookingForward.setVisibility(View.INVISIBLE);

                        if(validateQtdHintCoins(MORE_CHANCE_HINT_PRICE) && mQtdError > 0) {
                            mQtdError--;
                            drawGallowsImage(true);
                            setHintCoins(-MORE_CHANCE_HINT_PRICE);
                            TextView coinsQtdText = (TextView) rootView.findViewById(R.id.coinsQtdText);
                            coinsQtdText.setText(Integer.toString(getHintCoins()));
                        }

                        hintDialog.cancel();

                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        // Button for cancel the dialog
        cancelHintDialog.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        cancelHintDialog.setVisibility(View.INVISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:

                        if(getSoundStatus() == SOUND_ON_FLAG) {
                            buttonSound.start();
                        }

                        ImageView hangy = (ImageView) rootView.findViewById(R.id.hangy);
                        hangy.setVisibility(View.VISIBLE);
                        ImageView hangyLookingForward = (ImageView) rootView.findViewById(R.id.hangyLookingForward);
                        hangyLookingForward.setVisibility(View.INVISIBLE);

                        hintDialog.cancel();

                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        if(mQtdError == 0 || getHintCoins() < MORE_CHANCE_HINT_PRICE) {
            moreChanceHintPressed.setVisibility(View.VISIBLE);
            moreChanceHint.setVisibility(View.INVISIBLE);
            moreChanceHintDialogDisabled.setVisibility(View.VISIBLE);
        }else {
            moreChanceHintDialogDisabled.setVisibility(View.INVISIBLE);
        }

        if(getHintCoins() < FINGER_HINT_PRICE) {
            fingerHintPressed.setVisibility(View.INVISIBLE);
            fingerHint.setVisibility(View.INVISIBLE);
            fingerHintDialogDisabled.setVisibility(View.VISIBLE);
        }else {
            fingerHintDialogDisabled.setVisibility(View.INVISIBLE);
        }

        hintDialog.show();
    }

    private int getHintCoins() {
        SharedPreferences prefs = getContext().getSharedPreferences(COINS_PREFS, Context.MODE_PRIVATE);
        return prefs.getInt(COINS_KEY, FIRST_COINS_FILL);

    }

    private boolean validateQtdHintCoins(int numCoins) {
        SharedPreferences prefs = getContext().getSharedPreferences(COINS_PREFS, Context.MODE_PRIVATE);
        int restoredPref = prefs.getInt(COINS_KEY, FIRST_COINS_FILL);

        if (restoredPref >= numCoins) {
            return true;
        }else {
            return false;
        }
    }

    private boolean setHintCoins(int numCoins) {
        SharedPreferences.Editor editor = getContext().getSharedPreferences(COINS_PREFS, Context.MODE_PRIVATE).edit();
        SharedPreferences prefs = getContext().getSharedPreferences(COINS_PREFS, Context.MODE_PRIVATE);
        int restoredPref = prefs.getInt(COINS_KEY, FIRST_COINS_FILL);

        restoredPref = restoredPref + numCoins;
        editor.putInt(COINS_KEY, restoredPref);
        editor.commit();
        return true;

    }

    private boolean hasDailyCoins() {

        SharedPreferences.Editor editor = getContext().getSharedPreferences(DAILY_COINS_PREFS, Context.MODE_PRIVATE).edit();
        SharedPreferences prefs = getContext().getSharedPreferences(DAILY_COINS_PREFS, Context.MODE_PRIVATE);

        Calendar c = Calendar.getInstance();
        String day = Integer.toString(c.get(Calendar.DAY_OF_MONTH));
        String month = Integer.toString(c.get(Calendar.MONTH)+1);
        String year = Integer.toString(c.get(Calendar.YEAR));
        if(month.length() == 1) {
            month = "0" + month;
        }
        if(day.length() == 1) {
            day = "0" + day;
        }
        String sToday = year + month + day;
        int iToday = Integer.valueOf(sToday);

        int restoredPref = prefs.getInt(DAILY_COINS_KEY, 0);

        if(restoredPref == 0) {
            editor.putInt(DAILY_COINS_KEY, iToday);
            editor.commit();
            return false;
        }else if (restoredPref < iToday) {
            editor.putInt(DAILY_COINS_KEY, iToday);
            editor.commit();
            return true;
        }
        return false;

    }

    private void startMap(String location) {
        Uri mapUri = Uri.parse("geo:0,0?q=" + location);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(mapUri);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.no_app_to_show), Toast.LENGTH_SHORT).show();
        }
    }

    private void startGoogleBrowser(String url, String wordToSearch) {
        Uri webPage = Uri.parse(url + wordToSearch);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(webPage);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.no_app_to_show), Toast.LENGTH_SHORT).show();
        }
    }

    private void openHangyDialog() {
        final Dialog hangyDialog = new Dialog(getContext(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

        Drawable d = new ColorDrawable(Color.BLACK);
        d.setAlpha(180);
        hangyDialog.getWindow().setBackgroundDrawable(d);

        hangyDialog.setContentView(R.layout.hangy_dialog);
        hangyDialog.setCanceledOnTouchOutside(false);
        hangyDialog.setCancelable(false);

        //Main dialog layout
        LinearLayout hangyDialogLayout = (LinearLayout) hangyDialog.findViewById(R.id.hangyDialogLayout);
        LinearLayout.LayoutParams dialogParams = new LinearLayout.LayoutParams(
                (int)(widthPx * PERC_WIDTH_DIALOG),
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        hangyDialogLayout.setLayoutParams(dialogParams);

        TextView hangyText = (TextView) hangyDialog.findViewById(R.id.hangyText);
        hangyText.setText(getResources().getString(R.string.hangy_dialog_text));

        final ImageView hangyOkButton = (ImageView) hangyDialog.findViewById(R.id.hangyOkButton);

        // Button for close the dialog
        hangyOkButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        hangyOkButton.setVisibility(View.INVISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:

                        if(getSoundStatus() == SOUND_ON_FLAG) {
                            buttonSound.start();
                        }

                        hangyDialog.cancel();

                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        hangyDialog.show();
    }

    private boolean isShowHangyDialog() {
        SharedPreferences.Editor editor = getContext().getSharedPreferences(HANGY_DIALOG_PREFS, Context.MODE_PRIVATE).edit();
        SharedPreferences prefs = getContext().getSharedPreferences(HANGY_DIALOG_PREFS, Context.MODE_PRIVATE);
        boolean restoredPref = prefs.getBoolean(HANGY_DIALOG_KEY, true);

        if(restoredPref) {
            editor.putBoolean(HANGY_DIALOG_KEY, false);
            editor.commit();
        }

        return restoredPref;
    }

    @Override
    public void onStop() {
        super.onStop();
        keypadSound.stop();
        buttonSound.stop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        String sPath = Environment.getExternalStorageDirectory().toString() + File.separator + "HangmanTale" + File.separator;
        File fileDelete = new File(sPath, "HangmanTale_temp.jpg");
        if(fileDelete.exists()) {
            fileDelete.delete();
        }
        fileDelete = new File(sPath, "HangmanTale.jpg");
        if(fileDelete.exists()) {
            fileDelete.delete();
        }
    }
}
