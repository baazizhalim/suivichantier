package com.example.suivichantier;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index("chantierID")},foreignKeys = {
        @ForeignKey(entity = Chantier.class,
                parentColumns = "chantierID",
                childColumns = "chantierID",
                onDelete = ForeignKey.CASCADE)
})
public class Pv {

    @PrimaryKey
    private int pvID;
    private String file;
    private String description;
    private String date;
    private int chantierID; // Clé étrangère vers le lot associé

    // Constructeurs, getters et setters




    public Pv() {
    }
@Ignore
    public Pv(int pvID, String file, String description, String date, int chantierID) {
        this.pvID = pvID;
        this.file = file;
        this.description = description;
        this.date = date;
        this.chantierID = chantierID;
    }

    public int getPvID() {
        return pvID;
    }

    public void setPvID(int pvID) {
        this.pvID = pvID;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getChantierID() {
        return chantierID;
    }

    public void setChantierID(int chantierID) {
        this.chantierID = chantierID;
    }
}
