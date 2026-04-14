package com.lesadrax.registrationclient.from.model;

import com.lesadrax.registrationclient.from.utils.FormUtils;

import java.io.Serializable;

public class FormValue implements Serializable {

    private Object value;
    private String display;
    private Object remoteValue;
    private String parseType;
    private boolean otherValue;

    public FormValue(Object value, String display, Object remoteValue, String parseType) {
        this.value = value;
        this.display = display;
        this.remoteValue = remoteValue;
        this.parseType = parseType;
    }

    public FormValue(Object value, String display, Object remoteValue) {
        this.value = value;
        this.display = display;
        this.remoteValue = remoteValue;
        this.parseType = "string";
    }

    public FormValue(Object value, String display, Object remoteValue, boolean otherValue, String parseType) {
        this.value = value;
        this.display = display;
        this.remoteValue = remoteValue;
        this.otherValue = otherValue;
        this.parseType = parseType;
    }

    public Object getValue() {
        return value;
    }

    public int getValueInt(){
        return FormUtils.getInt(value);
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public Object getRemoteValue() {
        return remoteValue;
    }

    public String getParseType() {
        return parseType;
    }

    public void setParseType(String parseType) {
        this.parseType = parseType;
    }

    public boolean isOtherValue() {
        return otherValue;
    }

    public void setOtherValue(boolean otherValue) {
        this.otherValue = otherValue;
    }

    public void setRemoteValue(Object remoteValue) {
        this.remoteValue = remoteValue;
    }

    @Override
    public String toString() {
        return "FormValue{" +
                "value=" + value +
                ", display='" + display + '\'' +
                ", remoteValue=" + remoteValue +
                '}';
    }
}
