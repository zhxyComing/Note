package com.dixon.dnote.view;

import android.content.Context;
import android.view.View;

import com.dixon.allbase.fun.WindowPermissionHelper;
import com.dixon.allbase.view.BaseDialog;
import com.dixon.dlibrary.util.ScreenUtil;
import com.dixon.dnote.R;

/**
 * Create by: dixon.xu
 * Create on: 2020.07.28
 * Functional desc:
 */
public class NotePermissionDialog extends BaseDialog {

    public NotePermissionDialog(Context context) {
        super(context);
    }

    public NotePermissionDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected int contentView() {
        return R.layout.note_dialog_permission;
    }

    @Override
    protected void findView() {
        findViewById(R.id.note_iv_permission_ok).setOnClickListener(this);
    }

    @Override
    protected boolean isCancelOnOutSide() {
        return true;
    }

    @Override
    protected int widthPx() {
        return ScreenUtil.dpToPxInt(getContext(), 280);
    }

    @Override
    protected int heightPx() {
        return PX_AUTO;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.note_iv_permission_ok) {
            WindowPermissionHelper.gotoSetPage();
            dismiss();
        }
    }

    @Override
    protected int windowAnimStyle() {
        return R.style.dialogAnim;
    }
}
