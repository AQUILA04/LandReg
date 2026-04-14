package com.lesadrax.registrationclient.ui.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lesadrax.registrationclient.MyApp;
import com.lesadrax.registrationclient.data.model.Actor;
import com.lesadrax.registrationclient.databinding.ActivityActorPickerBinding;
import com.lesadrax.registrationclient.from.model.FormValue;
import com.lesadrax.registrationclient.ui.adapter.ActorPickerAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActorPickerActivity extends AppCompatActivity {

    private ActorPickerAdapter adapter;
    private final List<Actor> actors = new ArrayList<>();


    private ActivityActorPickerBinding b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityActorPickerBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        String fieldId = getIntent().getStringExtra("fieldId");

        b.back.setOnClickListener(v -> onBackPressed());

        adapter = new ActorPickerAdapter(this, actors);
        b.rv.setLayoutManager(new LinearLayoutManager(this));
        b.rv.setAdapter(adapter);

        adapter.setOnSelectItemListener(item -> {

            Map<String, FormValue> data = new HashMap<>();
//            intent.putExtra("DATA", data);

            data.put(fieldId, new FormValue(item.getTag(), item.getName(), item.getTag(), "string"));
            data.put(fieldId+"UIN", new FormValue(item.getTag(), item.getName(), item.getTag(), "string"));
            data.put(fieldId+"Fullname", new FormValue(item.getTag(), item.getName(), item.getName(), "string"));

            // Broadcast the result
            Intent broadcastIntent = new Intent("PICKER_SELECTED");
            broadcastIntent.putExtra("fieldId", fieldId);
            broadcastIntent.putExtra("DISPLAY", item.getName());
            broadcastIntent.putExtra("DATA", (Serializable) data);


            LocalBroadcastManager.getInstance(this)
                    .sendBroadcast(broadcastIntent);
            finish();
        });

        load();
    }

    private void load(){
        AsyncTask.execute(() -> {
            actors.clear();
            actors.addAll(MyApp.getDatabase().actorDao().getAllPersonActors());
            runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
//                b.opText.setText((operation == null) ? R.string.bcst : R.string.ccst);
            });
        });
    }
}