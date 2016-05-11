package com.nick.hangman;


import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nick.hangman.data.HangmanContract;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivityFragment extends Fragment {

    View rootView;

    private static final float IMAGE_PLAY_RATIO = 1.428f;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main_activity, container, false);

        int widthPx = getContext().getResources().getDisplayMetrics().widthPixels;
        float scaledDensity = getContext().getResources().getDisplayMetrics().scaledDensity;
        int imagePlayHorizontalPadding = getResources().getDimensionPixelSize(R.dimen.screen_play_padding_horizontal);

        int imagePlayWidth = (int)((widthPx - (imagePlayHorizontalPadding * 2)) / 2);
        int imagePlayHeight = (int)(imagePlayWidth * IMAGE_PLAY_RATIO);

        LinearLayout textPlayLayout = (LinearLayout) rootView.findViewById(R.id.textPlayLayout);
        LinearLayout.LayoutParams textPlayLayoutParams = new LinearLayout.LayoutParams(
                imagePlayWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        TextView textPlayRobot = new TextView(getContext());
        textPlayRobot.setText(getResources().getString(R.string.textPlayRobot));
        textPlayRobot.setTextSize(getResources().getDimension(R.dimen.text_play_robot_size) / scaledDensity);
        textPlayRobot.setTextColor(getResources().getColor(R.color.textPlayRobot));
        textPlayRobot.setTypeface(Typeface.DEFAULT_BOLD);
        textPlayRobot.setPadding(
                getResources().getDimensionPixelSize(R.dimen.text_play_padding_horizontal),
                0,
                0,
                0
        );
        textPlayRobot.setGravity(Gravity.CENTER);

        TextView textPlayImage = new TextView(getContext());
        textPlayImage.setText(getResources().getString(R.string.textPlayImage));
        textPlayImage.setTextSize(getResources().getDimension(R.dimen.text_play_image_size) / scaledDensity);
        textPlayImage.setTextColor(getResources().getColor(R.color.textPlayImage));
        textPlayImage.setTypeface(Typeface.DEFAULT_BOLD);
        textPlayImage.setPadding(
                0,
                0,
                getResources().getDimensionPixelSize(R.dimen.text_play_padding_horizontal),
                0
        );
        textPlayImage.setGravity(Gravity.CENTER);

        textPlayLayout.addView(textPlayRobot, textPlayLayoutParams);
        textPlayLayout.addView(textPlayImage, textPlayLayoutParams);

        LinearLayout imagePlayLayout = (LinearLayout) rootView.findViewById(R.id.imagePlayLayout);
        LinearLayout.LayoutParams imagePlayLayoutParams = new LinearLayout.LayoutParams(
                imagePlayWidth,
                imagePlayHeight
        );

        ImageView imgPlayImage = new ImageView(getContext());
        imgPlayImage.setImageDrawable(getResources().getDrawable(R.drawable.img_play_robot));
        imgPlayImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imgPlayImage.setAdjustViewBounds(true);
        imgPlayImage.setPadding(
                0,
                0,
                getResources().getDimensionPixelSize(R.dimen.image_play_padding_horizontal),
                0
        );

        ImageView imgPlayRobot = new ImageView(getContext());
        imgPlayRobot.setImageDrawable(getResources().getDrawable(R.drawable.img_play_image));
        imgPlayRobot.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imgPlayRobot.setAdjustViewBounds(true);
        imgPlayRobot.setPadding(
                getResources().getDimensionPixelSize(R.dimen.image_play_padding_horizontal),
                0,
                0,
                0
        );

        imagePlayLayout.addView(imgPlayImage, imagePlayLayoutParams);
        imagePlayLayout.addView(imgPlayRobot, imagePlayLayoutParams);

        final ImageView playButtonPressed = (ImageView)rootView.findViewById(R.id.playButtonPressed);
        playButtonPressed.setVisibility(View.INVISIBLE);

        final ImageView playButton = (ImageView)rootView.findViewById(R.id.playButton);
        playButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        playButton.setVisibility(View.INVISIBLE);
                        playButtonPressed.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:
                        playButton.setVisibility(View.VISIBLE);
                        playButtonPressed.setVisibility(View.INVISIBLE);
                        startActivity(new Intent(getContext(), TaleActivity.class));
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        final ImageView playRobotButtonPressed = (ImageView)rootView.findViewById(R.id.playRobotButtonPressed);
        playRobotButtonPressed.setVisibility(View.INVISIBLE);

        final ImageView playRobotButton = (ImageView)rootView.findViewById(R.id.playRobotButton);
        playRobotButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        playRobotButton.setVisibility(View.INVISIBLE);
                        playRobotButtonPressed.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:
                        playRobotButton.setVisibility(View.VISIBLE);
                        playRobotButtonPressed.setVisibility(View.INVISIBLE);
                        startActivity(new Intent(getContext(), PictureManagementActivity.class));
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

/*
        ImageView img = (ImageView) rootView.findViewById(R.id.waitIcon);
        img.setBackgroundResource(R.drawable.wait_icon);
        AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();
        frameAnimation.start();
*/


/////////////////////////////////////////////
/*
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
*/
//////////////////////////////////////////////


/*
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



                int qtd = getContext().getContentResolver().delete(
                        HangmanContract.ImageEntry.CONTENT_URI,
                        "_id in (?, ?, ?)",
                        new String[]{"5", "6", "7"}
                );

                System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
                System.out.println(qtd);

            }
        });
*/
        return rootView;
    }



}
