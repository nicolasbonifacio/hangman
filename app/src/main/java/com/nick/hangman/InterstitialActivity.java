package com.nick.hangman;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class InterstitialActivity extends Activity {
//    private Button mShowButton;
    private InterstitialAd mInterstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);

//        mShowButton = (Button) findViewById(R.id.showButton);
//        mShowButton.setEnabled(false);

    }

    public void loadInterstitial(View unusedView) {
//        mShowButton.setEnabled(false);
//        mShowButton.setText("Loading interstitial");

        mInterstitial = new InterstitialAd(this);
        mInterstitial.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id));
        mInterstitial.setAdListener(new ToastAdListener(this) {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mInterstitial.show();
                startActivity(new Intent(getBaseContext(), PictureManagementActivity.class));
//                mShowButton.setText("Show Interstitial");
//                mShowButton.setEnabled(true);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                startActivity(new Intent(getBaseContext(), PictureManagementActivity.class));
//                mShowButton.setText(getErrorReason());
            }
        });

        AdRequest ar = new AdRequest.Builder().build();
        mInterstitial.loadAd(ar);

    }
/*
    public void showInterstitial(View unusedView) {
        if(mInterstitial.isLoaded()) {
            mInterstitial.show();
        }

        mShowButton.setText("Interstitial not ready");
        mShowButton.setEnabled(false);

    }
*/
}
