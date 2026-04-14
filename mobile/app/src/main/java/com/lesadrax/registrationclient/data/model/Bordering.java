package com.lesadrax.registrationclient.data.model;

import java.io.Serializable;

public class Bordering implements Serializable {
    private int id;
    private String cardinalPoint;
    private String uin;

    // Constructeur, getters et setters
    public Bordering(String cardinalPoint, String uin) {
        this.cardinalPoint = cardinalPoint;
        this.uin = uin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCardinalPoint() { return cardinalPoint; }
    public void setCardinalPoint(String cardinalPoint) { this.cardinalPoint = cardinalPoint; }
    public String getUin() { return uin; }
    public void setUin(String uin) { this.uin = uin; }


    @Override
    public String toString() {
        return "Bordering{" +
                "id=" + id +
                ", cardinalPoint='" + cardinalPoint + '\'' +
                ", uin='" + uin + '\'' +
                '}';
    }
}
