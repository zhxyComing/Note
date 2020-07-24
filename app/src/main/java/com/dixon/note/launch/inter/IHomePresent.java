package com.dixon.note.launch.inter;

import com.dixon.allbase.bean.NoteBean;

import java.util.List;

/**
 * Create by: dixon.xu
 * Create on: 2020.06.19
 * Functional desc: Home P 层
 */
public interface IHomePresent {

    /**
     * 加载数据
     */
    void loadData();

    void loadSuccess(List<NoteBean> notes);

    void loadFail(String error);

    /**
     * 保存数据
     */
    void saveData(NoteBean note);

    void saveSuccess();

    void saveFail(String error);

    /**
     * 删除数据
     */
    void deleteData(NoteBean note);

    void deleteSuccess();

    void deleteFail(String error);

    /**
     * 修改数据
     */
    void updateData(NoteBean note);

    void updateSuccess();

    void updateFail(String error);
}
