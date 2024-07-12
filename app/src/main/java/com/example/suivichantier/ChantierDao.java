package com.example.suivichantier;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ChantierDao {
    @Insert
    void insert(Chantier chantier);

    @Query("SELECT * FROM chantier")
    List<Chantier> getAllChantiers();
}
