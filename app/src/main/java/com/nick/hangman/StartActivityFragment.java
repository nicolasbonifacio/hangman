package com.nick.hangman;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class StartActivityFragment extends Fragment {

    View rootView;

    private MediaPlayer buttonSound;

    private static final float IMAGE_PLAY_RATIO = 1.428f;

    private static final int IMAGE_PLAY_ROBOT_ID = 994;
    private static final int IMAGE_PLAY_IMAGE_ID = 993;

    public static final String SOUND_ON_OFF_PREFS = "soundOnOffPrefs";
    private static final int SOUND_ON_OFF_NOT_DEFINED_FLAG = -1;
    private static final int SOUND_ON_FLAG = 1;
    private static final int SOUND_OFF_FLAG = 0;

    public static final String NOTIFICATION_ENABLE_PREFS = "notificationEnablePrefs";
    public static final String NOTIFICATION_ENABLE_KEY = "notificationEnableKey";

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
        //textPlayRobot.setTextColor(getResources().getColor(R.color.textPlayRobot));
        //textPlayRobot.setTypeface(Typeface.DEFAULT_BOLD);
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
        //textPlayImage.setTextColor(getResources().getColor(R.color.textPlayImage));
        //textPlayImage.setTypeface(Typeface.DEFAULT_BOLD);
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
        imgPlayImage.setImageDrawable(getResources().getDrawable(R.drawable.img_play_image));
        imgPlayImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imgPlayImage.setAdjustViewBounds(true);
        imgPlayImage.setPadding(
                0,
                0,
                getResources().getDimensionPixelSize(R.dimen.image_play_padding_horizontal),
                0
        );
        imgPlayImage.setId(IMAGE_PLAY_IMAGE_ID);

        ImageView imgPlayImageNoShadow = new ImageView(getContext());
        imgPlayImageNoShadow.setImageDrawable(getResources().getDrawable(R.drawable.img_play_image_no_shadow));
        imgPlayImageNoShadow.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imgPlayImageNoShadow.setAdjustViewBounds(true);
        imgPlayImageNoShadow.setPadding(
                getResources().getDimensionPixelSize(R.dimen.image_play_padding_horizontal),
                0,
                0,
                0
        );



        ImageView imgPlayRobot = new ImageView(getContext());
        imgPlayRobot.setImageDrawable(getResources().getDrawable(R.drawable.img_play_robot));
        imgPlayRobot.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imgPlayRobot.setAdjustViewBounds(true);
        imgPlayRobot.setPadding(
                getResources().getDimensionPixelSize(R.dimen.image_play_padding_horizontal),
                0,
                0,
                0
        );
        imgPlayRobot.setId(IMAGE_PLAY_ROBOT_ID);

        ImageView imgPlayRobotNoShadow = new ImageView(getContext());
        imgPlayRobotNoShadow.setImageDrawable(getResources().getDrawable(R.drawable.img_play_robot_no_shadow));
        imgPlayRobotNoShadow.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imgPlayRobotNoShadow.setAdjustViewBounds(true);
        imgPlayRobotNoShadow.setPadding(
                getResources().getDimensionPixelSize(R.dimen.image_play_padding_horizontal),
                0,
                0,
                0
        );

        FrameLayout imagePlayFrameLayout = new FrameLayout(getContext());
        FrameLayout.LayoutParams imagePlayRobotLayoutParamsNoShadow = new FrameLayout.LayoutParams(
                imagePlayWidth - getResources().getDimensionPixelSize(R.dimen.images_play_no_shadow_reduction),
                imagePlayHeight - getResources().getDimensionPixelSize(R.dimen.images_play_no_shadow_reduction)
        );
        imagePlayRobotLayoutParamsNoShadow.gravity = Gravity.CENTER;

        FrameLayout.LayoutParams imagePlayImageLayoutParamsNoShadow = new FrameLayout.LayoutParams(
                imagePlayWidth - getResources().getDimensionPixelSize(R.dimen.images_play_no_shadow_reduction),
                imagePlayHeight - getResources().getDimensionPixelSize(R.dimen.images_play_no_shadow_reduction)
        );
        imagePlayImageLayoutParamsNoShadow.gravity = Gravity.CENTER_VERTICAL;

        imagePlayFrameLayout.addView(imgPlayRobotNoShadow, imagePlayRobotLayoutParamsNoShadow);
        imagePlayFrameLayout.addView(imgPlayRobot);
        imagePlayLayout.addView(imagePlayFrameLayout, imagePlayLayoutParams);

        imagePlayFrameLayout = new FrameLayout(getContext());
        imagePlayFrameLayout.addView(imgPlayImageNoShadow, imagePlayImageLayoutParamsNoShadow);
        imagePlayFrameLayout.addView(imgPlayImage);
        imagePlayLayout.addView(imagePlayFrameLayout, imagePlayLayoutParams);

        final ImageView playButtonRobotListener = (ImageView)rootView.findViewById(IMAGE_PLAY_ROBOT_ID);
        playButtonRobotListener.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        playButtonRobotListener.setVisibility(View.INVISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:
                        playButtonRobotListener.setVisibility(View.VISIBLE);
                        if(getSoundStatus() == SOUND_ON_FLAG) {
                            buttonSound.start();
                        }
                        startActivity(new Intent(getContext(), TaleActivity.class));
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        final ImageView playButtonImageListener = (ImageView)rootView.findViewById(IMAGE_PLAY_IMAGE_ID);
        playButtonImageListener.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        playButtonImageListener.setVisibility(View.INVISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:
                        playButtonImageListener.setVisibility(View.VISIBLE);
                        if(getSoundStatus() == SOUND_ON_FLAG) {
                            buttonSound.start();
                        }
                        startActivity(new Intent(getContext(), PictureManagementActivity.class));
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        //Sound icon
        int restoredPref = getSoundStatus();

        final ImageView noSoundIcon = (ImageView) rootView.findViewById(R.id.noSoundIcon);
        final ImageView noSoundIconPressed = (ImageView) rootView.findViewById(R.id.noSoundIconPressed);
        final ImageView soundIcon = (ImageView) rootView.findViewById(R.id.soundIcon);
        final ImageView soundIconPressed = (ImageView) rootView.findViewById(R.id.soundIconPressed);

        if(restoredPref == SOUND_OFF_FLAG) {
            //Sound off
            noSoundIcon.setVisibility(View.VISIBLE);
            noSoundIconPressed.setVisibility(View.INVISIBLE);
            soundIcon.setVisibility(View.INVISIBLE);
            soundIconPressed.setVisibility(View.INVISIBLE);
        }else {
            //Sound on
            noSoundIcon.setVisibility(View.INVISIBLE);
            noSoundIconPressed.setVisibility(View.INVISIBLE);
            soundIconPressed.setVisibility(View.INVISIBLE);
            soundIcon.setVisibility(View.VISIBLE);
        }

        //No sound is activated and user wants to activate sound
        noSoundIcon.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        noSoundIcon.setVisibility(View.INVISIBLE);
                        noSoundIconPressed.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:
                        soundIcon.setVisibility(View.VISIBLE);
                        noSoundIconPressed.setVisibility(View.INVISIBLE);
                        if(getSoundStatus() == SOUND_ON_FLAG) {
                            buttonSound.start();
                        }
                        setSoundStatus(SOUND_ON_FLAG);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        //The sound is activated and user wants to deactivate it
        soundIcon.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        soundIcon.setVisibility(View.INVISIBLE);
                        soundIconPressed.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:
                        noSoundIcon.setVisibility(View.VISIBLE);
                        soundIconPressed.setVisibility(View.INVISIBLE);
                        if(getSoundStatus() == SOUND_ON_FLAG) {
                            buttonSound.start();
                        }
                        setSoundStatus(SOUND_OFF_FLAG);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        //Notification icon
        boolean restoredNotificationPref = getNotificationStatus();

        final ImageView noNotificationIcon = (ImageView) rootView.findViewById(R.id.noNotificationIcon);
        final ImageView noNotificationIconPressed = (ImageView) rootView.findViewById(R.id.noNotificationIconPressed);
        final ImageView notificationIcon = (ImageView) rootView.findViewById(R.id.notificationIcon);
        final ImageView notificationIconPressed = (ImageView) rootView.findViewById(R.id.notificationIconPressed);

        if(!restoredNotificationPref) {
            //Notification off
            noNotificationIcon.setVisibility(View.VISIBLE);
            noNotificationIconPressed.setVisibility(View.INVISIBLE);
            notificationIcon.setVisibility(View.INVISIBLE);
            notificationIconPressed.setVisibility(View.INVISIBLE);
        }else {
            //Notification on
            noNotificationIcon.setVisibility(View.INVISIBLE);
            noNotificationIconPressed.setVisibility(View.INVISIBLE);
            notificationIconPressed.setVisibility(View.INVISIBLE);
            notificationIcon.setVisibility(View.VISIBLE);
        }

        //No notification is activated and user wants to activate notifications
        noNotificationIcon.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        noNotificationIcon.setVisibility(View.INVISIBLE);
                        noNotificationIconPressed.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:
                        notificationIcon.setVisibility(View.VISIBLE);
                        noNotificationIconPressed.setVisibility(View.INVISIBLE);
                        if(getSoundStatus() == SOUND_ON_FLAG) {
                            buttonSound.start();
                        }
                        setNotificationStatus(true);
                        Toast.makeText(getContext(), "Notifications enabled", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        //The notification is activated and user wants to deactivate it
        notificationIcon.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        notificationIcon.setVisibility(View.INVISIBLE);
                        notificationIconPressed.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:
                        noNotificationIcon.setVisibility(View.VISIBLE);
                        notificationIconPressed.setVisibility(View.INVISIBLE);
                        if(getSoundStatus() == SOUND_ON_FLAG) {
                            buttonSound.start();
                        }
                        setNotificationStatus(false);
                        Toast.makeText(getContext(), "Notifications disabled", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });


    }

    private int getSoundStatus() {
        SharedPreferences.Editor editor = getContext().getSharedPreferences(SOUND_ON_OFF_PREFS, Context.MODE_PRIVATE).edit();
        SharedPreferences prefs = getContext().getSharedPreferences(SOUND_ON_OFF_PREFS, Context.MODE_PRIVATE);
        int restoredPref = prefs.getInt("soundOnOff", SOUND_ON_OFF_NOT_DEFINED_FLAG);
        if (restoredPref == SOUND_ON_OFF_NOT_DEFINED_FLAG) {
            editor.putInt("soundOnOff", SOUND_ON_FLAG);
            editor.commit();
            restoredPref = SOUND_ON_FLAG;
        }
        return restoredPref;
    }

    private void setSoundStatus(int soundStatus) {
        SharedPreferences.Editor editor = getContext().getSharedPreferences(SOUND_ON_OFF_PREFS, Context.MODE_PRIVATE).edit();
        editor.putInt("soundOnOff", soundStatus);
        editor.commit();
    }

    private boolean getNotificationStatus() {
        SharedPreferences.Editor editor = getContext().getSharedPreferences(NOTIFICATION_ENABLE_PREFS, Context.MODE_PRIVATE).edit();
        SharedPreferences prefs = getContext().getSharedPreferences(NOTIFICATION_ENABLE_PREFS, Context.MODE_PRIVATE);
        boolean restoredPref = prefs.getBoolean(NOTIFICATION_ENABLE_KEY, true);
        if (restoredPref) {
            editor.putBoolean(NOTIFICATION_ENABLE_KEY, true);
            editor.commit();
            restoredPref = true;
        }
        return restoredPref;
    }

    private void setNotificationStatus(boolean notificationStatus) {
        SharedPreferences.Editor editor = getContext().getSharedPreferences(NOTIFICATION_ENABLE_PREFS, Context.MODE_PRIVATE).edit();
        editor.putBoolean(NOTIFICATION_ENABLE_KEY, notificationStatus);
        editor.commit();
    }

}
