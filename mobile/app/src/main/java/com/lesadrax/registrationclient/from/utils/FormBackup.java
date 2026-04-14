package com.lesadrax.registrationclient.from.utils;

import android.util.Log;

import com.lesadrax.registrationclient.from.model.FormField;
import com.lesadrax.registrationclient.from.model.FormValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FormBackup {

    public static Map<String, FormValue> backupActor(JSONObject object, List<FormField> fields){
        Map<String, FormValue> data = backup(object, fields);
        Map<String, FormValue> result = backup(object, fields);

        if(object.has("fingerprintStores") && !object.isNull("fingerprintStores")){
            Log.d("****V", "======> ");
            try {
                JSONArray fingerprintArray = object.getJSONArray("fingerprintStores");
                // Parcours du tableau JSON
                for (int i = 0; i < fingerprintArray.length(); i++) {
                    JSONObject fingerprint = fingerprintArray.getJSONObject(i);

                    if(i == 0){
                        Log.d("****V", "======> 1");
                        result.put("fingerFirstName", new FormValue(fingerprint.getString("fingerStr"), fingerprint.getString("fingerStr"), fingerprint.getString("fingerStr")));
                    }
                    if(i == 1){
                        Log.d("****V", "======> 2");
                        result.put("fingerSecondName", new FormValue(fingerprint.getString("fingerStr"), fingerprint.getString("fingerStr"), fingerprint.getString("fingerStr")));
                    }

                    if(i == 2){
                        Log.d("****V", "======> 3");
                        result.put("fingerThirdName", new FormValue(fingerprint.getString("fingerStr"), fingerprint.getString("fingerStr"), fingerprint.getString("fingerStr")));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for (Map.Entry<String, FormValue> entry : data.entrySet()) {
            if (entry.getKey().equals("representativeFullname")){
                result.put("representative", entry.getValue());
            }
            if (entry.getKey().equals("secondaryRepresentativeFullname")){
                result.put("secondaryRepresentative", entry.getValue());
            }
            if (entry.getKey().equals("thirdRepresentativeFullname")){
                result.put("thirdRepresentative", entry.getValue());
            }
            if (entry.getKey().equals("witnessFullname")){
                result.put("witness", entry.getValue());
            }

        }

        return result;
    }

    public static Map<String, FormValue> backup(JSONObject object, List<FormField> fields){

        Map<String, FormValue> data = new HashMap<>();
        Map<String, Object> keyValueMap = iterateJson(object);

        Log.e("RRRRRRR1", fields+"");

        if (fields != null) {
            for (Map.Entry<String, Object> entry : keyValueMap.entrySet()) {
                Object value = entry.getValue();
                if (value != null && value != JSONObject.NULL) {
//                    Log.d("RRRRRRR3", "Key: " + entry.getKey() + ", Valueee: " + value+" "+Objects.isNull(value));

                    FormField field = findField(fields, entry.getKey());
                    if (field != null) {
                        if (field.getType().equals("spinner")) {

                            Log.d("RRRRRRR", "Key: " + entry.getKey() + ", Value: " + value + "Type: "+ field.getType());

                            if (field.getDataType() == null) {
                                if (value instanceof String) {
                                    FormField.FormOption option = findOptionByName(field, (String) value);
                                    Log.d("****Ici", "====> 1"+ entry.getKey());
                                    if (option != null) {
                                        Log.d("****Ici", "====> 1"+ option.getKey()+" option "+option.getName()+" "+option.getId()+" "+field.getParseType());
                                        data.put(entry.getKey(), new FormValue(option.getId(), option.getName(), option.getKey(), field.getParseType()));
                                    }
                                }
                            } else if (field.getDataType() == FormField.DataType.KEY) {
                                FormField.FormOption option = findOptionByKey(field, value);
                                Log.d("****Ici", "====> 2"+ entry.getKey());;
                                if (option != null) {
                                    data.put(entry.getKey(), new FormValue(option.getId(), option.getName(), option.getKey(), field.getParseType()));
                                }
                            } else if (field.getDataType() == FormField.DataType.ID) {
                                FormField.FormOption option = findOptionByID(field, toID(value));
                                Log.d("****Ici", "====> 3"+ entry.getKey());
                                if (option != null) {
                                    data.put(entry.getKey(), new FormValue(option.getId(), option.getName(), option.getId(), field.getParseType()));
                                }
                            } else {
                                Log.d("****Ici", "====> 4"+ entry.getKey());
                                insetGeneric(data, entry.getKey(), value, field.getParseType());
                            }

                        } else if (field.getType().equals("radio")) {

                            if (field.getDataType() == null) {
                                insetGeneric(data, entry.getKey(), value, field.getParseType());
                            } else if (field.getDataType() == FormField.DataType.KEY) {
                                FormField.FormOption option = findOptionByKey(field, value);
                                if (option != null) {
                                    data.put(entry.getKey(), new FormValue(option.getId(), option.getName(), option.getKey(), field.getParseType()));
                                }
                            } else if (field.getDataType() == FormField.DataType.ID) {
                                FormField.FormOption option = findOptionByID(field, toID(value));
                                if (option != null) {
                                    data.put(entry.getKey(), new FormValue(option.getId(), option.getName(), option.getId(), field.getParseType()));
                                }
                            } else if (field.getDataType() == FormField.DataType.BOOL) {
                                Boolean b = toBoolean(value);
                                if (b != null) {
                                    data.put(entry.getKey(), new FormValue(b ? 1 : 0, "", b, field.getParseType()));
                                }
                            }

                        } else {
                            insetGeneric(data, entry.getKey(), value, field.getParseType());
                        }
                    } else {
                        insetGeneric(data, entry.getKey(), value, null);
                    }
                }
            }
        }

        return data;
    }

    private static void insetGeneric(Map<String, FormValue> data, String key, Object value, String parseType){
        if (value instanceof String) {
            data.put(key, new FormValue(value, (String) value,value, parseType));
        } else {
            data.put(key, new FormValue(value, String.valueOf(value), value, parseType));
        }
    }

    // Recursive method to iterate through JSON and return a map
    private static Map<String, Object> iterateJson(JSONObject jsonObject) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object value = jsonObject.get(key);

                if (value instanceof JSONObject) {
                    // If value is JSONObject, merge its map into the result
                    resultMap.putAll(iterateJson((JSONObject) value));
                } else if (value instanceof JSONArray) {
                    // If value is JSONArray, process it and merge its map
                    resultMap.putAll(iterateJsonArray(key, (JSONArray) value));
                } else {
                    // Add key-value pair to the map
                    resultMap.put(key, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    // Helper method to iterate JSONArray and return a map
    private static Map<String, Object> iterateJsonArray(String parentKey, JSONArray jsonArray) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                Object value = jsonArray.get(i);

                if (value instanceof JSONObject) {
                    // If value is JSONObject, merge its map into the result
                    resultMap.putAll(iterateJson((JSONObject) value));
                } else if (value instanceof JSONArray) {
                    // If value is JSONArray, recurse and merge its map
                    resultMap.putAll(iterateJsonArray(parentKey + "[" + i + "]", (JSONArray) value));
                } else {
                    // Add indexed key-value pair to the map
                    resultMap.put(parentKey + "[" + i + "]", value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    public static  FormField findField(List<FormField> data, String key){
        for (FormField f : data){
            if (f != null)
                if (f.getName().equals(key))
                    return f;
        }

        return null;
    }

    public static FormField.FormOption findOptionByKey(FormField data, Object key){
        if (data.getOptions() == null) return null;
        for (FormField.FormOption f : data.getOptions()){
            if (f.getKey() != null)
                if (f.getKey().equals(key))
                    return f;
        }

        return null;
    }

    public static FormField.FormOption findOptionByID(FormField data, long id){
        if (data.getOptions() == null) return null;
        for (FormField.FormOption f : data.getOptions()){
            if (f != null)
                if (f.getId() == id)
                    return f;
        }

        return null;
    }

    public static FormField.FormOption findOptionByName(FormField data, String name){
        if (data.getOptions() == null) return null;
        for (FormField.FormOption f : data.getOptions()){
            if (f.getName() != null)
                if (f.getName().equalsIgnoreCase(name))
                    return f;
        }

        return null;
    }

    private static long toID(Object object){

        if (object instanceof Long){
            return (Long) object;
        } else if (object instanceof Integer){
            return  (Integer) object;
        } else {
            return 0;
        }
    }

    private static Boolean toBoolean(Object object){

        if (object instanceof Boolean){
            return (Boolean) object;
        } else {
            return null;
        }
    }

    public static boolean isnull(JSONObject o, String key) throws JSONException {
        return o.isNull(key);
    }

}