package com.example.suivichantier;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Transaction;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.List;

@Dao
public interface ChantierDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Chantier chantier);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Chantier> chantiers);

    @Query("SELECT * FROM chantier")
    List<Chantier> getAllChantiers();

    @Query("SELECT * FROM chantier where entrepriseproprietaireID=:id")
    List<Chantier> getAllChantiers(int id);

    @Query("SELECT * FROM chantier where chantierID=:id")
    Chantier getChantierByID(int id);


}
