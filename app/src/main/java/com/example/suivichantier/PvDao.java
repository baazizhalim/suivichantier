package com.example.suivichantier;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PvDao {
    @Insert
    void insert(Pv pv );

    @Query("SELECT * FROM pv")
    List<Pv> getAllPvs();
}
