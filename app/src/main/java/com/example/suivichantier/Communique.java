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
public class Communique {

    @PrimaryKey
    private int communiqueID;

    private String communique;
    private String description;
    private String date;
    private int lotID; // Clé étrangère vers le lot associé

    // Constructeurs, getters et setters


    public Communique() {
    }
    @Ignore
    public Communique(int communiqueID, String communique, String description, String date, int lotID) {
        this.communiqueID = communiqueID;
        this.communique = communique;
        this.description = description;
        this.date = date;
        this.lotID = lotID;
    }

    public int getCommuniqueID() {
        return communiqueID;
    }

    public void setCommuniqueID(int communiqueID) {
        this.communiqueID = communiqueID;
    }

    public String getCommunique() {
        return communique;
    }

    public void setCommunique(String communique) {
        this.communique = communique;
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
