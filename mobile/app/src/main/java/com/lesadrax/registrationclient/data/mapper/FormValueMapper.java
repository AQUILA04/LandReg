package com.lesadrax.registrationclient.data.mapper;

import com.lesadrax.registrationclient.from.model.FormValue;

import java.util.Map;

public final class FormValueMapper {

    private FormValueMapper() {
    }

    public static String getString(Map<String, FormValue> form, String key) {
        FormValue value = form.get(key);
        if (value == null || value.getRemoteValue() == null) {
            return null;
        }
        return value.getRemoteValue().toString();
    }

    public static Integer getInteger(Map<String, FormValue> form, String key) {
        FormValue value = form.get(key);
        if (value == null || value.getRemoteValue() == null) {
            return null;
        }
        Object remoteValue = value.getRemoteValue();
        if (remoteValue instanceof Integer) {
            return (Integer) remoteValue;
        }
        if (remoteValue instanceof Double) {
            return ((Double) remoteValue).intValue();
        }
        if (remoteValue instanceof String) {
            try {
                return Integer.valueOf((String) remoteValue);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    public static Boolean getBoolean(Map<String, FormValue> form, String key) {
        FormValue value = form.get(key);
        if (value == null || value.getRemoteValue() == null) {
            return null;
        }
        Object remoteValue = value.getRemoteValue();
        if (remoteValue instanceof Boolean) {
            return (Boolean) remoteValue;
        }
        if (remoteValue instanceof String) {
            return Boolean.parseBoolean((String) remoteValue);
        }
        return null;
    }

    public static String getDisplay(Map<String, FormValue> form, String key) {
        FormValue value = form.get(key);
        if (value == null) {
            return null;
        }
        return value.getDisplay();
    }
}
