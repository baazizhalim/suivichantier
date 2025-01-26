package com.example.suivichantier;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index("markID")},foreignKeys = {
        @ForeignKey(entity = Mark.class,
                parentColumns = "markID",
                childColumns = "markID",
                onDelete = ForeignKey.CASCADE),
        })
public class Photo {

    @PrimaryKey @NonNull
    private String photoID;
    private String file;
    private String date;
    private String markID; // Clé étrangère vers la mark associé
    private int entrepriseID; // Clé étrangère vers l'entrepise associé

    //private boolean  synchro;

    public Photo() {
    }
    @Ignore
    public Photo(String photoID, String file, String date, String markID, int entrepriseID) {
        this.photoID = photoID;
        this.file = file;
        this.date = date;
        this.markID = markID;
        this.entrepriseID = entrepriseID;
        //this.synchro=false;
    }

    public String getPhotoID() {
        return photoID;
    }

    public void setPhotoID(String photoID) {
        this.photoID = photoID;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMarkID() {
        return markID;
    }

    public void setMarkID(String markID) {
        this.markID = markID;
    }

    public int getEntrepriseID() {
        return entrepriseID;
    }

    public void setEntrepriseID(int entrepriseID) {
        this.entrepriseID = entrepriseID;
    }

//    public boolean isSynchro() {
//        return synchro;
//    }
//
//    public void setSynchro(boolean synchro) {
//        this.synchro = synchro;
//    }
}
