package com.example.suivichantier;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MarkDao {
    @Insert
    void insert(Mark mark );

    @Query("SELECT * FROM mark")
    List<Mark> getAllMarks();
}
