package com.nick.hangman;


import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.nick.hangman.data.HangmanContract;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_activity, container, false);

        ImageView playButton = (ImageView)rootView.findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                startActivity(new Intent(getContext(), TaleActivity.class));

            }
        });



///////////////////////////////////////////////////////
        Button bannerButton = (Button)rootView.findViewById(R.id.bannerButton);
        bannerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

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

        Button baseButton = (Button)rootView.findViewById(R.id.baseButton);
        baseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

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

            }
        });

        Button testePictureButton = (Button)rootView.findViewById(R.id.testePictureButton);
        testePictureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                startActivity(new Intent(getContext(), PictureManagementActivity.class));
                //startActivity(new Intent(getContext(), PictureCreationActivity.class));

            }
        });

        Button testeBasePictureButton = (Button)rootView.findViewById(R.id.testeBasePictureButton);
        testeBasePictureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                File image = new File(Environment.getExternalStorageDirectory() + File.separator + "HangmanTale" + File.separator, "Hangman_1462499267870.jpg");
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap mBitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);

                Bitmap icon = mBitmap;
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
                try {
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temporary_file.jpg"));
                startActivity(Intent.createChooser(share, "Share Image"));

/*
                ContentValues values = new ContentValues();
                values.put(HangmanContract.ImageEntry.COLUMN_LAST_USED, 0);

                int qtd = getContext().getContentResolver().update(
                        HangmanContract.ImageEntry.CONTENT_URI,
                        values,
                        null,
                        null
                );

                System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                System.out.println(qtd);
*/
/*
                values = new ContentValues();
                values.put(HangmanContract.ImageEntry.COLUMN_LAST_USED, 1);

                qtd = getContext().getContentResolver().update(
                        HangmanContract.ImageEntry.CONTENT_URI,
                        values,
                        "_id = ?",
                        new String[]{"2"}
                );

                System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
                System.out.println(qtd);
*/

/*
                int qtd = getContext().getContentResolver().delete(
                        HangmanContract.ImageEntry.CONTENT_URI,
                        "_id in (?, ?, ?)",
                        new String[]{"5", "6", "7"}
                );

                System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
                System.out.println(qtd);
*/
            }
        });

        return rootView;
    }



}
