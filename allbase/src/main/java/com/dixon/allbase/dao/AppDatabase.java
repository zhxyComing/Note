package com.dixon.allbase.dao;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.dixon.allbase.bean.NoteBean;

/**
 * Create by: dixon.xu
 * Create on: 2020.06.19
 * Functional desc: App的数据库集
 */
@Database(entities = {NoteBean.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "note_db";
    private static volatile AppDatabase sInstance;

    public abstract NoteDao noteDao();

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context,
                            AppDatabase.class, DB_NAME).build();
                }
            }
        }
        return sInstance;
    }
}
