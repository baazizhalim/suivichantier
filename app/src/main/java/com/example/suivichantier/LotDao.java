package com.example.suivichantier;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LotDao {
    @Insert
    void insert(Lot lot);

    @Query("SELECT * FROM lot")
    List<Lot> getAllLots();
}
