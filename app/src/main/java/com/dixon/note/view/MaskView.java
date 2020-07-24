package com.dixon.note.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.dixon.dlibrary.util.AnimationUtil;

/**
 * Create by: dixon.xu
 * Create on: 2020.06.22
 * Functional desc: 蒙版View 目前有bug...
 */
public class MaskView extends View {

    private static final int STATUE_SHOW = 1;
    private static final int STATUE_HIDE = 0;
    private static final int STATUE_SHOWING = 2;
    private static final int STATUE_HIDING = 3;

    private int statue = STATUE_HIDE;
    private Animator curAnimator;

    public MaskView(Context context) {
        super(context);
        init();
    }

    public MaskView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MaskView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 默认隐藏
        setAlpha(0);
        setVisibility(GONE);
    }

    public void show() {
        if (statue == STATUE_SHOW || statue == STATUE_SHOWING) {
            return;
        }
        setVisibility(VISIBLE);
        if (curAnimator != null) {
            curAnimator.cancel();
        }
//        statue = STATUE_SHOWING;
        curAnimator = AnimationUtil.alpha(this, 0, 1, 300, null, new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                statue = STATUE_SHOWING;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                statue = STATUE_SHOW;
                setVisibility(VISIBLE);
            }
        });
        curAnimator.start();
    }

    public void hide() {
        if (statue == STATUE_HIDE || statue == STATUE_HIDING) {
            return;
        }
        if (curAnimator != null) {
            curAnimator.cancel();
        }
        curAnimator = AnimationUtil.alpha(this, 1, 0, 300, null, new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                statue = STATUE_HIDING;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                statue = STATUE_HIDE;
                setVisibility(GONE);
            }
        });
        curAnimator.start();
    }
}
