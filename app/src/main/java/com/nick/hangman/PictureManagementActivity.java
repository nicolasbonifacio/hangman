package com.nick.hangman;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import android.net.Uri;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nick.hangman.Objects.ImageTable;
import com.nick.hangman.data.HangmanContract;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class PictureManagementActivity extends AppCompatActivity {

    private AdView mAdView;

    private float mScaledDensity;
    private float mDensity;

    private static final int LANGUAGE_LAST_USED_FLAG = 1;

    private static final float IMAGE_PERC_HEIGHT = 0.3f; //30%
    private static final int AVATAR_WIDTH = 216;
    private static final int AVATAR_HEIGHT = 219;
    private static final int USET_IT_DIALOG_ID = 999;
    private static final int USET_IT_DIALOG_ID_PRESSED = 996;

    private static final String[] IMAGE_COLUMNS = {
            HangmanContract.ImageEntry.TABLE_NAME + "." + HangmanContract.ImageEntry._ID,
            HangmanContract.ImageEntry.TABLE_NAME + "." + HangmanContract.ImageEntry.COLUMN_IMAGE_WIDTH,
            HangmanContract.ImageEntry.TABLE_NAME + "." + HangmanContract.ImageEntry.COLUMN_IMAGE_HEIGHT,
            HangmanContract.ImageEntry.TABLE_NAME + "." + HangmanContract.ImageEntry.COLUMN_IMAGE_PATH,
            HangmanContract.ImageEntry.TABLE_NAME + "." + HangmanContract.ImageEntry.COLUMN_IMAGE_NAME
    };

    private static final String sImageLastUsedSelection =
                    HangmanContract.ImageEntry.TABLE_NAME +
                    "." + HangmanContract.ImageEntry.COLUMN_LAST_USED + " = ?";

    //IMAGE columns
    public static final int COL_IMAGE_ID = 0;
    public static final int COL_IMAGE_IMAGE_WIDTH = 1;
    public static final int COL_IMAGE_IMAGE_HEIGHT = 2;
    public static final int COL_IMAGE_IMAGE_PATH = 3;
    public static final int COL_IMAGE_IMAGE_NAME = 4;

    private ImageTable mImageTable;

    private MediaPlayer buttonSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_management);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        buttonSound = MediaPlayer.create(this, R.raw.button_sound);

        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.setAdListener(new ToastAdListener(this));
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    protected void onResume() {
        super.onResume();

        buttonSound = MediaPlayer.create(this, R.raw.button_sound);

        mScaledDensity = getResources().getDisplayMetrics().scaledDensity;
        mDensity = getResources().getDisplayMetrics().density;

        mImageTable = new ImageTable();
        boolean hasLastImage = loadImageLastUsedFromDB();

        int imageWidth;
        int imageHeight;

        if(hasLastImage) {
            imageWidth = mImageTable.getImageWidth();
            imageHeight = mImageTable.getImageHeight();
        }else {
            imageWidth = AVATAR_WIDTH;
            imageHeight = AVATAR_HEIGHT;
        }

        int screenWidthPx = getResources().getDisplayMetrics().widthPixels;
        int screenHeightPx = getResources().getDisplayMetrics().heightPixels;

        //Use 30% of the screen height as the image height
        float imageRatio = (float)imageHeight / imageWidth;
        int imageScreenHeight = (int)(screenHeightPx * IMAGE_PERC_HEIGHT);
        int imageScreenWidth = (int)(imageScreenHeight / imageRatio);

        if(imageScreenWidth > (screenWidthPx - (getResources().getDimension(R.dimen.activity_horizontal_margin) * 2))) {
            //If image became wider than the screen, after resizing
            imageScreenWidth = (int)((screenWidthPx - (getResources().getDimension(R.dimen.activity_horizontal_margin) * 2)));
            imageScreenHeight = (int)(imageScreenWidth / imageRatio);

        }

        LinearLayout imageLastUsedLayout = (LinearLayout) findViewById(R.id.imageLastUsedLayout);
        LinearLayout.LayoutParams imageLastUsedParams = new LinearLayout.LayoutParams(
                imageScreenWidth,
                imageScreenHeight
        );

        assert imageLastUsedLayout != null;
        imageLastUsedLayout.removeAllViews();

        LinearLayout.LayoutParams wrapContentImageLastUsedParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        if(hasLastImage) {
            //Load image on screen
            File image = new File(mImageTable.getImagePath(), mImageTable.getImageName());
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap imageLastUsedBitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);

            //Uri imageUri = Uri.parse(imageLastUsedURI);

            //Bitmap imageLastUsedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

            ImageView imageLastUsedView = new ImageView(this);
            imageLastUsedView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageLastUsedView.setAdjustViewBounds(false);
            imageLastUsedView.setPadding(0, 0, 0, (int)(getResources().getDimension(R.dimen.image_padding) / mDensity));
            imageLastUsedView.setImageBitmap(imageLastUsedBitmap);

            assert imageLastUsedLayout != null;
            imageLastUsedLayout.addView(imageLastUsedView, imageLastUsedParams);

            TextView imageLastUsedText = new TextView(this);
            imageLastUsedText.setText(getResources().getText(R.string.your_last_image_text));
            imageLastUsedText.setTextSize(getResources().getDimension(R.dimen.your_last_image_font_size) / mScaledDensity);
            imageLastUsedText.setPadding(0, 0, 0, (int)(getResources().getDimension(R.dimen.image_padding) / mDensity));

            imageLastUsedLayout.addView(imageLastUsedText, wrapContentImageLastUsedParams);

            LinearLayout.LayoutParams useItParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );

            FrameLayout useItLayout = new FrameLayout(this);
            FrameLayout.LayoutParams useItLayoutParams = new FrameLayout.LayoutParams(
                    getResources().getDimensionPixelSize(R.dimen.use_it_picture_button_width),
                    getResources().getDimensionPixelSize(R.dimen.use_it_picture_button_height)
            );
            useItLayoutParams.gravity = Gravity.CENTER;

            FrameLayout.LayoutParams useItLayoutParamsPressed = new FrameLayout.LayoutParams(
                    getResources().getDimensionPixelSize(R.dimen.use_it_picture_button_width_pressed),
                    getResources().getDimensionPixelSize(R.dimen.use_it_picture_button_height_pressed)
            );
            useItLayoutParamsPressed.gravity = Gravity.CENTER;

            final ImageView useItPressed = new ImageView(this);
            useItPressed.setId(USET_IT_DIALOG_ID_PRESSED);
            useItPressed.setImageDrawable(getResources().getDrawable(R.drawable.button_picture_use_it_blue_en_pressed));
            useItPressed.setVisibility(View.INVISIBLE);

            final ImageView useIt = new ImageView(this);
            useIt.setId(USET_IT_DIALOG_ID);
            useIt.setImageDrawable(getResources().getDrawable(R.drawable.button_picture_use_it_blue_en));

            useItLayout.addView(useIt, useItLayoutParams);
            useItLayout.addView(useItPressed, useItLayoutParamsPressed);

            useIt.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            useIt.setVisibility(View.INVISIBLE);
                            useItPressed.setVisibility(View.VISIBLE);
                            break;
                        case MotionEvent.ACTION_UP:
                            useIt.setVisibility(View.VISIBLE);
                            useItPressed.setVisibility(View.INVISIBLE);
                            buttonSound.start();
                            callTaleScreen();
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });

            imageLastUsedLayout.addView(useItLayout, useItParams);

        }else {
            //Load text with instructions to create an image, on screen
            ImageView avatarView = new ImageView(this);
            avatarView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            avatarView.setAdjustViewBounds(false);
            avatarView.setPadding(0, 0, 0, (int)(getResources().getDimension(R.dimen.image_padding) / mDensity));
            avatarView.setImageDrawable(getResources().getDrawable(R.drawable.avatar));

            assert imageLastUsedLayout != null;
            imageLastUsedLayout.addView(avatarView, imageLastUsedParams);

            TextView avatarText = new TextView(this);
            avatarText.setText(getResources().getText(R.string.avatar_text));
            avatarText.setTextSize(getResources().getDimension(R.dimen.avatar_text_font_size) / mScaledDensity);
            avatarText.setPadding(0, 0, 0, 0);
            avatarText.setGravity(Gravity.CENTER_HORIZONTAL);

            imageLastUsedLayout.addView(avatarText, wrapContentImageLastUsedParams);

        }

        final ImageView createNewImageButtonPressed = (ImageView) findViewById(R.id.createNewImageButtonPressed);
        assert createNewImageButtonPressed != null;
        createNewImageButtonPressed.setVisibility(View.INVISIBLE);

        final ImageView createNewImageButton = (ImageView) findViewById(R.id.createNewImageButton);
        assert createNewImageButton != null;
        createNewImageButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        createNewImageButton.setVisibility(View.INVISIBLE);
                        createNewImageButtonPressed.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:
                        createNewImageButton.setVisibility(View.VISIBLE);
                        createNewImageButtonPressed.setVisibility(View.INVISIBLE);
                        buttonSound.start();
                        startActivity(new Intent(getBaseContext(), PictureCreationActivity.class));
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        final ImageView selectSavedImageButtonPressed = (ImageView) findViewById(R.id.selectSavedImageButtonPressed);
        assert selectSavedImageButtonPressed != null;
        selectSavedImageButtonPressed.setVisibility(View.INVISIBLE);

        final ImageView selectSavedImageButton = (ImageView) findViewById(R.id.selectSavedImageButton);
        assert selectSavedImageButton != null;
        selectSavedImageButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        selectSavedImageButton.setVisibility(View.INVISIBLE);
                        selectSavedImageButtonPressed.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:
                        selectSavedImageButton.setVisibility(View.VISIBLE);
                        selectSavedImageButtonPressed.setVisibility(View.INVISIBLE);
                        buttonSound.start();
                        startActivity(new Intent(getBaseContext(), SelectExistingImageActivity.class));
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

    }

    private boolean loadImageLastUsedFromDB() {
        //Retrieve last used image Uri and dimensions from DB
        Uri uri = HangmanContract.ImageEntry.buildImageLastUsed(LANGUAGE_LAST_USED_FLAG);

        Cursor cursor = getContentResolver().query(uri, IMAGE_COLUMNS, null, null, null);

        if(cursor != null && cursor.moveToFirst()) {

            mImageTable.setId(cursor.getInt(COL_IMAGE_ID));
            mImageTable.setImageWidth(cursor.getInt(COL_IMAGE_IMAGE_WIDTH));
            mImageTable.setImageHeight(cursor.getInt(COL_IMAGE_IMAGE_HEIGHT));
            mImageTable.setImagePath(cursor.getString(COL_IMAGE_IMAGE_PATH));
            mImageTable.setImageName(cursor.getString(COL_IMAGE_IMAGE_NAME));

            cursor.close();

            return true;

        }else {
            //If there's no last used image, return null
            return false;
        }
    }

    private void callTaleScreen() {

        ParametersSelected paramsSel = new ParametersSelected();
        paramsSel.setImageImageWidth(mImageTable.getImageWidth());
        paramsSel.setImageImageHeight(mImageTable.getImageHeight());
        paramsSel.setImageImagePath(mImageTable.getImagePath());
        paramsSel.setImageImageName(mImageTable.getImageName());

        Intent intent = new Intent(this, TaleActivity.class);

        intent.putExtra("paramsSel", paramsSel);

        startActivity(intent);

    }
}
