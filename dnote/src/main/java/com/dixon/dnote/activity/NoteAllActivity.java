package com.dixon.dnote.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ListView;

import com.dixon.allbase.bean.NoteBean;
import com.dixon.allbase.model.RouterConstant;
import com.dixon.dlibrary.util.AnimationUtil;
import com.dixon.dlibrary.util.FontUtil;
import com.dixon.dlibrary.util.ToastUtil;
import com.dixon.dnote.R;
import com.dixon.dnote.adapter.NoteAllTableAdapter;
import com.dixon.dnote.core.NoteService;
import com.dixon.dnote.view.TouchBackView;
import com.dixon.simple.router.api.SimpleRouter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@SimpleRouter(value = RouterConstant.NOTE_ALL, interceptor = "")
public class NoteAllActivity extends Activity {

    private View mAllCard;
    private ListView mTableView;
    private View mAllContainer;
    private NoteAllTableAdapter mTableAdapter;
    private TouchBackView mTouchView;

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
        mTouchView = findViewById(R.id.note_tbv_all_table_move);
    }

    private void initView() {
        animIn();
        mAllContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFinish();
            }
        });
        addFootView();
        initMoveView();
    }

    private void initMoveView() {
        mTouchView.setDirection(TouchBackView.DIRECTION_TOP_RIGHT);
        // 移动窗口
        mTouchView.setOnTouchBackListener(new TouchBackView.OnSimpleTouchBackListener() {
            @Override
            public void onTouch(int offsetX, int offsetY) {
                // 不能使用offset方法 进入编辑态会重置视图 貌似是临时的移动
                mAllCard.setX(mAllCard.getX() + offsetX);
                mAllCard.setY(mAllCard.getY() + offsetY);
            }
        });
    }

    private void addFootView() {
        View footView = LayoutInflater.from(this).inflate(R.layout.note_float_ui_all_list_foot, null);
        View addView = footView.findViewById(R.id.note_iv_all_foot_add);
        addView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.toast("添加新笔记");
                NoteBean noteBean = new NoteBean();
                noteBean.setContent("尚未编辑");
                noteBean.setPriority(NoteBean.PRIORITY_NOT_IN_HURRY);
                noteBean.setTime(new Date().getTime());
                noteBean.setId(System.currentTimeMillis());
                noteBean.setTag(NoteBean.TAG_NORMAL);
                NoteService.getInstance().addData(noteBean, new NoteService.FinishCallback() {
                    @Override
                    public void onFinish() {
                        loadData();
                    }
                });
            }
        });
        mTableView.addFooterView(footView);
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