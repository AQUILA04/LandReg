package com.lesadrax.registrationclient.ui.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lesadrax.registrationclient.MyApp;
import com.lesadrax.registrationclient.data.model.Actor;
import com.lesadrax.registrationclient.databinding.ActivityActorsBinding;
import com.lesadrax.registrationclient.ui.adapter.ActorAdapter;

import java.util.ArrayList;
import java.util.List;

public class ActorsActivity extends AppCompatActivity {

    private ActorAdapter adapter;
    private final List<Actor> actors = new ArrayList<>();

    private ActivityActorsBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityActorsBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.back.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        b.add.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddActorActivity.class);
            startActivity(intent);
        });

        adapter = new ActorAdapter(this, actors);
        b.rvMembers.setLayoutManager(new LinearLayoutManager(this));
        b.rvMembers.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();

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

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}


