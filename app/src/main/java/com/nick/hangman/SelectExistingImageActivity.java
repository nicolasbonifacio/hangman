package com.nick.hangman;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nick.hangman.Objects.ImageTable;
import com.nick.hangman.data.HangmanContract;

import java.io.File;
import java.util.ArrayList;

public class SelectExistingImageActivity extends AppCompatActivity {

    private AdView mAdView;

    private int mGridCelHeight;
    ArrayList<ImageTable> mImageList;

    private static final String[] IMAGE_COLUMNS = {
            HangmanContract.ImageEntry.TABLE_NAME + "." + HangmanContract.ImageEntry._ID,
            HangmanContract.ImageEntry.TABLE_NAME + "." + HangmanContract.ImageEntry.COLUMN_IMAGE_WIDTH,
            HangmanContract.ImageEntry.TABLE_NAME + "." + HangmanContract.ImageEntry.COLUMN_IMAGE_HEIGHT,
            HangmanContract.ImageEntry.TABLE_NAME + "." + HangmanContract.ImageEntry.COLUMN_IMAGE_PATH,
            HangmanContract.ImageEntry.TABLE_NAME + "." + HangmanContract.ImageEntry.COLUMN_IMAGE_NAME
    };

    public static final String IMAGE_SORT_ODER = "image_name desc";

    //IMAGE columns
    public static final int COL_IMAGE_ID = 0;
    public static final int COL_IMAGE_IMAGE_WIDTH = 1;
    public static final int COL_IMAGE_IMAGE_HEIGHT = 2;
    public static final int COL_IMAGE_IMAGE_PATH = 3;
    public static final int COL_IMAGE_IMAGE_NAME = 4;

    private static final String COLUMN_IMAGE_LAST_USED_SELECTION = "last_used = ?";
    private static final String COLUMN_IMAGE_ID = "_id = ?";

    public static final int GRID_NUM_COLS = 3;
    public static final int GRID_HEIGHT_DIVISION_FACTOR = 4;

    public static final int IMAGE_LAST_USED_FLAG_FALSE = 0;
    public static final int IMAGE_LAST_USED_FLAG_TRUE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_existing_image);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mImageList = new ArrayList<ImageTable>();
        mImageList = loadExistingImages();

        if(mImageList != null && mImageList.size() > 0) {

            mGridCelHeight = (int)(getResources().getDisplayMetrics().heightPixels / GRID_HEIGHT_DIVISION_FACTOR);

            GridView selectExistingImageView = (GridView) findViewById(R.id.selectExistingImageView);
            assert selectExistingImageView != null;
            selectExistingImageView.setNumColumns(GRID_NUM_COLS);

            ImageAdapter imageAdapter = new ImageAdapter(this, mImageList.size(), mImageList);
            selectExistingImageView.setAdapter(imageAdapter);

            selectExistingImageView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {

                    callTaleScreen(position);

                }
            });

            selectExistingImageView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    deleteImage(position);

                    return true;
                }
            });

        }

    }

    private ArrayList<ImageTable> loadExistingImages() {

        Uri uri = HangmanContract.ImageEntry.CONTENT_URI;

        Cursor cursor = getContentResolver().query(uri,
                IMAGE_COLUMNS,
                null,
                null,
                IMAGE_SORT_ODER);

        if(cursor != null && cursor.moveToFirst()) {

            ArrayList<ImageTable> imageList = new ArrayList<ImageTable>();
            ImageTable imageTable;

            do {
                imageTable = new ImageTable();
                imageTable.setId(cursor.getInt(COL_IMAGE_ID));
                imageTable.setImageWidth(cursor.getInt(COL_IMAGE_IMAGE_WIDTH));
                imageTable.setImageHeight(cursor.getInt(COL_IMAGE_IMAGE_HEIGHT));
                imageTable.setImagePath(cursor.getString(COL_IMAGE_IMAGE_PATH));
                imageTable.setImageName(cursor.getString(COL_IMAGE_IMAGE_NAME));

                imageList.add(imageTable);

            }while(cursor.moveToNext());

            cursor.close();

            return imageList;

        }else {
            return null;
        }

    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private int numImages;
        private ArrayList<ImageTable> imageList;

        public ImageAdapter(Context c, int i, ArrayList<ImageTable> images) {
            mContext = c;
            numImages = i;
            imageList = images;
        }

        public int getCount() {
            return numImages;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {

            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
//                imageView.setLayoutParams(parent.getLayoutParams());
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setAdjustViewBounds(true);
                imageView.setPadding(0, 0, 0, 0);

            }else {
                imageView = (ImageView) convertView;
                convertView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, mGridCelHeight));
                ((ImageView) convertView).setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

            if(imageList != null && imageList.size() > 0) {

                File image = new File(imageList.get(position).getImagePath(), imageList.get(position).getImageName());
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap imageLoadedBitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);

                imageView.setImageBitmap(imageLoadedBitmap);

            }else {
                imageView = null;
            }

            return imageView;
        }

    }

    private void callTaleScreen(int position) {

        ParametersSelected paramsSel = new ParametersSelected();
        paramsSel.setImageImageWidth(mImageList.get(position).getImageWidth());
        paramsSel.setImageImageHeight(mImageList.get(position).getImageHeight());
        paramsSel.setImageImagePath(mImageList.get(position).getImagePath());
        paramsSel.setImageImageName(mImageList.get(position).getImageName());

        //Update DB setting all images to 'not last used'
        ContentValues imageValues = new ContentValues();
        imageValues.put(HangmanContract.ImageEntry.COLUMN_LAST_USED, IMAGE_LAST_USED_FLAG_FALSE);

        int qtd = getContentResolver().update(
                HangmanContract.ImageEntry.CONTENT_URI,
                imageValues,
                COLUMN_IMAGE_LAST_USED_SELECTION,
                new String[]{Integer.toString(IMAGE_LAST_USED_FLAG_TRUE)}
        );

        //Set the chosen image as 'last used'
        imageValues = new ContentValues();
        imageValues.put(HangmanContract.ImageEntry.COLUMN_LAST_USED, IMAGE_LAST_USED_FLAG_TRUE);

        qtd = getContentResolver().update(
                HangmanContract.ImageEntry.CONTENT_URI,
                imageValues,
                COLUMN_IMAGE_ID,
                new String[]{Integer.toString(mImageList.get(position).getId())}
        );

        Intent intent = new Intent(this, TaleActivity.class);

        intent.putExtra("paramsSel", paramsSel);

        startActivity(intent);

    }

    private void deleteImage(int position) {

        final int iPosition = position;

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getResources().getString(R.string.delete_image_dialog_title))
                .setMessage(getResources().getString(R.string.delete_image_dialog_message))
                .setPositiveButton(getResources().getString(R.string.delete_image_dialog_positive), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        int qtd = getContentResolver().delete(
                                HangmanContract.ImageEntry.CONTENT_URI,
                                COLUMN_IMAGE_ID,
                                new String[]{Integer.toString(mImageList.get(iPosition).getId())}
                        );

                        File fileDelete = new File(mImageList.get(iPosition).getImagePath(), mImageList.get(iPosition).getImageName());
                        if(fileDelete.exists()) {
                            fileDelete.delete();
                        }

                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);

                    }

                })
                .setNegativeButton(getResources().getString(R.string.delete_image_dialog_negative), null)
                .show();

    }

}
