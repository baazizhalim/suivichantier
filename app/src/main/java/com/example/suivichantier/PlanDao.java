package com.example.suivichantier;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PlanDao {
    @Insert
    void insert(Plan plan );

    @Query("SELECT * FROM plan")
    List<Plan> getAllPlans();
}
