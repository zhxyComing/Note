package com.dixon.allbase.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.dixon.allbase.fun.TimeFormat;
import com.dixon.dlibrary.util.ScreenUtil;

/**
 * Create by: dixon.xu
 * Create on: 2020.06.23
 * Functional desc: 临时 一个简单的时间View 可以根据Time展示对应指针
 */
public class TimeView extends View {

    private Paint mPaint;
    private long time;
    private int radius, radiusX, radiusY;
    private int insideCircleDp;
    private int hour, minute;
    private int hourAngle, minuteAngle;

    private int hourXOnCircle, hourYOnCircle, minuteXOnCircle, minuteYOnCircle;

    public TimeView(Context context) {
        super(context);
        init();
    }

    public TimeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        initData();
    }

    public void setTime(long time) {
        this.time = time;
        hour = TimeFormat.hour(time) % 12;
        minute = TimeFormat.minute(time);
        hourAngle = 360 / 12 * hour - 90;
        minuteAngle = 360 / 60 * minute - 90;
        requestLayout(); // measure layout
        postInvalidate(); // draw
    }

    private void initData() {
        insideCircleDp = ScreenUtil.dpToPxInt(getContext(), 2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getMeasuredWidth() > getMeasuredHeight()) {
            radius = getMeasuredHeight() / 2;
        } else {
            radius = getMeasuredWidth() / 2;
        }
        radiusX = getMeasuredWidth() / 2;
        radiusY = getMeasuredHeight() / 2;

        // 计算坐标
        hourXOnCircle = getXOnCircle(hourAngle, radius - ScreenUtil.dpToPxInt(getContext(), 6));
        hourYOnCircle = getYOnCircle(hourAngle, radius - ScreenUtil.dpToPxInt(getContext(), 6));
        minuteXOnCircle = getXOnCircle(minuteAngle, radius - ScreenUtil.dpToPxInt(getContext(), 3));
        minuteYOnCircle = getYOnCircle(minuteAngle, radius - ScreenUtil.dpToPxInt(getContext(), 3));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setColor(Color.parseColor("#F5F5F5"));
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(radiusX, radiusY, radius, mPaint);

        mPaint.setColor(Color.parseColor("#D9D9D9"));
        canvas.drawCircle(radiusX, radiusY, insideCircleDp, mPaint);

        mPaint.setStrokeWidth(3);
        canvas.drawLine(radiusX, radiusY, hourXOnCircle, hourYOnCircle, mPaint);
        mPaint.setStrokeWidth(2);
        canvas.drawLine(radiusX, radiusY, minuteXOnCircle, minuteYOnCircle, mPaint);

        mPaint.setColor(Color.parseColor("#FFFFFF"));
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(radiusX, radiusY, radius, mPaint);
    }

    public int getXOnCircle(int angle, int radius) {
        return (int) (radiusX + radius * Math.cos(angle * 3.14 / 180));
    }

    public int getYOnCircle(int angle, int radius) {
        return (int) (radiusY + radius * Math.sin(angle * 3.14 / 180));
    }

    public long getTime() {
        return time;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }
}
