package com.dixon.dnote.desktop.window;

import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.WindowManager;

import com.dixon.dlibrary.util.AppTracker;
import com.dixon.dlibrary.util.Ln;
import com.dixon.dlibrary.util.ScreenUtil;
import com.dixon.dlibrary.util.SharedUtil;
import com.dixon.dnote.desktop.window.ui.base.FloatContent;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Create by: dixon.xu
 * Create on: 2020.07.20
 * Functional desc: 悬浮窗容器 控制悬浮窗的展示
 * <p>
 * 主要用于控制悬浮窗的展示和隐藏、大小、位置等坐标信息
 * <p>
 * 悬浮窗对照编辑的使用场景并不是很实用：
 * 1.对于短文字，可以直接记住，打开App编辑，对照输入不是很必要；
 * 2.对于中长文字，直接复制粘贴了。
 * 所以不如设置WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE，没有EditText
 * 要编辑则点击跳转到App内进行编辑，需要一个独立的编辑页，方便退出（桌面小部件也跳此编辑页）
 * 说实话，之前的沉浸式效果还是不错的，但是Android的缺陷，使用同步式的实现实在是...
 */
public class FloatContainer {

    private static final String POSITION_X = "px";
    private static final String POSITION_Y = "py";

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    // 默认宽高 px
    private int mWidth, mHeight;
    // 默认初始坐标
    private int mX, mY;

    private FloatContent mContent;
    private boolean hasShow;

    public FloatContainer() {
        initParams();
    }

    /**
     * 初始化参数
     */
    private void initParams() {
        mWindowManager = (WindowManager) AppTracker.getCurApplication().getSystemService(WINDOW_SERVICE);
        mWidth = ScreenUtil.dpToPxInt(AppTracker.getCurApplication(), 50);
        mHeight = ScreenUtil.dpToPxInt(AppTracker.getCurApplication(), 50);
        mX = SharedUtil.getInt(POSITION_X, 300);
        mY = SharedUtil.getInt(POSITION_Y, 300);
    }

    /**
     * 设置内容View
     */
    public void setContent(FloatContent content) {
        mWidth = ScreenUtil.dpToPxInt(AppTracker.getCurApplication(), content.widthDp());
        mHeight = ScreenUtil.dpToPxInt(AppTracker.getCurApplication(), content.heightDp());
        // 移除旧视图
        if (hasShow && mContent != null) {
            mWindowManager.removeView(mContent);
        }
        mContent = content;
        hasShow = false; // 重置展示状态
    }

    /**
     * 显示悬浮窗
     */
    public void show() {
        if (mContent == null) {
            throw new NullPointerException("Content page has not been set");
        }
        // 已经展示过 不再重复展示
        if (hasShow) {
            return;
        }
        // WindowManager初始化失败 不展示
        if (mWindowManager == null) {
            return;
        }
        mLayoutParams = new WindowManager.LayoutParams();
        // 悬浮窗权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.width = mWidth;
        mLayoutParams.height = mHeight;
        mLayoutParams.gravity = Gravity.TOP | Gravity.START; // 以左上角为坐标原点 符合View
        mLayoutParams.x = mX;
        mLayoutParams.y = mY;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // 会导致输入法弹不出来 因为没焦点
        // 将悬浮窗控件添加到WindowManager
        mWindowManager.addView(mContent, mLayoutParams);
        hasShow = true;
    }

    /**
     * 隐藏悬浮窗
     */
    public void dismiss() {
        // 如果未展示或者内容为空 则直接忽略
        if (!canRun()) {
            return;
        }
        mWindowManager.removeView(mContent);
        hasShow = false;
        // 保存位置信息
        savePositionInfo();
    }

    /**
     * 更新并保存位置信息
     */
    private void savePositionInfo() {
        SharedUtil.putInt(POSITION_X, mX);
        SharedUtil.putInt(POSITION_Y, mY);
    }

    /**
     * 刷新内容
     */
    public void refreshContent() {
        if (!canRun()) {
            return;
        }
        mContent.onRefresh();
    }

    public boolean hasShow() {
        return hasShow;
    }

    /**
     * 在原基础上移动悬浮窗
     * <p>
     * 当移动距离超出屏幕范围时，不再响应
     */
    public void scrollBy(int offsetX, int offsetY) {
        if (!canRun()) {
            return;
        }
        mLayoutParams.x = mLayoutParams.x + offsetX;
        mLayoutParams.y = mLayoutParams.y + offsetY;
        // 限定x、y的取值范围
        if (mLayoutParams.x < 0) {
            mLayoutParams.x = 0;
        } else if (mLayoutParams.x > ScreenUtil.getDisplayWidth(AppTracker.getCurApplication())) {
            mLayoutParams.x = ScreenUtil.getDisplayWidth(AppTracker.getCurApplication());
        }
        if (mLayoutParams.y < 0) {
            mLayoutParams.y = 0;
        } else if (mLayoutParams.y > ScreenUtil.getDisplayHeight(AppTracker.getCurApplication())) {
            mLayoutParams.y = ScreenUtil.getDisplayHeight(AppTracker.getCurApplication());
        }
        Ln.i("FloatScroll", "x: " + mLayoutParams.x + " y: " + mLayoutParams.y);
        // 更新坐标
        mWindowManager.updateViewLayout(mContent, mLayoutParams);

        mX = mLayoutParams.x;
        mY = mLayoutParams.y;
    }

    /**
     * 重设位置
     * <p>
     * 当移动距离超出屏幕范围时，不再响应
     */
    public void scrollTo(int x, int y) {
        if (!canRun()) {
            return;
        }
        mLayoutParams.x = x;
        mLayoutParams.y = y;
        // 限定x、y的取值范围
        if (mLayoutParams.x < 0) {
            mLayoutParams.x = 0;
        } else if (mLayoutParams.x > ScreenUtil.getDisplayWidth(AppTracker.getCurApplication())) {
            mLayoutParams.x = ScreenUtil.getDisplayWidth(AppTracker.getCurApplication());
        }
        if (mLayoutParams.y < 0) {
            mLayoutParams.y = 0;
        } else if (mLayoutParams.y > ScreenUtil.getDisplayHeight(AppTracker.getCurApplication())) {
            mLayoutParams.y = ScreenUtil.getDisplayHeight(AppTracker.getCurApplication());
        }
        Ln.i("FloatScroll", "x: " + mLayoutParams.x + " y: " + mLayoutParams.y);
        // 更新坐标
        mWindowManager.updateViewLayout(mContent, mLayoutParams);

        mX = mLayoutParams.x;
        mY = mLayoutParams.y;
    }

    /**
     * 重设悬浮窗尺寸
     *
     * @param widthDp  宽度dp
     * @param heightDp 高度dp
     */
    public void resize(int widthDp, int heightDp) {
        if (!canRun()) {
            return;
        }
        if (mWidth == widthDp && mHeight == heightDp) {
            // 相同尺寸 不更新
            return;
        }
        mWidth = ScreenUtil.dpToPxInt(AppTracker.getCurApplication(), widthDp);
        mHeight = ScreenUtil.dpToPxInt(AppTracker.getCurApplication(), heightDp);
        mLayoutParams.width = mWidth;
        mLayoutParams.height = mHeight;
        // updateViewLayout在动态设置悬浮窗宽高时 不可避免的会卡...原因未知
        mWindowManager.updateViewLayout(mContent, mLayoutParams);
    }

    /**
     * 通过判断逻辑是否可往下执行 一般来说悬浮窗未展示、内容未设置、windowManager获取失败都不能继续执行
     */
    private boolean canRun() {
        // 如果未展示或者内容为空 则直接忽略
        if (!hasShow || mContent == null) {
            return false;
        }
        if (mWindowManager == null) {
            return false;
        }
        return true;
    }

    /**
     * 获取悬浮窗当前宽度
     */
    public int getWidth() {
        return mWidth;
    }

    /**
     * 获取悬浮窗当前高度
     */
    public int getHeight() {
        return mHeight;
    }

    /**
     * 获取悬浮窗当前X
     */
    public int getX() {
        return mX;
    }

    /**
     * 获取悬浮窗当前Y
     */
    public int getY() {
        return mY;
    }
}
