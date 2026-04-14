package com.lesadrax.registrationclient.from.model;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Form field model
public class FormField {
    private String[] extras;
    private String name;
    private String label;
    private String type;
    private String parseType;
    private String mime;
    private int lines;
    private int maxLines;
    private DataType dataType;
    private boolean required;
    private int maxSize;
    private VisibilityCondition visibilityCondition;
    private List<FormOption> options;
    private List<FormField> childs;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLinkName(){
        return getName()+"#";
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParseType() {
        return parseType;
    }

    public void setParseType(String parseType) {
        this.parseType = parseType;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public int getLines() {
        return lines;
    }

    public void setLines(int lines) {
        this.lines = lines;
    }

    public int getMaxLines() {
        return maxLines;
    }

    public void setMaxLines(int maxLines) {
        this.maxLines = maxLines;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public VisibilityCondition getVisibilityCondition() {
        return visibilityCondition;
    }

    public String[] getExtras() {
        return extras;
    }

    public void setExtras(String[] extras) {
        this.extras = extras;
    }

    public void setVisibilityCondition(VisibilityCondition visibilityCondition) {
        this.visibilityCondition = visibilityCondition;
    }

    public List<FormField> getChilds() {
        return childs;
    }

    public void setChilds(List<FormField> childs) {
        this.childs = childs;
    }

    public List<FormOption> getOptions() {
        if (options == null)
            options = new ArrayList<>();
        return options;
    }

    public void setOptions(List<FormOption> options) {
        this.options = options;
    }


    public static List<FormField> deserializeFormData(Context context, int res){


        List<FormField> data;

        InputStream inputStream = context.getResources().openRawResource(res);

        if (inputStream == null)
            return null;

        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            String jsonString = builder.toString();

            Gson gson = new Gson();
            FormField[] raw = gson.fromJson(jsonString, FormField[].class);


            data = Arrays.asList(raw);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return data;
    }

    // Form Option. Ex: Spinner item
    public static class FormOption {
        private long id;
        private String name;

        private Object key;

        public FormOption(long id, String name) {
            this.id = id;
            this.name = name;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getKey() {
            return key;
        }

        public void setKey(Object key) {
            this.key = key;
        }

        @NonNull
        @Override
        public String toString() {
            return getName();
        }
    }

    // Visibility config class
    public static class VisibilityCondition {
        private String ref;
        private String event;
        private boolean defaultVisible;
        private int[] elements;

        public String getRef() {
            return ref;
        }

        public void setRef(String ref) {
            this.ref = ref;
        }

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        public boolean isDefaultVisible() {
            return defaultVisible;
        }

        public void setDefaultVisible(boolean defaultVisible) {
            this.defaultVisible = defaultVisible;
        }

        public int[] getElements() {
            return elements;
        }

        public void setElements(int[] elements) {
            this.elements = elements;
        }
    }

    public enum DataType {
        ID,
        BOOL,
        KEY,

        TEXT;

    }

}