package com.dixon.note.launch;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.dixon.allbase.base.BaseActivity;
import com.dixon.allbase.model.RouterConstant;
import com.dixon.note.R;
import com.dixon.simple.router.api.SimpleRouter;
import com.dixon.simple.router.core.SRouter;

/**
 * Create by: dixon.xu
 * Create on: 2020.06.16
 * Functional desc: 启动页
 */
@SimpleRouter(value = RouterConstant.LAUNCH, interceptor = "")
public class LaunchActivity extends BaseActivity {

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SRouter.build(LaunchActivity.this, RouterConstant.NOTE_TABLE).execute();
                finish();
            }
        }, 1500);
    }
}