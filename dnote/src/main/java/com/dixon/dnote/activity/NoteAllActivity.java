package com.dixon.dnote.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ListView;

import com.dixon.allbase.bean.NoteBean;
import com.dixon.allbase.fun.TimeUtil;
import com.dixon.allbase.model.RouterConstant;
import com.dixon.dlibrary.util.AnimationUtil;
import com.dixon.dlibrary.util.FontUtil;
import com.dixon.dlibrary.util.ToastUtil;
import com.dixon.dnote.R;
import com.dixon.dnote.adapter.NoteAllTableAdapter;
import com.dixon.dnote.bean.NoteTableItem;
import com.dixon.dnote.core.NoteService;
import com.dixon.simple.router.api.SimpleRouter;

import java.util.ArrayList;
import java.util.List;


@SimpleRouter(value = RouterConstant.NOTE_ALL, interceptor = "")
public class NoteAllActivity extends Activity {

    private View mAllCard;
    private ListView mTableView;
    private View mAllContainer;
    private NoteAllTableAdapter mTableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_all);

        initView();
        loadData();
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        FontUtil.font(getWindow().getDecorView());
        mTableView = findViewById(R.id.note_lv_all_table);
        mAllCard = findViewById(R.id.note_cv_all_card);
        mAllContainer = findViewById(R.id.note_cv_all_container);
    }

    private void initView() {
        animIn();
        mAllContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFinish();
            }
        });
    }

    private void animIn() {
        Animator tranAnimator = AnimationUtil.tranY(mAllCard, 300, 0, 300, new DecelerateInterpolator(), null);
        Animator alphaAnimator = AnimationUtil.alpha(mAllCard, 0, 1, 300, new DecelerateInterpolator(), null);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(tranAnimator, alphaAnimator);
        set.start();
    }

    private void animOut(AnimatorListenerAdapter animatorListener) {
        Animator tranAnimator = AnimationUtil.tranY(mAllCard, 0, 300, 300, new DecelerateInterpolator(), null);
        Animator alphaAnimator = AnimationUtil.alpha(mAllCard, 1, 0, 300, new DecelerateInterpolator(), null);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(tranAnimator, alphaAnimator);
        set.addListener(animatorListener);
        set.start();
    }

    private void loadData() {
        NoteService.getInstance().queryAll(new NoteService.ResponseCallback<List<NoteBean>>() {
            @Override
            public void onSuccess(List<NoteBean> data) {
                // 更新笔记数量
                loadNotes(data);
            }

            @Override
            public void onFail(String desc) {
                ToastUtil.toast(desc);
                // 给个空列表
                loadNotes(new ArrayList<NoteBean>());
            }
        });
    }

    private void loadNotes(List<NoteBean> res) {
        if (mTableAdapter == null) {
            mTableAdapter = new NoteAllTableAdapter(res, this);
            mTableView.setAdapter(mTableAdapter);
        } else {
            mTableAdapter.reloadData(res);
        }
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
    protected void onPause() {
        super.onPause();
    }

    private void startFinish() {
        animOut(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startFinish();
    }
}