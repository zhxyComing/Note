package com.dixon.dnote.desktop.window.ui.base;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Create by: dixon.xu
 * Create on: 2020.07.21
 * Functional desc: 悬浮窗内容物基类
 * <p>
 * 不使用XML限定宽高 原因1是太分散 原因2是有的布局需要自适应宽高可能会变
 */
public abstract class FloatContent extends FrameLayout {

    public FloatContent(@NonNull Context context) {
        super(context);
    }

    public FloatContent(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatContent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * content 应当描述自己的初始宽高 以供悬浮窗设置尺寸
     * <p>
     * 不能自适应 因为悬浮窗不知道自适应的尺寸是多少 毕竟没有父级窗口
     */
    public abstract int widthDp();

    public abstract int heightDp();

    public abstract void onRefresh();
}
