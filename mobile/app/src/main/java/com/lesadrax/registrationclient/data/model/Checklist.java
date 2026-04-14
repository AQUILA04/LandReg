package com.lesadrax.registrationclient.data.model;

import java.io.Serializable;
import java.util.List;

public class Checklist implements Serializable {
    private int id;
    private String mayorUIN;
    private String traditionalChiefUIN;
    private String notableUIN;
    private String geometerUIN;
    private String ownerUIN;

    private String topographerUIN;

    private String socialLandAgentUIN;

    private String interestedThirdPartyUIN;

    private List<Bordering> borderingList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getters et Setters
    public String getMayorUIN() { return mayorUIN; }
    public void setMayorUIN(String mayorUIN) { this.mayorUIN = mayorUIN; }
    public String getTraditionalChiefUIN() { return traditionalChiefUIN; }
    public void setTraditionalChiefUIN(String traditionalChiefUIN) { this.traditionalChiefUIN = traditionalChiefUIN; }
    public String getNotableUIN() { return notableUIN; }
    public void setNotableUIN(String notableUIN) { this.notableUIN = notableUIN; }
    public String getGeometerUIN() { return geometerUIN; }
    public void setGeometerUIN(String geometerUIN) { this.geometerUIN = geometerUIN; }
    public String getOwnerUIN() { return ownerUIN; }
    public void setOwnerUIN(String ownerUIN) { this.ownerUIN = ownerUIN; }

    public String getTopographerUIN() {
        return topographerUIN;
    }

    public void setTopographerUIN(String topographerUIN) {
        this.topographerUIN = topographerUIN;
    }

    public String getSocialLandAgentUIN() {
        return socialLandAgentUIN;
    }

    public void setSocialLandAgentUIN(String socialLandAgentUIN) {
        this.socialLandAgentUIN = socialLandAgentUIN;
    }

    public String getInterestedThirdPartyUIN() {
        return interestedThirdPartyUIN;
    }

    public void setInterestedThirdPartyUIN(String interestedThirdPartyUIN) {
        this.interestedThirdPartyUIN = interestedThirdPartyUIN;
    }

    public List<Bordering> getBorderingList() { return borderingList; }
    public void setBorderingList(List<Bordering> borderingList) { this.borderingList = borderingList; }


    @Override
    public String toString() {
        return "Checklist{" +
                "id=" + id +
                ", mayorUIN='" + mayorUIN + '\'' +
                ", traditionalChiefUIN='" + traditionalChiefUIN + '\'' +
                ", notableUIN='" + notableUIN + '\'' +
                ", geometerUIN='" + geometerUIN + '\'' +
                ", ownerUIN='" + ownerUIN + '\'' +
                ", topographerUIN='" + topographerUIN + '\'' +
                ", socialLandAgentUIN='" + socialLandAgentUIN + '\'' +
                ", interestedThirdPartyUIN='" + interestedThirdPartyUIN + '\'' +
                ", borderingList=" + borderingList +
                '}';
    }
}


