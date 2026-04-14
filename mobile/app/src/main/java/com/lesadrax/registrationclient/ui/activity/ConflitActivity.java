package com.lesadrax.registrationclient.ui.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.lesadrax.registrationclient.MyApp;
import com.lesadrax.registrationclient.R;
import com.lesadrax.registrationclient.data.model.Actor;
import com.lesadrax.registrationclient.data.model.Operation;
import com.lesadrax.registrationclient.databinding.ActivityAddActorBinding;
import com.lesadrax.registrationclient.databinding.ActivityConflitBinding;
import com.lesadrax.registrationclient.from.model.FormValue;
import com.lesadrax.registrationclient.from.utils.FormUtils;

import java.util.Map;

public class ConflitActivity extends AppCompatActivity {

    private Operation data;

    private ActivityConflitBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = ActivityConflitBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        data = (Operation) getIntent().getSerializableExtra("DATA");

        b.form.setRes(R.raw.form_op_2);
        b.form.build();

        if (data != null){
            b.form.buildField(data.getFormValues());
        }

        b.back.setOnClickListener(v -> {
            onBackPressed();
        });
        b.btn.setOnClickListener(v -> {

            Map<String, FormValue> data = b.form.getFormData();

            if (data != null) {
                save(data);
            }
        });
    }

    boolean saving;
    private void save(Map<String, FormValue> formData){
        if (saving) return;
        saving = true;
        AsyncTask.execute(() -> {


            if (formData.get("niupPartieConflit1") != null) {
                data.setConflitTag(formData.get("niupPartieConflit1").getDisplay());
            }

            Map<String, FormValue> newData = data.getFormValues();

            newData.putAll(formData);

            data.setFormValues(newData);


            MyApp.getDatabase().operationDao().updateOperation(data);

            runOnUiThread(() -> {
                finish();
            });
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FormUtils.FilePickerResult(this, requestCode, resultCode, data);
    }

}