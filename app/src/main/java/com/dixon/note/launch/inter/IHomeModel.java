package com.dixon.note.launch.inter;

import com.dixon.allbase.bean.NoteBean;

/**
 * Create by: dixon.xu
 * Create on: 2020.06.19
 * Functional desc: Home M 层
 */
public interface IHomeModel {

    void loadData();

    void saveData(NoteBean note);

    void updateData(NoteBean note);

    void deleteData(NoteBean note);
}
