package com.lesadrax.registrationclient.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.lesadrax.registrationclient.MyApp;
import com.lesadrax.registrationclient.data.model.Actor;
import com.lesadrax.registrationclient.data.model.SynchroData;
import com.lesadrax.registrationclient.data.model.SynchroResponse;
import com.lesadrax.registrationclient.data.network.ApiService;
import com.lesadrax.registrationclient.data.network.RetrofitClient;
import com.lesadrax.registrationclient.data.repository.dao.ActorDao;
import com.lesadrax.registrationclient.data.utis.DataUtils;
import com.lesadrax.registrationclient.databinding.DialogSyncBinding;
import com.lesadrax.registrationclient.sessionManager.SessionManager;
import com.lesadrax.registrationclient.ui.SignatureActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyncDialog extends Dialog {

    private List<Actor> data = new ArrayList<>();

    private  int totalData;

    private int totalToSync;

    int count = 0;

    private DialogSyncBinding b;

    public SyncDialog(@NonNull Context context) {
        super(context);
    }

    ApiService apiService;

    String batchNumber;

    public SyncDialog(@NonNull Context context, List<Actor> actors, int totalData) {
        super(context);
        this.data = actors;
        this.totalData = totalData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        b = DialogSyncBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        Objects.requireNonNull(getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        sync();
    }

    private void sync(){
        b.progressPan.setVisibility(View.VISIBLE);
        b.resultPan.setVisibility(View.GONE);
        String token = new SessionManager(getContext()).getAccessToken();
        apiService = RetrofitClient.getClient(token).create(ApiService.class);
        Log.d("****Token", "======> "+token);
        System.out.println(token);
        totalToSync = data.size();
        SynchroData initData = new SynchroData(data.size(), totalData);
        apiService.sendSynchroData(initData).enqueue(new Callback<SynchroResponse>() {
            @Override
            public void onResponse(@NonNull Call<SynchroResponse> call, @NonNull Response<SynchroResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                        SynchroData synchroData = response.body().getData();
                        Log.d("*****Batch", "=====> "+response.toString());
                        Log.d("*****Batch", "=====> "+synchroData.toString());
                        batchNumber = synchroData.getBatchNumber();

                    for (Actor actor : data){
                        JsonObject object = DataUtils.actorData(actor, batchNumber);
                        if(object != null)
                            Log.d("*****SUCCESS", "=====>  "+object);
                        createSyncActor(object, actor);
                    }
                } else {
                    System.out.println("Erreur : " + response.code());
                    try {
                        Log.d("*****Error", "=====> INIT NOT RESPONSE "+response.errorBody().string());
                        b.progressPan.setVisibility(View.GONE);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<SynchroResponse> call, Throwable t) {
             Log.d("*****Failure", "=====> "+t.toString());
            }
        });



    }

    private void createSyncActor(JsonObject object, Actor actor) {
        if (object == null) {
            count++;
            getFinishInfo();
            return;
        }

        apiService.createActor(object).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                count++;
                Executors.newSingleThreadExecutor().execute(() -> {
                    if (response.isSuccessful()) {
                        Log.d("*****SUCCESS", "=====> Actor successfully synced");
                        actor.setSynced(true);
                        actor.setMessage("Synchronisation réussie");
                    } else {
                        try {
                            String errorMessage = response.errorBody() != null
                                    ? response.errorBody().string()
                                    : "Erreur inconnue";
                            Log.d("*****Error", "=====> Sync failed: " + errorMessage);
                            actor.setMessage(errorMessage);
                        } catch (IOException e) {
                            Log.e("*****Error", "=====> Error parsing response: " + e.getMessage());
                            actor.setMessage("Erreur inattendue lors de la synchronisation");
                        }
                    }
                    MyApp.getDatabase().actorDao().updateActor(actor);
                });
                getFinishInfo();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                count++;
                Log.e("*****Failure", "=====> Sync failed: " + t.getMessage());
                Executors.newSingleThreadExecutor().execute(() -> {
                    actor.setMessage("Échec de la synchronisation : " + t.getMessage());
                    MyApp.getDatabase().actorDao().updateActor(actor);
                });
                getFinishInfo();
            }
        });
    }

    private void getFinishInfo(){
        if(totalToSync == count){
            apiService.finishSynchro(batchNumber).enqueue(new Callback<SynchroResponse>() {
                @Override
                public void onResponse(Call<SynchroResponse> call, Response<SynchroResponse> response) {
                    if(response.isSuccessful()){

                        SynchroData synchroData = response.body().getData();
                        b.progressPan.setVisibility(View.GONE);
                        b.resultPan.setVisibility(View.VISIBLE);
                        if(synchroData != null){
                            b.sent.setText(""+synchroData.getSynchroCandidateCount());
                            b.received.setText(""+synchroData.getTotalReceivedCount());
                            b.validated.setText(""+synchroData.getSuccessPacketCount());
                            b.error.setText(""+synchroData.getFailedPacketCount());
                        }
                        b.btn.setOnClickListener(v->{ b.resultPan.setVisibility(View.GONE);});

                    }
                    else{
                        Log.d("*****Error", "=====> FINISH NOT RESPONSE "+response.code());
                        try {
                            Log.d("*****Error", "=====> FINISH NOT RESPONSE "+response.errorBody().string());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<SynchroResponse> call, Throwable t) {
                    Log.d("*****Error", "=====> FINISH FAILURE"+t.getMessage());
                    b.progressPan.setVisibility(View.GONE);
                    Toast.makeText(getContext(), ""+t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }



}
