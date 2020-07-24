package com.dixon.note.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.dixon.dlibrary.util.ScreenUtil;
import com.dixon.note.inter.IDrop;
import com.dixon.note.util.DropHelper;

/**
 * Create by: dixon.xu
 * Create on: 2020.06.19
 * Functional desc: 设置页竖向统一布局
 * 可复用
 */
public class SetLayout extends LinearLayout implements IDrop {

    private DropHelper dropHelper;

    public SetLayout(Context context) {
        super(context);
        init();
    }

    public SetLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SetLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.END);

        dropHelper = new DropHelper(this);
        dropHelper.setDropHeight(0, ScreenUtil.dpToPx(getContext(), 240));
    }

    /**
     * 动态添加功能菜单
     *
     * @param tag                 功能Tag 用于点击回调、响应
     * @param text                功能展示名
     * @param icon                功能icon
     * @param onItemClickListener 点击事件
     */
    public void putItem(final String tag, String text, int icon, final OnItemClickListener onItemClickListener) {
        final SetFunView setFunView = new SetFunView(getContext());
        setFunView.setText(text);
        setFunView.setIcon(icon);
        addView(setFunView);

        setFunView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(tag, setFunView);
            }
        });
    }

    @Override
    public void drop() {
        dropHelper.drop();
    }

    @Override
    public void dropOpen() {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setVisibility(VISIBLE);
        }
        dropHelper.dropOpen();
    }

    @Override
    public void dropClose() {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setVisibility(GONE);
        }
        dropHelper.dropClose();
    }

    @Override
    public boolean isDropOpen() {
        return dropHelper.isDropOpen();
    }

    public boolean isDropClose() {
        return dropHelper.isDropClose();
    }

    @Override
    public boolean isOpening() {
        return dropHelper.isOpening();
    }

    @Override
    public boolean isClosing() {
        return dropHelper.isClosing();
    }

    public interface OnItemClickListener {
        void onItemClick(String tag, View setFunView);
    }
}
