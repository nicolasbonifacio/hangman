package com.nick.hangman.Objects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;

public class CropDrawable extends BitmapDrawable {

    private Rect mSrc;
    private RectF mDst;

    public CropDrawable(Bitmap b, int left, int top, int right, int bottom) {
        super(b);
        mSrc = new Rect(left, top, right, bottom);
        mDst = new RectF(0, 0, right - left, bottom - top);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(getBitmap(), mSrc, mDst, null);
    }

    @Override
    public int getIntrinsicWidth() {
        return mSrc.width();
    }

    @Override
    public int getIntrinsicHeight() {
        return mSrc.height();
    }
}