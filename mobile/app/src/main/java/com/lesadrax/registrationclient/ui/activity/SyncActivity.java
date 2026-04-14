package com.lesadrax.registrationclient.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import com.lesadrax.registrationclient.databinding.ActivitySyncBinding;

public class SyncActivity extends AppCompatActivity {

    private ActivitySyncBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = ActivitySyncBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.actors.setOnClickListener(v -> {
            startActivity(new Intent(this, SyncActorActivity.class));
        });

        b.op.setOnClickListener(v->{
            startActivity(new Intent(this, SynchOperationActivity.class));
        });

        b.back.setOnClickListener(v->{
            onBackPressed();
        });

//        b.op.setOnClickListener(v -> {
//            startActivity(new Intent(this, Sync.class));
//        });

    }
}