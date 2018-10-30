package com.physics_2d_demo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.physics_2d_demo.MxTest.Phys2DManager;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/*
 * @author Stefan Wyszyński 2009 przeniesiona do androida z J2ME
 */
public class SimpleDrawingView extends View {
    private final int paintColor = Color.BLACK;
    private Paint drawPaint;
    private Disposable subscribe;

    private Phys2DManager polygonsScene;
    private long timebefore = 0, timeafter = 0;

    public void startRendering() {
        if (subscribe == null) {
            timebefore = System.currentTimeMillis();
            subscribe = Observable.interval(1, 1, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        postInvalidate();
                    });
        }
    }

    public void stopRendering() {
        if (subscribe != null && !subscribe.isDisposed()) {
            subscribe.dispose();
            subscribe = null;
        }
    }

    public SimpleDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setupPaint();
    }

    private void setupPaint() {
        // Setup paint with color and stroke styles
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(5);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        try {
            polygonsScene = new Phys2DManager(w, h);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        timeafter = System.currentTimeMillis() - timebefore;
        timebefore = System.currentTimeMillis();

        if (polygonsScene != null) {
            polygonsScene.gameUpdate((float) timeafter / 1000);
            polygonsScene.render(canvas, drawPaint);
        }
    }

    private float x, y;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float pointX = event.getX();
        float pointY = event.getY();

        // Checks for the event that occurs
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = pointX;
                y = pointY;
                return true;
            case MotionEvent.ACTION_MOVE:
                float x = (pointX - this.x) * 100f;
                float y = (pointY - this.y) * 100f;
                polygonsScene.movePoly(0, x, y);
                break;
            default:
                return false;
        }
        return true;
    }

}