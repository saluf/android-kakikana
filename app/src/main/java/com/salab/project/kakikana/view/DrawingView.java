package com.salab.project.kakikana.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.salab.project.kakikana.R;

/**
 * A customized View that user can draw on. The image is cached with a Bitmap file.
 * Ref: https://developers.google.com/codelabs/advanced-android-kotlin-training-canvas#0
 * Ref: https://guides.codepath.com/android/Basic-Painting-with-Views#simple-drawing-with-canvas
 */

public class DrawingView extends View {

    private static final String TAG = DrawingView.class.getSimpleName();

    // load setting from resources
    private final int paintColor = getResources().getColor(R.color.paintColor, null);
    private final int backgroundColor = getResources().getColor(R.color.canvasColor, null);
    private final int strokeWidth = (int) getResources().getDimension(R.dimen.stroke_width_drawing_view);

    // paint used to set up style and color
    private Paint drawPaint;
    // path is responsible for drawing according to user touch event
    private Path path = new Path();

    // cache drawing in Bitmap, and also return drawing instantly
    private Bitmap cachedBitmap;
    private Canvas cachedCanvas;
    // record current location
    private float currentX = 0f;
    private float currentY = 0f;

    // constructors
    public DrawingView(Context context) {
        super(context);
    }

    public DrawingView(Context context, @Nullable AttributeSet attrs) {
        // get called when inflating xml
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setupPaint();
    }

    public DrawingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DrawingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    // every time view changes in size including initialization
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (cachedBitmap != null) {
            // clean up Bitmap cached in memory
            cachedBitmap.recycle();
        }

        // cache all drawing in Bitmap
        cachedBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        cachedCanvas = new Canvas(cachedBitmap);
        cachedCanvas.drawColor(backgroundColor);

    }

    // Setup paint with color and stroke styles
    private void setupPaint() {
        drawPaint = new Paint();

        // make it smoother
        drawPaint.setAntiAlias(true);

        // stoke styles
        drawPaint.setColor(paintColor);
        drawPaint.setStrokeWidth(strokeWidth);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // get touched coordinates
        float pointX = event.getX();
        float pointY = event.getY();

        // Checks for the event that occurs
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                motionDown(pointX, pointY);
                break;
            case MotionEvent.ACTION_MOVE:
                motionMove(pointX, pointY);
                break;
            case MotionEvent.ACTION_UP:
                motionUp();
            default:
                return false;
        }
        return true;
    }

    private void motionUp() {
        // touch ends, all drawing should be on the cache Bitmap, so clean it up
        path.reset();
    }

    private void motionMove(float pointX, float pointY) {
        // connect points by smoother curve.
        path.quadTo(currentX, currentY, (pointX + currentX) / 2, (pointY + currentY) / 2);

        // move to touched point
        currentX = pointX;
        currentY = pointY;

        // draw on cached bitmap
        cachedCanvas.drawPath(path, drawPaint);
        // ask system to redraw this view
        invalidate();

//        Log.d(TAG, "Touch event (action move): " + pointX + "," + pointY);
    }

    private void motionDown(float pointX, float pointY) {
        // move path to touched point but will not draw anything by single touch event
        path.moveTo(pointX, pointY);

        currentX = pointX;
        currentY = pointY;

//        Log.d(TAG, "Touch event (action down): " + pointX + "," + pointY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 0, 0 means the bitmap left-upper corner to align that corner of this view object
        canvas.drawBitmap(cachedBitmap, 0f, 0f, drawPaint);
//        Log.d(TAG, "draw");
    }

    public void clear() {
        cachedCanvas.drawColor(backgroundColor);
        invalidate();
//        Log.d(TAG, "cleared");
    }

    public Bitmap getCachedBitmap() {
        return cachedBitmap;
    }
}
