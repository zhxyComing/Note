package com.dixon.dnote.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dixon.allbase.base.BaseActivity;
import com.dixon.allbase.bean.NoteBean;
import com.dixon.allbase.fun.SelectChangeManager;
import com.dixon.allbase.fun.TimeFormat;
import com.dixon.allbase.model.RouterConstant;
import com.dixon.dlibrary.util.FontUtil;
import com.dixon.dlibrary.util.ToastUtil;
import com.dixon.dnote.R;
import com.dixon.dnote.core.NoteService;
import com.dixon.dnote.desktop.window.NoteFloatService;
import com.dixon.dnote.event.NoteTableRefreshEvent;
import com.dixon.simple.router.api.SimpleParam;
import com.dixon.simple.router.api.SimpleRouter;
import com.dixon.simple.router.core.SRouter;

import org.greenrobot.eventbus.EventBus;

import java.text.MessageFormat;
import java.util.Date;

/**
 * Create by: dixon.xu
 * Create on: 2020.07.22
 * Functional desc: 笔记编辑页 从悬浮窗直接跳转到此页编辑 避免进入首页造成回退多级路径
 * <p>
 * 理论上来说此页只能更新 但是实际上支持新建
 * <p>
 * 进入此页时 悬浮窗会隐藏 退出时重新显示
 */
@SimpleRouter(value = RouterConstant.NOTE_EDIT, interceptor = "")
public class NoteEditActivity extends BaseActivity implements View.OnClickListener {

    private EditText inputView;
    private View priorityView;
    private TextView tipView;

    private int tag = NoteBean.TAG_NORMAL;
    private int priority = NoteBean.PRIORITY_NOT_IN_HURRY;

    // 可能为null null则新建
    @SimpleParam(value = "update_data")
    NoteBean updateNoteBean;

    private String tipSuffix;

    private SelectChangeManager<TextView> selectChangeManager = new SelectChangeManager<TextView>() {
        @Override
        protected void selected(String tag, TextView obj) {
            NoteEditActivity.this.tag = Integer.parseInt(tag);
            obj.setBackgroundResource(R.drawable.note_shape_dialog_tag_select);
        }

        @Override
        protected void clearSelected(String tag, TextView obj) {
            obj.setBackgroundResource(R.drawable.note_shape_dialog_tag_unselected);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);
        SRouter.initParams(this);

        if (updateNoteBean != null) {
            initUpdateData();
        }
        init();
    }

    private void init() {
        tipSuffix = TimeFormat.dayDesc() + "，今天是" + TimeFormat.formatChina(new Date().getTime()) + "，您已记录";
        inputView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tipView.setText(MessageFormat.format("{0}{1}个字。", tipSuffix, s.length()));
            }
        });
        tipView.setText(MessageFormat.format("{0}{1}个字。", tipSuffix, updateNoteBean == null ? 0 : updateNoteBean.getContent().length()));
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        inputView = findViewById(R.id.note_et_edit_input);
        tipView = findViewById(R.id.note_tv_edit_tip);
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
    }

    private void initUpdateData() {
        inputView.setText(updateNoteBean.getContent());
        selectChangeManager.setSelected(String.valueOf(updateNoteBean.getTag()));
        setPriorityView(updateNoteBean.getPriority());
        FontUtil.font(inputView);
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
                NoteEditActivity.this.priority = NoteBean.PRIORITY_NOT_IN_HURRY;
                priorityView.setBackgroundResource(R.drawable.note_shape_dialog_priority_not_in_hurry);
                break;
            case NoteBean.PRIORITY_ORDINARY:
                NoteEditActivity.this.priority = NoteBean.PRIORITY_ORDINARY;
                priorityView.setBackgroundResource(R.drawable.note_shape_dialog_priority_ordinary);
                break;
            case NoteBean.PRIORITY_SECONDARY:
                NoteEditActivity.this.priority = NoteBean.PRIORITY_SECONDARY;
                priorityView.setBackgroundResource(R.drawable.note_shape_dialog_priority_secondary);
                break;
            case NoteBean.PRIORITY_IMPORTANT:
                NoteEditActivity.this.priority = NoteBean.PRIORITY_IMPORTANT;
                priorityView.setBackgroundResource(R.drawable.note_shape_dialog_priority_important);
                break;
        }
    }

    private void findViewAndAddToManager(int tag, int id) {
        TextView tagView = findViewById(id);
        tagView.setOnClickListener(this);
        selectChangeManager.put(String.valueOf(tag), tagView);
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
                EventBus.getDefault().post(new NoteTableRefreshEvent());
                finish();
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
        final NoteBean noteBean = new NoteBean();
        noteBean.setTime(new Date().getTime());
        noteBean.setPriority(priority);
        noteBean.setContent(content);
        noteBean.setTag(tag);
        noteBean.setId(System.currentTimeMillis());
        NoteService.getInstance().addData(noteBean, new NoteService.FinishCallback() {
            @Override
            public void onFinish() {
                ToastUtil.toast("添加完成");
                // 设置为悬浮窗笔记
                NoteService.getInstance().saveFloatData(noteBean, null);
                // 刷新主页面
                EventBus.getDefault().post(new NoteTableRefreshEvent());
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 隐藏悬浮窗 避免隐藏操作
        NoteFloatService.getInstance().dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 重新显示
        NoteFloatService.getInstance().showFloatWindow();
    }
}