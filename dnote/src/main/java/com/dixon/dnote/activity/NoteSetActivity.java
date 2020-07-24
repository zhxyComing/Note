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

@SimpleRouter(value = RouterConstant.NOTE_SET, interceptor = "")
public class NoteSetActivity extends BaseActivity {

    private View mFloatStyleView;
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
    }

    private String getNextStyle() {
        String floatStyle = SharedUtil.getString(NoteConstant.SP_FLOAT_STYLE, NoteConstant.FLOAT_STYLE_CLASSIC);
        switch (floatStyle) {
            case NoteConstant.FLOAT_STYLE_SIMPLE:
                return NoteConstant.FLOAT_STYLE_DESC;
            case NoteConstant.FLOAT_STYLE_DESC:
                return NoteConstant.FLOAT_STYLE_CLASSIC;
            case NoteConstant.FLOAT_STYLE_CLASSIC:
                return NoteConstant.FLOAT_STYLE_SIMPLE;
        }
        return "";
    }

    private String getFloatStyleDesc() {
        String floatStyle = SharedUtil.getString(NoteConstant.SP_FLOAT_STYLE, NoteConstant.FLOAT_STYLE_CLASSIC);
        switch (floatStyle) {
            case NoteConstant.FLOAT_STYLE_SIMPLE:
                return "最简式";
            case NoteConstant.FLOAT_STYLE_DESC:
                return "三段式";
            case NoteConstant.FLOAT_STYLE_CLASSIC:
                return "经典式";
        }
        return "";
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mFloatStyleView = findViewById(R.id.note_ll_set_float_style);
        mFloatStyleDescView = findViewById(R.id.note_tv_set_float_style_desc);
    }
}