package com.dixon.dnote.desktop.window;

import android.view.MotionEvent;

/**
 * Create by: dixon.xu
 * Create on: 2020.07.20
 * Functional desc: 触摸监控
 * <p>
 * 功能
 * 1.响应点击
 * 2.响应滑动
 */
public class TouchControl {

    private OnControlListener onControlListener;

    private int lastX, lastY;
    private boolean singleClickTag; // 判断此次touch是不是只是点击而不是move

    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 记录触摸点坐标
                lastX = x;
                lastY = y;
                singleClickTag = true;
                break;
            case MotionEvent.ACTION_MOVE:
                // 计算偏移量
                int offsetX = x - lastX;
                int offsetY = y - lastY;
                // 回调偏移量
                if (onControlListener != null) {
                    onControlListener.onTouchMove(offsetX, offsetY);
                }
                lastX = x;
                lastY = y;
                singleClickTag = false;
                break;
            case MotionEvent.ACTION_UP:
                if (singleClickTag && onControlListener != null) {
                    onControlListener.onClick();
                }
                break;
        }
        return true;
    }

    public void setOnControlListener(OnControlListener onControlListener) {
        this.onControlListener = onControlListener;
    }

    public interface OnControlListener {
        // 滑动了x、y的距离
        void onTouchMove(int offsetX, int offsetY);

        // 仅仅是点击
        void onClick();
    }
}
