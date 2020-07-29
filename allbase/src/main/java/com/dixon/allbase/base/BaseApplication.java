package com.dixon.allbase.base;

import android.app.Application;

import com.dixon.dlibrary.BuildConfig;
import com.dixon.dlibrary.util.DUtil;
import com.dixon.simple.router.core.SRouter;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

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
        initUM();
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

    /**
     * 初始化友盟
     */
    private void initUM() {
        UMConfigure.init(this, "5f213bffb4b08b653e8f5fb3", "未设置", UMConfigure.DEVICE_TYPE_PHONE, "");
        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
    }
}
