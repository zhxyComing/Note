package com.dixon.note.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.dixon.allbase.fun.SelectChangeManager;
import com.dixon.dlibrary.util.ScreenUtil;
import com.dixon.note.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Create by: dixon.xu
 * Create on: 2020.06.19
 * Functional desc: Menu 功能菜单
 * 可复用
 */
public class MenuLayout extends LinearLayout {

    private Map<String, MenuFunView> tabs = new HashMap<>();

    private SelectChangeManager<MenuFunView> selectChangeManager = new SelectChangeManager<MenuFunView>() {
        @Override
        protected void selected(String tag, MenuFunView obj) {
            obj.setChecked(true);
        }

        @Override
        protected void clearSelected(String tag, MenuFunView obj) {
            obj.setChecked(false);
        }
    };

    public MenuLayout(Context context) {
        super(context);
        init();
    }

    public MenuLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MenuLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER);
        addPlaceHolder();
    }

    /**
     * 动态添加功能菜单
     *
     * @param tag                功能Tag 用于点击回调、响应
     * @param text               功能展示名
     * @param icon               功能icon
     * @param defaultChecked     默认是否选中
     * @param onTabClickListener 点击事件
     */
    public void putMenu(final String tag, String text, int icon, boolean defaultChecked, final OnMenuTabClickListener onTabClickListener) {
        final MenuFunView menuFunView = new MenuFunView(getContext());
        menuFunView.setText(text);
        menuFunView.setIcon(icon);
        menuFunView.setDefaultChecked(defaultChecked);
        addView(menuFunView);
        LinearLayout.LayoutParams layoutParams = (LayoutParams) menuFunView.getLayoutParams();
        layoutParams.width = ScreenUtil.dpToPxInt(getContext(), 40);
        layoutParams.height = LayoutParams.MATCH_PARENT;
        menuFunView.setLayoutParams(layoutParams);

        selectChangeManager.put(tag, menuFunView);
        menuFunView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectChangeManager.setSelected(tag);
                onTabClickListener.onTabClick(tag, menuFunView);
            }
        });
        tabs.put(tag, menuFunView);

        addPlaceHolder();
    }

    /**
     * 添加的一个隐藏的按钮 后续会启用 临时方法
     */
    public void putMenuInvisible(final String tag, String text, int icon, boolean defaultChecked, final OnMenuTabClickListener onTabClickListener) {
        final MenuFunView menuFunView = new MenuFunView(getContext());
        menuFunView.setText(text);
        menuFunView.setIcon(icon);
        menuFunView.setDefaultChecked(defaultChecked);
        addView(menuFunView);
        LinearLayout.LayoutParams layoutParams = (LayoutParams) menuFunView.getLayoutParams();
        layoutParams.width = ScreenUtil.dpToPxInt(getContext(), 50);
        layoutParams.height = LayoutParams.MATCH_PARENT;
        menuFunView.setLayoutParams(layoutParams);

        selectChangeManager.put(tag, menuFunView);
        menuFunView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectChangeManager.setSelected(tag);
                onTabClickListener.onTabClick(tag, menuFunView);
            }
        });

        menuFunView.setVisibility(INVISIBLE);

        addPlaceHolder();
    }

    /**
     * 添加占位符
     */
    private void addPlaceHolder() {
        LayoutInflater.from(getContext()).inflate(R.layout.item_view_place_holder, this, true);
    }

    public interface OnMenuTabClickListener {
        void onTabClick(String tag, View menuFunView);
    }

    public void setTab(String tag) {
        Objects.requireNonNull(tabs.get(tag)).callOnClick();
    }
}
