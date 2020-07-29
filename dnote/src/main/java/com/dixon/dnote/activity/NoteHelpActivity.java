package com.dixon.dnote.activity;

import android.os.Bundle;

import com.dixon.allbase.base.BaseActivity;
import com.dixon.allbase.model.RouterConstant;
import com.dixon.dnote.R;
import com.dixon.simple.router.api.SimpleRouter;

@SimpleRouter(value = RouterConstant.NOTE_HELP, interceptor = "")
public class NoteHelpActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_help);
    }
}