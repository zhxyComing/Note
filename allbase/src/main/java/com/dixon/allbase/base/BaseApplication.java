package com.dixon.allbase.base;

import android.app.Application;

import com.dixon.dlibrary.BuildConfig;
import com.dixon.dlibrary.util.DUtil;
import com.dixon.simple.router.core.SRouter;

/**
 * Create by: dixon.xu
 * Create on: 2020.06.16
 * Functional desc: 基础Application
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initDUtil();
        SRouter.init(this);
    }

    /**
     * 初始化工具集框架
     */
    private void initDUtil() {
        DUtil.init(this);
        if (BuildConfig.DEBUG) {
            DUtil.setDebug(true);
        }
        DUtil.setSharedPreference(this, "dixon.note");
        DUtil.setDefaultFont("Yun-Book.ttf");
    }
}
