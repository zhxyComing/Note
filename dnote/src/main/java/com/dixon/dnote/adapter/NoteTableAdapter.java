package com.dixon.dnote.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dixon.allbase.bean.NoteBean;
import com.dixon.allbase.fun.TimeFormat;
import com.dixon.dnote.R;
import com.dixon.dnote.bean.NoteTableItem;

import java.util.List;

/**
 * Create by: dixon.xu
 * Create on: 2020.07.13
 * Functional desc: NoteTable 列表
 */
public class NoteTableAdapter extends BaseAdapter {

    private List<NoteTableItem> mItems;
    private Context mContext;
    private OnItemFunctionClickListener mFunctionClickListener;

    public NoteTableAdapter(List<NoteTableItem> items, Context context) {
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
    public int getItemViewType(int position) {
        return mItems.get(position).getType();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        ViewHolderTime holderTime = null;
        ViewHolderNote holderNote = null;
        if (convertView == null) {
            switch (type) {
                case NoteTableItem.TYPE_TIME:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_note_table_time, null);
                    holderTime = new ViewHolderTime(convertView);
                    setTimeItem(holderTime, mItems.get(position).getTimeDesc());
                    convertView.setTag(holderTime);
                    break;
                case NoteTableItem.TYPE_NOTE:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_note_table_note, null);
                    holderNote = new ViewHolderNote(convertView);
                    setNoteItem(holderNote, mItems.get(position).getNoteBean());
                    convertView.setTag(holderNote);
                    break;
                default:
                    break;
            }
        } else {
            switch (type) {
                case NoteTableItem.TYPE_TIME:
                    holderTime = (ViewHolderTime) convertView.getTag();
                    setTimeItem(holderTime, mItems.get(position).getTimeDesc());
                    break;
                case NoteTableItem.TYPE_NOTE:
                    holderNote = (ViewHolderNote) convertView.getTag();
                    setNoteItem(holderNote, mItems.get(position).getNoteBean());
                    break;
                default:
                    break;
            }
        }
        return convertView;
    }

    private void setNoteItem(ViewHolderNote holderNote, final NoteBean noteBean) {
        holderNote.contentView.setText(noteBean.getContent());
        holderNote.timeDetailView.setText(TimeFormat.format(noteBean.getTime()));
        holderNote.desktopTagView.setVisibility(View.VISIBLE);
        setPriorityView(holderNote.priorityView, noteBean.getPriority());
        setTagView(holderNote.tagView, noteBean.getTag());
        holderNote.itemCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFunctionClickListener != null) {
                    mFunctionClickListener.onItemCardClick(noteBean);
                }
            }
        });
        holderNote.itemCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mFunctionClickListener != null) {
                    mFunctionClickListener.onItemCardLongClick(noteBean);
                }
                return true;
            }
        });
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

    private void setPriorityView(View priorityView, int priority) {
        switch (priority) {
            case NoteBean.PRIORITY_IMPORTANT:
                priorityView.setBackgroundColor(mContext.getResources().getColor(R.color.colorPriorityImportant));
                break;
            case NoteBean.PRIORITY_SECONDARY:
                priorityView.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrioritySecondary));
                break;
            case NoteBean.PRIORITY_ORDINARY:
                priorityView.setBackgroundColor(mContext.getResources().getColor(R.color.colorPriorityOrdinary));
                break;
            case NoteBean.PRIORITY_NOT_IN_HURRY:
                priorityView.setBackgroundColor(mContext.getResources().getColor(R.color.colorPriorityNotInHurry));
                break;
        }
    }

    private void setTimeItem(ViewHolderTime holderTime, String timeDesc) {
        holderTime.timeSimpleView.setText(timeDesc);
    }

    private static final class ViewHolderTime {

        private TextView timeSimpleView;

        public ViewHolderTime(View container) {
            timeSimpleView = container.findViewById(R.id.note_tv_time_simple_desc);
        }
    }

    private static final class ViewHolderNote {

        private TextView timeDetailView;
        private TextView contentView;
        private TextView tagView;
        private ImageView desktopTagView;
        private View priorityView;
        private View itemCardView;

        public ViewHolderNote(View container) {
            timeDetailView = container.findViewById(R.id.note_tv_time_detail_desc);
            contentView = container.findViewById(R.id.note_tv_content_desc);
            desktopTagView = container.findViewById(R.id.note_iv_desktop_tag);
            priorityView = container.findViewById(R.id.note_v_priority);
            tagView = container.findViewById(R.id.note_tv_tag_desc);
            itemCardView = container.findViewById(R.id.note_ll_item_card);
        }
    }

    public void reloadData(List<NoteTableItem> items) {
        this.mItems = items;
        notifyDataSetChanged();
    }

    public interface OnItemFunctionClickListener {

        void onItemCardClick(NoteBean noteBean);

        void onItemCardLongClick(NoteBean noteBean);
    }

    public void setOnItemFunctionClickListener(OnItemFunctionClickListener functionClickListener) {
        mFunctionClickListener = functionClickListener;
    }
}
