package com.nick.hangman;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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

    private MediaPlayer buttonSound;

    public static final String SOUND_ON_OFF_PREFS = "soundOnOffPrefs";
    private static final int SOUND_ON_OFF_NOT_DEFINED_FLAG = -1;
    private static final int SOUND_ON_FLAG = 1;

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

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

        buttonSound = MediaPlayer.create(this, R.raw.button_sound);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_game_main, menu);
        return true;
    }

    public void getScreen() {

        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "JogoDaForca");

        if (!folder.exists()) {
            folder.mkdir();
        }

        View view = content;
        View v = view.getRootView();
        v.setDrawingCacheEnabled(true);
        Bitmap b = v.getDrawingCache();
        mPath = Environment.getExternalStorageDirectory().toString() + File.separator + "JogoDaForca" + File.separator;
        File myPath = new File(mPath, "Jogo_da_forca_temp.jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_share) {

            if(getSoundStatus() == SOUND_ON_FLAG) {
                buttonSound.start();
            }

            content = findViewById(R.id.gallowsLayout);

            int permissionWriteStorage = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if(permissionWriteStorage == PackageManager.PERMISSION_GRANTED) {

                getScreen();
                processScreen();

            }else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

            }


        }
        return super.onOptionsItemSelected(item);
    }

    private void processScreen() {

        File image = new File(Environment.getExternalStorageDirectory() + File.separator + "JogoDaForca" + File.separator, "Jogo_da_forca_temp.jpg");
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap mBitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);

        Bitmap icon = mBitmap;
        Intent share = new Intent(Intent.ACTION_SEND);

        //share.setType("image/jpeg");

        share.setType("image/jpeg");
        //share.setType("*/*");
        share.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_text));
        share.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.share_email_title));

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "JogoDaForca" + File.separator + "JogoDaForca.jpg");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/JogoDaForca/JogoDaForca.jpg"));
        startActivity(Intent.createChooser(share, getResources().getText(R.string.share_title)));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getScreen();
                    processScreen();

                } else {
                    Utils utils = new Utils();
                    utils.showPermissionErrorDialog(this);
                }
            }

        }
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

    private int getSoundStatus() {
        SharedPreferences.Editor editor = getSharedPreferences(SOUND_ON_OFF_PREFS, Context.MODE_PRIVATE).edit();
        SharedPreferences prefs = getSharedPreferences(SOUND_ON_OFF_PREFS, Context.MODE_PRIVATE);
        int restoredPref = prefs.getInt("soundOnOff", SOUND_ON_OFF_NOT_DEFINED_FLAG);
        if (restoredPref == SOUND_ON_OFF_NOT_DEFINED_FLAG) {
            editor.putInt("soundOnOff", SOUND_ON_FLAG);
            editor.commit();
            restoredPref = SOUND_ON_FLAG;
        }
        return restoredPref;
    }

}
