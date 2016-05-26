package com.nick.hangman;

import com.nick.hangman.Objects.ImageTable;
import com.nick.hangman.Views.DragRectView;
import com.nick.hangman.data.HangmanContract;

import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PictureCreationActivity extends AppCompatActivity {

    private static final int SELECT_PHOTO = 100;
    private static final int IMAGE_LAST_USED_FLAG_FALSE = 0;
    private static final int IMAGE_LAST_USED_FLAG_TRUE = 1;

    private static final String COLUMN_IMAGE_LAST_USED_SELECTION = "last_used = ?";

    public static final String SOUND_ON_OFF_PREFS = "soundOnOffPrefs";
    private static final int SOUND_ON_OFF_NOT_DEFINED_FLAG = -1;
    private static final int SOUND_ON_FLAG = 1;

    private static final long MIN_MEMORY = 536870912;

    private Bitmap mBitmapImage;

    private int mWidth;
    private int mHeight;

    private int mLeft;
    private int mTop;
    private int mRight;
    private int mBottom;

    private boolean useRatioWidth = false;
    private boolean useRatioHeight = false;
    private float ratioWidth;
    private float ratioHeight;

    private MediaPlayer buttonSound;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_creation);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        buttonSound = MediaPlayer.create(this, R.raw.button_sound);

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);

        final ImageView selectImageButtonPressed = (ImageView) findViewById(R.id.selectImageButtonPressed);
        assert selectImageButtonPressed != null;
        selectImageButtonPressed.setVisibility(View.INVISIBLE);

        final ImageView selectImageButton = (ImageView) findViewById(R.id.selectImageButton);
        assert selectImageButton != null;
        selectImageButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        selectImageButton.setVisibility(View.INVISIBLE);
                        selectImageButtonPressed.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:
                        selectImageButton.setVisibility(View.VISIBLE);
                        selectImageButtonPressed.setVisibility(View.INVISIBLE);

                        mLeft = 0;
                        mTop = 0;
                        mRight = 0;
                        mBottom = 0;

                        if(getSoundStatus() == SOUND_ON_FLAG) {
                            buttonSound.start();
                        }

                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, SELECT_PHOTO);

                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        mLeft = 0;
        mTop = 0;
        mRight = 0;
        mBottom = 0;

        final ImageView createImageButtonPressed = ((ImageView) findViewById(R.id.createImageButtonPressed));
        assert createImageButtonPressed != null;
        createImageButtonPressed.setVisibility(View.INVISIBLE);

        final ImageView createImageButton = ((ImageView) findViewById(R.id.createImageButton));
        assert createImageButton != null;

        createImageButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        createImageButton.setVisibility(View.INVISIBLE);
                        createImageButtonPressed.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:
                        createImageButton.setVisibility(View.VISIBLE);
                        createImageButtonPressed.setVisibility(View.INVISIBLE);
                        if(getSoundStatus() == SOUND_ON_FLAG) {
                            buttonSound.start();
                        }
                        if(mRight > 0 && mBottom > 0) {
                            cropImage(mLeft, mTop, mRight, mBottom);
                        }else {
                            Toast.makeText(getBaseContext(), getResources().getText(R.string.area_not_selected), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();

                    final String[] imageColumns = { MediaStore.Images.ImageColumns.ORIENTATION };
                    Cursor cursor = getContentResolver().query(selectedImage,
                            imageColumns, null, null, null);
                    final int orientation;
                    if(cursor.moveToFirst()){
                        orientation = cursor.getInt(0);
                        cursor.close();
                    }else {
                        orientation = 0;
                    }

                    try {

                        if(isImageSmaller(selectedImage)) {
                            //Image is smaller than cel dimensions
                            mBitmapImage = handleBitmapSmaller(selectedImage);
                            mBitmapImage = rotateImageIfRequired(mBitmapImage, orientation);
                        }else {
                            //Image is larger than cel dimensions

                            ActivityManager actManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                            ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
                            actManager.getMemoryInfo(memInfo);
                            long totalMemory = memInfo.totalMem;

                            mBitmapImage = handleBitmapBigger(this, selectedImage, orientation, totalMemory);
                            mWidth = mBitmapImage.getWidth();
                            mHeight = mBitmapImage.getHeight();
                        }
                    }catch(IOException e) {
                        e.printStackTrace();
                    }

                    setImageToView();

                }else {
                    Toast.makeText(getBaseContext(), getResources().getText(R.string.image_not_selected), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void setImageToView() {

        RelativeLayout imageLayout = (RelativeLayout) findViewById(R.id.imageLayout);
        if(imageLayout != null && imageLayout.getChildCount() > 0) {
            imageLayout.removeAllViews();
        }
        RelativeLayout.LayoutParams imageLayoutParams = new RelativeLayout.LayoutParams(
                mWidth,
                mHeight);

        ImageView imageView = new ImageView(this);
        //imageView.setId(123456);
        imageView.setScaleType(ImageView.ScaleType.FIT_START);
        imageView.setAdjustViewBounds(false);
        imageView.setPadding(0, 0, 0, 0);

        imageView.setImageBitmap(mBitmapImage);

        assert imageLayout != null;
        imageLayout.addView(imageView, imageLayoutParams);

        DragRectView view = new DragRectView(this);

        imageLayout.addView(view, imageLayoutParams);

        if (null != view) {
            view.setOnUpCallback(new DragRectView.OnUpCallback() {
                @Override
                public void onRectFinished(final Rect rect) {

                    mLeft = rect.left;
                    mTop = rect.top;
                    mRight = rect.right;
                    mBottom = rect.bottom;

                    //cropImage(rect.left, rect.top, rect.right, rect.bottom);

                }
            });
        }
    }

    private boolean isImageSmaller(Uri selectedImage) throws FileNotFoundException {

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        int width_tmp = o.outWidth, height_tmp = o.outHeight;

        int celWidth = getResources().getDisplayMetrics().widthPixels;
        int celHeight = (int)(getResources().getDisplayMetrics().heightPixels - (100*getResources().getDisplayMetrics().density));

        if(width_tmp < celWidth && height_tmp < celHeight) {
            return true;
        }else {
            return false;
        }

    }

    private Bitmap handleBitmapSmaller(Uri selectedImage) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        int width_tmp = o.outWidth, height_tmp = o.outHeight;

        int celWidth = getResources().getDisplayMetrics().widthPixels;
        int celHeight = (int)(getResources().getDisplayMetrics().heightPixels - (100*getResources().getDisplayMetrics().density));

        ratioWidth = (float)celWidth / width_tmp;
        ratioHeight = (float)celHeight / height_tmp;
        if(ratioWidth >= ratioHeight) {
            //Image and cel have the same height
            mWidth = (celHeight * width_tmp) / height_tmp;
            mHeight = celHeight;
            useRatioHeight = true;
        }else {
            //Image and cel have the same width
            mWidth = celWidth;
            mHeight = (celWidth * height_tmp) / width_tmp;
            useRatioWidth = true;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = 1;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);

    }

    private void cropImage(int left, int top, int right, int bottom) {

        FileOutputStream outStream = null;
        try {
            if(((right - left) > 0) && ((bottom - top) > 0)) {

                File folder = new File(Environment.getExternalStorageDirectory() +
                        File.separator + "HangmanTale");

                boolean isSuccess = true;
                if (!folder.exists()) {
                    isSuccess = folder.mkdir();
                }

                String absolutePath = folder.getAbsolutePath();

                long currentTime = System.currentTimeMillis();

                String fileName = String.format("Hangman_%d.jpg", currentTime);

                String totalPath = String.format(Environment.getExternalStorageDirectory() + File.separator +
                        "HangmanTale" + File.separator + fileName);

                outStream = new FileOutputStream(totalPath);

                if(mWidth > getResources().getDisplayMetrics().widthPixels) {
                    float ratio = (float)mWidth / getResources().getDisplayMetrics().widthPixels;
                    right = (int)(right * ratio);
                    bottom = (int)(bottom * ratio);
                    left = (int)(left * ratio);
                    top = (int)(top * ratio);
                }

                if(mHeight > getResources().getDisplayMetrics().heightPixels) {
                    float ratio = (float)mHeight / getResources().getDisplayMetrics().heightPixels;
                    right = (int)(right * ratio);
                    bottom = (int)(bottom * ratio);
                    left = (int)(left * ratio);
                    top = (int)(top * ratio);
                }

                if((top + (bottom - top)) > mHeight) {
                    bottom = mHeight;
                }
                if((left + (right - left)) > mWidth) {
                    right = mWidth;
                }
                if(top < 0) {
                    top = 0;
                }
                if(left < 0) {
                    left = 0;
                }

                Bitmap imageToSave;
                if(useRatioWidth) {
                    imageToSave = Bitmap.createBitmap(mBitmapImage, (int)((float)left/ratioWidth), (int)((float)top/ratioWidth), (int)((float)(right - left)/ratioWidth), (int)((float)(bottom - top)/ratioWidth));
                }else if(useRatioHeight) {
                    imageToSave = Bitmap.createBitmap(mBitmapImage, (int)((float)left/ratioHeight), (int)((float)top/ratioHeight), (int)((float)(right - left)/ratioHeight), (int)((float)(bottom - top)/ratioHeight));
                }else {
                    imageToSave = Bitmap.createBitmap(mBitmapImage, left, top, (right - left), (bottom - top));
                }

                imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

                outStream.close();

                //Update DB setting all images to 'not last used'
                ContentValues imageValues = new ContentValues();
                imageValues.put(HangmanContract.ImageEntry.COLUMN_LAST_USED, IMAGE_LAST_USED_FLAG_FALSE);

                int qtd = getContentResolver().update(
                        HangmanContract.ImageEntry.CONTENT_URI,
                        imageValues,
                        COLUMN_IMAGE_LAST_USED_SELECTION,
                        new String[]{Integer.toString(IMAGE_LAST_USED_FLAG_TRUE)}
                );

                //Insert image info in DB
                imageValues = new ContentValues();
                imageValues.put(HangmanContract.ImageEntry.COLUMN_IMAGE_WIDTH, imageToSave.getWidth());
                imageValues.put(HangmanContract.ImageEntry.COLUMN_IMAGE_HEIGHT, imageToSave.getHeight());
                imageValues.put(HangmanContract.ImageEntry.COLUMN_IMAGE_PATH, absolutePath);
                imageValues.put(HangmanContract.ImageEntry.COLUMN_IMAGE_NAME, fileName);
                imageValues.put(HangmanContract.ImageEntry.COLUMN_LAST_USED, IMAGE_LAST_USED_FLAG_TRUE);

                Uri insertedUri = getContentResolver().insert(
                        HangmanContract.ImageEntry.CONTENT_URI,
                        imageValues
                );

                //
                ImageTable imageTable = new ImageTable();
                imageTable.setImageWidth(imageToSave.getWidth());
                imageTable.setImageHeight(imageToSave.getHeight());
                imageTable.setImagePath(absolutePath);
                imageTable.setImageName(fileName);

                callTaleScreen(imageTable);

            }else {
                Toast.makeText(this, getResources().getText(R.string.area_not_selected), Toast.LENGTH_SHORT).show();
            }
        }

        catch (Exception e) {
            Toast.makeText(this, getResources().getString(R.string.share_no_permission_storage), Toast.LENGTH_LONG).show();
        }

    }

    public static Bitmap handleBitmapBigger(Context context, Uri selectedImage, final int orientation, long totalMemory)
            throws IOException {

        int MAX_HEIGHT = 0;
        int MAX_WIDTH = 0;
        if(totalMemory < MIN_MEMORY) {
            MAX_HEIGHT = 128;
            MAX_WIDTH = 128;
        }else {
            MAX_HEIGHT = 1024;
            MAX_WIDTH = 1024;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(img, orientation);
        return img;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    private static Bitmap rotateImageIfRequired(Bitmap img, final int orientation) throws IOException {

        switch (orientation) {
            case 90:
                return rotateImage(img, 90);
            case 180:
                return rotateImage(img, 180);
            case 270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    private void callTaleScreen(ImageTable imageTable) {

        ParametersSelected paramsSel = new ParametersSelected();
        paramsSel.setImageImageWidth(imageTable.getImageWidth());
        paramsSel.setImageImageHeight(imageTable.getImageHeight());
        paramsSel.setImageImagePath(imageTable.getImagePath());
        paramsSel.setImageImageName(imageTable.getImageName());

        Intent intent = new Intent(this, TaleActivity.class);

        intent.putExtra("paramsSel", paramsSel);

        startActivity(intent);

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
