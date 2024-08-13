package com.example.suivichantier;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PlanDao {
    @Insert
    void insert(Plan plan );
    @Insert
    void insertAll(List<Plan> plans);
    @Query("SELECT * FROM plan")
    List<Plan> getAllPlans();

    @Query("SELECT * FROM plan where lotID=:lotID")
    List<Plan> getAllPlans(int lotID);
}
