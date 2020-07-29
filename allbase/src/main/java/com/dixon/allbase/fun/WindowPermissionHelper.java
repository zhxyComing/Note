package com.dixon.allbase.fun;

import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;

import com.dixon.dlibrary.util.AppTracker;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Create by: dixon.xu
 * Create on: 2020.07.14
 * Functional desc: 悬浮窗帮助类
 */
public class WindowPermissionHelper {

    /**
     * 是否有悬浮窗权限
     */
    public static boolean hasPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                Class cls = Class.forName("android.content.Context");
                Field declaredField = cls.getDeclaredField("APP_OPS_SERVICE");
                declaredField.setAccessible(true);
                Object obj = declaredField.get(cls);
                if (!(obj instanceof String)) {
                    return false;
                }
                String str2 = (String) obj;
                obj = cls.getMethod("getSystemService", String.class).invoke(AppTracker.getCurApplication(), str2);
                cls = Class.forName("android.app.AppOpsManager");
                Field declaredField2 = cls.getDeclaredField("MODE_ALLOWED");
                declaredField2.setAccessible(true);
                Method checkOp = cls.getMethod("checkOp", Integer.TYPE, Integer.TYPE, String.class);
                int result = (Integer) checkOp.invoke(obj, 24, Binder.getCallingUid(), AppTracker.getCurApplication().getPackageName());
                return result == declaredField2.getInt(cls);
            } catch (Exception e) {
                return false;
            }
        } else {
            return Settings.canDrawOverlays(AppTracker.getCurApplication());
        }
    }

    /**
     * 跳转到悬浮窗权限设置页
     */
    public static void gotoSetPage() {
        if (AppTracker.getCurActivity() == null) {
            throw new NullPointerException("请在App内执行跳转");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                Class clazz = Settings.class;
                Field field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION");
                Intent intent = new Intent(field.get(null).toString());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("package:" + AppTracker.getCurActivity().getPackageName()));
                AppTracker.getCurActivity().startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", AppTracker.getCurActivity().getPackageName(), null));
            AppTracker.getCurActivity().startActivity(intent);
        }
    }
}
