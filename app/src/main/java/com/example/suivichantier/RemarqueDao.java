package com.example.suivichantier;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RemarqueDao {
    @Insert
    void insert(Remarque remarque );

    @Query("SELECT * FROM remarque")
    List<Remarque> getAllRemarques();
}
