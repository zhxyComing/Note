package com.dixon.dnote.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.dixon.allbase.base.BaseActivity;
import com.dixon.allbase.bean.NoteBean;
import com.dixon.allbase.fun.TimeUtil;
import com.dixon.allbase.model.RouterConstant;
import com.dixon.dlibrary.util.ToastUtil;
import com.dixon.dnote.R;
import com.dixon.dnote.adapter.NoteTableAdapter;
import com.dixon.dnote.bean.NoteTableItem;
import com.dixon.dnote.core.NoteService;
import com.dixon.dnote.event.NoteTableRefreshEvent;
import com.dixon.dnote.view.NoteEditDialog;
import com.dixon.dnote.view.NoteFunctionDialog;
import com.dixon.simple.router.api.SimpleParam;
import com.dixon.simple.router.api.SimpleRouter;
import com.dixon.simple.router.core.SRouter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

@SimpleRouter(value = RouterConstant.NOTE_TABLE, interceptor = "")
public class NoteTableActivity extends BaseActivity {

    public static final String FROM_WIDGET = "widget";
    public static final String FROM_FLOAT = "float";

    private ListView mTableView;
    private View mCreateNewView;
    private View mSetView;

    private NoteTableAdapter mTableAdapter;

    @SimpleParam(value = "from")
    String mFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_table);
        SRouter.initParams(this);

        loadData();
        initLogic();
        EventBus.getDefault().register(this);
    }

    private void initLogic() {
        mCreateNewView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteEditDialog.showCreateNoteDialog(new NoteEditDialog.Listener() {
                    @Override
                    public void onEditSuccess() {
                        loadData();
                    }
                });
            }
        });
        outSideIn();
        mSetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SRouter.build(NoteTableActivity.this, RouterConstant.NOTE_SET).execute();
            }
        });
    }

    /**
     * 从外部进入该页面的特殊处理逻辑
     */
    private void outSideIn() {
        if (!TextUtils.isEmpty(mFrom)) {
            switch (mFrom) {
                case FROM_WIDGET:
                    // 这里监听不到...后续再解决吧
                    // 从桌面小部件进入 直接打开查看、更新弹窗
                    NoteBean widgetData = NoteService.getInstance().getWidgetData();
                    if (widgetData != null) {
                        NoteEditDialog.showUpdateNoteDialog(widgetData, new NoteEditDialog.Listener() {
                            @Override
                            public void onEditSuccess() {
                                loadData();
                            }
                        });
                    }
                    break;
                case FROM_FLOAT:
                    NoteBean floatData = NoteService.getInstance().getFloatData();
                    if (floatData != null) {
                        // 弹出笔记编辑页
                        NoteEditDialog.showUpdateNoteDialog(floatData, new NoteEditDialog.Listener() {
                            @Override
                            public void onEditSuccess() {
                                loadData();
                            }
                        });
                    }
                    break;
            }
        }
    }

    private void loadData() {
        NoteService.getInstance().queryAll(new NoteService.ResponseCallback<List<NoteBean>>() {
            @Override
            public void onSuccess(List<NoteBean> data) {
                loadNotes(parseToNoteItem(data));
            }

            @Override
            public void onFail(String desc) {
                ToastUtil.toast(desc);
            }
        });
    }

    private void loadNotes(List<NoteTableItem> res) {
        if (mTableAdapter == null) {
            mTableAdapter = new NoteTableAdapter(res, this);
            mTableView.setAdapter(mTableAdapter);
            setTableItemClickListener();
        } else {
            mTableAdapter.reloadData(res);
        }
    }

    private void setTableItemClickListener() {
        mTableAdapter.setOnItemFunctionClickListener(new NoteTableAdapter.OnItemFunctionClickListener() {
            @Override
            public void onItemCardClick(NoteBean noteBean) {
                // 点击item，查看或更新笔记
                NoteEditDialog.showUpdateNoteDialog(noteBean, new NoteEditDialog.Listener() {
                    @Override
                    public void onEditSuccess() {
                        loadData();
                    }
                });
            }

            @Override
            public void onItemCardLongClick(final NoteBean noteBean) {
                // 长按item，调出功能键
                NoteFunctionDialog.showDialog(noteBean, new NoteFunctionDialog.Listener() {
                    @Override
                    public void onDeleteSuccess() {
                        loadData();
                    }
                });
            }
        });
    }

    private List<NoteTableItem> parseToNoteItem(List<NoteBean> all) {
        List<NoteTableItem> items = new ArrayList<>();

        List<NoteBean> todayNote = new ArrayList<>();
        List<NoteBean> yesterdayNote = new ArrayList<>();
        List<NoteBean> currentMonthNote = new ArrayList<>();
        List<NoteBean> distantNote = new ArrayList<>();

        for (NoteBean noteBean : all) {
            if (TimeUtil.isToday(noteBean.getTime())) {
                todayNote.add(noteBean);
            } else if (TimeUtil.isYesterday(noteBean.getTime())) {
                yesterdayNote.add(noteBean);
            } else if (TimeUtil.isCurrentMonth(noteBean.getTime())) {
                currentMonthNote.add(noteBean);
            } else {
                distantNote.add(noteBean);
            }
        }
        addToNoteTableList(items, "今天", todayNote);
        addToNoteTableList(items, "昨天", yesterdayNote);
        addToNoteTableList(items, "当月", currentMonthNote);
        addToNoteTableList(items, "遥远的过去～", distantNote);
        return items;
    }

    private void addToNoteTableList(List<NoteTableItem> items, String timeDesc, List<NoteBean> noteBeans) {
        if (!noteBeans.isEmpty()) {
            NoteTableItem timeItem = new NoteTableItem();
            timeItem.setType(NoteTableItem.TYPE_TIME);
            timeItem.setTimeDesc(timeDesc);
            items.add(timeItem);
            for (NoteBean noteBean : noteBeans) {
                NoteTableItem noteItem = new NoteTableItem();
                noteItem.setType(NoteTableItem.TYPE_NOTE);
                noteItem.setNoteBean(noteBean);
                items.add(noteItem);
            }
        }
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mTableView = findViewById(R.id.note_lv_table);
        mCreateNewView = findViewById(R.id.note_iv_add_new);
        mSetView = findViewById(R.id.note_iv_set_go);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 其它页面请求刷新笔记列表
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NoteTableRefreshEvent event) {
        loadData();
    }
}