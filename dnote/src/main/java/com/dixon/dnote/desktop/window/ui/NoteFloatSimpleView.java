package com.dixon.dnote.desktop.window.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dixon.allbase.bean.NoteBean;
import com.dixon.allbase.model.RouterConstant;
import com.dixon.dlibrary.util.ToastUtil;
import com.dixon.dnote.R;
import com.dixon.dnote.core.NoteService;
import com.dixon.dnote.desktop.window.NoteFloatService;
import com.dixon.dnote.desktop.window.TouchControl;
import com.dixon.dnote.desktop.window.ui.base.FloatContent;
import com.dixon.simple.router.core.SRouter;

/**
 * Create by: dixon.xu
 * Create on: 2020.07.14
 * Functional desc: 笔记悬浮窗内容View 悬浮窗应通过外部控制实现 内容则可以随时替换
 * <p>
 * 一个简单的悬浮窗View 提供点击跳转到笔记的功能
 */
public class NoteFloatSimpleView extends FloatContent {

    private static final int WIDTH_DP = 50;
    private static final int HEIGHT_DP = 50;

    private TouchControl touchControl;

    public NoteFloatSimpleView(@NonNull Context context) {
        super(context);
        init();
    }

    public NoteFloatSimpleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NoteFloatSimpleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.note_float_ui_simple, this, true);
        touchControl = new TouchControl();
        touchControl.setOnControlListener(onControlListener);
        initView();
    }

    private void initView() {

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
            NoteBean floatData = NoteService.getInstance().getFloatData();
            // 跳转到笔记页
            // getContext拿到的是application，要想启动activity，需要设置FLAG_ACTIVITY_NEW_TASK
            // SRouter会判断，如果context为application，则会附加该flag
            SRouter.build(getContext(), RouterConstant.NOTE_EDIT)
                    .addParams("update_data", floatData)
                    .execute();
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
}
