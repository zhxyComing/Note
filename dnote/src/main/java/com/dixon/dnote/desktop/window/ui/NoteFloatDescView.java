package com.dixon.dnote.desktop.window.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dixon.allbase.bean.NoteBean;
import com.dixon.allbase.model.RouterConstant;
import com.dixon.dlibrary.util.AnimationUtil;
import com.dixon.dlibrary.util.SharedUtil;
import com.dixon.dlibrary.util.ToastUtil;
import com.dixon.dnote.R;
import com.dixon.dnote.core.NoteService;
import com.dixon.dnote.desktop.window.NoteFloatService;
import com.dixon.dnote.desktop.window.TouchControl;
import com.dixon.dnote.desktop.window.ui.base.FloatContent;
import com.dixon.simple.router.core.SRouter;

/**
 * Create by: dixon.xu
 * Create on: 2020.07.21
 * Functional desc: 笔记悬浮窗内容View
 * <p>
 * 悬浮窗内容View 有以下功能
 * 1.笔记描述
 * 2.笔记跳转
 * 3.悬浮窗关闭
 * 4.悬浮窗尺寸三段式大小重设
 */
public class NoteFloatDescView extends FloatContent {

    private static final String STATUE_DESC_FLOAT = "statue_desc_float";

    private static final int STATUE_STANDARD = 0; // 标准状态 宽高均为SIZE_BIG（180）
    private static final int STATUE_SECONDARY = 1; // 次级状态 宽高均为SIZE_NORMAL（100）
    private static final int STATUE_SMALL = 2; // 小化状态 宽为SIZE_NORMAL（100） 高为SIZE_SMALL（26）

    private static final int SIZE_BIG = 160; // 标准尺寸
    private static final int SIZE_NORMAL = 100; // 次级尺寸
    private static final int SIZE_SMALL = 26; // 最小尺寸

    private int mCurrentStatue;
    private int mWidth, mHeight; // 实际宽高 和状态有关

    private TouchControl touchControl;

    private ViewGroup mContentView;
    private TextView mDescView;

    public NoteFloatDescView(@NonNull Context context) {
        super(context);
        init();
    }

    public NoteFloatDescView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NoteFloatDescView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        restoreStatue();
        setBackgroundColor(getResources().getColor(R.color.colorWhiteGrey));
        LayoutInflater.from(getContext()).inflate(R.layout.note_float_ui_desc, this, true);
        touchControl = new TouchControl();
        touchControl.setOnControlListener(onControlListener);
        initView();
    }

    private void initView() {
        mContentView = findViewById(R.id.note_ll_float_desc_content);
        // 设置内容
        mDescView = findViewById(R.id.note_tv_float_desc_content);
        NoteBean floatData = NoteService.getInstance().getFloatData();
        if (floatData != null) {
            mDescView.setText(floatData.getContent());
        }
        // 设置点击最小化
        View smallerView = findViewById(R.id.note_tv_float_desc_small);
        smallerView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                resize();
            }
        });
        // 设置关闭按钮
        View closeView = findViewById(R.id.note_tv_float_desc_close);
        closeView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteFloatService.getInstance().dismiss();
            }
        });
        // 跳转到笔记页
        View goView = findViewById(R.id.note_tv_float_desc_go);
        goView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEdit();
            }
        });
    }

    private void startEdit() {
        NoteBean floatData = NoteService.getInstance().getFloatData();
        if (floatData == null) {
            ToastUtil.toast("尚未设置悬浮窗笔记");
            return;
        }
        SRouter.build(getContext(), RouterConstant.NOTE_EDIT)
                .addParams("update_data", floatData)
                .execute();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return touchControl.onTouchEvent(event);
    }

    private TouchControl.OnControlListener onControlListener = new TouchControl.OnControlListener() {

        // 滑动
        @Override
        public void onTouchMove(int offsetX, int offsetY) {
            // 移动悬浮窗
            NoteFloatService.getInstance().move(offsetX, offsetY);
        }

        // 点击
        @Override
        public void onClick() {

        }
    };

    @Override
    public int widthDp() {
        return mWidth;
    }

    @Override
    public int heightDp() {
        return mHeight;
    }

    @Override
    public void onRefresh() {
        NoteBean floatData = NoteService.getInstance().getFloatData();
        if (floatData != null) {
            mDescView.setText(floatData.getContent());
        } else {
            mDescView.setText("");
        }
    }

    /**
     * 保存状态信息
     */
    public void saveStatue() {
        SharedUtil.putInt(STATUE_DESC_FLOAT, mCurrentStatue);
    }

    /**
     * 恢复状态
     */
    public void restoreStatue() {
        mCurrentStatue = SharedUtil.getInt(STATUE_DESC_FLOAT, STATUE_STANDARD);
        // 标准宽高 默认标准
        if (mCurrentStatue == STATUE_STANDARD) {
            mWidth = SIZE_BIG;
            mHeight = SIZE_BIG;
            // 次级宽高
        } else if (mCurrentStatue == STATUE_SECONDARY) {
            mWidth = SIZE_NORMAL;
            mHeight = SIZE_NORMAL;
            // 最小化状态
        } else if (mCurrentStatue == STATUE_SMALL) {
            mWidth = SIZE_NORMAL;
            mHeight = SIZE_SMALL;
            setAlpha(0.3f);
        }
    }

    /**
     * 根据当前尺寸重设尺寸
     */
    private void resize() {
        switch (mCurrentStatue) {
            case STATUE_STANDARD:
                resizeOriginToSecondary();
                mCurrentStatue = STATUE_SECONDARY;
                break;
            case STATUE_SECONDARY:
                resizeSecondaryToSmall();
                mCurrentStatue = STATUE_SMALL;
                break;
            case STATUE_SMALL:
                resizeSmallToOrigin();
                mCurrentStatue = STATUE_STANDARD;
                break;
        }
        saveStatue();
    }

    /**
     * 从原始尺寸到次级尺寸
     */
    private void resizeOriginToSecondary() {
        ValueAnimator animator = ValueAnimator.ofInt(SIZE_BIG, SIZE_NORMAL);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                NoteFloatService.getInstance().resize(value, value);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mContentView.setVisibility(VISIBLE);
                AnimationUtil.alpha(mContentView, 0, 1, 150, null, null).start();
            }
        });
        mContentView.setVisibility(GONE);
        animator.start();
    }

    /**
     * 从次级尺寸到最小尺寸
     */
    private void resizeSecondaryToSmall() {
        ValueAnimator animator = ValueAnimator.ofInt(SIZE_NORMAL, SIZE_SMALL);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                // 宽度不变 只变高度
                NoteFloatService.getInstance().resize(SIZE_NORMAL, value);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mContentView.setVisibility(VISIBLE);
                // 最小化透明度变小
                AnimationUtil.alpha(NoteFloatDescView.this, 1, 0.3f).start();
            }
        });
        mContentView.setVisibility(GONE);
        animator.start();
    }

    /**
     * 从最小化尺寸到原始尺寸
     */
    private void resizeSmallToOrigin() {
        NoteFloatService.getInstance().resize(SIZE_BIG, SIZE_BIG);
        setAlpha(1);
    }
}
