package com.lesadrax.registrationclient.from.model;

public class ListItem {
    private String id;
    private String value;

    public ListItem(String id, String value) {
        this.id = id;
        this.value = value;
    }

    // Getters
    public String getId() { return id; }
    public String getValue() { return value; }

    @Override
    public String toString() {
        return value; // Pour l'affichage dans l'AutoCompleteTextView
    }
}
