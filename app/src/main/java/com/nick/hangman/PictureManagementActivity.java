package com.nick.hangman;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import android.net.Uri;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.IOException;
import java.net.URISyntaxException;

public class PictureManagementActivity extends AppCompatActivity {

    private AdView mAdView;

    private float mScaledDensity;
    private float mDensity;

    private static final float IMAGE_PERC_HEIGHT = 0.3f; //30%

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

        //////////apagar depois... vair vir do banco de dados
        String imageLastUsedURI = "content://media/external/images/media/58022";
        imageLastUsedURI = null;
        int imageWidth = 189;
        int imageHeight = 254;

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

        if(imageLastUsedURI != null) {
            //Load image on screen
            try {

                Uri imageUri = Uri.parse(imageLastUsedURI);

                Bitmap imageLastUsedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

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



            }catch(IOException e) {
                e.printStackTrace();
            }


        }else {
            //Load text with instructions to create an image, on screen
            ImageView avatarView = new ImageView(this);
            avatarView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            avatarView.setAdjustViewBounds(false);
            avatarView.setPadding(0, 0, 0, 0);
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


    }

    private String loadImageLastUsedFromDB() {
        //Load last used image from DB



        //If there's no last used image, return null
        return null;
    }

}
