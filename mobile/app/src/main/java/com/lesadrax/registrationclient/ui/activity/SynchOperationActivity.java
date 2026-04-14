package com.lesadrax.registrationclient.ui.activity;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lesadrax.registrationclient.MyApp;
import com.lesadrax.registrationclient.R;
import com.lesadrax.registrationclient.data.model.Actor;
import com.lesadrax.registrationclient.data.model.Operation;
import com.lesadrax.registrationclient.databinding.ActivitySyncActorBinding;
import com.lesadrax.registrationclient.databinding.ActivitySynchOperationBinding;
import com.lesadrax.registrationclient.ui.adapter.ActorAdapter;
import com.lesadrax.registrationclient.ui.adapter.OperationAdapter;
import com.lesadrax.registrationclient.ui.dialog.SyncDialog;
import com.lesadrax.registrationclient.ui.dialog.SynchOperationDialog;

import java.util.ArrayList;
import java.util.List;

public class SynchOperationActivity extends AppCompatActivity {
    ActivitySynchOperationBinding b;
    private OperationAdapter adapter;
    private final List<Operation> operations = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivitySynchOperationBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.back.setOnClickListener(v -> {
            onBackPressed();
        });


        adapter = new OperationAdapter(this, operations, true);
        b.rv.setLayoutManager(new LinearLayoutManager(this));
        b.rv.setAdapter(adapter);

        b.btn.setOnClickListener(v -> {

            List<Operation> data = adapter.getSelectedData();
            if (!data.isEmpty()){
                SynchOperationDialog dialog = new SynchOperationDialog(this, data, operations.size());
                dialog.setOnDismissListener(dialog1 -> load());
                dialog.show();
            }

        });

        load();
    }

    private void load(){
        AsyncTask.execute(() -> {
            operations.clear();
            operations.addAll(MyApp.getDatabase().operationDao().getAllOperationsCompleted());
            runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
//                b.opText.setText((operation == null) ? R.string.bcst : R.string.ccst);
            });
        });
    }
}