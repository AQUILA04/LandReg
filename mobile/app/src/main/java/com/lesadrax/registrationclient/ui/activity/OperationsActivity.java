package com.lesadrax.registrationclient.ui.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lesadrax.registrationclient.MyApp;
import com.lesadrax.registrationclient.data.model.Actor;
import com.lesadrax.registrationclient.data.model.Operation;
import com.lesadrax.registrationclient.databinding.ActivityActorsBinding;
import com.lesadrax.registrationclient.databinding.ActivityOperationsBinding;
import com.lesadrax.registrationclient.ui.SignatureActivity;
import com.lesadrax.registrationclient.ui.adapter.ActorAdapter;
import com.lesadrax.registrationclient.ui.adapter.OperationAdapter;

import java.util.ArrayList;
import java.util.List;

public class OperationsActivity extends AppCompatActivity {

    private OperationAdapter adapter;
    private final List<Operation> data = new ArrayList<>();

    private ActivityOperationsBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityOperationsBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.back.setOnClickListener(v -> {
            onBackPressed();
        });

        b.add.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignatureActivity.class).putExtra("before", "before");
            startActivity(intent);
        });

        adapter = new OperationAdapter(this, data, false);
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
            data.clear();
            data.addAll(MyApp.getDatabase().operationDao().getAllOperations());
            runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
//                b.opText.setText((operation == null) ? R.string.bcst : R.string.ccst);
            });
        });
    }
}
