package com.dixon.note.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.dixon.allbase.bean.NoteBean;
import com.dixon.dlibrary.util.FontUtil;
import com.dixon.dlibrary.util.ScreenUtil;
import com.dixon.note.databinding.ItemHomeViewPagerBinding;
import com.dixon.note.inter.IDrop;
import com.dixon.note.util.DropHelper;

import java.util.List;

/**
 * Create by: dixon.xu
 * Create on: 2020.06.16
 * Functional desc: Home页ViewPager 用于展示Note 并能根据指定Note切换对应页码
 * 主要为了抽离封装
 */
public class NoteViewPager extends ViewPager implements IDrop {

    private SparseArray<Item> mItems = new SparseArray<>();

    private List<NoteBean> mNotes;

    private DropHelper mDropHelper;
    private OnItemClickListener mItemClickListener;

    public NoteViewPager(@NonNull Context context) {
        super(context);
        init();
    }

    public NoteViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setAdapter(notePagerAdapter);
        // 屏幕高度 - 想空余的高度 = 最大高度
        int maxHeight = ScreenUtil.getDisplayHeight(getContext()) - ScreenUtil.dpToPxInt(getContext(), 380);
        // 设置默认高度
        int originHeight = ScreenUtil.dpToPxInt(getContext(), 220);

        mDropHelper = new DropHelper(this);
        mDropHelper.setDropHeight(originHeight, maxHeight);
        // 默认展开状态 但是UI要自己实现 因为DropHelper获取不到LayoutParams
    }

    private PagerAdapter notePagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            // 强转
            ItemHomeViewPagerBinding viewBinding = (ItemHomeViewPagerBinding) mItems.get(position).binding;
            setView(viewBinding, position);
            container.addView(viewBinding.getRoot());
            return viewBinding.getRoot();
        }

        private void setView(ItemHomeViewPagerBinding binding, final int position) {
            NoteBean noteBean = mItems.get(position).noteBean;
            binding.appVpItemText.setText(noteBean.getContent());
            // 动态添加的view要动态刷新font
            FontUtil.font(binding.getRoot());
            binding.appHomeVpDelete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onDeleteClick(mItems.get(position).noteBean);
                    }
                }
            });
            binding.appHomeVpEdit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onEditClick(mItems.get(position).noteBean);
                    }
                }
            });
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            //super.destroyItem(container, position, object);
            //删除页卡
            Item item = mItems.get(position);
            if (item != null) {
                container.removeView(item.binding.getRoot());
            }
        }
    };

    /**
     * 设置Note数据
     */
    public void setData(List<NoteBean> notes) {
        mNotes = notes;
        mItems.clear();
        for (int i = 0; i < notes.size(); i++) {
            Item item = new Item(notes.get(i), ItemHomeViewPagerBinding.inflate(LayoutInflater.from(getContext())));
            mItems.append(i, item);
        }
        // 这样开销会比较大 但是这个VP比较简单 所以实际还好
        // 之所以不用notifyDataSetChanged 是因为VP刷新无效...
        setAdapter(notePagerAdapter);
//        notePagerAdapter.notifyDataSetChanged();
    }

    /**
     * item内部的点击事件
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mItemClickListener = onItemClickListener;
    }

    /**
     * 根据NoteBean设置页码
     */
    public void setCurrentItem(NoteBean note) {
        for (int i = 0; i < mItems.size(); i++) {
            if (mItems.get(i).noteBean.equals(note)) {
                setCurrentItem(i, true);
            }
        }
    }

    /**
     * 存储Note和View的对应关系
     */
    private static class Item {

        private NoteBean noteBean;
        private ViewBinding binding;

        public Item(NoteBean noteBean, ViewBinding binding) {
            this.noteBean = noteBean;
            this.binding = binding;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "noteBean=" + noteBean +
                    ", binding=" + binding +
                    '}';
        }
    }

    // 上拉或下拉
    @Override
    public void drop() {
        mDropHelper.drop();
    }

    // 下拉展开
    @Override
    public void dropOpen() {
        mDropHelper.dropOpen();
    }

    public void dropOpenRightNow() {
        mDropHelper.dropOpenRightNow();
    }

    // 上拉收起
    @Override
    public void dropClose() {
        mDropHelper.dropClose();
    }

    /**
     * 判断是否下拉完成
     */
    @Override
    public boolean isDropOpen() {
        return mDropHelper.isDropOpen();
    }

    @Override
    public boolean isDropClose() {
        return mDropHelper.isDropClose();
    }

    @Override
    public boolean isOpening() {
        return mDropHelper.isOpening();
    }

    @Override
    public boolean isClosing() {
        return mDropHelper.isClosing();
    }

    public void setOnDropListener(DropHelper.OnDropListener dropListener) {
        mDropHelper.setOnDropListener(dropListener);
    }

    public void addOnNoteChangedListener(final OnNoteChangedListener listener) {
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (listener != null) {
                    listener.onChanged(mNotes.get(position));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public interface OnNoteChangedListener {
        void onChanged(NoteBean noteBean);
    }

    public interface OnItemClickListener {

        void onDeleteClick(NoteBean noteBean);

        void onEditClick(NoteBean noteBean);
    }
}
