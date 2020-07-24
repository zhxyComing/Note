package com.dixon.note.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dixon.allbase.bean.NoteBean;
import com.dixon.allbase.fun.TimeFormat;
import com.dixon.allbase.view.TimeView;
import com.dixon.dlibrary.util.FontUtil;
import com.dixon.note.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by: dixon.xu
 * Create on: 2020.06.16
 * Functional desc: Home页NoteListView 支持通过Note设定位置
 * 主要为了抽离封装
 */
public class NoteListView extends ListView {

    private List<NoteWrapper> mNotes = new ArrayList<>();

    public NoteListView(Context context) {
        super(context);
        init();
    }

    public NoteListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NoteListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setDivider(null);
        setAdapter(noteListAdapter);
    }

    /**
     * 设置数据并刷新视图
     */
    public void setData(List<NoteBean> notes) {
        mNotes.clear();
        // 包装后添加到列表
        for (int i = 0; i < notes.size(); i++) {
            // 默认选中第一个
            if (i == 0) {
                mNotes.add(new NoteWrapper(notes.get(i), true));
                continue;
            }
            mNotes.add(new NoteWrapper(notes.get(i), false));
        }
        noteListAdapter.notifyDataSetChanged();
    }

    private BaseAdapter noteListAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mNotes.size();
        }

        @Override
        public Object getItem(int position) {
            return mNotes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mNotes.get(position).note.getId();
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_home_list_view, null);
                vh = new ViewHolder(convertView);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            NoteWrapper noteWrapper = mNotes.get(position);
            NoteBean note = noteWrapper.note;
            vh.titleView.setText(TimeFormat.format(note.getTime()));
            // 只显示第一行
            vh.contentView.setText(getFirstLineString(note.getContent()));
            // 设置选中状态
//            if (noteWrapper.checked) {
//                vh.checkedView.setImageResource(R.drawable.home_lv_select_checked);
//            } else {
//                vh.checkedView.setImageResource(R.drawable.home_lv_select_normal);
//            }
            vh.checkedView.setTime(note.getTime());
            return convertView;
        }
    };

    private String getFirstLineString(String content) {
        String[] split = content.split("\n");
        // 超过一行 第一行文字加省略号
        if (split.length > 1) {
            return String.format("%s...", content.split("\n")[0]);
        } else if (split.length > 0) {
            // 就一行 原样展示
            return content.split("\n")[0];
        }
        return "";
    }

    private static final class ViewHolder {

        private TextView titleView, contentView;
        private TimeView checkedView;

        public ViewHolder(View container) {
            titleView = container.findViewById(R.id.app_lv_item_title);
            contentView = container.findViewById(R.id.app_lv_item_content);
            checkedView = container.findViewById(R.id.app_lv_item_checked);
            // ListView是动态添加的 所以字体也要动态去设置
            FontUtil.font(container);
        }
    }

    /**
     * 设定Item显示位置
     */
    public void setCurrentItem(NoteBean noteBean) {
        for (int i = 0; i < mNotes.size(); i++) {
            if (mNotes.get(i).note.equals(noteBean)) {
                smoothScrollToPosition(i);
            }
        }
    }

    private static class NoteWrapper {

        private NoteBean note;
        private boolean checked;

        public NoteWrapper(NoteBean note, boolean checked) {
            this.note = note;
            this.checked = checked;
        }
    }

    /**
     * 设置选中状态
     */
    public void setNoteChecked(NoteBean note) {
        for (NoteWrapper noteWrapper : mNotes) {
            if (noteWrapper.note.equals(note)) {
                noteWrapper.checked = true;
            } else {
                noteWrapper.checked = false;
            }
        }
        noteListAdapter.notifyDataSetChanged();
    }

    public void setOnNoteClickListener(final OnNoteClickListener listener) {
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.onClick(mNotes.get(position).note);
            }
        });
    }

    public interface OnNoteClickListener {
        void onClick(NoteBean noteBean);
    }
}
