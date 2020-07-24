package com.dixon.dnote.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

import com.dixon.dlibrary.util.ScreenUtil;

/**
 * Create by: dixon.xu
 * Create on: 2020.07.23
 * Functional desc: 带横线的EditText
 * <p>
 * 注意不能使用androidx.appcompat.widget.AppCompatEditText 会导致cursor不显示
 */
public class NoteLineEditText extends androidx.appcompat.widget.AppCompatEditText {

    private Paint mPaint;

    public NoteLineEditText(Context context) {
        super(context);
        init();
    }

    public NoteLineEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NoteLineEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(18);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int size = getLineHeight(); //  行高 不包括文字高度

        //直接绘制150行 以防超出
        for (int i = 0; i < 150; i++) {
            mPaint.setColor(Color.parseColor("#BCAAA4"));
            canvas.drawLine(0f + toPx(4),
                    size * (i + 1) + toPx(3) * i + toPx(2), // 这个多出来的间距不知道是什么 后期研究吧
                    getMeasuredWidth() - toPx(4),
                    size * (i + 1) + toPx(3) * i + toPx(2),
                    mPaint);
            mPaint.setColor(Color.parseColor("#E0E0E0"));
            canvas.drawText(String.valueOf(i + 1),
                    getMeasuredWidth() - toPx(6),
                    size * (i + 1) + toPx(3) * i - 4,
                    mPaint);
        }
    }

    private int toPx(int dp) {
        return ScreenUtil.dpToPxInt(getContext(), dp);
    }
}
