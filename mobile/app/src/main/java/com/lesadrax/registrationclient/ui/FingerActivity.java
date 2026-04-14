package com.lesadrax.registrationclient.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.lesadrax.registrationclient.MultiprotectReceiverCallbackImpl;
import com.lesadrax.registrationclient.MyApp;
import com.lesadrax.registrationclient.R;
import com.lesadrax.registrationclient.data.model.Actor;
import com.lesadrax.registrationclient.data.model.UpdateModel;
import com.lesadrax.registrationclient.data.model.UpdateResponse;
import com.lesadrax.registrationclient.data.network.ApiService;
import com.lesadrax.registrationclient.data.network.RetrofitClient;
import com.lesadrax.registrationclient.data.utis.DataUtils;
import com.lesadrax.registrationclient.databinding.ActivityFingerBinding;
import com.lesadrax.registrationclient.databinding.ActivityOperationBinding;
import com.lesadrax.registrationclient.from.model.FormValue;
import com.lesadrax.registrationclient.from.utils.FingerManager;
import com.lesadrax.registrationclient.from.utils.Utils;
import com.lesadrax.registrationclient.sessionManager.SessionManager;
import com.lesadrax.registrationclient.ui.activity.ActorListActivity;
import com.lesadrax.registrationclient.ui.activity.ActorsActivity;
import com.lesadrax.registrationclient.ui.activity.AddActorActivity;
import com.lesadrax.registrationclient.ui.activity.MainActivity;
import com.morpho.Intent_manager.IntentManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FingerActivity extends AppCompatActivity {
    ActivityFingerBinding b;
    private static PowerManager.WakeLock wakeLock = null;
    private static Context context;
    ArrayList<String> fingerNames;
    private static IntentManager intentManager = null;
    FingerManager fingerManager;
    Actor actor;
    Map<String, FormValue> data;
    AtomicBoolean finger1, finger2, finger3;
    boolean isUpdate = false;

    boolean isOnLineMode = false;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityFingerBinding.inflate(getLayoutInflater());

        setContentView(b.getRoot());
        //
        progressDialog = new ProgressDialog(this);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        fingerManager = new FingerManager(this);
        actor = (Actor) getIntent().getSerializableExtra("ACTOR");
        isOnLineMode = getIntent().getBooleanExtra("ONLINE_MODE", false);
        //
        finger1 = new AtomicBoolean(false);
        finger2 = new AtomicBoolean(false);
        finger3 = new AtomicBoolean(false);
        if(actor!= null){
            data = actor.getFormValues();
            putData(data);
        }
        else{
            Toast.makeText(this, "Une erreur s'est produite", Toast.LENGTH_SHORT).show();
        }

        b.back.setOnClickListener(v->{
            onBackPressed();
        });

        //wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNotSleep");

        //Acquire wakelock before biometric operation

        if((wakeLock != null) && !wakeLock.isHeld())
        {
            wakeLock.acquire();
        }

        //Perform biometric operation
        //Release wakelock
        if((wakeLock != null) && wakeLock.isHeld())
        {
            wakeLock.release();
        }
        //

        //
        fingerNames = new ArrayList<>();
        fingerNames.add("Pouce Gauche");
        fingerNames.add("Index Gauche");
        fingerNames.add("Majeur Gauche");
        fingerNames.add("Annulaire Gauche");
        fingerNames.add("Auriculaire Gauche");
        fingerNames.add("Pouce Droit");
        fingerNames.add("Index Droit");
        fingerNames.add("Majeur Droit");
        fingerNames.add("Annulaire Droit");
        fingerNames.add("Auriculaire Droit");
        //
        ArrayAdapter<String> fingerAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, fingerNames);
        b.handList.setAdapter(fingerAdapter);
        //
        b.btn.setOnClickListener(v -> {
            Log.d("FingerActivity", ""+isOnLineMode);
            if (!isOnLineMode) {
                if (!isUpdate && (!finger1.get() || !finger2.get() || !finger3.get())) {
                    Toast.makeText(this, "Le scan des trois doigts est obligatoire", Toast.LENGTH_LONG).show();
                    return;
                }

                actor.setFormValues(data);
                Log.d("*****FV", "=====> " + actor.getFormValues().toString());

                if (isUpdate) {
                    AsyncTask.execute(() -> {
                        actor.setMessage("");
                        MyApp.getDatabase().actorDao().updateActor(actor);
                        runOnUiThread(() -> Toast.makeText(this, "Mise à jour effectuée", Toast.LENGTH_LONG).show());
                    });
                } else {
                    AsyncTask.execute(() -> {
                        MyApp.getDatabase().actorDao().insertActor(actor);
                        runOnUiThread(() -> Toast.makeText(FingerActivity.this, "Enregistrement effectué", Toast.LENGTH_LONG).show());
                    });
                }

                startActivity(new Intent(this, ActorsActivity.class));
                finishAffinity();
            } else {
                updateActor(); // Mode en ligne
            }
        });


        manageScanButton();
    }

    private void updateActor(){

        progressDialog.setMessage("Modification ...");
        progressDialog.show();
        ApiService apiService = RetrofitClient.getClient(new SessionManager(FingerActivity.this).getAccessToken()).create(ApiService.class);

        apiService.updateObject("" + actor.getId(), DataUtils.actorData(actor, ""))
                .enqueue(new Callback<LinkedHashMap<String, Object>>() {
                    @Override
                    public void onResponse(Call<LinkedHashMap<String, Object>> call, Response<LinkedHashMap<String, Object>> response) {
                        Log.d("***update", "====> " + response.body());

                        runOnUiThread(() -> {
                            progressDialog.dismiss();

                            if (response.isSuccessful() && response.body() != null) {
                                LinkedHashMap<String, Object> responseMap = response.body();

                                // Vérifier si la réponse contient "data"
                                if (responseMap.containsKey("data") && responseMap.get("data") != null) {
                                    String dataStr = Objects.requireNonNull(responseMap.get("data")).toString().toLowerCase();
                                    if (dataStr.contains("success")) {
                                        Toast.makeText(FingerActivity.this, "Mise à jour effectuée", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(FingerActivity.this, ActorListActivity.class));
                                        finishAffinity();
                                    } else {
                                        Toast.makeText(FingerActivity.this, "Une erreur s'est produite dans la modification.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(FingerActivity.this, "Réponse invalide.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                String errorMessage = response.message();
                                try {
                                    Log.d("*****Error", "======> "+Objects.requireNonNull(response.errorBody()).string());
                                    Toast.makeText(FingerActivity.this, "Erreur : " + Objects.requireNonNull(response.errorBody()).string(), Toast.LENGTH_LONG).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<LinkedHashMap<String, Object>> call, Throwable t) {
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            Log.d("*****U", "=====> " + t.getMessage());
                            Toast.makeText(FingerActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                });



    }

    public void manageScanButton(){

        if(b.checkFingerFirst.isChecked() || b.checkFingerSecond.isChecked() || b.checkFingerThird.isChecked()){
            b.scan.setVisibility(View.VISIBLE);
        }
        else{
            b.scan.setVisibility(View.GONE);
        }

        b.scan.setOnClickListener(v->{

            if(b.handList.getText().toString().trim().isEmpty()){
                Toast.makeText(this, "Veillez choisir le nom du doigt à scanner", Toast.LENGTH_SHORT).show();
                return;
            }

            if(b.checkFingerFirst.isChecked() && (b.handList.getText().toString().trim().equals(b.fingerSecondName.getText().toString().trim()) || (b.handList.getText().toString().trim().equals(b.fingerThirdName.getText().toString().trim())))){
                Toast.makeText(this, "Ce doigt a été déjà scanné", Toast.LENGTH_SHORT).show();
                return;
            }

            if(b.checkFingerSecond.isChecked() && (b.handList.getText().toString().trim().equals(b.fingerFirstName.getText().toString().trim()) || (b.handList.getText().toString().trim().equals(b.fingerThirdName.getText().toString().trim())))){
                Toast.makeText(this, "Ce doigt a été déjà scanné", Toast.LENGTH_SHORT).show();
                return;
            }

            if(b.checkFingerThird.isChecked() && (b.handList.getText().toString().trim().equals(b.fingerFirstName.getText().toString().trim()) || (b.handList.getText().toString().trim().equals(b.fingerSecondName.getText().toString().trim())))){
                Toast.makeText(this, "Ce doigt a été déjà scanné", Toast.LENGTH_SHORT).show();
                return;
            }

            if(b.checkFingerFirst.isChecked()){
                getFingerName(b.fingerFirstName);

                data.put("fingerFirstName", new FormValue(b.handList.getText().toString().trim(), b.handList.getText().toString().trim(), b.handList.getText().toString().trim(), "string"));
                Log.d("*******FingerName", "======> "+data.get("fingerFirstName").toString());
            }
            else if(b.checkFingerSecond.isChecked()){
                getFingerName(b.fingerSecondName);
                data.put("fingerSecondName", new FormValue(b.handList.getText().toString().trim(), b.handList.getText().toString().trim(), b.handList.getText().toString().trim(), "string"));
            }
            else if(b.checkFingerThird.isChecked()){
                getFingerName(b.fingerThirdName);
                data.put("fingerThirdName", new FormValue(b.handList.getText().toString().trim(), b.handList.getText().toString().trim(), b.handList.getText().toString().trim(), "string"));
            }
            if (fingerManager.isDeviceAvailable()) {
                fingerManager.captureFingerprint(new FingerManager.OnFingerprintCapturedListener() {
                    @Override
                    public void onSuccess(Bitmap fingerprintImage) {
                        Log.d("******Finger taken", "======> ");
                        runOnUiThread(() -> {
                            if(b.checkFingerFirst.isChecked()){
                                b.fingerFirstImage.setImageBitmap(fingerprintImage);
                                String path = Utils.convertBitmapToFile(fingerprintImage, ""+System.currentTimeMillis());
                                data.put("fingerFirstImage", new FormValue(path, path, path, "string"));
                                finger1.set(true);
                            }
                            else if(b.checkFingerSecond.isChecked()){
                                b.fingerSecondImage.setImageBitmap(fingerprintImage);
                                String path = Utils.convertBitmapToFile(fingerprintImage, ""+System.currentTimeMillis());
                                data.put("fingerSecondImage", new FormValue(path, path, path, "string"));
                                Log.d("******FP", "======> "+data.get("fingerSecondImage").getDisplay());

                                finger2.set(true);
                            }
                            else if(b.checkFingerThird.isChecked()){
                                b.fingerThirdImage.setImageBitmap(fingerprintImage);
                                String path = Utils.convertBitmapToFile(fingerprintImage, ""+System.currentTimeMillis());
                                data.put("fingerThirdImage", new FormValue(path, path, path, "string"));
                                finger3.set(true);
                            }
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        //Toast.makeText(FingerActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        Log.d("*****FingerP", "======>  "+errorMessage);
                    }
                });
            } else {
                Toast.makeText(this, "No biometric device available.", Toast.LENGTH_SHORT).show();
            }
        });

        b.checkFingerThird.setOnClickListener(v->{
            b.checkFingerThird.setChecked(true);
            b.checkFingerSecond.setChecked(false);
            b.checkFingerFirst.setChecked(false);
            //
            b.scan.setVisibility(View.VISIBLE);
        });

        b.checkFingerSecond.setOnClickListener(v->{
            b.checkFingerThird.setChecked(false);
            b.checkFingerSecond.setChecked(true);
            b.checkFingerFirst.setChecked(false);
            //
            b.scan.setVisibility(View.VISIBLE);
        });

        b.checkFingerFirst.setOnClickListener(v->{
            b.checkFingerThird.setChecked(false);
            b.checkFingerSecond.setChecked(false);
            b.checkFingerFirst.setChecked(true);
            //
            b.scan.setVisibility(View.VISIBLE);
        });

    }

    public void getFingerName(TextView _textView){
        _textView.setText(b.handList.getText().toString().trim());
    }

    public void putData(Map<String, FormValue> data){

        Log.d("*****FV", "=====> "+data.toString());

        if(data.get("fingerFirstName") != null && !data.get("fingerFirstName").getDisplay().isEmpty()){
            b.fingerFirstName.setText(data.get("fingerFirstName").getDisplay());
            isUpdate = true;
        }

        if(data.get("fingerSecondName") != null && !data.get("fingerSecondName").getDisplay().isEmpty()){
            b.fingerSecondName.setText(data.get("fingerSecondName").getDisplay());
        }

        if(data.get("fingerThirdName") != null && !data.get("fingerThirdName").getDisplay().isEmpty()){
            b.fingerThirdName.setText(data.get("fingerThirdName").getDisplay());
        }

        if(data.get("fingerFirstImage") != null && !data.get("fingerFirstImage").getDisplay().isEmpty()){
            b.fingerFirstImage.setImageBitmap(BitmapFactory.decodeFile(data.get("fingerFirstImage").getDisplay()));
        }

        if(data.get("fingerSecondImage") != null && !data.get("fingerSecondImage").getDisplay().isEmpty()){
            b.fingerSecondImage.setImageBitmap(BitmapFactory.decodeFile(data.get("fingerSecondImage").getDisplay()));
        }

        if(data.get("fingerThirdImage") != null && !data.get("fingerThirdImage").getDisplay().isEmpty()){
            b.fingerThirdImage.setImageBitmap(BitmapFactory.decodeFile(data.get("fingerThirdImage").getDisplay()));
        }

    }
}