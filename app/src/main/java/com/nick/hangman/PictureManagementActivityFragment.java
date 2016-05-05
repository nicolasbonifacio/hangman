package com.nick.hangman;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * A placeholder fragment containing a simple view.
 */
public class PictureManagementActivityFragment extends Fragment {

    private View rootView;

    public PictureManagementActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_picture_management, container, false);

        return rootView;
    }


}
