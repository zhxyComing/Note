package com.dixon.dnote.view;

import android.content.Context;
import android.view.View;

import com.dixon.allbase.bean.NoteBean;
import com.dixon.allbase.view.BaseDialog;
import com.dixon.dlibrary.util.AppTracker;
import com.dixon.dlibrary.util.ScreenUtil;
import com.dixon.dnote.R;
import com.dixon.dnote.core.NoteService;
import com.dixon.dnote.desktop.NoteWidget;
import com.dixon.dnote.desktop.window.NoteFloatService;

/**
 * Create by: dixon.xu
 * Create on: 2020.07.14
 * Functional desc: 笔记功能Dialog
 */
public class NoteDeleteDialog extends BaseDialog {

    private NoteBean noteBean;
    private Listener listener;

    private NoteDeleteDialog(Context context, NoteBean noteBean, Listener listener) {
        super(context);
        this.noteBean = noteBean;
        this.listener = listener;
    }

    private NoteDeleteDialog(Context context, int themeResId, NoteBean noteBean, Listener listener) {
        super(context, themeResId);
        this.noteBean = noteBean;
        this.listener = listener;
    }

    @Override
    protected int contentView() {
        return R.layout.note_dialog_delete;
    }

    @Override
    protected void findView() {
        findViewById(R.id.note_tv_delete_ok).setOnClickListener(this);
    }

    @Override
    protected boolean isCancelOnOutSide() {
        return true;
    }

    @Override
    protected int widthPx() {
        return ScreenUtil.getDisplayWidth(getContext());
    }

    @Override
    protected int heightPx() {
        return PX_AUTO;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.note_tv_delete_ok) {
            deleteNote();
        }
    }

    private void deleteNote() {
        NoteService.getInstance().deleteData(noteBean, new NoteService.FinishCallback() {
            @Override
            public void onFinish() {
                if (listener != null) {
                    listener.onDeleteSuccess();
                }
                dismiss();
            }
        });
    }

    @Override
    protected int windowAnimStyle() {
        return R.style.dialogAnim;
    }

    public static void showDialog(NoteBean noteBean, Listener listener) {
        new NoteDeleteDialog(AppTracker.getCurActivity(), R.style.dialog, noteBean, listener).show();
    }

    public interface Listener {

        void onDeleteSuccess();
    }
}
