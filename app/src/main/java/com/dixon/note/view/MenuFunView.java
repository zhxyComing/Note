package com.dixon.note.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
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
 * Functional desc: Home页底部菜单栏功能组件 支持点击态和普通态
 * 可复用
 */
public class MenuFunView extends LinearLayout {

    private ImageView iconView;
    private TextView textView;

    private boolean curChecked;
    private Animator curAnimator;

    private int mScrollDistance = 0;

    public MenuFunView(Context context) {
        super(context);
        init();
    }

    public MenuFunView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MenuFunView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER);
        LayoutInflater.from(getContext()).inflate(R.layout.item_menu_fun, this, true);
        findView();

        mScrollDistance = -ScreenUtil.dpToPxInt(getContext(), 10);
    }

    private void findView() {
        textView = findViewById(R.id.app_menu_fun_content);
        iconView = findViewById(R.id.app_menu_fun_icon);
    }

    private void init(AttributeSet attrs) {
        init();

        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.MenuFunView);
        String text = ta.getString(R.styleable.MenuFunView_menu_text);
        int iconId = ta.getResourceId(R.styleable.MenuFunView_menu_icon, -1);
        boolean defaultChecked = ta.getBoolean(R.styleable.MenuFunView_menu_checked, false);
        ta.recycle();

        // 设置UI
        setText(text);
        setIcon(iconId);
        setCheckedStatue(defaultChecked);
    }

    public void setText(String text) {
        textView.setText(text);
    }

    public void setIcon(int id) {
        iconView.setImageResource(id);
    }

    public void setDefaultChecked(boolean isChecked) {
        setCheckedStatue(isChecked);
    }

    /**
     * 根据选中状态 立即切换UI
     */
    private void setCheckedStatue(boolean isChecked) {
        if (isChecked) {
            iconView.setVisibility(VISIBLE);
            textView.setVisibility(VISIBLE);
        } else {
            iconView.setVisibility(VISIBLE);
            textView.setVisibility(GONE);
        }
        curChecked = isChecked;
    }

    /**
     * 单个功能键选中动画
     * <p>
     * 选中 则icon上移，文字出现
     * 未选中 则icon下移，文字隐藏
     * <p>
     * 这块后续看怎么优化一下
     */
    public void setChecked(boolean isChecked) {
        // 当前没选中 但是要求选中
        if (!curChecked && isChecked) {
            if (curAnimator != null && curAnimator.isRunning()) {
                curAnimator.cancel();
            }
            curAnimator = AnimationUtil.tranY(iconView, 0, mScrollDistance, 300, null, new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    // 回归原位 要不位置会突变 这里代码不够优雅 后续修改吧
                    AnimationUtil.tranY(iconView, mScrollDistance, 0, 0, null, null).start();
                    AnimationUtil.alpha(textView, 0, 1, 150, null, null).start();
                    textView.setVisibility(VISIBLE);
                }
            });
            curAnimator.start();
        } else if (curChecked && !isChecked) {
            if (curAnimator != null && curAnimator.isRunning()) {
                curAnimator.cancel();
            }
            // 当前选中了 但是要求不选中
            textView.setVisibility(GONE);
            curAnimator = AnimationUtil.tranY(iconView, mScrollDistance, 0, 300, null, new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    // 回归原位 要不位置会突变 这里代码不够优雅 后续修改吧
                    AnimationUtil.tranY(iconView, mScrollDistance, 0, 0, null, null).start();
                }
            });
            curAnimator.start();
        }
        curChecked = isChecked;
    }
}
