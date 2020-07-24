package com.dixon.note.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.dixon.dlibrary.util.AnimationUtil;
import com.dixon.dlibrary.util.ScreenUtil;
import com.dixon.note.R;

/**
 * Create by: dixon.xu
 * Create on: 2020.06.19
 * Functional desc: 竖向菜单Item
 * 可复用
 */
public class SetFunView extends FrameLayout {

    private ImageView iconView;
    private TextView textView;
    private FrameLayout layout;

    public SetFunView(Context context) {
        super(context);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.item_set_fun, this, true);
        findView();
    }

    private void findView() {
        textView = findViewById(R.id.app_set_fun_content);
        iconView = findViewById(R.id.app_set_fun_icon);
        layout = findViewById(R.id.app_set_fun_layout);
    }


    public void setText(String text) {
        textView.setText(text);
    }

    public void setIcon(int id) {
        iconView.setImageResource(id);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener listener) {
//        super.setOnClickListener(l);
        layout.setOnClickListener(listener);
    }
}
