package com.dixon.note.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dixon.allbase.bean.NoteBean;
import com.dixon.dlibrary.util.ScreenUtil;
import com.dixon.note.R;
import com.dixon.note.inter.IDrop;
import com.dixon.note.util.DropHelper;

/**
 * Create by: dixon.xu
 * Create on: 2020.06.22
 * Functional desc: 笔记编辑组件
 */
public class NoteEditView extends LinearLayout implements IDrop {

    private TextView updateView;
    private EditText inputView;
    private DropHelper dropHelper;
    private NoteBean editNote;

    private int padding;

    public NoteEditView(@NonNull Context context) {
        super(context);
        init();
    }

    public NoteEditView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NoteEditView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
        padding = ScreenUtil.dpToPxInt(getContext(), 20);
        setStandardPadding();

        LayoutInflater.from(getContext()).inflate(R.layout.item_home_note_edit, this, true);
        findView();

        dropHelper = new DropHelper(this);
        dropHelper.setDropHeight(0, ScreenUtil.dpToPx(getContext(), 340));
    }

    private void findView() {
        updateView = findViewById(R.id.app_home_edit_update);
        inputView = findViewById(R.id.app_home_edit_input);
    }

    public void setOnUpdateClickListener(final OnUpdateClickListener listener) {
        updateView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String note = inputView.getText().toString();
                if (editNote == null) {
                    listener.onNewCommit(note);
                } else {
                    editNote.setContent(inputView.getText().toString());
                    // 更细时间 顺便方便排序
                    editNote.setTime(System.currentTimeMillis());
                    listener.onUpdate(editNote);
                }
                inputView.setText("");
            }
        });
    }

    @Override
    public void drop() {
        // NoteEdit 后续要自己实现drop逻辑
//        dropHelper.drop();
    }

    @Override
    public void dropOpen() {
        updateView.setText(editNote == null ? "新建" : "更新");
        setStandardPadding();
        inputView.setVisibility(VISIBLE);
        updateView.setVisibility(VISIBLE);
        dropHelper.dropOpen();
        // 设置焦点 否则输入法无法弹出
        inputView.setFocusable(true);
        inputView.requestFocus();
    }

    @Override
    public void dropClose() {
        // 每次关闭都清空要编辑的数据
        if (editNote != null) {
            editNote = null;
            inputView.setText("");
        }
        setPadding(0, 0, 0, 0);
        inputView.setVisibility(GONE);
        updateView.setVisibility(GONE);
        dropHelper.dropClose();
    }

    @Override
    public boolean isDropOpen() {
        return dropHelper.isDropOpen();
    }

    @Override
    public boolean isDropClose() {
        return dropHelper.isDropClose();
    }

    @Override
    public boolean isOpening() {
        return dropHelper.isOpening();
    }

    @Override
    public boolean isClosing() {
        return dropHelper.isClosing();
    }

    public interface OnUpdateClickListener {

        void onNewCommit(String text);

        void onUpdate(NoteBean noteBean);
    }

    private void setStandardPadding() {
        setPadding(padding, padding, padding, 0);
    }

    /**
     * 先设置 再打开 则变为编辑模式
     * <p>
     * 编辑模式在dropClose的情况下会退出 即生命周期仅一回合
     */
    public void setEditNote(NoteBean note) {
        editNote = note;
        updateView.setText("更新");
        inputView.setText(note.getContent());
    }
}
