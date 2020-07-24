package com.dixon.allbase.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


/**
 * 专门用于电子书继承、定制的Dialog
 */
public abstract class BaseDialog extends Dialog implements View.OnClickListener {

    protected static final int PX_AUTO = 0;

    public BaseDialog(Context context) {
        super(context);
    }

    public BaseDialog(Context context, int themeResId) {
        super(context, themeResId);
    }


    // 返回content_view id
    protected abstract int contentView();

    protected abstract void findView();

    protected abstract boolean isCancelOnOutSide();

    protected abstract int widthPx();

    protected abstract int heightPx();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(contentView());
        findView();
        setCanceledOnTouchOutside(isCancelOnOutSide());

        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.CENTER;
            if (widthPx() != PX_AUTO) {
                lp.width = widthPx();
            }
            if (heightPx() != PX_AUTO) {
                lp.height = heightPx();
            }
            window.setAttributes(lp);
            if (windowAnimStyle() != 0) {
                window.setWindowAnimations(windowAnimStyle());
            }
        }
    }

    protected int windowAnimStyle() {
        return 0;
    }
}
