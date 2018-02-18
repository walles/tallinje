package com.gmail.walles.johan.tallinje;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class NumberLine extends View {
    private Paint numbersPaint;

    public NumberLine(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);

        numbersPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        numbersPaint.setColor(Color.BLACK);
        numbersPaint.setTextSize(100);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // FIXME: Pre-compute stuff for onDraw(), see:
        // https://developer.android.com/training/custom-views/custom-drawing.html#layouteevent
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawText(Long.toString(System.currentTimeMillis()), 100f, 100f, numbersPaint);
    }
}
