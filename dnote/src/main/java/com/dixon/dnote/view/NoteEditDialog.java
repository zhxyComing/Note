package com.dixon.dnote.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dixon.allbase.bean.NoteBean;
import com.dixon.allbase.fun.SelectChangeManager;
import com.dixon.allbase.fun.TimeFormat;
import com.dixon.allbase.view.BaseDialog;
import com.dixon.dlibrary.util.AppTracker;
import com.dixon.dlibrary.util.FontUtil;
import com.dixon.dlibrary.util.ScreenUtil;
import com.dixon.dlibrary.util.ToastUtil;
import com.dixon.dnote.R;
import com.dixon.dnote.core.NoteService;

import java.text.MessageFormat;
import java.util.Date;

/**
 * Create by: dixon.xu
 * Create on: 2020.07.13
 * Functional desc: 笔记查看、添加、更新Dialog
 */
public class NoteEditDialog extends BaseDialog {

    private EditText inputView;
    private View priorityView;
    private TextView tipView;

    private Listener listener;
    private int tag = NoteBean.TAG_NORMAL;
    private int priority = NoteBean.PRIORITY_NOT_IN_HURRY;
    private String tipSuffix;

    private NoteBean updateNoteBean;

    private SelectChangeManager<TextView> selectChangeManager = new SelectChangeManager<TextView>() {
        @Override
        protected void selected(String tag, TextView obj) {
            NoteEditDialog.this.tag = Integer.parseInt(tag);
            obj.setBackgroundResource(R.drawable.note_shape_dialog_tag_select);
        }

        @Override
        protected void clearSelected(String tag, TextView obj) {
            obj.setBackgroundResource(R.drawable.note_shape_dialog_tag_unselected);
        }
    };

    private NoteEditDialog(Context context, Listener listener) {
        super(context);
        this.listener = listener;
    }

    private NoteEditDialog(Context context, int themeResId, Listener listener) {
        super(context, themeResId);
        this.listener = listener;
    }

    private NoteEditDialog(Context context, Listener listener, NoteBean noteBean) {
        super(context);
        this.listener = listener;
        this.updateNoteBean = noteBean;
    }

    private NoteEditDialog(Context context, int themeResId, Listener listener, NoteBean noteBean) {
        super(context, themeResId);
        this.listener = listener;
        this.updateNoteBean = noteBean;
    }

    private void initUpdateData() {
        inputView.setText(updateNoteBean.getContent());
        selectChangeManager.setSelected(String.valueOf(updateNoteBean.getTag()));
        setPriorityView(updateNoteBean.getPriority());
    }

    @Override
    protected int contentView() {
        return R.layout.note_dialog_edit;
    }

    @Override
    protected void findView() {
        inputView = findViewById(R.id.note_et_edit_input);
        tipView = findViewById(R.id.note_tv_edit_tip);
        FontUtil.font(inputView, tipView);
        findViewById(R.id.note_tv_edit_ok).setOnClickListener(this);

        findViewAndAddToManager(NoteBean.TAG_NORMAL, R.id.note_tv_tag_normal);
        findViewAndAddToManager(NoteBean.TAG_STUDY, R.id.note_tv_tag_study);
        findViewAndAddToManager(NoteBean.TAG_LIFE, R.id.note_tv_tag_life);
        findViewAndAddToManager(NoteBean.TAG_WORK, R.id.note_tv_tag_work);
        findViewAndAddToManager(NoteBean.TAG_WARN, R.id.note_tv_tag_warn);

        // 默认普通tag选中
        findViewById(R.id.note_tv_tag_normal).setBackgroundResource(R.drawable.note_shape_dialog_tag_select);

        priorityView = findViewById(R.id.note_tv_priority);
        priorityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priorityClick();
            }
        });

        if (updateNoteBean != null) {
            initUpdateData();
        }
        initTipView();
    }

    private void initTipView() {
        tipSuffix = TimeFormat.formatChina(new Date().getTime()) + " ";
        inputView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tipView.setText(MessageFormat.format("{0}{1}字", tipSuffix, s.length()));
            }
        });
        tipView.setText(MessageFormat.format("{0}{1}字", tipSuffix, updateNoteBean == null ? 0 : updateNoteBean.getContent().length()));
    }

    /**
     * 点击优先级切换下一个
     */
    private void priorityClick() {
        switch (priority) {
            case NoteBean.PRIORITY_NOT_IN_HURRY:
                setPriorityView(NoteBean.PRIORITY_ORDINARY);
                break;
            case NoteBean.PRIORITY_ORDINARY:
                setPriorityView(NoteBean.PRIORITY_SECONDARY);
                break;
            case NoteBean.PRIORITY_SECONDARY:
                setPriorityView(NoteBean.PRIORITY_IMPORTANT);
                break;
            case NoteBean.PRIORITY_IMPORTANT:
                setPriorityView(NoteBean.PRIORITY_NOT_IN_HURRY);
                break;
        }
    }

    /**
     * 根据优先级设定视图
     */
    private void setPriorityView(int priority) {
        switch (priority) {
            case NoteBean.PRIORITY_NOT_IN_HURRY:
                NoteEditDialog.this.priority = NoteBean.PRIORITY_NOT_IN_HURRY;
                priorityView.setBackgroundResource(R.drawable.note_shape_dialog_priority_not_in_hurry);
                break;
            case NoteBean.PRIORITY_ORDINARY:
                NoteEditDialog.this.priority = NoteBean.PRIORITY_ORDINARY;
                priorityView.setBackgroundResource(R.drawable.note_shape_dialog_priority_ordinary);
                break;
            case NoteBean.PRIORITY_SECONDARY:
                NoteEditDialog.this.priority = NoteBean.PRIORITY_SECONDARY;
                priorityView.setBackgroundResource(R.drawable.note_shape_dialog_priority_secondary);
                break;
            case NoteBean.PRIORITY_IMPORTANT:
                NoteEditDialog.this.priority = NoteBean.PRIORITY_IMPORTANT;
                priorityView.setBackgroundResource(R.drawable.note_shape_dialog_priority_important);
                break;
        }
    }

    private void findViewAndAddToManager(int tag, int id) {
        TextView tagView = (TextView) findViewById(id);
        tagView.setOnClickListener(this);
        selectChangeManager.put(String.valueOf(tag), tagView);
    }

    @Override
    protected boolean isCancelOnOutSide() {
        return true;
    }

    @Override
    protected int widthPx() {
//        return ScreenUtil.dpToPxInt(getContext(), 380);
        return ScreenUtil.getDisplayWidth(getContext()) - ScreenUtil.dpToPxInt(getContext(), 60);
    }

    @Override
    protected int heightPx() {
        return ScreenUtil.dpToPxInt(getContext(), 480);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.note_tv_edit_ok) {
            if (updateNoteBean != null) {
                updateNote();
            } else {
                addNote();
            }
        } else if (v.getId() == R.id.note_tv_tag_normal) {
            setTagSelected(NoteBean.TAG_NORMAL);
        } else if (v.getId() == R.id.note_tv_tag_study) {
            setTagSelected(NoteBean.TAG_STUDY);
        } else if (v.getId() == R.id.note_tv_tag_life) {
            setTagSelected(NoteBean.TAG_LIFE);
        } else if (v.getId() == R.id.note_tv_tag_work) {
            setTagSelected(NoteBean.TAG_WORK);
        } else if (v.getId() == R.id.note_tv_tag_warn) {
            setTagSelected(NoteBean.TAG_WARN);
        }
    }

    private void updateNote() {
        String content = inputView.getText().toString();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.toast("笔记不能为空哦～");
            return;
        }
        updateNoteBean.setPriority(priority);
        updateNoteBean.setContent(content);
        updateNoteBean.setTag(tag);
        NoteService.getInstance().updateData(updateNoteBean, new NoteService.FinishCallback() {
            @Override
            public void onFinish() {
                ToastUtil.toast("更新完成");
                if (listener != null) {
                    listener.onEditSuccess();
                }
                dismiss();
            }
        });
    }

    private void setTagSelected(int tag) {
        selectChangeManager.setSelected(String.valueOf(tag));
    }

    private void addNote() {
        String content = inputView.getText().toString();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.toast("笔记不能为空哦～");
            return;
        }
        NoteBean noteBean = new NoteBean();
        noteBean.setTime(new Date().getTime());
        noteBean.setPriority(priority);
        noteBean.setContent(content);
        noteBean.setTag(tag);
        noteBean.setId(System.currentTimeMillis());
        NoteService.getInstance().addData(noteBean, new NoteService.FinishCallback() {
            @Override
            public void onFinish() {
                ToastUtil.toast("添加完成");
                if (listener != null) {
                    listener.onEditSuccess();
                }
                dismiss();
            }
        });
    }

    @Override
    protected int windowAnimStyle() {
        return R.style.dialogAnim;
    }

    /**
     * 创建新笔记的dialog
     */
    public static void showCreateNoteDialog(Listener listener) {
        new NoteEditDialog(AppTracker.getCurActivity(), R.style.dialog, listener).show();
    }

    /**
     * 更新旧笔记的dialog
     */
    public static void showUpdateNoteDialog(NoteBean noteBean, Listener listener) {
        new NoteEditDialog(AppTracker.getCurActivity(), R.style.dialog, listener, noteBean).show();
    }

    public interface Listener {

        void onEditSuccess();
    }
}
