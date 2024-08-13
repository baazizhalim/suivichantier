package com.example.suivichantier;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MarkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Mark mark );

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Mark> marks);

    @Query("SELECT * FROM mark")
    List<Mark> getAllMarks();

    @Query("SELECT * FROM mark where planID=:planID")
    List<Mark> getAllMarks(int planID);

    @Delete
    void delete(Mark mark );

    @Update
    void update(Mark mark );
}
