package com.dixon.dnote.desktop;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.dixon.allbase.bean.NoteBean;
import com.dixon.dlibrary.util.AppTracker;
import com.dixon.dlibrary.util.SharedUtil;
import com.dixon.dlibrary.util.ToastUtil;
import com.dixon.dnote.R;
import com.dixon.dnote.activity.NoteTableActivity;
import com.dixon.dnote.core.NoteService;

import java.util.HashSet;
import java.util.Set;

/**
 * Create by: dixon.xu
 * Create on: 2020.07.14
 * Functional desc: 桌面小部件 只支持一个笔记 极简原则
 */
public class NoteWidget extends AppWidgetProvider {

    // 更新widget的广播对应的action
    public static final String ACTION_UPDATE = "com.dixon.note.widget.UPDATE";
    // 每新建一个widget都会为该widget分配一个id
    private static final Set<Integer> idsSet = new HashSet<>();
    // 缓存的id 防止重启后不执行update 进而找不到id
    public static final String IDS_CACHE = "ids_cache";

    /**
     * 每次窗口小部件被更新都调用一次该方法
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        // 每次 widget 被创建时，对应的将widget的id添加到set中
        for (int appWidgetId : appWidgetIds) {
            idsSet.add(appWidgetId);
        }
        saveIdSet();
        updateAllAppWidgets(context, appWidgetManager, idsSet);
    }

    // 缓存id
    private void saveIdSet() {
        Set<String> ids = new HashSet<>();
        for (Integer id : idsSet) {
            ids.add(Integer.toString(id));
        }
        SharedUtil.putStringSet(IDS_CACHE, ids);
    }

    // 获取缓存的id
    private Set<Integer> getCacheIdSet() {
        Set<Integer> ids = new HashSet<>();
        Set<String> stringIds = SharedUtil.getStringSet(IDS_CACHE, null);
        if (stringIds == null) {
            return ids;
        }
        for (String stringId : stringIds) {
            ids.add(Integer.valueOf(stringId));
        }
        return ids;
    }

    /**
     * 获取打开主页的 PendingIntent
     */
    private PendingIntent getOpenPendingIntent(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, NoteTableActivity.class);
        // 不生效 无法传递来源...
//        intent.putExtra("from", NoteTableActivity.FROM_WIDGET);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        return pi;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_UPDATE.equals(intent.getAction())) {
            // 更新
            if (idsSet.isEmpty()) {
                // ids为空 有俩种情况 一种确实没有桌面小部件
                // 另外一种是重启了App 此时系统不会执行update 导致idsSet没有初始化
                // 这时候需要读取第一次缓存的ids
                idsSet.addAll(getCacheIdSet());
            }
            updateAllAppWidgets(context, AppWidgetManager.getInstance(context), idsSet);
        }
    }

    // 更新所有的 widget
    private void updateAllAppWidgets(Context context, AppWidgetManager appWidgetManager, Set set) {
        // widget 的id
        int appID;
        // 迭代器，用于遍历所有保存的widget的id
        for (Object o : set) {
            appID = (Integer) o;
            // 获取 example_appwidget.xml 对应的RemoteViews
            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.note_widget_normal);
            uiSet(remoteView);
            // 设置点击按钮对应的PendingIntent：即点击按钮时，发送广播。
            remoteView.setOnClickPendingIntent(R.id.note_ll_widget_container, getOpenPendingIntent(context));
            // 更新 widget
            appWidgetManager.updateAppWidget(appID, remoteView);
        }
    }

    private void uiSet(RemoteViews remoteView) {
        // 设置显示数字
        NoteBean widgetData = NoteService.getInstance().getWidgetData();
        if (widgetData == null) {
            ToastUtil.toast("你还没有设置桌面小部件笔记哦～");
            remoteView.setTextViewText(R.id.note_tv_widget_text, "");
            return;
        }
        remoteView.setTextViewText(R.id.note_tv_widget_text, widgetData.getContent());
    }

    /**
     * 每删除一次窗口小部件就调用一次
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    /**
     * 当最后一个该窗口小部件删除时调用该方法
     */
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    /**
     * 当该窗口小部件第一次添加到桌面时调用该方法
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    /**
     * 当小部件大小改变时
     */
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    /**
     * 当小部件从备份恢复时调用该方法
     */
    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
    }

    /**
     * 刷新桌面小部件
     */
    public static void update() {
        Intent intent = new Intent(NoteWidget.ACTION_UPDATE);
        intent.setComponent(new ComponentName(AppTracker.getCurApplication(), NoteWidget.class));// 必须写 8.0以上要求
        AppTracker.getCurApplication().sendBroadcast(intent);
    }
}
