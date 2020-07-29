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
    private Point pointBottomRight, pointTopRight, pointBottomLeft, pointTopLeft;

    private TouchControl touchControl;
    private OnTouchBackListener onTouchBackListener;

    public static final int DIRECTION_TOP_LEFT = 0;
    public static final int DIRECTION_TOP_RIGHT = 1;
    public static final int DIRECTION_BOTTOM_LEFT = 2;
    public static final int DIRECTION_BOTTOM_RIGHT = 3;

    private int direction;

    private boolean singleClickTag; // 判断此次touch是不是只是点击而不是move

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

    public void setDirection(int direction) {
        this.direction = direction;
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
                if (onTouchBackListener != null) {
                    onTouchBackListener.onClick();
                }
            }
        });
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#CFCFCF"));

        pointBottomRight = new Point();
        pointTopRight = new Point();
        pointBottomLeft = new Point();
        pointTopLeft = new Point();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        pointBottomRight.set(getMeasuredWidth(), getMeasuredHeight()); // 右下
        pointTopRight.set(getMeasuredWidth(), 0); // 右上
        pointBottomLeft.set(0, getMeasuredHeight()); // 左下
        pointTopLeft.set(0, 0); // 左上
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                singleClickTag = true;
                break;
            case MotionEvent.ACTION_MOVE:
                singleClickTag = false;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touchControl.onTouchEvent(event);
                if (onTouchBackListener != null && !singleClickTag) {
                    onTouchBackListener.onTouchFinish();
                }
                return true;
        }
        return touchControl.onTouchEvent(event);
    }

    public interface OnTouchBackListener {

        void onTouch(int offsetX, int offsetY);

        void onTouchFinish();

        void onClick();
    }

    public static class OnSimpleTouchBackListener implements OnTouchBackListener {

        @Override
        public void onTouch(int offsetX, int offsetY) {

        }

        @Override
        public void onTouchFinish() {

        }

        @Override
        public void onClick() {

        }
    }

    public void setOnTouchBackListener(OnTouchBackListener onTouchBackListener) {
        this.onTouchBackListener = onTouchBackListener;
    }

    // 绘制三角形背景
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        @SuppressLint("DrawAllocation")
//        Path path = new Path();
        switch (direction) {
            case DIRECTION_BOTTOM_LEFT:
//                path.moveTo(pointTopLeft.x, pointTopLeft.y);
//                path.lineTo(pointBottomLeft.x, pointBottomLeft.y);
//                path.lineTo(pointBottomRight.x, pointBottomRight.y);
                canvas.drawCircle(getMeasuredWidth() / 3f, getMeasuredHeight() / 3f * 2f, getMeasuredWidth() / 12f, mPaint);
                break;
            case DIRECTION_BOTTOM_RIGHT:
//                path.moveTo(pointBottomRight.x, pointBottomRight.y);
//                path.lineTo(pointTopRight.x, pointTopRight.y);
//                path.lineTo(pointBottomLeft.x, pointBottomLeft.y);
                canvas.drawCircle(getMeasuredWidth() / 3f * 2f, getMeasuredHeight() / 3f * 2f, getMeasuredWidth() / 12f, mPaint);
                break;
            case DIRECTION_TOP_LEFT:
//                path.moveTo(pointTopLeft.x, pointTopLeft.y);
//                path.lineTo(pointBottomLeft.x, pointBottomLeft.y);
//                path.lineTo(pointTopRight.x, pointTopRight.y);
                canvas.drawCircle(getMeasuredWidth() / 3f, getMeasuredHeight() / 3f, getMeasuredWidth() / 12f, mPaint);
                break;
            case DIRECTION_TOP_RIGHT:
//                path.moveTo(pointTopLeft.x, pointTopLeft.y);
//                path.lineTo(pointTopRight.x, pointTopRight.y);
//                path.lineTo(pointBottomRight.x, pointBottomRight.y);
                canvas.drawCircle(getMeasuredWidth() / 3f * 2, getMeasuredHeight() / 3f, getMeasuredWidth() / 12f, mPaint);
                break;
        }
//        path.close();//闭环
//        canvas.drawPath(path, mPaint);
    }
}
