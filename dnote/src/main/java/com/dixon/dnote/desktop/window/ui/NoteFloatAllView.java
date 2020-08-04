package com.dixon.dnote.desktop.window.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dixon.allbase.model.RouterConstant;
import com.dixon.dlibrary.util.AnimationUtil;
import com.dixon.dlibrary.util.HandlerUtil;
import com.dixon.dlibrary.util.ScreenUtil;
import com.dixon.dnote.R;
import com.dixon.dnote.desktop.window.NoteFloatService;
import com.dixon.dnote.desktop.window.TouchControl;
import com.dixon.dnote.desktop.window.ui.base.FloatContent;
import com.dixon.simple.router.core.SRouter;

/**
 * Create by: dixon.xu
 * Create on: 2020.07.14
 * Functional desc: 笔记悬浮窗内容View 悬浮窗应通过外部控制实现 内容则可以随时替换
 * <p>
 * 一个简单的悬浮窗View 展示全部笔记
 */
public class NoteFloatAllView extends FloatContent {

    private static final int WIDTH_DP = 24;
    private static final int HEIGHT_DP = 64;

    private TouchControl touchControl;

    public NoteFloatAllView(@NonNull Context context) {
        super(context);
        init();
    }

    public NoteFloatAllView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NoteFloatAllView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.note_float_ui_all_in, this, true);
        touchControl = new TouchControl();
        touchControl.setOnControlListener(onControlListener);
        initView();
    }

    private void initView() {
        // 贴边
        runSlideAnimator();
        // 调低透明度
        HandlerUtil.runOnUiThreadDelayed(new Runnable() {
            @Override
            public void run() {
                AnimationUtil.alpha(NoteFloatAllView.this, 0.9f, 0.3f).start();
            }
        }, 3000);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if ((event.getAction() == MotionEvent.ACTION_UP
                || event.getAction() == MotionEvent.ACTION_CANCEL)) {
            // 如果在小化状态移动了 自动贴边
            runSlideAnimator();
        }
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
            // 跳转到All页 是个背景透明页面
            SRouter.build(getContext(), RouterConstant.NOTE_ALL).execute();
        }
    };

    @Override
    public int widthDp() {
        return WIDTH_DP;
    }

    @Override
    public int heightDp() {
        return HEIGHT_DP;
    }

    @Override
    public void onRefresh() {
        // 不需要刷新
    }

    /**
     * 运行悬浮窗贴边动画
     */
    private void runSlideAnimator() {
        int curX = NoteFloatService.getInstance().getX();
        int midX = ScreenUtil.getDisplayWidth(getContext()) / 2;
        if (curX < midX) {
            // 左贴边
            ValueAnimator valueAnimator = ValueAnimator.ofInt(curX, 0);
            valueAnimator.setDuration(300);
            valueAnimator.setInterpolator(new DecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    NoteFloatService.getInstance().moveTo(value, NoteFloatService.getInstance().getY());
                }
            });
            valueAnimator.start();
        } else {
            // 右贴边
            ValueAnimator valueAnimator = ValueAnimator.ofInt(curX, ScreenUtil.getDisplayWidth(getContext()));
            valueAnimator.setDuration(300);
            valueAnimator.setInterpolator(new DecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    NoteFloatService.getInstance().moveTo(value, NoteFloatService.getInstance().getY());
                }
            });
            valueAnimator.start();
        }
    }
}
