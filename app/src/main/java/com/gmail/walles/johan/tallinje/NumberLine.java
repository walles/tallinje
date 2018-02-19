package com.gmail.walles.johan.tallinje;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

public class NumberLine extends View {
    private Paint numbersPaint;
    private double centerCoordinate = 0.0;
    private double coordinatesPerDecimeter = 10.0;
    private final static DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();

    public NumberLine(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        numbersPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        numbersPaint.setColor(Color.BLACK);
        numbersPaint.setTextSize(100);
        numbersPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // FIXME: Pre-compute stuff for onDraw(), see:
        // https://developer.android.com/training/custom-views/custom-drawing.html#layouteevent
    }

    private void drawNumbers(Canvas canvas) {
        double step = 1.0;

        Rect clipBounds = canvas.getClipBounds();
        int widthPixels = clipBounds.right - clipBounds.left;
        double widthMm = widthPixels / TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1, displayMetrics);
        double widthCoordinates = coordinatesPerDecimeter / (widthMm / 100.0);

        double leftCoordinate = centerCoordinate - (widthCoordinates / 2);
        double rightCoordinate = centerCoordinate + (widthCoordinates / 2);

        // Compute the first coordinate outside of the screen on the left
        double x0 = Math.floor(leftCoordinate / step) * step;

        // Compute the first coordinate outside of the screen to the right
        double x1 = Math.ceil(rightCoordinate / step) * step;

        for (double x = x0; x <= x1; x += step) {
            // Draw this number

            // Convert x to pixels
            double pixelX = clipBounds.left + widthPixels * ((x - leftCoordinate) / widthCoordinates);

            // FIXME: Use *view* top and bottom, not *clip* top and bottom. Using the clip bounds
            // will fail if we ever get to do a partial redraw.
            double pixelY = (clipBounds.top + clipBounds.bottom) / 2;

            canvas.drawText(Double.toString(x),
                    (float)pixelX, (float)pixelY, numbersPaint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawText(Long.toString(System.currentTimeMillis()), 100f, 100f, numbersPaint);

        drawNumbers(canvas);
    }
}
