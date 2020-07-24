package com.dixon.note.launch.inter;

import com.dixon.allbase.bean.NoteBean;

import java.util.List;

/**
 * Create by: dixon.xu
 * Create on: 2020.06.19
 * Functional desc: Home P 层
 */
public class HomePresent implements IHomePresent {

    private IHomeView view;
    private IHomeModel model;

    public HomePresent(IHomeView view) {
        this.view = view;
        model = new HomeModel(this);
    }

    @Override
    public void loadData() {
        model.loadData();
    }

    @Override
    public void loadSuccess(List<NoteBean> notes) {
        view.setNoteDisplayData(notes);
    }

    @Override
    public void loadFail(String error) {
        view.showToast(error);
    }

    @Override
    public void saveData(NoteBean note) {
        model.saveData(note);
    }

    @Override
    public void saveSuccess() {
        view.showToast("保存成功");
        // 重新加载数据
        loadData();
    }

    @Override
    public void saveFail(String error) {
        view.showToast("保存失败");
    }

    @Override
    public void deleteData(NoteBean note) {
        model.deleteData(note);
    }

    @Override
    public void deleteSuccess() {
        view.showToast("删除成功");
        loadData();
    }

    @Override
    public void deleteFail(String error) {
        view.showToast("删除失败");
    }

    @Override
    public void updateData(NoteBean note) {
        model.updateData(note);
    }

    @Override
    public void updateSuccess() {
        view.showToast("更新成功");
        loadData();
    }

    @Override
    public void updateFail(String error) {
        view.showToast("更新失败");
    }
}
