package com.lesadrax.registrationclient.from.utils;

import com.lesadrax.registrationclient.from.model.FormValue;

import java.util.Map;

public class FormDataUtils {


    public static FormValue getFormValue(Map<String, FormValue> data, String key){
        if (data == null)
            return null;
        if (!data.containsKey(key))
            return null;
        return data.get(key);
    }

    public static String getFormValueDisplay(Map<String, FormValue> data, String key){
        FormValue value = getFormValue(data, key);
        if (value == null)
            return null;

        return value.getDisplay();
    }

    public static Object getFormValueValue(Map<String, FormValue> data, String key){
        FormValue value = getFormValue(data, key);
        if (value == null)
            return null;

        return value.getValue();
    }

}
