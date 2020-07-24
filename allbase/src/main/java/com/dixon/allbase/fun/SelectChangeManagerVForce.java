package com.dixon.allbase.fun;

import java.util.HashMap;
import java.util.Map;

/**
 * 提供类似RadioButton的功能：选中一个，其他全部取消
 * <p>
 * 强制引用版本 需要自行回收
 * 主要为了适配put(xxx,new xxx)局部变量被回收的情况
 *
 * @param <T>
 */
public abstract class SelectChangeManagerVForce<T> {

    private Map<String, T> map = new HashMap<>();

    public void put(String tag, T obj) {
        map.put(tag, obj);
    }

    public void setSelected(String tag) {
        for (String key : map.keySet()) {
            T obj = map.get(key);
            if (obj != null) {
                if (key.equals(tag)) {
                    selected(key, obj);
                } else {
                    clearSelected(key, obj);
                }
            }
        }
    }

    protected abstract void selected(String tag, T obj);

    protected abstract void clearSelected(String tag, T obj);

    public void clear() {
        map.clear();
    }

    public void remove(String tag) {
        map.remove(tag);
    }
}
