package com.lesadrax.registrationclient.data.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.lesadrax.registrationclient.data.repository.db.ActorListConverter;
import com.lesadrax.registrationclient.data.repository.db.ChecklistConverter;
import com.lesadrax.registrationclient.data.repository.db.Converters;
import com.lesadrax.registrationclient.from.model.FormValue;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Entity(tableName = "operations")
public class Operation implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String tag;
    private String conflitTag;

    private boolean synced;

    private boolean isCompleted;

    private String message;

    private long conflitID;

    @Ignore
    private boolean selected;

    @TypeConverters(Converters.class) // Link the TypeConverter
    private Map<String, FormValue> formValues;

    @TypeConverters(ChecklistConverter.class)
    private Checklist checklistBeforeOperation;

    @TypeConverters(ChecklistConverter.class)
    private Checklist checklistAfterOperation;

    // Nouveau champ pour la liste d'acteurs (remplace persons)
    @TypeConverters(ActorListConverter.class)
    private List<ActorModel> actors;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public String getConflitTag() {
        return conflitTag;
    }

    public void setConflitTag(String conflitTag) {
        this.conflitTag = conflitTag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Map<String, FormValue> getFormValues() {
        return formValues;
    }

    public void setFormValues(Map<String, FormValue> formValues) {
        this.formValues = formValues;
    }

    public Checklist getChecklistBeforeOperation() {
        return checklistBeforeOperation;
    }

    public void setChecklistBeforeOperation(Checklist checklistBeforeOperation) {
        this.checklistBeforeOperation = checklistBeforeOperation;
    }

    public Checklist getChecklistAfterOperation() {
        return checklistAfterOperation;
    }

    public void setChecklistAfterOperation(Checklist checklistAfterOperation) {
        this.checklistAfterOperation = checklistAfterOperation;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public List<ActorModel> getActors() {
        return actors;
    }

    public void setActors(List<ActorModel> actors) {
        this.actors = actors;
    }

    public long getConflitID() {
        return conflitID;
    }

    public void setConflitID(long conflitID) {
        this.conflitID = conflitID;
    }
}
