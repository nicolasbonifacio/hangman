package com.nick.hangman;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class GameMainActivity extends AppCompatActivity {
    private AdView mAdView;

    View content;
    private String mPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_main);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Menu menu = toolbar.getMenu();
        if ( null != menu ) menu.clear();
        toolbar.inflateMenu(R.menu.menu_game_main);
        setSupportActionBar(toolbar);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_game_main, menu);
        return true;
    }

    public void getScreen() {

        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "HangmanTale");

        if (!folder.exists()) {
            folder.mkdir();
        }

        View view = content;
        View v = view.getRootView();
        v.setDrawingCacheEnabled(true);
        Bitmap b = v.getDrawingCache();
        mPath = Environment.getExternalStorageDirectory().toString() + File.separator + "HangmanTale" + File.separator;
        File myPath = new File(mPath, "HangmanTale_temp.jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            MediaStore.Images.Media.insertImage( getContentResolver(), b,
                    "Screen", "screen");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_share) {

            content = findViewById(R.id.gallowsLayout);

            getScreen();


            File image = new File(Environment.getExternalStorageDirectory() + File.separator + "HangmanTale" + File.separator, "HangmanTale_temp.jpg");
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap mBitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);

            Bitmap icon = mBitmap;
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/jpeg");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "HangmanTale" + File.separator + "HangmanTale.jpg");
            try {
                f.createNewFile();
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
            share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/HangmanTale/HangmanTale.jpg"));
            startActivity(Intent.createChooser(share, getResources().getText(R.string.share_title)));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        File fileDelete = new File(mPath, "HangmanTale_temp.jpg");
        if(fileDelete.exists()) {
            fileDelete.delete();
        }
        fileDelete = new File(mPath, "HangmanTale.jpg");
        if(fileDelete.exists()) {
            fileDelete.delete();
        }
    }

}
