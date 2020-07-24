package com.dixon.dnote.desktop.window.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dixon.allbase.bean.NoteBean;
import com.dixon.allbase.model.RouterConstant;
import com.dixon.dlibrary.util.AnimationUtil;
import com.dixon.dlibrary.util.ScreenUtil;
import com.dixon.dlibrary.util.SharedUtil;
import com.dixon.dlibrary.util.ToastUtil;
import com.dixon.dnote.R;
import com.dixon.dnote.core.NoteService;
import com.dixon.dnote.desktop.window.NoteFloatService;
import com.dixon.dnote.desktop.window.TouchControl;
import com.dixon.dnote.desktop.window.ui.base.FloatContent;
import com.dixon.dnote.view.TouchBackView;
import com.dixon.simple.router.core.SRouter;

/**
 * Create by: dixon.xu
 * Create on: 2020.07.14
 * Functional desc: 笔记悬浮窗内容View
 * <p>
 * 经典悬浮窗 提供经典功能 并做了加强
 */
public class NoteFloatClassicView extends FloatContent {

    // 用于保存当前状态
    private static final String SP_CLASSIC_STATUE = "classic_statue";
    // 用于保存当前宽高
    private static final String SP_CLASSIC_SIZE_WIDTH = "classic_size_width";
    private static final String SP_CLASSIC_SIZE_HEIGHT = "classic_size_height";

    // 用于区分当前状态
    private static final int STATUE_STANDARD = 0;
    private static final int STATUE_SMALL = 1;

    private static final int DEFAULT_STANDARD_WIDTH_DP = 180;
    private static final int DEFAULT_STANDARD_HEIGHT_DP = 180;

    private static final int DEFAULT_SMALL_WIDTH_DP = 27;
    private static final int DEFAULT_SMALL_HEIGHT_DP = 92;

    private TouchControl touchControl;

    private TouchBackView mSizeView;
    private EditText mContentView;
    private View mSmallLayout, mStandardLayout;

    private int mStatue;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public NoteFloatClassicView(@NonNull Context context) {
        super(context);
        init();
    }

    public NoteFloatClassicView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NoteFloatClassicView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.note_float_ui_classic, this, true);
        touchControl = new TouchControl();
        touchControl.setOnControlListener(onControlListener);
        findView();
        initView();
    }

    private void findView() {
        mSizeView = findViewById(R.id.note_tbv_float_classic_resize);
        mContentView = findViewById(R.id.note_net_float_classic_content);
        mStandardLayout = findViewById(R.id.note_cv_float_classic_standard_layout);
        mSmallLayout = findViewById(R.id.note_cv_float_classic_small_layout);
    }

    private void initView() {
        // 重设尺寸的监听
        mSizeView.setOnTouchBackListener(new ResizeTouchListener());
        // 设置内容
        setContentText();
        // 内容点击跳转
        mContentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteBean floatData = NoteService.getInstance().getFloatData();
                if (floatData == null) {
                    ToastUtil.toast("尚未设置悬浮窗笔记");
                    return;
                }
                // 跳转到笔记编辑页
                SRouter.build(getContext(), RouterConstant.NOTE_EDIT)
                        .addParams("update_data", floatData)
                        .execute();
            }
        });
        // 关闭按钮
        findViewById(R.id.note_tv_float_classic_close).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteFloatService.getInstance().dismiss();
            }
        });
        // 小化按钮
        findViewById(R.id.note_tv_float_classic_small).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToSmall();
            }
        });
    }

    private void setContentText() {
        NoteBean floatData = NoteService.getInstance().getFloatData();
        if (floatData != null) {
            mContentView.setText(floatData.getContent());
        } else {
            mContentView.setText("");
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if ((event.getAction() == MotionEvent.ACTION_UP
                || event.getAction() == MotionEvent.ACTION_CANCEL)
                && mStatue == STATUE_SMALL) {
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
            // 小化状态点击则回归原状态
            if (mStatue == STATUE_SMALL) {
                changeToStandard();
            }
        }
    };

    @Override
    public int widthDp() {
        return SharedUtil.getInt(SP_CLASSIC_SIZE_WIDTH, DEFAULT_STANDARD_WIDTH_DP);
    }

    @Override
    public int heightDp() {
        return SharedUtil.getInt(SP_CLASSIC_SIZE_HEIGHT, DEFAULT_STANDARD_HEIGHT_DP);
    }

    @Override
    public void onRefresh() {
        setContentText();
    }

    // 通过滑动重设尺寸
    private class ResizeTouchListener implements TouchBackView.OnTouchBackListener {

        @Override
        public void onTouch(int offsetX, int offsetY) {
            int widthPx = NoteFloatService.getInstance().getWidth() + offsetX;
            int heightPx = NoteFloatService.getInstance().getHeight() + offsetY;
            NoteFloatService.getInstance().resize(ScreenUtil.pxToDpInt(getContext(), widthPx),
                    ScreenUtil.pxToDpInt(getContext(), heightPx));
        }

        @Override
        public void onTouchFinish() {
            // 位置由悬浮窗来保存 因为不管内容如何 位置总是统一的
            // 大小由内容来保存 因为内容决定了大小尺寸
            saveStandardSize(ScreenUtil.pxToDpInt(getContext(), NoteFloatService.getInstance().getWidth()),
                    ScreenUtil.pxToDpInt(getContext(), NoteFloatService.getInstance().getHeight()));
        }
    }

    /**
     * 切换到小化模式
     */
    private void changeToSmall() {
        AnimationUtil.alpha(mStandardLayout, 0.9f, 0, 300, null, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mStatue = STATUE_SMALL;
                // 隐藏标准视图 显示小化视图
                mStandardLayout.setVisibility(GONE);
                mSmallLayout.setVisibility(VISIBLE);
                mSmallLayout.setAlpha(0.9f);
                // 小化视图显示动画
                AnimationUtil.height(mSmallLayout, 0, ScreenUtil.dpToPxInt(getContext(), DEFAULT_SMALL_HEIGHT_DP), 300, null, new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        // 高度变完后自动贴边 延迟0.3s
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                runSlideAnimator();
                            }
                        }, 300);
                    }
                }).start();
                // 重设悬浮窗宽高
                NoteFloatService.getInstance().resize(DEFAULT_SMALL_WIDTH_DP, DEFAULT_SMALL_HEIGHT_DP);
                // 过3s重设透明度
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AnimationUtil.alpha(mSmallLayout, 0.9f, 0.3f).start();
                    }
                }, 3000);
            }
        }).start();
    }

    /**
     * 切换到标准模式
     */
    private void changeToStandard() {
//        mSmallLayout.setAlpha(0.9f);
        AnimationUtil.height(mSmallLayout, ScreenUtil.dpToPxInt(getContext(), DEFAULT_SMALL_HEIGHT_DP), 0, 300, null, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mStatue = STATUE_STANDARD;
                // 隐藏标准视图 显示小化视图
                mStandardLayout.setVisibility(VISIBLE);
                mSmallLayout.setVisibility(GONE);
                // 小化视图显示动画
                AnimationUtil.alpha(mStandardLayout, 0, 0.9f).start();
                // 重设悬浮窗宽高
                int[] size = getStandardSize();
                NoteFloatService.getInstance().resize(size[0], size[1]);
                // 从右边展出时 位置会错位 所以要重新调整位置
                NoteFloatService.getInstance().move(-ScreenUtil.dpToPxInt(getContext(), size[0]), 0);
                // 取消其它动画任务
                mHandler.removeMessages(0);
            }
        }).start();
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

    /**
     * 缓存标准状态宽高信息
     */
    private void saveStandardSize(int widthDp, int heightDp) {
        SharedUtil.putInt(SP_CLASSIC_SIZE_WIDTH, widthDp);
        SharedUtil.putInt(SP_CLASSIC_SIZE_HEIGHT, heightDp);
    }

    /**
     * 获取标准状态宽高信息
     */
    private int[] getStandardSize() {
        int width = SharedUtil.getInt(SP_CLASSIC_SIZE_WIDTH, DEFAULT_STANDARD_WIDTH_DP);
        int height = SharedUtil.getInt(SP_CLASSIC_SIZE_HEIGHT, DEFAULT_STANDARD_HEIGHT_DP);
        return new int[]{width, height};
    }
}
