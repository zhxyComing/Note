package com.dixon.dnote.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.dixon.allbase.bean.NoteBean;
import com.dixon.dlibrary.util.FontUtil;
import com.dixon.dlibrary.util.ToastUtil;
import com.dixon.dnote.R;
import com.dixon.dnote.core.NoteService;
import com.dixon.dnote.event.NoteTableRefreshEvent;
import com.dixon.dnote.view.NoteDeleteDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by: dixon.xu
 * Create on: 2020.07.13
 * Functional desc: NoteTable 列表
 */
public class NoteAllTableAdapter extends BaseAdapter {

    private List<NoteBean> mItems;
    private Context mContext;

    public NoteAllTableAdapter(List<NoteBean> items, Context context) {
        this.mItems = items;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderNote holderNote = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_note_table_note_all, null);
            holderNote = new ViewHolderNote(convertView);
            convertView.setTag(holderNote);
        } else {
            holderNote = (ViewHolderNote) convertView.getTag();
        }
        setNoteItem(holderNote, mItems.get(position));
        if (position % 2 == 0) {
            holderNote.itemBg.setBackgroundColor(mContext.getResources().getColor(R.color.colorWhite));
        } else {
            holderNote.itemBg.setBackgroundColor(mContext.getResources().getColor(R.color.colorWhiteGrey));
        }
        return convertView;
    }

    private void setNoteItem(final ViewHolderNote holderNote, final NoteBean noteBean) {
        holderNote.contentView.setText(noteBean.getContent());
        holderNote.inputView.setText(noteBean.getContent());
        setTagView(holderNote.tagView, noteBean.getTag());
        holderNote.itemCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeItemToEdit(holderNote);
            }
        });
        holderNote.itemCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                NoteDeleteDialog.showDialog(noteBean, new NoteDeleteDialog.Listener() {
                    @Override
                    public void onDeleteSuccess() {
                        refreshList();
                        EventBus.getDefault().post(new NoteTableRefreshEvent());
                    }
                });
                return true;
            }
        });
        holderNote.saveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String content = holderNote.inputView.getText().toString();
                if (content.equals(holderNote.contentView.getText().toString())) {
                    // 回归原始状态
                    changeItemToOrigin(holderNote);
                    return;
                }
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.toast("内容不能为空");
                    return;
                }
                noteBean.setContent(content);
                NoteService.getInstance().updateData(noteBean, new NoteService.FinishCallback() {
                    @Override
                    public void onFinish() {
                        EventBus.getDefault().post(new NoteTableRefreshEvent());
                        holderNote.contentView.setText(content);
                        ToastUtil.toast("更新成功");
                    }
                });
                // 回归原始状态
                changeItemToOrigin(holderNote);
            }
        });
        // 回归原始态
        changeItemToOrigin(holderNote);
    }

    private void changeItemToOrigin(ViewHolderNote holderNote) {
        holderNote.inputView.setVisibility(View.GONE);
        holderNote.contentView.setVisibility(View.VISIBLE);
        holderNote.saveView.setVisibility(View.GONE);
        holderNote.tagView.setVisibility(View.VISIBLE);
    }

    // 切换到编辑态
    private void changeItemToEdit(ViewHolderNote holderNote) {
        holderNote.inputView.setVisibility(View.VISIBLE);
        holderNote.contentView.setVisibility(View.INVISIBLE);
        holderNote.inputView.setFocusable(true);
        holderNote.inputView.requestFocus();
        holderNote.saveView.setVisibility(View.VISIBLE);
        holderNote.tagView.setVisibility(View.GONE);
    }

    private void setTagView(TextView tagView, int tag) {
        switch (tag) {
            case NoteBean.TAG_NORMAL:
                tagView.setBackgroundResource(R.drawable.note_shape_circle_tag_normal);
                tagView.setText("普");
                tagView.setTextColor(Color.parseColor("#909090"));
                break;
            case NoteBean.TAG_STUDY:
                tagView.setBackgroundResource(R.drawable.note_shape_circle_tag_study);
                tagView.setText("学");
                tagView.setTextColor(Color.parseColor("#ffffff"));
                break;
            case NoteBean.TAG_LIFE:
                tagView.setBackgroundResource(R.drawable.note_shape_circle_tag_life);
                tagView.setText("生");
                tagView.setTextColor(Color.parseColor("#ffffff"));
                break;
            case NoteBean.TAG_WORK:
                tagView.setBackgroundResource(R.drawable.note_shape_circle_tag_work);
                tagView.setText("工");
                tagView.setTextColor(Color.parseColor("#ffffff"));
                break;
            case NoteBean.TAG_WARN:
                tagView.setBackgroundResource(R.drawable.note_shape_circle_tag_warn);
                tagView.setText("警");
                tagView.setTextColor(Color.parseColor("#ffffff"));
                break;
        }
    }

    private static final class ViewHolderNote {

        private TextView contentView;
        private TextView tagView;
        private View itemCardView;
        private EditText inputView;
        private View itemBg;
        private View saveView;

        public ViewHolderNote(View container) {
            contentView = container.findViewById(R.id.note_tv_content_desc);
            tagView = container.findViewById(R.id.note_tv_tag_desc);
            itemCardView = container.findViewById(R.id.note_ll_item_card);
            inputView = container.findViewById(R.id.note_et_content_desc);
            itemBg = container.findViewById(R.id.note_ll_all_item_bg);
            saveView = container.findViewById(R.id.note_iv_all_item_save);
            FontUtil.font(inputView, contentView, tagView);
        }

    }

    public void reloadData(List<NoteBean> items) {
        this.mItems = items;
        notifyDataSetChanged();
    }

    /**
     * 刷新列表
     */
    private void refreshList() {
        NoteService.getInstance().queryAll(new NoteService.ResponseCallback<List<NoteBean>>() {
            @Override
            public void onSuccess(List<NoteBean> data) {
                // 更新笔记数量
                reloadData(data);
            }

            @Override
            public void onFail(String desc) {
                ToastUtil.toast(desc);
                // 给个空列表
                reloadData(new ArrayList<NoteBean>());
            }
        });
    }
}
