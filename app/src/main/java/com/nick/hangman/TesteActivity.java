package com.nick.hangman;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.nick.hangman.data.HangmanContract;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class TesteActivity extends AppCompatActivity {

    private static final String[] WORD_COLUMNS = {
            HangmanContract.WordEntry.TABLE_NAME + "." + HangmanContract.WordEntry._ID,
            HangmanContract.WordEntry.COLUMN_WORD
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teste);

        String word = "";
        String[] letras;

        Cursor mCursor = getContentResolver().query(HangmanContract.WordEntry.CONTENT_URI,
                WORD_COLUMNS,
                null, //selection
                null, //selection args
                null);

        int total = 0;

        if(mCursor != null && mCursor.moveToFirst()) {

            System.out.println("***************** Start *********************");

            do {

                word = "";
                word = mCursor.getString(1);
                letras = new String[word.length()];

                for(int j = 0; j < word.length(); j++) {
                    letras[j] = word.substring(j, j+1);
                }


                for(int i = 0; i < word.length(); i++) {

                    switch(letras[i]) {
                        case "A": break;
                        case "B": break;
                        case "C": break;
                        case "D": break;
                        case "E": break;
                        case "F": break;
                        case "G": break;
                        case "H": break;
                        case "I": break;
                        case "J": break;
                        case "K": break;
                        case "L": break;
                        case "M": break;
                        case "N": break;
                        case "O": break;
                        case "P": break;
                        case "Q": break;
                        case "R": break;
                        case "S": break;
                        case "T": break;
                        case "U": break;
                        case "V": break;
                        case "W": break;
                        case "X": break;
                        case "Y": break;
                        case "Z": break;
                        case " ": break;
                        default:
                            System.out.println(mCursor.getString(0) + " - " + mCursor.getString(1));
                            break;
                    }

                }
                total++;
            }while(mCursor.moveToNext());


            System.out.println("***************** End *********************");
            System.out.println(total);

        }






    }
}
