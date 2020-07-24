package com.dixon.note.launch.inter;

import com.dixon.allbase.bean.NoteBean;
import com.dixon.allbase.dao.AppDatabase;
import com.dixon.allbase.dao.NoteDao;
import com.dixon.dlibrary.util.AppTracker;
import com.dixon.dlibrary.util.HandlerUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Create by: dixon.xu
 * Create on: 2020.06.19
 * Functional desc: Home 页 Model 层
 */
public class HomeModel implements IHomeModel {

    private ExecutorService service = Executors.newSingleThreadExecutor();
    private IHomePresent present;
    private NoteDao noteDao;

    public HomeModel(IHomePresent present) {
        this.present = present;
        noteDao = AppDatabase.getInstance(AppTracker.getCurApplication()).noteDao();
    }

    /**
     * 加载全部数据
     */
    @Override
    public void loadData() {
        service.execute(new Runnable() {
            @Override
            public void run() {
                final List<NoteBean> all = noteDao.getAll();
                // 最近时间排前
                Collections.sort(all, new Comparator<NoteBean>() {
                    @Override
                    public int compare(NoteBean o1, NoteBean o2) {
                        return Long.compare(o2.getTime(), o1.getTime());
                    }
                });
                HandlerUtil.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (all.isEmpty()) {
                            present.loadFail("您还没有笔记～");
                        } else {
                            present.loadSuccess(all);
                        }
                    }
                });
            }
        });
    }

    /**
     * 保存单个数据
     */
    @Override
    public void saveData(final NoteBean note) {
        service.execute(new Runnable() {
            @Override
            public void run() {
                // 目前只有成功 没有失败...
                noteDao.insert(note);
                HandlerUtil.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        present.saveSuccess();
                    }
                });
            }
        });
    }

    /**
     * 更新单个数据
     */
    @Override
    public void updateData(final NoteBean note) {
        service.execute(new Runnable() {
            @Override
            public void run() {
                // 目前只有成功 没有失败...
                noteDao.update(note);
                HandlerUtil.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        present.updateSuccess();
                    }
                });
            }
        });
    }

    /**
     * 删除单个数据
     */
    @Override
    public void deleteData(final NoteBean note) {
        service.execute(new Runnable() {
            @Override
            public void run() {
                // 目前只有成功 没有失败...
                noteDao.delete(note);
                HandlerUtil.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        present.deleteSuccess();
                    }
                });
            }
        });
    }
}
