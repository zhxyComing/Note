package com.dixon.dnote.bean;

import com.dixon.allbase.bean.NoteBean;

/**
 * Create by: dixon.xu
 * Create on: 2020.07.13
 * Functional desc:
 */
public class NoteTableItem {

    public static final int TYPE_TIME = 0;
    public static final int TYPE_NOTE = 1;

    private int type;
    private NoteBean noteBean;
    private String timeDesc;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public NoteBean getNoteBean() {
        return noteBean;
    }

    public void setNoteBean(NoteBean noteBean) {
        this.noteBean = noteBean;
    }

    public String getTimeDesc() {
        return timeDesc;
    }

    public void setTimeDesc(String timeDesc) {
        this.timeDesc = timeDesc;
    }

    @Override
    public String toString() {
        return "NoteTableItem{" +
                "type='" + type + '\'' +
                ", noteBean=" + noteBean +
                ", timeDesc='" + timeDesc + '\'' +
                '}';
    }
}
