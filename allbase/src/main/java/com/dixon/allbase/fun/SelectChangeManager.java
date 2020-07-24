package com.dixon.allbase.fun;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 提供类似RadioButton的功能：选中一个，其他全部取消
 * <p>
 * 自身有防内存泄漏的机制
 * 提供选中和取消选中俩种回调供调用方自行实现
 *
 * @param <T>
 */
public abstract class SelectChangeManager<T> {

    private Map<String, WeakReference<T>> map = new HashMap<>();

    public void put(String tag, T obj) {
        // 每次put 都会校验 把值为null的key清除调
        clearGC();
        map.put(tag, new WeakReference<T>(obj));
    }

    public void setSelected(String tag) {
        for (String key : map.keySet()) {
            T obj = map.get(key).get();
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

    public void clearGC() {
        for (String key : map.keySet()) {
            T obj = map.get(key).get();
            if (obj == null) {
                map.remove(key);
            }
        }
    }
}
