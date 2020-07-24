package com.dixon.allbase.base;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.dixon.dlibrary.util.FontUtil;
import com.dixon.dlibrary.util.StatusBarUtil;

/**
 * Create by: dixon.xu
 * Create on: 2020.06.16
 * Functional desc: 基础Activity
 */
public class BaseActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setColorForStatus(this);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        if (overrideFont()) {
            FontUtil.font(getWindow().getDecorView());
        }
    }

    /**
     * 默认重写字体
     */
    protected boolean overrideFont() {
        return true;
    }
}
