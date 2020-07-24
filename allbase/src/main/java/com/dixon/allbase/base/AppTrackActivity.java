package com.dixon.allbase.base;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.dixon.dlibrary.util.AppTracker;

/**
 * Create by: dixon.xu
 * Create on: 2020.06.16
 * Functional desc: App前后台状态监控Activity 桌签常用
 */
public abstract class AppTrackActivity extends BaseActivity implements AppTracker.AppStateChangeListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppTracker.register(this);
    }
}
