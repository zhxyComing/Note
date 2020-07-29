package com.dixon.dnote.core;

import com.dixon.allbase.bean.NoteBean;
import com.dixon.allbase.dao.AppDatabase;
import com.dixon.allbase.dao.NoteDao;
import com.dixon.dlibrary.util.AppTracker;
import com.dixon.dlibrary.util.HandlerUtil;
import com.dixon.dlibrary.util.SharedUtil;
import com.dixon.dnote.desktop.NoteWidget;
import com.dixon.dnote.desktop.window.NoteFloatService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Create by: dixon.xu
 * Create on: 2020.07.13
 * Functional desc: 笔记数据服务 提供笔记数据的增删改查 以及悬浮窗笔记数据的特殊处理
 */
public class NoteService {

    private static final String TAG_DESKTOP_WIDGET = "desktop_widget";
    private static final String TAG_DESKTOP_FLOAT = "desktop_float";

    private ExecutorService service = Executors.newSingleThreadExecutor();
    private NoteDao noteDao = AppDatabase.getInstance(AppTracker.getCurApplication()).noteDao();

    private NoteService() {
    }

    private static NoteService instance;

    public static NoteService getInstance() {
        if (instance == null) {
            synchronized (NoteService.class) {
                if (instance == null) {
                    instance = new NoteService();
                }
            }
        }
        return instance;
    }

    public void queryAll(final ResponseCallback<List<NoteBean>> callback) {
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
                            callback.onFail("您还没有笔记～");
                        } else {
                            callback.onSuccess(all);
                        }
                    }
                });
            }
        });
    }

    public void query(final String id, final ResponseCallback<NoteBean> callback) {
        service.execute(new Runnable() {
            @Override
            public void run() {
                final NoteBean noteBean = noteDao.findById(id);
                HandlerUtil.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (noteBean != null) {
                            callback.onSuccess(noteBean);
                        } else {
                            callback.onFail("未找到该数据");
                        }
                    }
                });
            }
        });
    }

    public void addData(final NoteBean noteBean, final FinishCallback callback) {
        service.execute(new Runnable() {
            @Override
            public void run() {
                // 目前只有成功 没有失败...
                noteDao.insert(noteBean);
                HandlerUtil.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onFinish();
                        }
                    }
                });
            }
        });
    }

    public void updateData(final NoteBean note, final FinishCallback callback) {
        service.execute(new Runnable() {
            @Override
            public void run() {
                // 目前只有成功 没有失败...
                noteDao.update(note);
                // 每次更新完后 刷新窗口缓存数据
                refreshWindowData(note.getId());
                HandlerUtil.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onFinish();
                        }
                    }
                });
            }
        });
    }

    public void deleteData(final NoteBean note, final FinishCallback callback) {
        service.execute(new Runnable() {
            @Override
            public void run() {
                // 目前只有成功 没有失败...
                noteDao.delete(note);
                // 每次删除完后 刷新窗口缓存数据
                refreshWindowData(note.getId());
                HandlerUtil.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onFinish();
                        }
                    }
                });
            }
        });
    }

    public void saveWidgetData(final NoteBean noteBean, final FinishCallback callback) {
        // 保存桌面小部件数据
        SharedUtil.putBean(TAG_DESKTOP_WIDGET, noteBean);
        // 回调保存完成
        if (callback != null) {
            callback.onFinish();
        }
    }

    public NoteBean getWidgetData() {
        return SharedUtil.getBean(TAG_DESKTOP_WIDGET, NoteBean.class, null);
    }

    public NoteBean getFloatData() {
        return SharedUtil.getBean(TAG_DESKTOP_FLOAT, NoteBean.class, null);
    }

    public void saveFloatData(final NoteBean noteBean, final FinishCallback callback) {
        // 保存桌面小部件数据
        SharedUtil.putBean(TAG_DESKTOP_FLOAT, noteBean);
        // 回调保存完成
        if (callback != null) {
            callback.onFinish();
        }
    }

    /**
     * 刷新桌面小程序数据
     * <p>
     * 更新数据库后需要同步刷新SP数据 以及UI同步
     */
    private void refreshWeightData() {
        NoteBean widgetData = getWidgetData();
        if (widgetData != null) {
            query(String.valueOf(widgetData.getId()), new ResponseCallback<NoteBean>() {
                @Override
                public void onSuccess(NoteBean data) {
                    // 更新数据
                    saveWidgetData(data, new FinishCallback() {
                        @Override
                        public void onFinish() {
                            NoteWidget.update();
                        }
                    });
                }

                @Override
                public void onFail(String desc) {
                    // 查询不到该数据 说明删除了
                    saveWidgetData(null, new FinishCallback() {
                        @Override
                        public void onFinish() {
                            NoteWidget.update();
                        }
                    });
                }
            });
        }
    }

    /**
     * 刷新桌面悬浮窗数据
     * <p>
     * 更新数据库后需要同步刷新SP数据 以及UI同步
     */
    private void refreshFloatData() {
        NoteBean floatData = getFloatData();
        if (floatData != null) {
            query(String.valueOf(floatData.getId()), new ResponseCallback<NoteBean>() {
                @Override
                public void onSuccess(NoteBean data) {
                    // 查询到数据了 更新数据
                    saveFloatData(data, new FinishCallback() {
                        @Override
                        public void onFinish() {
                            // 通知悬浮窗刷新
                            NoteFloatService.getInstance().refresh();
                        }
                    });
                }

                @Override
                public void onFail(String desc) {
                    // 查询不到该数据 说明删除了
                    saveFloatData(null, new FinishCallback() {
                        @Override
                        public void onFinish() {
                            // 通知悬浮窗刷新
                            NoteFloatService.getInstance().refresh();
                        }
                    });
                }
            });
        }
    }

    /**
     * 判断是否需要刷新Weight
     */
    private boolean isNeedRefreshWeight(long editId) {
        NoteBean widgetData = getWidgetData();
        if (widgetData != null && widgetData.getId() == editId) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否需要刷新Float
     */
    private boolean isNeedRefreshFloat(long editId) {
        NoteBean floatData = getFloatData();
        if (floatData != null && floatData.getId() == editId) {
            return true;
        }
        return false;
    }

    /**
     * 刷新悬浮窗或桌面小部件的数据
     */
    private void refreshWindowData(long id) {
        if (isNeedRefreshWeight(id)) {
            refreshWeightData();
        }
        if (isNeedRefreshFloat(id)) {
            refreshFloatData();
        }
    }

    public interface ResponseCallback<T> {

        void onSuccess(T data);

        void onFail(String desc);
    }

    public interface FinishCallback {

        void onFinish();
    }
}
