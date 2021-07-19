package com.example.cnicreader;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class ImageCanvas extends View {

    Paint paint;

    public ImageCanvas(Context context) {
        super(context);
        paint = new Paint();
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);

        canvas.drawRect(0,0,getWidth(),getHeight(),paint);


    }
}
