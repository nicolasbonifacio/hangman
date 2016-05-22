package com.nick.hangman;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;

/**
 * A placeholder fragment containing a simple view.
 */
public class SelectExistingImageActivityFragment extends Fragment {

    public SelectExistingImageActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_existing_image, container, false);

    }
}
