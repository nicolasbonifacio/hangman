package com.nick.hangman.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.nick.hangman.R;
import com.nick.hangman.TaleActivityFragment;

/**
 * Created by Nick on 2016-04-24.
 */
public class CategoryView extends View {

    int mColor100;
    int mColor200;
    int mColor500;
    int mColorA700;
    int mPerc;
    int mId;

    Paint paint;
    RectF r;
    ImageView d;

    Context mContext;

    public CategoryView(Context context, int id, int perc, int color100, int color200, int color500, int colorA700) {
        super(context);
        mColor100 = color100;
        mColor200 = color200;
        mColor500 = color500;
        mColorA700 = colorA700;
        mPerc = perc;
        paint = new Paint();
        r = new RectF();
        mId = id;
        mContext = context;

        System.out.println("wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float centerX = canvas.getWidth()/2;
        float centerY = canvas.getHeight()/2;
        System.out.println("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
        System.out.println(centerX+" - "+centerY);
        //fundo do percentual não completado
        paint.setColor(mColor200); //200
        r.set((centerX-200), (centerY-200), (centerX+200), (centerY+200));
        canvas.drawArc(r, 0, 360, true, paint);



        //percentual completado
        paint.setColor(mColorA700); //A700
        r.set((centerX-215), (centerY-215), (centerX+215), (centerY+215));
        canvas.drawArc(r, -90, (360*mPerc)/100, true, paint);

        //fundo do percentual não completado 2
        paint.setColor(mColor200); //200
        r.set((centerX-175), (centerY-175), (centerX+175), (centerY+175));
        canvas.drawArc(r, 0, 360, true, paint);

        //circulo central
        paint.setColor(mColor500); //500
        r.set((centerX-170), (centerY-170), (centerX+170), (centerY+170));
        canvas.drawArc(r, 0, 360, true, paint);


        paint.setColor(mColor100); //100
        paint.setTextSize(60);
        paint.setFakeBoldText(true);
        canvas.drawText(Integer.toString(mPerc)+"%", 0, Integer.toString(mPerc).length()+1, centerX-50, centerY+30, paint);



    }

}
