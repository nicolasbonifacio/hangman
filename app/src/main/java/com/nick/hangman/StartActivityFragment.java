package com.nick.hangman;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class StartActivityFragment extends Fragment {

    View rootView;

    private MediaPlayer buttonSound;

    private static final float IMAGE_PLAY_RATIO = 1.428f;

    public StartActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_start, container, false);

        buttonSound = MediaPlayer.create(getContext(), R.raw.button_sound);

        createStructure();

        return rootView;
    }

    private void createStructure() {

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

        final ImageView playButtonPressed = (ImageView)rootView.findViewById(R.id.playRobotButtonPressed);
        playButtonPressed.setVisibility(View.INVISIBLE);

        final ImageView playButton = (ImageView)rootView.findViewById(R.id.playRobotButton);
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
                        buttonSound.start();
                        startActivity(new Intent(getContext(), TaleActivity.class));

                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        final ImageView playImageButtonPressed = (ImageView)rootView.findViewById(R.id.playButtonPressed);
        playImageButtonPressed.setVisibility(View.INVISIBLE);

        final ImageView playImageButton = (ImageView)rootView.findViewById(R.id.playButton);
        playImageButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        playImageButton.setVisibility(View.INVISIBLE);
                        playImageButtonPressed.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:
                        playImageButton.setVisibility(View.VISIBLE);
                        playImageButtonPressed.setVisibility(View.INVISIBLE);
                        buttonSound.start();
                        startActivity(new Intent(getContext(), PictureManagementActivity.class));
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

    }

}
