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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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

                ContentValues values = new ContentValues();
                values.put(HangmanContract.TaleScoreCategoryEntry.COLUMN_CATEGORY_ENABLED, 1);

                int qtd = getContext().getContentResolver().update(
                        HangmanContract.TaleScoreCategoryEntry.CONTENT_URI,
                        values,
                        "_id = ?",
                        new String[]{"1"}
                );

                System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                System.out.println(qtd);

            }
        });
//////////////////////////////////////////////////////////

        return rootView;
    }

}
