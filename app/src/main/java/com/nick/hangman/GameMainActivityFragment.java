package com.nick.hangman;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nick.hangman.Objects.Word;
import com.nick.hangman.data.HangmanContract;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class GameMainActivityFragment extends Fragment {

    static final String DETAIL_URI = "URI";

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
    public static final float PERC_STARS_DIALOG_WIDTH = 0.6f; //60%
    private static final int ALL_CATEGORIES_ID = 0;

    private static final String WORD_SORT_ORDER = "RANDOM() LIMIT 1";

    //////////////////// remove this and change for one based on the level ///////////////
    private static final int TOTAL_QTD_ERROR = 6;
    //////////////////////////////////////////////////////////////////////////////////////

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

    private static final String sWordLanguageCategorySelection =
            HangmanContract.WordEntry.TABLE_NAME +
                    "." + HangmanContract.WordEntry.COLUMN_LOC_KEY_LANGUAGE + " = ? AND " +
                    HangmanContract.WordEntry.TABLE_NAME +
                    "." + HangmanContract.WordEntry.COLUMN_LOC_KEY_CATEGORY + " = ? AND " +
                    HangmanContract.WordEntry.TABLE_NAME +
                    "." + HangmanContract.WordEntry.COLUMN_WORD_USED + " = ?";

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

    public GameMainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_game_main, container, false);

        keypadSound = MediaPlayer.create(getContext(), R.raw.keypad_sound);
        buttonSound = MediaPlayer.create(getContext(), R.raw.button_sound);

        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra("paramsSel")) {
            paramsSel = (ParametersSelected) intent.getSerializableExtra("paramsSel");

            if(paramsSel.getImageImageName() != null && !paramsSel.getImageImageName().equals("")) {
                mIsImageSelected = true;
            }else {
                mIsImageSelected = false;
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
                            keypadSound.start();
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
                            keypadSound.start();
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
                            keypadSound.start();
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

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if ( getActivity() instanceof GameMainActivity ){
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.menu_game_main, menu);
            finishCreatingMenu(menu);
        }
    }

    private void finishCreatingMenu(Menu menu) {
        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);
        menuItem.setIntent(createShareForecastIntent());
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "bla bla bla");
        return shareIntent;
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

            int id = 1001;
            for(int j = 0; j < words.length; j++) {
                wordLineLayout = new LinearLayout(getContext());
                wordLineLayout.setOrientation(LinearLayout.HORIZONTAL);
                wordLineLayout.setLayoutParams(params);
                wordLineLayout.setPadding(0, (int)mDensity * WORD_PADDING_TIMES, 0, (int)mDensity * WORD_PADDING_TIMES);

                for (int i = 1001; i <= words[j].length() + 1000; i++) {

                    ImageView dash = new ImageView(getContext());
                    dash.setId(id);
                    dash.setImageDrawable(getResources().getDrawable(mUtils.getCharacterImage(DASH_FLAG)));
                    dash.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    dash.setAdjustViewBounds(true);
                    dash.setPadding(0, 0, 0, 0);
                    dash.setMaxHeight(mCharacterHeight);
                    dash.setMinimumHeight(mCharacterHeight);
                    wordLineLayout.addView(dash, paramsCharacter);

                    if (id > lastDash) {
                        lastDash = id;
                    }
                    id++;
                }
                wordLayout.addView(wordLineLayout, params);
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
                    // Verify if the game was won
                    if (mQtdHits >= mTotalCharacters) {
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

                ImageView imageHead = new ImageView(getContext());
                imageHead.setImageBitmap(imageLastUsedBitmap);

                imageHead.setX((int) ((IMAGE_FACE_CENTER_IN_DP * mDensity) - (imageWidth / 2)));
                imageHead.setY((int) (IMAGE_FACE_Y_IN_DP * mDensity)); //sempre tem que ser 23dp

                gallowsLayout.addView(imageHead, gallowsParams);

            }else {
                mGallowsView.setImageDrawable(getResources().getDrawable(mUtils.getGallowsImage(mQtdError)));
            }

            // Verify if the game is over
            if(mQtdError >= TOTAL_QTD_ERROR) {
                endGamePopup(false);
            }
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
                        buttonSound.start();
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
        String wordScreen = mWord.getWord().replace(" ", "\n");
        wordSelectedDialogView.setText(wordScreen);
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
                        buttonSound.start();
                        dialog.cancel();
                        getActivity().finish();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        disableKeypad();

        dialog.setCanceledOnTouchOutside(false);

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
            MediaStore.Images.Media.insertImage( getContext().getContentResolver(), b,
                    "Screen", "screen");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        File image = new File(Environment.getExternalStorageDirectory() + File.separator + "HangmanTale" + File.separator, "HangmanTale_temp.jpg");
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap mBitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);

        Bitmap icon = mBitmap;
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
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

    @Override
    public void onStop() {
        super.onStop();
        keypadSound.stop();
        buttonSound.stop();
    }

}
