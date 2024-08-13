package com.example.suivichantier;

public class ListItemChantier {
    private String nom;
    private int chantierID;
    private String chantierDescription;



    public ListItemChantier(String nom, int chantierID) {
        this.nom = nom;
        this.chantierID = chantierID;

    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getChantierID() {
        return chantierID;
    }

    public void setChantierID(int chantierID) {
        this.chantierID = chantierID;
    }


}