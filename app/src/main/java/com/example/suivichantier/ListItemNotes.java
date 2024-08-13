package com.example.suivichantier;

public class ListItemNotes {
    private String chantier;
    private int lotID;
    private String lotDescription;



    public ListItemNotes(String chantier, int lotID, String lotDescription) {
        this.chantier = chantier;
        this.lotID = lotID;
        this.lotDescription = lotDescription;
    }

    public String getChantier() {
        return chantier;
    }

    public void setChantier(String chantier) {
        this.chantier = chantier;
    }

    public int getLotID() {
        return lotID;
    }

    public void setLotID(int lotID) {
        this.lotID = lotID;
    }

    public String getLotDescription() {
        return lotDescription;
    }

    public void setLotDescription(String lotDescription) {
        this.lotDescription = lotDescription;
    }
}