package com.nick.hangman;


import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nick.hangman.data.HangmanContract;


public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_activity, container, false);

        Button playButton = (Button)rootView.findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                startActivity(new Intent(getContext(), TaleActivity.class));

            }
        });



///////////////////////////////////////////////////////
        Button baseButton = (Button)rootView.findViewById(R.id.baseButton);
        baseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
/*
                ContentValues values = new ContentValues();
                values.put(HangmanContract.WordEntry.COLUMN_WORD_USED, 0);

                int qtd = getContext().getContentResolver().update(
                        HangmanContract.WordEntry.CONTENT_URI,
                        values,
                        null,
                        null
                );

                System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                System.out.println(qtd);
*/

                startActivity(new Intent(getContext(), BannerActivity.class));

            }
        });
//////////////////////////////////////////////////////////

        Button interestitialButton = (Button)rootView.findViewById(R.id.interstitialButton);
        interestitialButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                startActivity(new Intent(getContext(), InterstitialActivity.class));

            }
        });

        return rootView;
    }



}
