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
import android.view.ScaleGestureDetector;
import android.view.View;

import java.text.NumberFormat;
import java.util.Locale;

public class NumberLine extends View implements
        GestureDetector.OnGestureListener,
        ScaleGestureDetector.OnScaleGestureListener
{
    private static final double MAX_STEPS_PER_DECIMETER = 13.0;
    private static final double MIN_STEPS_PER_DECIMETER = 5.0;
    private static final float NUMBERS_HEIGHT_MM = 5;
    private static final float LINE_HEIGHT_MM = 1.5f;
    private static final float TICKLINE_THICKNESS_MM = 0.5f;

    private final Paint numbersPaint;
    private final Paint linePaint;
    private final Paint tickLinePaint;
    private final Paint minorTickLinePaint;

    private double centerCoordinate = 0.0;
    private double coordinatesPerDecimeter = 10.0;
    private double step = 1.0;

    private final GestureDetector gestureDetector;
    private final ScaleGestureDetector scaleGestureDetector;

    private static final NumberFormat numberFormat =
            NumberFormat.getNumberInstance(Locale.getDefault());

    public NumberLine(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        numbersPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        numbersPaint.setColor(Color.BLACK);
        numbersPaint.setTextSize(mmToPx(NUMBERS_HEIGHT_MM));
        numbersPaint.setTextAlign(Paint.Align.CENTER);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(mmToPx(LINE_HEIGHT_MM));

        tickLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tickLinePaint.setColor(Color.BLACK);
        tickLinePaint.setStrokeWidth(mmToPx(TICKLINE_THICKNESS_MM));

        minorTickLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        minorTickLinePaint.setColor(Color.DKGRAY);
        minorTickLinePaint.setStrokeWidth(mmToPx(TICKLINE_THICKNESS_MM * 0.5f));

        gestureDetector = new GestureDetector(context, this);
        scaleGestureDetector = new ScaleGestureDetector(context, this);
    }

    private float mmToPx(double mm) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, (float)mm, displayMetrics);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = scaleGestureDetector.onTouchEvent(event);
        handled = gestureDetector.onTouchEvent(event) || handled;

        return handled || super.onTouchEvent(event);
    }

    static int countDecimals(double number) {
        String roundedString = String.format(Locale.ENGLISH,"%s", number);
        int count = roundedString.length() - roundedString.indexOf('.') - 1;
        if (count < 10) {
            return count;
        }

        // Maybe "0.600000000000001"?
        // Remove last digit
        roundedString = roundedString.substring(0, roundedString.length() - 1);
        // Remove trailing zeroes
        roundedString = roundedString.replaceAll("0*$", "");
        // Try again
        return roundedString.length() - roundedString.indexOf('.') - 1;
    }

    static String formatToPrecision(double number, double step) {
        double roundedToStep = Math.round(number / step) * step;

        if (step >= 1.0) {
            return numberFormat.format((long)roundedToStep);
        } else {
            numberFormat.setMaximumFractionDigits(countDecimals(roundedToStep));

            return numberFormat.format(roundedToStep);
        }
    }

    static int getStepDigit(double step) {
        if (step >= 1.0) {
            return Integer.parseInt(Long.toString((long)step).substring(0, 1));
        }

        String stepString = Double.toString(step);
        return Integer.parseInt(stepString.substring(stepString.length() - 1));
    }

    private void adjustStep() {
        while (coordinatesPerDecimeter / step > MAX_STEPS_PER_DECIMETER) {
            step = increaseStep(step);
        }

        while (coordinatesPerDecimeter / step < MIN_STEPS_PER_DECIMETER) {
            step = decreaseStep(step);
        }
    }

    static double decreaseStep(double step) {
        int stepDigit = getStepDigit(step);

        if (stepDigit == 1) {
            return step / 2.0;
        }

        if (stepDigit == 2) {
            return step / 2.0;
        }

        return 2.0 * step / 5.0;
    }

    static double increaseStep(double step) {
        if (getStepDigit(step) == 2) {
            return step * (5.0 / 2.0);
        } else {
            return step * 2.0;
        }
    }

    private void drawMinorTicks(Canvas canvas) {
        Scaler scaler = new Scaler(canvas);
        for (double x = scaler.leftmostLabelCoordinate; x <= scaler.rightmostLabelCoordinate; x += scaler.minorStep) {
            double pixelX = scaler.xToPixels(x);
            double pixelY = scaler.pixelCenterY;

            canvas.drawLine(
                    (float)pixelX, (float)(pixelY + numbersPaint.getTextSize() * 0.7),
                    (float)pixelX, (float)(pixelY + numbersPaint.getTextSize() * 1.3),
                    minorTickLinePaint);
        }
    }

    private void drawMajorTicks(Canvas canvas) {
        Scaler scaler = new Scaler(canvas);
        for (double x = scaler.leftmostMajorTickCoordinate;
                x <= scaler.rightmostMajorTickCoordinate;
                x += scaler.majorStep)
        {
            double pixelX = scaler.xToPixels(x);
            double pixelY = scaler.pixelCenterY;

            canvas.drawLine(
                    (float)pixelX, (float)(pixelY + numbersPaint.getTextSize() * 0.5),
                    (float)pixelX, (float)(pixelY + numbersPaint.getTextSize() * 1.5),
                    tickLinePaint);
        }
    }

    private void drawNumbers(Canvas canvas) {
        Scaler scaler = new Scaler(canvas);
        for (double x = scaler.leftmostLabelCoordinate; x <= scaler.rightmostLabelCoordinate; x += step) {
            // Draw this number

            double pixelX = scaler.xToPixels(x);
            double pixelY = scaler.pixelCenterY;

            canvas.drawText(formatToPrecision(x, step), (float)pixelX, (float)pixelY, numbersPaint);
        }
    }

    private void drawLine(Canvas canvas) {
        Scaler scaler = new Scaler(canvas);

        float y = scaler.pixelCenterY;
        y += numbersPaint.getTextSize();

        canvas.drawLine(scaler.clipBounds.left, y, scaler.clipBounds.right, y, linePaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (canvas.getWidth() == 0 || canvas.getHeight() == 0) {
            // This happens while Android Studio tries to preview us and leads to an infinite loop
            // if we don't catch it. leftmostLabelCoordinate and rightmostLabelCoordinate both become negative and positive Infinity if the
            // width is zero.
            return;
        }

        adjustStep();

        drawMinorTicks(canvas);
        drawMajorTicks(canvas);
        drawLine(canvas);

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

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        coordinatesPerDecimeter /= detector.getScaleFactor();
        invalidate();

        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        // This method intentionally left blank
    }

    private class Scaler {
        public final Rect clipBounds;

        /**
         * Leftmost label coordinate, outside of the screen.
         */
        public final double leftmostLabelCoordinate;

        /**
         * Rightmost label coordinate, outside of the screen.
         */
        public final double rightmostLabelCoordinate;

        public final double minorStep;

        public final double majorStep;
        public final double leftmostMajorTickCoordinate;
        public final double rightmostMajorTickCoordinate;

        /**
         * Screen midpoint Y in pixels.
         */
        public final float pixelCenterY;

        /**
         * Leftmost screen edge in coordinates.
         */
        private final double leftCoordinate;

        /**
         * Screen width in coordinates.
         */
        private final double widthCoordinates;

        public Scaler(Canvas canvas) {
            clipBounds = canvas.getClipBounds();

            DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
            double widthMm = clipBounds.width() / TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1, displayMetrics);
            widthCoordinates = coordinatesPerDecimeter / (widthMm / 100.0);

            leftCoordinate = centerCoordinate - (widthCoordinates / 2);
            double rightCoordinate = centerCoordinate + (widthCoordinates / 2);

            // Compute the first coordinate outside of the screen on the left
            leftmostLabelCoordinate = Math.floor(leftCoordinate / step) * step;

            // Compute the first coordinate outside of the screen to the right
            rightmostLabelCoordinate = Math.ceil(rightCoordinate / step) * step;

            minorStep = getMinorStep(step);
            majorStep = minorStep * 10.0;
            leftmostMajorTickCoordinate = Math.floor(leftCoordinate / majorStep) * majorStep;
            rightmostMajorTickCoordinate = Math.ceil(rightCoordinate / majorStep) * majorStep;

            // FIXME: Use *view* top and bottom, not *clip* top and bottom. Using the clip bounds
            // will fail if we ever get to do a partial redraw.
            pixelCenterY = (clipBounds.top + clipBounds.bottom) / 2;
        }

        public double xToPixels(double x) {
            return clipBounds.left + clipBounds.width() * ((x - leftCoordinate) / widthCoordinates);
        }

        private double getMinorStep(double step) {
            int stepDigit = getStepDigit(step);
            switch (stepDigit) {
                case 1:
                    return step / 10.0;
                case 2:
                    return step / 2.0;
                case 5:
                    return step / 5.0;
                default:
                    throw new IllegalArgumentException("Step: " + step + ", step digit: " + stepDigit);
            }
        }

    }
}
