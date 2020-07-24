package com.dixon.allbase.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.dixon.allbase.bean.NoteBean;

import java.util.List;

@Dao
public interface NoteDao {

    // 查询所有note
    @Query("SELECT * FROM notebean")
    List<NoteBean> getAll();

    // 根据id查询note
    @Query("SELECT * FROM notebean WHERE id = :id")
    NoteBean findById(String id);

    //Update
    @Update
    void update(NoteBean user);

    //Insert
    @Insert
    void insert(NoteBean user);

    //Delete
    @Delete
    void delete(NoteBean user);
}