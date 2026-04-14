package com.lesadrax.registrationclient.from.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lesadrax.registrationclient.from.model.FormField;
import com.lesadrax.registrationclient.from.model.FormValue;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.security.cert.PKIXRevocationChecker;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FormFieldParser {

    public static List<FormField> parseFormFields(Context context, int res) {
        try {
            // Open the JSON file from res/raw
            InputStream inputStream = context.getResources().openRawResource(res);
            InputStreamReader reader = new InputStreamReader(inputStream);

            // Define the type of list
            Type listType = new TypeToken<List<FormField>>() {}.getType();

            // Parse the JSON using Gson
            Gson gson = new Gson();
            List<FormField> formFields = gson.fromJson(reader, listType);

            // Close the reader
            reader.close();

            return formFields;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static Map<String, FormValue> trimData(List<FormField> fields, Map<String, FormValue> data) {

        // PROTECTION 1 : Si la liste des champs est null, retourner un HashMap vide
        if (fields == null) {
            Log.e("FormFieldParser", "trimData: fields list is null, returning empty map");
            return new HashMap<>();
        }

        // PROTECTION 2 : Si la map des données est null, retourner un HashMap vide
        if (data == null) {
            Log.e("FormFieldParser", "trimData: data map is null, returning empty map");
            return new HashMap<>();
        }

        Map<String, FormValue> result = new HashMap<>();

        // Collect the field names into a set for quick lookup
        Set<String> fieldNames = new HashSet<>();
        for (FormField field : fields) {
            fieldNames.add(field.getName());
        }

        // Filter the data map to include only the entries with keys in the field names set
        for (Map.Entry<String, FormValue> entry : data.entrySet()) {
            if (fieldNames.contains(entry.getKey())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }

        return result;
    }

}
