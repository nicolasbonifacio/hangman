package com.nick.hangman;

import com.nick.hangman.Views.DragRectView;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
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

public class PictureSelectionActivity extends AppCompatActivity {

    private static final int SELECT_PHOTO = 100;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_selection);

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);

        Button selectImageButton = (Button) findViewById(R.id.selectImageButton);
        assert selectImageButton != null;
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mLeft = 0;
                mTop = 0;
                mRight = 0;
                mBottom = 0;

                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });

        mLeft = 0;
        mTop = 0;
        mRight = 0;
        mBottom = 0;

        Button createImageButton = ((Button) findViewById(R.id.createImageButton));
        assert createImageButton != null;
        createImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                if(mRight > 0 && mBottom > 0) {
                    cropImage(mLeft, mTop, mRight, mBottom);
                }else {
                    Toast.makeText(getBaseContext(), getResources().getText(R.string.area_not_selected), Toast.LENGTH_SHORT).show();
                }
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
                            mBitmapImage = handleBitmapBigger(this, selectedImage, orientation);
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
/*
        ImageView iv = (ImageView) findViewById(R.id.imageView2);
        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        iv.setAdjustViewBounds(false);
        iv.setPadding(0, 0, 0, 0);
        //Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.up_image);

        int lg_width = getResources().getDisplayMetrics().widthPixels;
        int lg_height = 1000;
        int img_width = mWidth;
        int img_height = mHeight;

        float ratioWidth = (float)lg_width / img_width;
        float ratioHeight = (float)lg_height / img_height;

        int cropRight = (int)((right * img_width) / lg_width);
        int cropBottom = (int)((bottom * img_height) / lg_height);
        int cropLeft = (int)(cropRight - ((right - left) / ratioWidth));
        int cropTop = (int)(cropBottom - ((bottom - top) / ratioHeight));

        BitmapDrawable d = new CropDrawable(mBitmapImage, cropLeft, cropTop, cropRight, cropBottom);
        iv.setImageDrawable(d);
*/

        FileOutputStream outStream = null;
        try {
            if(((right - left) > 0) && ((bottom - top) > 0)) {

                File folder = new File(Environment.getExternalStorageDirectory() +
                        File.separator + "HangmanTale");

                boolean isSuccess = true;
                if (!folder.exists()) {
                    isSuccess = folder.mkdir();
                }

                long currentTime = System.currentTimeMillis();
                String caminho = String.format(Environment.getExternalStorageDirectory() + File.separator +
                        "HangmanTale" + File.separator + "Hangman_%d.jpg", currentTime);

                outStream = new FileOutputStream(caminho);

                //Bitmap imageToSave = d.getBitmap();

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

                Bitmap imageToSave;
                if(useRatioWidth) {
                    imageToSave = Bitmap.createBitmap(mBitmapImage, (int)((float)left/ratioWidth), (int)((float)top/ratioWidth), (int)((float)(right - left)/ratioWidth), (int)((float)(bottom - top)/ratioWidth));
                }else if(useRatioHeight) {
                    imageToSave = Bitmap.createBitmap(mBitmapImage, (int)((float)left/ratioHeight), (int)((float)top/ratioHeight), (int)((float)(right - left)/ratioHeight), (int)((float)(bottom - top)/ratioHeight));
                }else {
                    imageToSave = Bitmap.createBitmap(mBitmapImage, left, top, (right - left), (bottom - top));
                }

                //Bitmap imageToSave = Bitmap.createBitmap(mBitmapImage, left, top, (right - left), (bottom - top));
                imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

                outStream.close();

                String newImageURL = MediaStore.Images.Media.insertImage(getContentResolver(), imageToSave, "Hangman_" + currentTime + ".jpg", "Hangman_" + currentTime + ".jpg");

                Toast.makeText(this, "Image created.", Toast.LENGTH_SHORT).show();



            }else {
                Toast.makeText(this, getResources().getText(R.string.area_not_selected), Toast.LENGTH_SHORT).show();
            }
        }

        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }
/*
    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);


        // The new size we want to scale to
        final int REQUIRED_SIZE = 140;
        int scale = 1;
        // Find the correct scale value. It should be the power of 2.
        //int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int width_tmp = o.outWidth, height_tmp = o.outHeight;

        int celWidth = getResources().getDisplayMetrics().widthPixels;
        int celHeight = (int)(getResources().getDisplayMetrics().heightPixels - (100*getResources().getDisplayMetrics().density));

        if((height_tmp < celHeight) && (width_tmp < celWidth)) {
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

        }else {


            float ratioWidthReduct = (float)width_tmp / celWidth;
            float ratioHeightReduct = (float)height_tmp / celHeight;

            while (true) {
                if (width_tmp < celWidth
                        && height_tmp < celHeight) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;



//                if(ratioWidthReduct > ratioHeightReduct) {
                    //width equal
//                    mWidth = celWidth;
//                    mHeight = (celWidth * height_tmp) / width_tmp;

//                }else {
                    //height equal
//                    mWidth = (celHeight * width_tmp) / height_tmp;
//                    mHeight = celHeight;

//                }



            }
            mWidth = width_tmp;
            mHeight = height_tmp;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);

    }
*/

    public static Bitmap handleBitmapBigger(Context context, Uri selectedImage, final int orientation)
            throws IOException {
        int MAX_HEIGHT = 1024;
        int MAX_WIDTH = 1024;

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

}
