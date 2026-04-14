package com.lesadrax.registrationclient.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lesadrax.registrationclient.MyApp;
import com.lesadrax.registrationclient.data.model.Actor;
import com.lesadrax.registrationclient.databinding.ActivitySyncActorBinding;
import com.lesadrax.registrationclient.ui.adapter.ActorAdapter;
import com.lesadrax.registrationclient.ui.dialog.SyncDialog;

import java.util.ArrayList;
import java.util.List;

public class SyncActorActivity extends AppCompatActivity {

    private ActorAdapter adapter;
    private final List<Actor> actors = new ArrayList<>();

    private ActivitySyncActorBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivitySyncActorBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.back.setOnClickListener(v -> {
            onBackPressed();
        });


        adapter = new ActorAdapter(this, actors, true);
        b.rv.setLayoutManager(new LinearLayoutManager(this));
        b.rv.setAdapter(adapter);

        b.btn.setOnClickListener(v -> {

            List<Actor> data = adapter.getSelectedData();
            if (!data.isEmpty()){
                SyncDialog dialog = new SyncDialog(this, data, actors.size());
                dialog.setOnDismissListener(dialog1 -> load());
                dialog.show();
            }

        });

        load();
    }


    private void load(){
        AsyncTask.execute(() -> {
            actors.clear();
            actors.addAll(MyApp.getDatabase().actorDao().getAllActors());
            runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
//                b.opText.setText((operation == null) ? R.string.bcst : R.string.ccst);
            });
        });
    }
}