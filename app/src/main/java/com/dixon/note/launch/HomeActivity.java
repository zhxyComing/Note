package com.dixon.note.launch;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.dixon.allbase.base.BaseActivity;
import com.dixon.allbase.bean.NoteBean;
import com.dixon.allbase.fun.SelectChangeManagerVForce;
import com.dixon.allbase.fun.TimeFormat;
import com.dixon.allbase.model.RouterConstant;
import com.dixon.dlibrary.util.ToastUtil;
import com.dixon.note.R;
import com.dixon.note.databinding.ActivityHomeBinding;
import com.dixon.note.launch.inter.HomePresent;
import com.dixon.note.launch.inter.IHomePresent;
import com.dixon.note.launch.inter.IHomeView;
import com.dixon.note.util.DropHelper;
import com.dixon.note.view.MenuLayout;
import com.dixon.note.view.NoteEditView;
import com.dixon.note.view.NoteListView;
import com.dixon.note.view.NoteViewPager;
import com.dixon.note.view.SetLayout;
import com.dixon.simple.router.api.SimpleRouter;

import java.util.List;

import cn.refactor.lib.colordialog.PromptDialog;

/**
 * Create by: dixon.xu
 * Create on: 2020.06.16
 * Functional desc: Home页 即首页
 * <p>
 * 本来是想作为桌面笔记App的首页 结果效果实在是不好 改成启动后直接跳转NoteTableActivity笔记列表页
 * <p>
 * 本页废弃(页面暂时保留 功能已经比较完善了)
 * app模块只剩下启动页 即app模块后续只作为壳模块
 */
@SimpleRouter(value = RouterConstant.HOME, interceptor = "")
public class HomeActivity extends BaseActivity implements IHomeView, MenuLayout.OnMenuTabClickListener, SetLayout.OnItemClickListener {

    // Tab的标志（id）
    private static final String TAB_NOTE_DISPLAY = "note";
    private static final String TAB_UPCOMING = "upcoming";  //待办
    private static final String TAB_NOTE_EDIT = "edit";
    private static final String TAB_PUNCH = "punch"; // 打卡
    private static final String TAB_SET = "set";

    private static final String SET_TAB_ABOUT = "about";

    private ActivityHomeBinding binding;

    private IHomePresent mPresent;

    /**
     * 单选菜单栏的行为管理
     * <p>
     * 一个菜单展开时，控制其它菜单收起
     */
    private SelectChangeManagerVForce<ModelAction> mModelActions = new SelectChangeManagerVForce<ModelAction>() {
        @Override
        protected void selected(String tag, ModelAction obj) {
            obj.open();
        }

        @Override
        protected void clearSelected(String tag, ModelAction obj) {
            obj.close();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mPresent = new HomePresent(this);
        mPresent.loadData();

        initView();
    }

    private void initView() {
        // 笔记展示UI
        initNoteDisplayView();
        // 菜单栏UI
        initMenu();
        // 笔记编辑栏
        initNoteEdit();
        // 设置
        initSet();
        // 菜单栏切换行为定义
        initModelActions();
    }

    private void initSet() {
        binding.appHomeSetLayout.putItem(SET_TAB_ABOUT, "关于", R.mipmap.icon_about, this);
    }

    /**
     * 初始化单选菜单组件行为管理
     */
    private void initModelActions() {
        mModelActions.clear();
        // 笔记展示页在点击菜单时的响应
        // 如open表示笔记展示菜单点击时不响应
        // close表示其它菜单点击时笔记菜单如果展开了，则收起
        mModelActions.put(TAB_NOTE_DISPLAY, new ModelAction() {
            @Override
            public void open() {
                // 笔记展示页 笔记默认不是不展开的
//                binding.appHomeMask.hide();
            }

            @Override
            public void close() {
                if (binding.appHomeVp.isDropOpen() || binding.appHomeVp.isOpening()) {
                    binding.appHomeVp.dropClose();
                }
            }
        });
        // 笔记编辑页
        mModelActions.put(TAB_NOTE_EDIT, new ModelAction() {
            @Override
            public void open() {
                if (binding.appHomeNoteEdit.isDropClose() || binding.appHomeNoteEdit.isClosing()) {
                    binding.appHomeNoteEdit.dropOpen();
                }
//                binding.appHomeMask.show();
            }

            @Override
            public void close() {
                // 只要没关闭 打开动画中也关闭 但是关闭动画不打断
                if (binding.appHomeNoteEdit.isDropOpen() || binding.appHomeNoteEdit.isOpening()) {
                    binding.appHomeNoteEdit.dropClose();
                }
            }
        });
        // 设置页
        mModelActions.put(TAB_SET, new ModelAction() {
            @Override
            public void open() {
                if (binding.appHomeSetLayout.isDropClose() || binding.appHomeSetLayout.isClosing()) {
                    binding.appHomeSetLayout.dropOpen();
                }
//                binding.appHomeMask.show();
            }

            @Override
            public void close() {
                if (binding.appHomeSetLayout.isDropOpen() || binding.appHomeSetLayout.isOpening()) {
                    binding.appHomeSetLayout.dropClose();
                }
            }
        });
    }

    private void initNoteEdit() {
        // 保存笔记
        binding.appHomeNoteEdit.setOnUpdateClickListener(new NoteEditView.OnUpdateClickListener() {
            @Override
            public void onNewCommit(String text) {
                if (TextUtils.isEmpty(text)) {
                    ToastUtil.toast("请正确输入笔记内容～");
                    return;
                }
                NoteBean noteBean = new NoteBean();
                long time = System.currentTimeMillis();
                noteBean.setId(time);
                noteBean.setTime(time);
                noteBean.setContent(text);
                mPresent.saveData(noteBean);
                hideInput();
                binding.appHomeMenu.setTab(TAB_NOTE_DISPLAY);
            }

            @Override
            public void onUpdate(NoteBean noteBean) {
                if (TextUtils.isEmpty(noteBean.getContent())) {
                    ToastUtil.toast("请正确输入笔记内容～");
                    return;
                }
                mPresent.updateData(noteBean);
                hideInput();
                binding.appHomeMenu.setTab(TAB_NOTE_DISPLAY);
            }
        });
    }

    /**
     * 菜单栏UI
     */
    private void initMenu() {
        binding.appHomeMenu.putMenu(TAB_NOTE_DISPLAY, "笔记", R.mipmap.icon_note, true, this);
        binding.appHomeMenu.putMenuInvisible(TAB_UPCOMING, "待办", R.mipmap.icon_upcoming, false, this);
        binding.appHomeMenu.putMenu(TAB_NOTE_EDIT, "写笔记", R.mipmap.icon_edit, false, this);
        binding.appHomeMenu.putMenuInvisible(TAB_PUNCH, "打卡", R.mipmap.icon_punch, false, this);
        binding.appHomeMenu.putMenu(TAB_SET, "设置", R.mipmap.icon_set, false, this);
    }

    /**
     * 初始化笔记展示UI
     */
    private void initNoteDisplayView() {
        // 笔记缩略ViewPager及监听
        binding.appHomeVp.addOnNoteChangedListener(new NoteViewPager.OnNoteChangedListener() {
            @Override
            public void onChanged(NoteBean noteBean) {
                binding.appHomeLv.setCurrentItem(noteBean);
                binding.appHomeLv.setNoteChecked(noteBean);
            }
        });
        binding.appHomeVp.setOnItemClickListener(new NoteViewPager.OnItemClickListener() {
            @Override
            public void onDeleteClick(NoteBean noteBean) {
                showDeleteDialog(noteBean);
            }

            @Override
            public void onEditClick(NoteBean noteBean) {
                goEdit(noteBean);
            }
        });

        // 笔记时间线ListView及监听
        binding.appHomeLv.setOnNoteClickListener(new NoteListView.OnNoteClickListener() {
            @Override
            public void onClick(NoteBean noteBean) {
                binding.appHomeVp.setCurrentItem(noteBean);
                binding.appHomeLv.setNoteChecked(noteBean);
            }
        });

        // 下拉drop
        binding.appHomeDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.appHomeVp.drop();
                // 点击了选中 说明不编辑或设置了 切换回笔记展示tab
                binding.appHomeMenu.setTab(TAB_NOTE_DISPLAY);
            }
        });
        // 下拉结束监听
        binding.appHomeVp.setOnDropListener(new DropHelper.OnDropListenerAdapter() {
            @Override
            public void finish() {
                super.finish();
                // 下拉结束
                binding.appHomeDrop.rotateBy(180);
            }
        });

        binding.appHomeVp.dropOpenRightNow();
        // 时间提醒文案
        binding.appHomeTimeDesc.setText(TimeFormat.dayDesc());

//        binding.appHomeMask.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                binding.appHomeMask.hide();
//                binding.appHomeMenu.setTab(TAB_NOTE_DISPLAY);
//            }
//        });
    }

    /**
     * 加载笔记展示数据
     */
    private void loadNoteDisplayData(final List<NoteBean> notes) {
        // 笔记缩略ViewPager及监听
        binding.appHomeVp.setData(notes);
        // 笔记时间线ListView及监听
        binding.appHomeLv.setData(notes);
    }

    /**
     * 设置笔记数据
     */
    @Override
    public void setNoteDisplayData(List<NoteBean> notes) {
        loadNoteDisplayData(notes);
        // 展示第一个数据
        binding.appHomeVp.setCurrentItem(notes.get(0));
        binding.appHomeLv.setCurrentItem(notes.get(0));
    }

    @Override
    public void showToast(String msg) {
        ToastUtil.toast(msg);
    }

    /**
     * 菜单栏点击事件
     *
     * @param tag         菜单功能的Tag
     * @param menuFunView 菜单功能的View 实际类型的MenuFunView
     */
    @Override
    public void onTabClick(String tag, View menuFunView) {
        if (tag.equals(TAB_NOTE_EDIT) && binding.appHomeNoteEdit.isDropOpen()) {
            binding.appHomeMenu.setTab(TAB_NOTE_DISPLAY);
        } else {
            mModelActions.setSelected(tag);
            if (tag.equals(TAB_NOTE_DISPLAY)) {
                binding.appHomeVp.drop();
            }
        }
    }

    /**
     * 设置栏点击事件
     *
     * @param tag        设置功能的Tag
     * @param setFunView 设置功能的View 实际类型为SetFunView
     */
    @Override
    public void onItemClick(String tag, View setFunView) {
        switch (tag) {
            case SET_TAB_ABOUT:
                ToastUtil.toast("桌面便签 V1.0.0");
                break;
        }
    }

    /**
     * 用于UI单选管理
     * <p>
     * 如选中笔记展示 则笔记编辑和设置收起
     * 如选中笔记编辑 则设置和笔记展示收起
     */
    private interface ModelAction {

        void open();

        void close();
    }

    @Override
    public void onBackPressed() {
        if (binding.appHomeNoteEdit.isDropOpen() || binding.appHomeSetLayout.isDropOpen()) {
            binding.appHomeMenu.setTab(TAB_NOTE_DISPLAY);
            return;
        }
        super.onBackPressed();
    }

    /**
     * 删除提醒Dialog
     */
    private void showDeleteDialog(final NoteBean noteBean) {
        new PromptDialog(this)
                .setDialogType(PromptDialog.DIALOG_TYPE_WARNING)
                .setAnimationEnable(true)
                .setTitleText("删除提醒")
                .setContentText("删除后不可恢复！（点击空白区域以退出）")
                .setPositiveListener("好的，继续删除", new PromptDialog.OnPositiveListener() {
                    @Override
                    public void onClick(PromptDialog dialog) {
                        dialog.dismiss();
                        mPresent.deleteData(noteBean);
                    }
                }).show();
    }

    /**
     * 打开编辑
     */
    private void goEdit(NoteBean noteBean) {
        binding.appHomeNoteEdit.setEditNote(noteBean);
        binding.appHomeMenu.setTab(TAB_NOTE_EDIT);
    }

    private void hideInput() {
        // 隐藏输入法
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}