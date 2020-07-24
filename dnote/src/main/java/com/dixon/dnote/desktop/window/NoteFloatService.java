package com.dixon.dnote.desktop.window;

import com.dixon.allbase.fun.FloatWindowHelper;
import com.dixon.dlibrary.util.AppTracker;
import com.dixon.dlibrary.util.SharedUtil;
import com.dixon.dnote.core.NoteConstant;
import com.dixon.dnote.desktop.window.ui.NoteFloatClassicView;
import com.dixon.dnote.desktop.window.ui.NoteFloatDescView;
import com.dixon.dnote.desktop.window.ui.NoteFloatSimpleView;

/**
 * Create by: dixon.xu
 * Create on: 2020.07.14
 * Functional desc: 桌面悬浮窗服务
 * <p>
 * 规则：
 * 1.同一时刻只能至多拥有一个悬浮窗
 */
public class NoteFloatService {

    private static NoteFloatService instance;

    // 控制悬浮窗的展示 只是个容器 不涉及实际内容
    private FloatContainer mContainer;

    private NoteFloatService() {
        mContainer = new FloatContainer();
    }

    public static NoteFloatService getInstance() {
        if (instance == null) {
            synchronized (NoteFloatService.class) {
                if (instance == null) {
                    instance = new NoteFloatService();
                }
            }
        }
        return instance;
    }

    /**
     * 对外接口 展示悬浮窗
     */
    public void showFloatWindow() {
        if (!FloatWindowHelper.hasPermission()) {
            // 没有权限 去申请权限
            // todo 展示申请权限弹窗
            return;
        }
        // 悬浮窗View
        showNoteFloatWindow();
    }

    /**
     * 展示笔记悬浮窗
     */
    private void showNoteFloatWindow() {
        // 悬浮窗组件不应该依赖任何activity 所以这里context使用application
        // 根据设置使用不同悬浮窗内容
        switch (SharedUtil.getString(NoteConstant.SP_FLOAT_STYLE, NoteConstant.FLOAT_STYLE_CLASSIC)) {
            case NoteConstant.FLOAT_STYLE_CLASSIC:
                mContainer.setContent(new NoteFloatClassicView(AppTracker.getCurApplication()));
                break;
            case NoteConstant.FLOAT_STYLE_SIMPLE:
                mContainer.setContent(new NoteFloatSimpleView(AppTracker.getCurApplication()));
                break;
            case NoteConstant.FLOAT_STYLE_DESC:
                mContainer.setContent(new NoteFloatDescView(AppTracker.getCurApplication()));
        }
        mContainer.show();
    }

    /**
     * 在原基础上位移
     */
    public void move(int xPx, int yPx) {
        if (!mContainer.hasShow()) {
            return;
        }
        mContainer.scrollBy(xPx, yPx);
    }

    /**
     * 重设位置
     */
    public void moveTo(int xPx, int yPx) {
        if (!mContainer.hasShow()) {
            return;
        }
        mContainer.scrollTo(xPx, yPx);
    }

    /**
     * 重设尺寸
     */
    public void resize(int widthDp, int heightDp) {
        if (!mContainer.hasShow()) {
            return;
        }
        mContainer.resize(widthDp, heightDp);
    }

    /**
     * 隐藏悬浮窗
     */
    public void dismiss() {
        if (!mContainer.hasShow()) {
            return;
        }
        mContainer.dismiss();
    }

    /**
     * 刷新悬浮窗内容
     */
    public void refresh() {
        if (!mContainer.hasShow()) {
            return;
        }
        mContainer.refreshContent();
    }

    /**
     * 悬浮窗是否展示了
     */
    public boolean hasShow() {
        return mContainer.hasShow();
    }

    /**
     * 获取悬浮窗当前宽度
     */
    public int getWidth() {
        if (!mContainer.hasShow()) {
            return 0;
        }
        return mContainer.getWidth();
    }

    public int getHeight() {
        if (!mContainer.hasShow()) {
            return 0;
        }
        return mContainer.getHeight();
    }

    public int getX() {
        if (!mContainer.hasShow()) {
            return 0;
        }
        return mContainer.getX();
    }

    public int getY() {
        if (!mContainer.hasShow()) {
            return 0;
        }
        return mContainer.getY();
    }
}
