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
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.text.NumberFormat;
import java.util.Locale;

public class NumberLine extends View implements GestureDetector.OnGestureListener {
    private Paint numbersPaint;
    private double centerCoordinate = 0.0;
    private double coordinatesPerDecimeter = 10.0;

    private final GestureDetector gestureDetector;

    private static final NumberFormat numberFormat =
            NumberFormat.getNumberInstance(Locale.getDefault());

    public NumberLine(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        numbersPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        numbersPaint.setColor(Color.BLACK);
        numbersPaint.setTextSize(100);
        numbersPaint.setTextAlign(Paint.Align.CENTER);

        gestureDetector = new GestureDetector(context, this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    static String formatToPrecision(double number, double step) {
        double roundedToStep = Math.round(number / step) * step;
        return numberFormat.format((long)roundedToStep);
    }

    private void drawNumbers(Canvas canvas) {
        double step = 1.0;

        Rect clipBounds = canvas.getClipBounds();
        int widthPixels = clipBounds.right - clipBounds.left;
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
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

            canvas.drawText(formatToPrecision(x, step), (float)pixelX, (float)pixelY, numbersPaint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawText(Long.toString(System.currentTimeMillis()), 100f, 100f, numbersPaint);

        drawNumbers(canvas);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float pixelsX, float pixelsY) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        double pixelsPerMm = displayMetrics.xdpi / 25.4;
        double pixelsPerDm = pixelsPerMm * 100.0;
        double coordinatesX = (pixelsX / pixelsPerDm) * coordinatesPerDecimeter;
        centerCoordinate += coordinatesX;
        invalidate();

        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
