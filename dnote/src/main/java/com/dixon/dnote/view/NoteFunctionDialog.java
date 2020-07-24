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
public class NoteFunctionDialog extends BaseDialog {

    private NoteBean noteBean;
    private Listener listener;

    private NoteFunctionDialog(Context context, NoteBean noteBean, Listener listener) {
        super(context);
        this.noteBean = noteBean;
        this.listener = listener;
    }

    private NoteFunctionDialog(Context context, int themeResId, NoteBean noteBean, Listener listener) {
        super(context, themeResId);
        this.noteBean = noteBean;
        this.listener = listener;
    }

    @Override
    protected int contentView() {
        return R.layout.note_dialog_function;
    }

    @Override
    protected void findView() {
        findViewById(R.id.note_tv_function_delete).setOnClickListener(this);
        findViewById(R.id.note_tv_function_app_widget).setOnClickListener(this);
        findViewById(R.id.note_tv_function_app_float).setOnClickListener(this);
    }

    @Override
    protected boolean isCancelOnOutSide() {
        return true;
    }

    @Override
    protected int widthPx() {
        return ScreenUtil.dpToPxInt(getContext(), 240);
    }

    @Override
    protected int heightPx() {
        return PX_AUTO;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.note_tv_function_delete) {
            deleteNote();
        } else if (v.getId() == R.id.note_tv_function_app_widget) {
            NoteService.getInstance().saveWidgetData(noteBean, new NoteService.FinishCallback() {
                @Override
                public void onFinish() {
                    // 保存数据后 刷新桌面小部件（仅能刷新 显示权在用户那儿）
                    NoteWidget.update();
                    dismiss();
                }
            });
        } else if (v.getId() == R.id.note_tv_function_app_float) {
            NoteService.getInstance().saveFloatData(noteBean, new NoteService.FinishCallback() {
                @Override
                public void onFinish() {
                    // 保存数据后 显示或更新桌面悬浮窗
                    showOrUpdateDesktopFloat();
                    dismiss();
                }
            });
        }
    }

    private void showOrUpdateDesktopFloat() {
        // 如果已经展示 则直接刷新
        if (NoteFloatService.getInstance().hasShow()) {
            NoteFloatService.getInstance().refresh();
        } else {
            // 未展示则展示全新的悬浮窗 悬浮窗内容则根据用户选择自定
            // 多次调用showFloatWindow()会重置悬浮窗
            NoteFloatService.getInstance().showFloatWindow();
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
        new NoteFunctionDialog(AppTracker.getCurActivity(), R.style.dialog, noteBean, listener).show();
    }

    public interface Listener {

        void onDeleteSuccess();
    }
}
