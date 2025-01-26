package com.example.suivichantier;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity(indices = {@Index("lotID")},foreignKeys = {
        @ForeignKey(entity = Lot.class,
                parentColumns = "lotID",
                childColumns = "lotID",
                onDelete = ForeignKey.CASCADE)
})
public class Plan {

    @PrimaryKey
    private int planID;

    private String file;
    private String description;
    private int lotID; // Clé étrangère vers le lot associé

    // Constructeurs, getters et setters


    public Plan() {
    }

    @Ignore
    public Plan(int planID, String file, String description, int lotID) {
        this.planID = planID;
        this.file = file;
        this.description = description;
        this.lotID = lotID;
    }

    public int getPlanID() {
        return planID;
    }

    public void setPlanID(int planID) {
        this.planID = planID;
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

    @Override
    public String toString() {

        String pattern = "\\d+([a-zA-Z ]+)"; // Expression régulière

        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(file);

        if (matcher.find()) {
            return matcher.group(1); // Cette méthode détermine ce qui est affiché dans le Spinner
        }
        return file;
    }
}