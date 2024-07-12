package com.example.suivichantier;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index("lotID")},foreignKeys = {
        @ForeignKey(entity = Lot.class,
                parentColumns = "lotID",
                childColumns = "lotID",
                onDelete = ForeignKey.CASCADE)
})
public class PlanExecution {

    @PrimaryKey
    private int planExecutionID;

    private String file;
    private String description;
    private int lotID; // Clé étrangère vers le lot associé

    // Constructeurs, getters et setters


    public PlanExecution() {
    }

    @Ignore
    public PlanExecution(int planExecutionID, String file, String description, int lotID) {
        this.planExecutionID = planExecutionID;
        this.file = file;
        this.description = description;
        this.lotID = lotID;
    }

    public int getPlanExecutionID() {
        return planExecutionID;
    }

    public void setPlanExecutionID(int planExecutionID) {
        this.planExecutionID = planExecutionID;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLotID() {
        return lotID;
    }

    public void setLotID(int lotID) {
        this.lotID = lotID;
    }
}
