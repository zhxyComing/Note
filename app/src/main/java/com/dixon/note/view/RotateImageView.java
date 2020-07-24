package com.dixon.note.view;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;

import com.dixon.dlibrary.util.AnimationUtil;
import com.dixon.dlibrary.view.CircleImageView;

/**
 * Create by: dixon.xu
 * Create on: 2020.06.19
 * Functional desc:
 * 可旋转的ImageView
 * 用于Home页DropImageView 下拉完成时旋转180度 回归时继续旋转180度回到原位
 * 可复用
 */
public class RotateImageView extends CircleImageView {

    private int curAngle;
    private Animator curAnimator;
    private static final int RUN_TIME = 300;

    public RotateImageView(Context context) {
        super(context);
    }

    public RotateImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RotateImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 在当前基础上继续旋转xx度
     */
    public void rotateBy(int angle) {
        if (curAnimator != null && curAnimator.isRunning()) {
            curAnimator.cancel();
        }
        curAnimator = AnimationUtil.rotate(this, curAngle, curAngle += angle, RUN_TIME, new DecelerateInterpolator(), null);
        curAnimator.start();
    }
}
