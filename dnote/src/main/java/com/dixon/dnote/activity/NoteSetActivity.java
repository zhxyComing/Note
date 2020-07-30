package com.dixon.dnote.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dixon.allbase.base.BaseActivity;
import com.dixon.allbase.model.RouterConstant;
import com.dixon.dlibrary.util.SharedUtil;
import com.dixon.dnote.R;
import com.dixon.dnote.core.NoteConstant;
import com.dixon.dnote.desktop.window.NoteFloatService;
import com.dixon.simple.router.api.SimpleRouter;
import com.dixon.simple.router.core.SRouter;

@SimpleRouter(value = RouterConstant.NOTE_SET, interceptor = "")
public class NoteSetActivity extends BaseActivity {

    private View mFloatStyleView, mHelpView;
    private TextView mFloatStyleDescView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_set);
        initView();
    }

    private void initView() {
        mFloatStyleDescView.setText(getFloatStyleDesc());
        mFloatStyleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedUtil.putString(NoteConstant.SP_FLOAT_STYLE, getNextStyle());
                // 显示了 才去重新加载
                if (NoteFloatService.getInstance().hasShow()) {
                    NoteFloatService.getInstance().showFloatWindow();
                }
                mFloatStyleDescView.setText(getFloatStyleDesc());
            }
        });
        mHelpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SRouter.build(NoteSetActivity.this, RouterConstant.NOTE_HELP).execute();
            }
        });
    }

    private String getNextStyle() {
        String floatStyle = SharedUtil.getString(NoteConstant.SP_FLOAT_STYLE, NoteConstant.FLOAT_STYLE_CLASSIC);
        switch (floatStyle) {
            case NoteConstant.FLOAT_STYLE_SIMPLE:
                return NoteConstant.FLOAT_STYLE_DESC;
            case NoteConstant.FLOAT_STYLE_DESC:
                return NoteConstant.FLOAT_STYLE_CLASSIC;
            case NoteConstant.FLOAT_STYLE_CLASSIC:
                return NoteConstant.FLOAT_STYLE_ALL;
            case NoteConstant.FLOAT_STYLE_ALL:
                return NoteConstant.FLOAT_STYLE_SIMPLE;
        }
        return "";
    }

    private String getFloatStyleDesc() {
        String floatStyle = SharedUtil.getString(NoteConstant.SP_FLOAT_STYLE, NoteConstant.FLOAT_STYLE_CLASSIC);
        switch (floatStyle) {
            case NoteConstant.FLOAT_STYLE_SIMPLE:
                return "Simple";
            case NoteConstant.FLOAT_STYLE_DESC:
                return "Standard";
            case NoteConstant.FLOAT_STYLE_CLASSIC:
                return "Classic";
            case NoteConstant.FLOAT_STYLE_ALL:
                return "All";
        }
        return "";
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mFloatStyleView = findViewById(R.id.note_ll_set_float_style);
        mFloatStyleDescView = findViewById(R.id.note_tv_set_float_style_desc);
        mHelpView = findViewById(R.id.note_ll_set_help);
    }
}