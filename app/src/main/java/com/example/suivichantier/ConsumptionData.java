package com.example.suivichantier;

public class ConsumptionData {
    private String type;
    private int nbr;

    public ConsumptionData (String type, int nbr ){
        this.type=type;
        this.nbr=nbr;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getNbr() {
        return nbr;
    }

    public void setNbr(int nbr) {
        this.nbr = nbr;
    }
}

