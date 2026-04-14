package com.lesadrax.registrationclient.data.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.lesadrax.registrationclient.data.repository.db.Converters;
import com.lesadrax.registrationclient.from.model.FormValue;

import java.io.Serializable;
import java.util.Map;

@Entity(tableName = "actors")
public class Actor implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String role;
    private String tag;
    private String name;
    private boolean person;
    private boolean synced;
    private String message;
    private String deleted;

    @Ignore
    private boolean selected;

    @Ignore
    private int personID = 0;

    @Ignore
    private int finger1ID = 0;

    @Ignore
    private int finger2ID = 0;

    @Ignore
    private int finger3ID = 0;

    @Ignore
    private int docID = 0;



    @TypeConverters(Converters.class) // Link the TypeConverter
    private Map<String, FormValue> formValues;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPerson() {
        return person;
    }

    public void setPerson(boolean person) {
        this.person = person;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public Map<String, FormValue> getFormValues() {
        return formValues;
    }

    public int getPersonID() {
        return personID;
    }

    public void setPersonID(int personID) {
        this.personID = personID;
    }

    public void setFormValues(Map<String, FormValue> formValues) {
        this.formValues = formValues;
    }


    public int getFinger1ID() {
        return finger1ID;
    }

    public void setFinger1ID(int finger1ID) {
        this.finger1ID = finger1ID;
    }

    public int getFinger2ID() {
        return finger2ID;
    }

    public void setFinger2ID(int finger2ID) {
        this.finger2ID = finger2ID;
    }

    public int getFinger3ID() {
        return finger3ID;
    }

    public void setFinger3ID(int finger3ID) {
        this.finger3ID = finger3ID;
    }

    public int getDocID() {
        return docID;
    }

    public void setDocID(int docID) {
        this.docID = docID;
    }
}
