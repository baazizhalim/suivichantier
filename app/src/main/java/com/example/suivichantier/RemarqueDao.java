package com.example.suivichantier;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RemarqueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Remarque remarque );
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Remarque> remarques);
    @Query("SELECT * FROM remarque")
    List<Remarque> getAllRemarques();
}
