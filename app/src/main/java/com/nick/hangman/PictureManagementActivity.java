package com.nick.hangman;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_management);

        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.setAdListener(new ToastAdListener(this));
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

            Button useIt = new Button(this);
            useIt.setId(999);
            useIt.setText(getResources().getText(R.string.use_image_button));

            imageLastUsedLayout.addView(useIt, wrapContentImageLastUsedParams);

            Button useIt1 = (Button) findViewById(useIt.getId());
            assert useIt1 != null;
            useIt1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {

                    callTaleScreen();

                }
            });

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

        Button createNewImageButton = (Button) findViewById(R.id.createNewImageButton);
        assert createNewImageButton != null;
        createNewImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                startActivity(new Intent(getBaseContext(), PictureCreationActivity.class));

            }
        });

        Button selectSavedImageButton = (Button) findViewById(R.id.selectSavedImageButton);
        assert selectSavedImageButton != null;
        selectSavedImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                startActivity(new Intent(getBaseContext(), SelectExistingImageActivity.class));

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
