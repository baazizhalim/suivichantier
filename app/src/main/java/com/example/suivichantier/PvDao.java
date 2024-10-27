package com.example.suivichantier;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PvDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Pv pv );
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Pv> pvs);
    @Query("SELECT * FROM pv")
    List<Pv> getAllPvs();

    @Query("SELECT * FROM pv where chantierID=:chantierID")
    List<Pv> getAllPvs(int chantierID);
}
