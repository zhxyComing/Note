package com.dixon.dnote.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.dixon.dnote.desktop.window.TouchControl;

/**
 * Create by: dixon.xu
 * Create on: 2020.07.23
 * Functional desc: 触摸反馈View 三角形
 */
public class TouchBackView extends View {

    private Paint mPaint;
    private Point pointA, pointB, pointC;

    private TouchControl touchControl;
    private OnTouchBackListener onTouchBackListener;

    public TouchBackView(Context context) {
        super(context);
        init();
    }

    public TouchBackView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TouchBackView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        touchControl = new TouchControl();
        touchControl.setOnControlListener(new TouchControl.OnControlListener() {
            @Override
            public void onTouchMove(int offsetX, int offsetY) {
                if (onTouchBackListener != null) {
                    onTouchBackListener.onTouch(offsetX, offsetY);
                }
            }

            @Override
            public void onClick() {

            }
        });
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#CFCFCF"));

        pointA = new Point();
        pointB = new Point();
        pointC = new Point();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        pointA.set(getMeasuredWidth(), getMeasuredHeight()); // 三角形右下角
        pointB.set(getMeasuredWidth(), 0); // 三角形右上角
        pointC.set(0, getMeasuredHeight()); // 三角形左下角
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            touchControl.onTouchEvent(event);
            if (onTouchBackListener != null) {
                onTouchBackListener.onTouchFinish();
            }
            return true;
        }
        return touchControl.onTouchEvent(event);
    }

    public interface OnTouchBackListener {

        void onTouch(int offsetX, int offsetY);

        void onTouchFinish();
    }

    public void setOnTouchBackListener(OnTouchBackListener onTouchBackListener) {
        this.onTouchBackListener = onTouchBackListener;
    }

    // 绘制三角形背景
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        @SuppressLint("DrawAllocation")
        Path path = new Path();
        path.moveTo(pointA.x, pointA.y);
        path.lineTo(pointB.x, pointB.y);
        path.lineTo(pointC.x, pointC.y);
        path.close();//闭环
        canvas.drawPath(path, mPaint);
    }
}
