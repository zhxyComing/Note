package com.dixon.note.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.dixon.dlibrary.util.AnimationUtil;
import com.dixon.note.inter.IDrop;

/**
 * Create by: dixon.xu
 * Create on: 2020.06.22
 * Functional desc: 展开关闭帮助类
 */
public class DropHelper implements IDrop {

    // 展开状态
    public static final int DROP_STATUE_OPEN = 0;
    // 收回（关闭）状态
    public static final int DROP_STATUE_CLOSE = 1;
    // 展开中
    public static final int DROP_STATUE_OPEN_RUN = 2;
    // 关闭中
    public static final int DROP_STATUE_CLOSE_RUN = 3;

    private View mDropView;
    private int mDropStatue;
    private OnDropListener onDropListener;
    private float mOriginHeight, mDropHeight;

    public DropHelper(View dropView, int defaultStatue) {
        mDropView = dropView;
        mDropStatue = defaultStatue;
    }

    public DropHelper(View dropView) {
        this(dropView, DROP_STATUE_CLOSE);
    }

    public void setOnDropListener(OnDropListener onDropListener) {
        this.onDropListener = onDropListener;
    }

    public void setDropHeight(float originHeight, float dropHeight) {
        mOriginHeight = originHeight;
        mDropHeight = dropHeight;
    }

    /**
     * 下拉监听
     */
    public interface OnDropListener {

        void start();

        void finish();
    }

    public static class OnDropListenerAdapter implements OnDropListener {

        @Override
        public void start() {

        }

        @Override
        public void finish() {

        }
    }

    /**
     * 展开、收回
     */
    @Override
    public void drop() {
        if (mDropStatue == DROP_STATUE_OPEN) {
            dropClose();
        } else if (mDropStatue == DROP_STATUE_CLOSE) {
            dropOpen();
        }
    }

    /**
     * 展开
     */
    @Override
    public void dropOpen() {
        mDropStatue = DROP_STATUE_OPEN_RUN;
        AnimationUtil.height(mDropView, mOriginHeight, mDropHeight, 450, new OvershootInterpolator(), new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (onDropListener != null) {
                    onDropListener.start();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mDropStatue = DROP_STATUE_OPEN;
                if (onDropListener != null) {
                    onDropListener.finish();
                }
            }
        }).start();
    }

    public void dropOpenRightNow() {
        mDropStatue = DROP_STATUE_OPEN;
        AnimationUtil.height(mDropView, mOriginHeight, mDropHeight, 0, new OvershootInterpolator(), new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (onDropListener != null) {
                    onDropListener.start();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (onDropListener != null) {
                    onDropListener.finish();
                }
            }
        }).start();
    }

    /**
     * 收回
     */
    @Override
    public void dropClose() {
        mDropStatue = DROP_STATUE_CLOSE_RUN;
        AnimationUtil.height(mDropView, mDropHeight, mOriginHeight, 450, new OvershootInterpolator(), new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (onDropListener != null) {
                    onDropListener.start();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mDropStatue = DROP_STATUE_CLOSE;
                if (onDropListener != null) {
                    onDropListener.finish();
                }
            }
        }).start();
    }

    public void dropCloseRightNow() {
        mDropStatue = DROP_STATUE_CLOSE;
        AnimationUtil.height(mDropView, mDropHeight, mOriginHeight, 0, new OvershootInterpolator(), new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (onDropListener != null) {
                    onDropListener.start();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (onDropListener != null) {
                    onDropListener.finish();
                }
            }
        }).start();
    }

    /**
     * 判断是否展开
     */
    @Override
    public boolean isDropOpen() {
        return mDropStatue == DROP_STATUE_OPEN;
    }

    /**
     * 判断是否收回
     */
    public boolean isDropClose() {
        return mDropStatue == DROP_STATUE_CLOSE;
    }

    @Override
    public boolean isOpening() {
        return mDropStatue == DROP_STATUE_OPEN_RUN;
    }

    @Override
    public boolean isClosing() {
        return mDropStatue == DROP_STATUE_CLOSE_RUN;
    }

}
