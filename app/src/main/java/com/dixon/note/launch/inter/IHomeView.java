package com.dixon.note.launch.inter;

import com.dixon.allbase.bean.NoteBean;

import java.util.List;

/**
 * Create by: dixon.xu
 * Create on: 2020.06.19
 * Functional desc: Home V 层
 */
public interface IHomeView {

    /**
     * 刷新主页内容
     */
    void setNoteDisplayData(List<NoteBean> notes);

    void showToast(String msg);
}
