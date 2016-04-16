package com.nick.hangman;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nick.hangman.data.HangmanContract;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class GameMainActivityFragment extends Fragment {

    static final String DETAIL_URI = "URI";

    private static final int WORD_NOT_USED_FLAG = 0;
    private static final int WORD_USED_FLAG = 1;
    private static final String WORD_SORT_ORDER = "RANDOM() LIMIT 1";

    private static final String[] WORD_COLUMNS = {
            HangmanContract.WordEntry.TABLE_NAME + "." + HangmanContract.WordEntry._ID,
            HangmanContract.WordEntry.COLUMN_WORD
    };

    private static final String[] FIRST_KEYPAD_LINE_CHARACTERS = {"A", "B", "C", "D", "E", "F",
            "G", "H", "I"};

    private static final String[] SECOND_KEYPAD_LINE_CHARACTERS = {"J", "K", "L", "M", "N", "O",
            "P", "Q", "R"};

    private static final String[] THIRD_KEYPAD_LINE_CHARACTERS = {"S", "T", "U", "V", "W", "X",
            "Y", "Z"};


    //WORD columns
    public static final int COL_WORD_ID = 0;
    public static final int COL_WORD_WORD = 1;

    private Uri mUri;
    private Cursor mCursor;
    private View rootView;

    private ParametersSelected paramsSel;
    private Word mWord;

    private ArrayList<String> mListWord;
    private int qtdError;

    TextView mTestImageView;

    public GameMainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_game_main, container, false);

        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra("paramsSel")) {
            paramsSel = (ParametersSelected) intent.getSerializableExtra("paramsSel");

            TextView player1NameView = (TextView) rootView.findViewById(R.id.player1GameNameTextView);
            TextView player1ScoreView = (TextView) rootView.findViewById(R.id.player1GameScoreValueTextView);
            TextView categoryDescrView = (TextView) rootView.findViewById(R.id.categoryGameDescrTextView);
            TextView levelDescrView = (TextView) rootView.findViewById(R.id.levelGameDescrTextView);

            player1NameView.setText(paramsSel.getPlayer1DescrName());
            player1ScoreView.setText(Integer.toString(paramsSel.getPlayer1Score()));
            categoryDescrView.setText(paramsSel.getCategoryDescrCategory());
            levelDescrView.setText(paramsSel.getLevelDescrLevel());

            //Fetch DB for the word and splits it into an ArrayList, putting each character in one position
            loadWordNotUsed();
            qtdError = 0;


            /////////////////// to be removed //////////////////////
            mTestImageView = (TextView) rootView.findViewById(R.id.testImageTextView);
            ////////////////////////////////////////////////////////


            mListWord = new ArrayList<String>();
            for(int i=0; i<mWord.getWord().length(); i++) {
                mListWord.add(i, mWord.getWord().substring(i, i + 1));
            }

            //Create word structure
            LinearLayout wordLayout = (LinearLayout) rootView.findViewById(R.id.wordLayout);
            for (int i = 1001; i <= mWord.getWord().length()+1000; i++) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                TextView dash = new TextView(getContext());
                dash.setId(i);
//                final int id_ = dash.getId();
                dash.setText("_ ");

                wordLayout.addView(dash, params);
            }

            //Create game keypad line 1
            LinearLayout firstKeyPadLineLayout = (LinearLayout) rootView.findViewById(R.id.firstKeypadLineLayout);
            for (int i = 2001; i <= FIRST_KEYPAD_LINE_CHARACTERS.length+2000; i++) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        100,
                        120);
                Button btn = new Button(getContext());
                btn.setId(i);
                final int id_ = btn.getId();
                btn.setText(FIRST_KEYPAD_LINE_CHARACTERS[i-2001]);

                firstKeyPadLineLayout.addView(btn, params);

                //First key pad line button listener
                final Button btn1 = ((Button) rootView.findViewById(id_));
                btn1.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {

                        handleGuess(FIRST_KEYPAD_LINE_CHARACTERS[btn1.getId()-2001]);
                        btn1.setEnabled(false);
                        btn1.setBackgroundColor(Color.parseColor("#645252"));

                    }
                });
            }

            //Create game keypad line 2
            LinearLayout secondKeyPadLineLayout = (LinearLayout) rootView.findViewById(R.id.secondKeypadLineLayout);
            for (int i = 3001; i <= SECOND_KEYPAD_LINE_CHARACTERS.length+3000; i++) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        100,
                        120);
                Button btn = new Button(getContext());
                btn.setId(i);
                final int id_ = btn.getId();
                btn.setText(SECOND_KEYPAD_LINE_CHARACTERS[i-3001]);

                secondKeyPadLineLayout.addView(btn, params);

                //Second key pad line button listener
                final Button btn2 = ((Button) rootView.findViewById(id_));
                btn2.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {

                        handleGuess(SECOND_KEYPAD_LINE_CHARACTERS[btn2.getId()-3001]);
                        btn2.setEnabled(false);
                        btn2.setBackgroundColor(Color.parseColor("#645252"));

                    }
                });
            }

            //Create game keypad line 3
            LinearLayout thirdKeyPadLineLayout = (LinearLayout) rootView.findViewById(R.id.thirdKeypadLineLayout);
            for (int i = 4001; i <= THIRD_KEYPAD_LINE_CHARACTERS.length+4000; i++) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        100,
                        120);
                Button btn = new Button(getContext());
                btn.setId(i);
                final int id_ = btn.getId();
                btn.setText(THIRD_KEYPAD_LINE_CHARACTERS[i-4001]);

                thirdKeyPadLineLayout.addView(btn, params);

                //Third key pad line button listener
                final Button btn3 = ((Button) rootView.findViewById(id_));
                btn3.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {

                        handleGuess(THIRD_KEYPAD_LINE_CHARACTERS[btn3.getId()-4001]);
                        btn3.setEnabled(false);
                        btn3.setBackgroundColor(Color.parseColor("#645252"));

                    }
                });
            }

        }else {
            Toast.makeText(getContext(),(String)getResources().getString(R.string.error_loading_word),
                    Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }

    private void loadWordNotUsed() {
        mUri = HangmanContract.WordEntry.buildWordWithLanguageCategoryLevel(paramsSel.getLanguageId(),
                paramsSel.getCategoryId(),
                paramsSel.getLevelId(),
                WORD_NOT_USED_FLAG);

        mCursor = getContext().getContentResolver().query(mUri, WORD_COLUMNS, null, null, WORD_SORT_ORDER);

        if(mCursor != null && mCursor.moveToFirst()) {

            mWord = new Word();
            mWord.setId(mCursor.getInt(COL_WORD_ID));
            mWord.setWord(mCursor.getString(COL_WORD_WORD));
            mWord.setLanguageId(paramsSel.getLanguageId());
            mWord.setCategoryId(paramsSel.getCategoryId());
            mWord.setLevelId(paramsSel.getLevelId());
            mWord.setWordUsed(WORD_USED_FLAG);

            mCursor.close();
        }
    }

    private void handleGuess(String character) {
        boolean hasError = true;

        for(int i=0; i<mListWord.size(); i++) {
            if(mListWord.get(i).equals(character)) {
                hasError = false;
                drawCharacter(i+1, character);
            }
        }

        if(hasError) {
            qtdError++;
            mTestImageView.setText(Integer.toString(qtdError));
        }

    }

    private void drawCharacter(int position, String sCharacter) {

        TextView dashView;

        for (int i = 1001; i <= mWord.getWord().length()+1000; i++) {

            if(i == position+1000) {
                dashView = (TextView) rootView.findViewById(i);
                dashView.setText(sCharacter + " ");
            }

        }

    }

}
