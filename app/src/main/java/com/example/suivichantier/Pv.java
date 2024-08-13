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
public class Pv {

    @PrimaryKey
    private int pvID;
    private String pv;
    private String description;
    private String date;
    private int lotID; // Clé étrangère vers le lot associé

    // Constructeurs, getters et setters


    public Pv() {
    }
@Ignore
    public Pv(int pvID, String pv, String description, String date, int lotID) {
        this.pvID = pvID;
        this.pv = pv;
        this.description = description;
        this.date = date;
        this.lotID = lotID;
    }

    public int getPvID() {
        return pvID;
    }

    public void setPvID(int pvID) {
        this.pvID = pvID;
    }

    public String getPv() {
        return pv;
    }

    public void setPv(String pv) {
        this.pv = pv;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getLotID() {
        return lotID;
    }

    public void setLotID(int lotID) {
        this.lotID = lotID;
    }
}
