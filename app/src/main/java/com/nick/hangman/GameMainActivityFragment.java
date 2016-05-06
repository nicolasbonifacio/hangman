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
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nick.hangman.Objects.Word;
import com.nick.hangman.data.HangmanContract;

import java.io.File;
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
    private int mCharacterHeight;
    private boolean mIsImageSelected;

    private Utils mUtils;

    public GameMainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_game_main, container, false);

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
            int widthPx = getContext().getResources().getDisplayMetrics().widthPixels;
            int heightPx = getContext().getResources().getDisplayMetrics().heightPixels;
            mDensity = getContext().getResources().getDisplayMetrics().density;

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

//                float scaledDensity = getContext().getResources().getDisplayMetrics().scaledDensity;
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

    private boolean loadWordNotUsed() {

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
            mUri = HangmanContract.WordEntry.buildWordWithLanguageCategoryLevel(paramsSel.getLanguageId(),
                    paramsSel.getCategoryId(),
                    WORD_NOT_USED_FLAG,
                    paramsSel.getLevelId());

            mCursor = getContext().getContentResolver().query(mUri, WORD_COLUMNS, null, null, WORD_SORT_ORDER);
        }

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

mWord.setWord("HOUSE OF CARDS");
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
                        imageHeight // sempre tem que ser 61dp
                );

                mGallowsView.setImageDrawable(getResources().getDrawable(mUtils.getGallowsImageWithImage(mQtdError)));

                //Uri imageUri = Uri.parse(paramsSel.getImageImageUri());
                //Bitmap imageLastUsedBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);

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

        // Clear keypad
        Button btn;

        for (int i = 2001; i <= FIRST_KEYPAD_LINE_CHARACTERS.length + 2000; i++) {
            btn = (Button) rootView.findViewById(i);
            btn.setEnabled(true);
            btn.setBackgroundColor(Color.parseColor("#d6d7d7"));
        }

        for (int i = 3001; i <= SECOND_KEYPAD_LINE_CHARACTERS.length + 3000; i++) {
            btn = (Button) rootView.findViewById(i);
            btn.setEnabled(true);
            btn.setBackgroundColor(Color.parseColor("#d6d7d7"));
        }

        for (int i = 4001; i <= THIRD_KEYPAD_LINE_CHARACTERS.length + 4000; i++) {
            btn = (Button) rootView.findViewById(i);
            btn.setEnabled(true);
            btn.setBackgroundColor(Color.parseColor("#d6d7d7"));
        }

        //Clear word
        TextView dash;
        LinearLayout wordLayout = (LinearLayout) rootView.findViewById(R.id.wordLayout);
        wordLayout.removeAllViews();

    }

    private void endGamePopup(boolean gameWon) {

        final Dialog dialog = new Dialog(getContext());

        dialog.setContentView(R.layout.custom_dialog);

        TextView endGameView = (TextView) dialog.findViewById(R.id.endGameTextView);
        TextView wordSelectedView = (TextView) dialog.findViewById(R.id.wordSelected);
        TextView numStars = (TextView) dialog.findViewById(R.id.numStars);

        wordSelectedView.setText(mWord.getWord());

        int qtdStars = loadPointsAndStarsFromScoreModel(mQtdError);
        numStars.setText(Integer.toString(qtdStars));

        final Button btnNewGame = ((Button) dialog.findViewById(R.id.newGame));
        btnNewGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.cancel();
                getActivity().finish();
            }
        });

        if(gameWon) {
            endGameView.setText(getResources().getString(R.string.win_game));
        }else {
            endGameView.setText(getResources().getString(R.string.lose_game));
        }

        dialog.show();
    }

    private int loadPointsAndStarsFromScoreModel(int numErrors) {
        mUri = HangmanContract.ScoreModelEntry.buildScoreModelWithCategoryNumErrors(paramsSel.getCategoryId(), numErrors);

        mCursor = getContext().getContentResolver().query(mUri, SCORE_MODEL_COLUMNS, null, null, null);

        int qtdStars = 0;
        int qtdPoints = 0;

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

            updatePlayerScore(qtdPoints);

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

}
