package com.dixon.allbase.bean;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * Create by: dixon.xu
 * Create on: 2020.06.16
 * Functional desc: 笔记Bean
 */
@Entity
public class NoteBean implements Serializable {

    public static final int PRIORITY_IMPORTANT = 0; // 重要的
    public static final int PRIORITY_SECONDARY = 1; // 次级的
    public static final int PRIORITY_ORDINARY = 2; // 普通的
    public static final int PRIORITY_NOT_IN_HURRY = 3; //不着急的

    public static final int TAG_NORMAL = 0; // 普通笔记
    public static final int TAG_STUDY = 1; // 学习型笔记
    public static final int TAG_LIFE = 2; // 生活型笔记
    public static final int TAG_WORK = 3; // 工作型笔记
    public static final int TAG_WARN = 4; // 警示笔记

    @PrimaryKey
    private long id;
    private String content;
    private long time;
    private int priority;
    private int tag;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int hashCode() {
        return (int) (content.hashCode() + id);
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (this.getClass() != obj.getClass()) return false;
        NoteBean noteBean = (NoteBean) obj;
        return noteBean.id == id;
    }
}
