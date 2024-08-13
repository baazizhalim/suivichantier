package com.example.suivichantier;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index("markID"),@Index("entrepriseID")},foreignKeys = {
        @ForeignKey(entity = Mark.class,
                parentColumns = "markID",
                childColumns = "markID",
                onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Entreprise.class,
                parentColumns = "entrepriseID",
                childColumns = "entrepriseID",
                onDelete = ForeignKey.CASCADE)

})
public class Photo {

    @PrimaryKey
    private int photoID;
    private String file;
    private String date;
    private String markID; // Clé étrangère vers la mark associé
    private int entrepriseID; // Clé étrangère vers l'entrepise associé
    // Constructeurs, getters et setters


    public Photo() {
    }
    @Ignore
    public Photo(int photoID, String file, String date, String markID, int entrepriseID) {
        this.photoID = photoID;
        this.file = file;
        this.date = date;
        this.markID = markID;
        this.entrepriseID = entrepriseID;
    }

    public int getPhotoID() {
        return photoID;
    }

    public void setPhotoID(int photoID) {
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
}
