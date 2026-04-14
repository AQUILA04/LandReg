package com.lesadrax.registrationclient.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.io.IOException;
import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.lesadrax.registrationclient.MyApp;
import com.lesadrax.registrationclient.R;
import com.lesadrax.registrationclient.data.model.Actor;
import com.lesadrax.registrationclient.data.model.ActorModel;
import com.lesadrax.registrationclient.data.model.AuthenticateResponse;
import com.lesadrax.registrationclient.data.model.AuthenticateUinResponse;
import com.lesadrax.registrationclient.data.model.AuthenticationData;
import com.lesadrax.registrationclient.data.model.Bordering;
import com.lesadrax.registrationclient.data.model.Checklist;
import com.lesadrax.registrationclient.data.model.Operation;
import com.lesadrax.registrationclient.data.model.Role;
import com.lesadrax.registrationclient.data.network.ApiService;
import com.lesadrax.registrationclient.data.network.RetrofitClient;
import com.lesadrax.registrationclient.data.utis.DataUtils;
import com.lesadrax.registrationclient.databinding.FormCheckListBinding;
import com.lesadrax.registrationclient.from.model.FormValue;
import com.lesadrax.registrationclient.from.utils.ChecklistComparator;
import com.lesadrax.registrationclient.from.utils.FingerManager;
import com.lesadrax.registrationclient.from.utils.FileUtils;
import com.lesadrax.registrationclient.from.utils.ToastHelper;
import com.lesadrax.registrationclient.from.utils.Utils;
import com.lesadrax.registrationclient.sessionManager.SessionManager;
import com.lesadrax.registrationclient.ui.activity.ActorListActivity;
import com.lesadrax.registrationclient.ui.activity.AddActorActivity;
import com.lesadrax.registrationclient.ui.activity.OperationActivity;
import com.lesadrax.registrationclient.ui.activity.OperationsActivity;
import com.lesadrax.registrationclient.ui.adapter.RolesAdapter;
import com.lesadrax.registrationclient.ui.adapter.SquareAdapter;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignatureActivity extends AppCompatActivity implements RolesAdapter.OnRoleDeleteListener {

    ArrayList<Role> roles;
    ArrayList<String> fingerNames;
    RolesAdapter rolesAdapter;
    private FormCheckListBinding b;

    // ✅ CORRECTION 1 : Suppression de validated[] en doublon, on utilise uniquement validateActors
    List<Boolean> validateActors;

    int pos = -1;
    FingerManager fingerManager; // ✅ CORRECTION 2 : déclaré une seule fois, instancié une seule fois dans onCreate
    Actor actor;
    SquareAdapter adapter;
    List<Integer> items;
    List<ActorModel> actorList;
    ProgressDialog progressDialog;
    private static PowerManager.WakeLock wakeLock = null;
    ArrayList<Role> choosenLimits;
    String path = "";

    ArrayAdapter<String> roleAdapter;
    Checklist checklist;

    List<Bordering> borderingList;

    String page = null;

    Checklist cbo;

    Operation data;
    ApiService apiService;

    // Liste pour tracker les NIU déjà utilisés
    private ArrayList<String> usedUins;

    // Contrôle la prévention des doublons NIU
    private boolean preventDuplicateUin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = FormCheckListBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        checklist = new Checklist();
        actorList = new ArrayList<>();
        page = getIntent().getStringExtra("before");

        apiService = RetrofitClient.getClient(new SessionManager(SignatureActivity.this).getAccessToken()).create(ApiService.class);

        b.uin.setText("LIN-");

        b.addRole.setOnClickListener(v -> showAddRolePopup());

        if (page != null && Objects.equals(page, "before")) {
            b.title.setText("CheckList avant Opérations");
        } else {
            b.title.setText("CheckList après Opération");
            if (getIntent().getStringExtra("ONLINE") == null || !Objects.equals(getIntent().getStringExtra("ONLINE"), "YES")) {
                data = (Operation) getIntent().getSerializableExtra("DATA");
            }
        }

        roles = new ArrayList<>();
        fingerNames = new ArrayList<>();
        choosenLimits = new ArrayList<>();
        borderingList = new ArrayList<>();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        // ✅ CORRECTION 2 : fingerManager instancié une seule fois
        fingerManager = new FingerManager(this);

        b.back.setOnClickListener(v -> {
            onBackPressed();
            MyApp.getInstance().clearTempData();
        });

        searchUIN();

        // ✅ CORRECTION 3 : WakeLock supprimé car il était acquis et libéré immédiatement sans aucune utilité

        roles.add(new Role("Maire/représentant", "MAYOR"));
        roles.add(new Role("Chef traditionnel / Coutumier", "TRADITIONAL_CHIEF"));
        roles.add(new Role("Notable du lieu", "NOTABLE"));
        roles.add(new Role("Géomètre de la mairie", "SURVEYOR"));
        roles.add(new Role("Propriétaire/ Mandataire", "OWNER_OR_REPRESENTATIVE"));
        roles.add(new Role("Angent Topographe", "TOPOGRAPHER"));
        roles.add(new Role("Agent Socio foncier", "SOCIAL_LAND_AGENT"));
        roles.add(new Role("Tiers Intéressé", "TIERS"));
        roles.add(new Role("Limitrophe Nord", "NORTH"));
        roles.add(new Role("Limitrophe Sud", "SOUTH"));
        roles.add(new Role("Limitrophe Est", "EAST"));
        roles.add(new Role("Limitrophe Ouest", "WEST"));

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        b.actorsValidate.setLayoutManager(layoutManager);

        items = new ArrayList<>();
        validateActors = new ArrayList<>();

        for (int i = 0; i < roles.size(); i++) {
            items.add(i + 1);
            validateActors.add(false);
        }

        // ✅ CORRECTION 4 : actorList pré-rempli avec null pour permettre set(index) dans le désordre
        while (actorList.size() < roles.size()) {
            actorList.add(null);
        }

        adapter = new SquareAdapter(this, items);
        b.actorsValidate.setAdapter(adapter);
        adapter.notifyDataSetChanged();

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

        roleAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, roles.stream()
                .map(Role::getName)
                .collect(Collectors.toList()));
        ArrayAdapter<String> fingerAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, fingerNames);
        b.roleList.setAdapter(roleAdapter);
        b.handList.setAdapter(fingerAdapter);

        b.roleList.setOnItemClickListener((parent, view, position, id) -> {
            pos = position;
            Log.d("******PoZI", pos + " " + position);
            b.fingerImage.setImageResource(R.drawable.fg_icon);
            b.uin.setText("LIN-");
        });

        validateCheckList();
    }

    private void validateCheckList() {

        b.btn.setOnClickListener(v -> {
            // ✅ CORRECTION 5 : validation basée sur validateActors.size() et non plus validated.length fixe à 10
            for (int i = 0; i < validateActors.size(); i++) {
                if (!validateActors.get(i)) {
                    Toast.makeText(this, "Toutes les personnes doivent être validées.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            checklist.setBorderingList(borderingList);

            if (getIntent().getStringExtra("before") != null && Objects.equals(getIntent().getStringExtra("before"), "before")) {
                startActivity(new Intent(this, OperationActivity.class)
                        .putExtra("cbo", checklist)
                        .putExtra("actor_list_key", (Serializable) actorList));
                finish();
            } else {
                // Mode ONLINE
                if (getIntent().getStringExtra("ONLINE") != null && Objects.equals(getIntent().getStringExtra("ONLINE"), "YES")) {
                    data = MyApp.getInstance().getTempOperation();

                    if (data != null && data.getChecklistAfterOperation() != null) {
                        Log.d("****CheckList", "=====> " + data.getChecklistAfterOperation().toString());
                        Log.d("****CheckList", "=====> " + checklist.toString());
                        ChecklistComparator.ComparaisonResult result = ChecklistComparator.comparer(data.getChecklistAfterOperation(), checklist);

                        if (result.sontEgaux) {
                            // ✅ CORRECTION 6 : progressDialog initialisé ici avant show() pour éviter NullPointerException
                            progressDialog = new ProgressDialog(this);
                            progressDialog.setMessage("Modification...");
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            ApiService apiService = RetrofitClient.getClient(new SessionManager(SignatureActivity.this).getAccessToken()).create(ApiService.class);

                            apiService.updateConstatation(DataUtils.operationData(data, "update")).enqueue(new Callback<LinkedHashMap<String, Object>>() {
                                @Override
                                public void onResponse(@NonNull Call<LinkedHashMap<String, Object>> call, @NonNull Response<LinkedHashMap<String, Object>> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        LinkedHashMap<String, Object> responseMap = response.body();

                                        if (responseMap.containsKey("message") && responseMap.get("message") != null) {
                                            String dataStr = responseMap.get("message").toString().toLowerCase();

                                            if (dataStr.contains("success")) {
                                                runOnUiThread(() -> {
                                                    if (progressDialog != null && progressDialog.isShowing()) {
                                                        progressDialog.dismiss();
                                                    }
                                                    if (!isFinishing()) {
                                                        Toast.makeText(SignatureActivity.this, "Mise à jour effectuée", Toast.LENGTH_SHORT).show();
                                                        MyApp.getInstance().clearTempData();
                                                        startActivity(new Intent(SignatureActivity.this, ActorListActivity.class));
                                                        finishAffinity();
                                                    }
                                                });
                                            } else {
                                                runOnUiThread(() -> {
                                                    if (progressDialog != null && progressDialog.isShowing()) {
                                                        progressDialog.dismiss();
                                                    }
                                                    Toast.makeText(SignatureActivity.this, "Une erreur s'est produite dans la modification.", Toast.LENGTH_SHORT).show();
                                                });
                                            }
                                        } else {
                                            runOnUiThread(() -> {
                                                if (progressDialog != null && progressDialog.isShowing()) {
                                                    progressDialog.dismiss();
                                                }
                                                Toast.makeText(SignatureActivity.this, "Réponse invalide reçue.", Toast.LENGTH_SHORT).show();
                                            });
                                        }
                                    } else {
                                        runOnUiThread(() -> {
                                            if (progressDialog != null && progressDialog.isShowing()) {
                                                progressDialog.dismiss();
                                            }
                                            try {
                                                Toast.makeText(SignatureActivity.this, "Erreur : " + response.errorBody().string(), Toast.LENGTH_LONG).show();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onFailure(Call<LinkedHashMap<String, Object>> call, Throwable t) {
                                    runOnUiThread(() -> {
                                        if (progressDialog != null && progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }
                                        Toast.makeText(SignatureActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                                }
                            });
                        } else {
                            ToastHelper.showErrorToast(SignatureActivity.this, "Il y a eu différence de Rôle sur certaines personnes. Veuillez Recommencer tout en respectant vos rôles.");
                        }
                    } else {
                        ToastHelper.showErrorToast(SignatureActivity.this, "Une erreur s'est produite dans la récupération des données de checklist");
                    }
                } else {
                    // Validation après lecture du PV pro
                    data.setChecklistAfterOperation(checklist);
                    data.setCompleted(true);
                    ChecklistComparator.ComparaisonResult result = ChecklistComparator.comparer(data.getChecklistBeforeOperation(), data.getChecklistAfterOperation());
                    if (result.sontEgaux) {
                        Executors.newSingleThreadExecutor().execute(() -> MyApp.getDatabase().operationDao().updateOperation(data));
                        Toast.makeText(SignatureActivity.this, "Validation du PV effectuée", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(this, OperationsActivity.class));
                        finish();
                    } else {
                        ToastHelper.showErrorToast(SignatureActivity.this, "Il y a eu différence de Rôle sur certaines personnes. Veuillez Recommencer tout en respectant vos rôles.");
                    }
                }
            }
        });

        b.scan.setOnClickListener(v -> {
            if (b.uin.getText().toString().trim().isEmpty() || b.uin.getText().toString().trim().equals("LIN-")) {
                Toast.makeText(SignatureActivity.this, "NIU obligatoire", Toast.LENGTH_SHORT).show();
            } else if (!b.uin.getText().toString().trim().startsWith("LIN-")) {
                Toast.makeText(SignatureActivity.this, "Le NIU doit commencer par 'LIN-'", Toast.LENGTH_SHORT).show();
            } else {
                String chiffres = b.uin.getText().toString().trim().substring(4);
                if (!chiffres.matches("\\d+")) {
                    Toast.makeText(SignatureActivity.this, "La partie après 'LIN-' doit contenir uniquement des chiffres", Toast.LENGTH_SHORT).show();
                } else {
                    if (b.roleList.getText().toString().trim().isEmpty() || b.handList.getText().toString().trim().isEmpty()) {
                        Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show();
                    } else {
                        if (fingerManager.isDeviceAvailable()) {
                            fingerManager.captureFingerprint(new FingerManager.OnFingerprintCapturedListener() {
                                @Override
                                public void onSuccess(Bitmap fingerprintImage) {
                                    Log.d("******Finger taken", "======> ");
                                    runOnUiThread(() -> {
                                        b.fingerImage.setImageBitmap(fingerprintImage);
                                        path = Utils.convertBitmapToFile(fingerprintImage, "" + System.currentTimeMillis());
                                    });
                                }

                                @Override
                                public void onError(String errorMessage) {
                                    Log.d("*****FingerP", "======>  " + errorMessage);
                                }
                            });
                        } else {
                            Toast.makeText(this, "No biometric device available.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        b.validate.setOnClickListener(v -> {
            if (pos >= 0 && !path.isEmpty()) {
                // ✅ CORRECTION 6 : progressDialog initialisé ici avant l'appel authenticate()
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Authentification...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                authenticate(b.uin.getText().toString().trim(), FileUtils.convertFileToBase64WithPrefix(path), b.roleList.getText().toString().trim());
            } else {
                Toast.makeText(this, "Le scan est obligatoire pour l'authentification", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showPopup(String name, String firstname, String niu, String enterpriseName) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        if (name != null) {
            MaterialTextView nomTextView = new MaterialTextView(this);
            nomTextView.setText("Nom: " + name);
            nomTextView.setTextSize(18);
            layout.addView(nomTextView);
        }
        if (firstname != null) {
            MaterialTextView prenomTextView = new MaterialTextView(this);
            prenomTextView.setText("Prénom: " + firstname);
            prenomTextView.setTextSize(18);
            layout.addView(prenomTextView);
        }
        if (niu != null) {
            MaterialTextView uinView = new MaterialTextView(this);
            uinView.setText("NIU: " + niu);
            uinView.setTextSize(18);
            layout.addView(uinView);
        }
        if (enterpriseName != null && !enterpriseName.isEmpty()) {
            MaterialTextView entreprise = new MaterialTextView(this);
            entreprise.setText("Nom: " + enterpriseName);
            entreprise.setTextSize(18);
            layout.addView(entreprise);
        }

        if (pos == 0) {
            checklist.setMayorUIN(niu);
        } else if (pos == 1) {
            checklist.setTraditionalChiefUIN(niu);
        } else if (pos == 2) {
            checklist.setNotableUIN(niu);
        } else if (pos == 3) {
            checklist.setGeometerUIN(niu);
        } else if (pos == 4) {
            checklist.setOwnerUIN(niu);
        } else if (pos == 5) {
            checklist.setTopographerUIN(niu);
        } else if (pos == 6) {
            checklist.setSocialLandAgentUIN(niu);
        } else if (pos == 7) {
            checklist.setInterestedThirdPartyUIN(niu);
        } else if (pos > 7) {
            Bordering bordering = new Bordering(roles.get(pos).getValue(), niu);
            borderingList.add(bordering);
        }

        Button okButton = new Button(this);
        okButton.setText("OK");
        layout.addView(okButton);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Information")
                .setView(layout)
                .setCancelable(false);

        AlertDialog dialog = builder.create();
        okButton.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }


    public void validateItem(int pos) {
        switch (pos) {
            case 0:
                b.square1.setBackgroundResource(R.drawable.square_border_green);
                break;
            case 1:
                b.square2.setBackgroundResource(R.drawable.square_border_green);
                break;
            case 2:
                b.square3.setBackgroundResource(R.drawable.square_border_green);
                break;
            case 3:
                b.square4.setBackgroundResource(R.drawable.square_border_green);
                break;
            case 4:
                b.square5.setBackgroundResource(R.drawable.square_border_green);
                break;
            case 5:
                b.square6.setBackgroundResource(R.drawable.square_border_green);
                break;
            case 6:
                b.square7.setBackgroundResource(R.drawable.square_border_green);
                break;
            case 7:
                b.square8.setBackgroundResource(R.drawable.square_border_green);
                break;
            case 8:
                b.square9.setBackgroundResource(R.drawable.square_border_green);
                break;
            case 9:
                b.square10.setBackgroundResource(R.drawable.square_border_green);
                break;
        }
        // ✅ CORRECTION 1 : on ne touche plus validated[], on utilise uniquement validateActors (géré dans authenticate)
    }

    @Override
    public void onDeleteRole(int position) {
        Log.d("*****S", "====> " + position);

        // ✅ CORRECTION 7 : suppression correcte avec index calculé proprement
        int roleIndex = 12 + position;

        if (roleIndex < roles.size()) {
            roles.remove(roleIndex);
        }
        choosenLimits.remove(position);

        // Supprimer le bon index dans items, validateActors et actorList
        if (roleIndex < items.size()) {
            items.remove(roleIndex);
        }
        if (roleIndex < validateActors.size()) {
            validateActors.remove(roleIndex);
        }
        if (roleIndex < actorList.size()) {
            actorList.remove(roleIndex);
        }

        rolesAdapter.notifyItemRemoved(position);
        rolesAdapter.notifyItemRangeChanged(position, choosenLimits.size());

        roleAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, roles.stream()
                .map(Role::getName)
                .collect(Collectors.toList()));
        b.roleList.setAdapter(roleAdapter);

        adapter.notifyDataSetChanged();
        roleAdapter.notifyDataSetChanged();

        Log.d("*****sss", "======> " + roles.size());
    }

    private void showAddRolePopup() {
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup, null);

        ImageView ivClose = popupView.findViewById(R.id.iv_close);
        RecyclerView rvRoles = popupView.findViewById(R.id.rv_roles);
        AutoCompleteTextView etNewRole = popupView.findViewById(R.id.role_list);
        Button btnSaveRole = popupView.findViewById(R.id.btn_save_role);

        ArrayList<Role> limits = new ArrayList<>();
        limits.add(new Role("Limitrophe Nord Est", "NORTH_EAST"));
        limits.add(new Role("Limitrophe Nord Ouest", "NORTH_WEST"));
        limits.add(new Role("Limitrophe Sud Ouest", "SOUTH_WEST"));
        limits.add(new Role("Limitrophe Sud Est", "SOUTH_EAST"));
        limits.add(new Role("Limitrophe Nord Nord Est", "NORTH_NORTH_EAST"));
        limits.add(new Role("Limitrophe Nord Nord Ouest", "NORTH_NORTH_WEST"));
        limits.add(new Role("Limitrophe Ouest Nord Ouest", "WEST_NORTH_WEST"));
        limits.add(new Role("Limitrophe Est Nord Est", "EAST_NORTH_EAST"));
        limits.add(new Role("Limitrophe Sud Sud Est", "SOUTH_SOUTH_EAST"));
        limits.add(new Role("Limitrophe Sud Sud Ouest", "SOUTH_SOUTH_WEST"));
        limits.add(new Role("Limitrophe Ouest Sud Ouest", "WEST_SOUTH_WEST"));
        limits.add(new Role("Limitrophe Est Sud Est", "EAST_SOUTH_EAST"));

        ArrayAdapter<String> limitAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, limits.stream()
                .map(Role::getName)
                .collect(Collectors.toList()));
        etNewRole.setAdapter(limitAdapter);

        AtomicReference<Role> choosenRole = new AtomicReference<>(new Role());
        etNewRole.setOnItemClickListener((parent, view, position, id) -> choosenRole.set(limits.get(position)));

        rolesAdapter = new RolesAdapter(choosenLimits, this);
        rvRoles.setLayoutManager(new LinearLayoutManager(this));
        rvRoles.setAdapter(rolesAdapter);

        final PopupWindow popupWindow = new PopupWindow(popupView,
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT,
                true);
        popupWindow.showAtLocation(b.addRole, android.view.Gravity.CENTER, 0, 0);

        ivClose.setOnClickListener(v -> popupWindow.dismiss());

        btnSaveRole.setOnClickListener(v -> {
            String newRole = etNewRole.getText().toString().trim();
            if (!newRole.isEmpty()) {
                if (!choosenLimits.contains(choosenRole.get())) {
                    roles.add(choosenRole.get());
                    choosenLimits.add(choosenRole.get());

                    roleAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, roles.stream()
                            .map(Role::getName)
                            .collect(Collectors.toList()));
                    b.roleList.setAdapter(roleAdapter);
                    rolesAdapter.notifyDataSetChanged();

                    // ✅ CORRECTION 8 : items.size() + 1 au lieu de roles.size() (qui est déjà incrémenté)
                    items.add(items.size() + 1);
                    validateActors.add(false);

                    // ✅ CORRECTION 4 : actorList aussi étendue pour le nouveau rôle
                    actorList.add(null);

                    adapter.notifyDataSetChanged();
                    roleAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "Ce limitrophe existe!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Veuillez choisir une limitrophe", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void authenticate(String uin, String fingerprint, String role) {

        // ✅ CORRECTION 9 : capturer pos localement pour éviter le bug async (pos peut changer pendant la requête)
        final int currentPos = pos;

        // Vérification des doublons de NIU
        if (preventDuplicateUin && usedUins != null && usedUins.contains(uin)) {
            runOnUiThread(() -> {
                Toast.makeText(SignatureActivity.this,
                        "Ce NIU (" + uin + ") a déjà été utilisé pour un autre rôle. Veuillez utiliser un NIU différent.",
                        Toast.LENGTH_LONG).show();
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            });
            return;
        }

        AuthenticationData authenticationData = new AuthenticationData(uin, fingerprint, null);

        Log.d("*****Authenticate", "=======> " + authenticationData.toString());
        Call<AuthenticateResponse> call = apiService.authenticate(authenticationData);

        call.enqueue(new Callback<AuthenticateResponse>() {
            @Override
            public void onResponse(Call<AuthenticateResponse> call, Response<AuthenticateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    });

                    AuthenticateResponse authenticateResponse = response.body();
                    Log.d("******Success", "=====> " + response.body().toString());

                    String status = authenticateResponse.getStatus();
                    int statusCode = authenticateResponse.getStatusCode();
                    String service = authenticateResponse.getService();

                    if (authenticateResponse.getData() != null) {
                        String result = authenticateResponse.getData().getStatus();
                        ActorModel actorModel = authenticateResponse.getData().getActor();

                        if (result != null && !result.equals("MATCH")) {
                            runOnUiThread(() -> Toast.makeText(SignatureActivity.this,
                                    "Les données envoyées ne correspondent pas à un acteur.", Toast.LENGTH_SHORT).show());
                            return;
                        }

                        Log.d("AUTH_SUCCESS", "Status: " + status + ", Code: " + statusCode + ", Service: " + service);
                        Log.d("AUTH_SUCCESS", "Result: " + result);

                        if (actorModel != null) {
                            Log.d("AUTH_SUCCESS", "Actor UIN: " + actorModel.getUin());
                            Log.d("AUTH_SUCCESS", "Actor Name: " + actorModel.getName());
                            Log.d("AUTH_SUCCESS", "Actor Firstname: " + actorModel.getFirstname());
                            Log.d("AUTH_SUCCESS", "Actor Lastname: " + actorModel.getLastname());
                            Log.d("AUTH_SUCCESS", "Actor Type: " + actorModel.getType());

                            String type = actorModel.getType() != null ? actorModel.getType().name() : "";

                            runOnUiThread(() -> {
                                // ✅ CORRECTION 9 : placement à currentPos (index du rôle sélectionné)
                                // garantit que l'acteur est toujours à la bonne position dans la liste
                                // peu importe l'ordre d'authentification
                                if (currentPos < actorList.size()) {
                                    actorList.set(currentPos, actorModel);
                                } else {
                                    // Sécurité : remplir les trous si nécessaire
                                    while (actorList.size() <= currentPos) {
                                        actorList.add(null);
                                    }
                                    actorList.set(currentPos, actorModel);
                                }

                                showPopup(actorModel.getLastname(), actorModel.getFirstname(), actorModel.getUin(), type);
                                adapter.updatePosition(currentPos);
                                validateActors.set(currentPos, true);

                                // Ajouter le NIU à la liste des UINs utilisés
                                if (preventDuplicateUin) {
                                    if (usedUins == null) {
                                        usedUins = new ArrayList<>();
                                    }
                                    usedUins.add(uin);
                                }
                            });

                        } else {
                            runOnUiThread(() -> {
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                Toast.makeText(SignatureActivity.this, "Acteur non disponible", Toast.LENGTH_SHORT).show();
                            });
                            Log.e("****AUTH_ERROR", "Data is null in the response.");
                        }
                    } else {
                        runOnUiThread(() -> {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(SignatureActivity.this, "Erreur : " + response.code(), Toast.LENGTH_SHORT).show();
                        });
                        Log.e("****AUTH_ERROR", "Code: " + response.code() + ", Message: " + response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthenticateResponse> call, Throwable t) {
                runOnUiThread(() -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(SignatureActivity.this, "Échec de connexion : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                });
                Log.e("******AUTH_FAILURE", t.getMessage(), t);
            }
        });
    }


    private void searchActorByUin(String uin) {
        Call<AuthenticateUinResponse> call = apiService.getActorByUin(uin);

        call.enqueue(new Callback<AuthenticateUinResponse>() {
            @Override
            public void onResponse(Call<AuthenticateUinResponse> call, @NonNull Response<AuthenticateUinResponse> response) {
                if (response.isSuccessful()) {
                    AuthenticateUinResponse authResponse = response.body();

                    if (authResponse != null) {
                        Log.d("****Tag", "Status global: " + authResponse.getStatus());
                        Log.d("****Tag", "Code: " + authResponse.getStatusCode());
                        Log.d("****Tag", "Service: " + authResponse.getService());

                        ActorModel data = authResponse.getData();
                        if (data != null) {
                            runOnUiThread(() -> ToastHelper.showSuccessToast(SignatureActivity.this, "✓ NIU valide et existant"));
                        } else {
                            runOnUiThread(() -> ToastHelper.showErrorToast(SignatureActivity.this, "✗ NIU invalide ou inexistant"));
                        }
                    }
                } else {
                    Log.e("****Tag", "Erreur HTTP: " + response.code());
                    Toast.makeText(SignatureActivity.this, "Erreur serveur: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthenticateUinResponse> call, Throwable t) {
                Log.e("****Tag", "Erreur réseau: " + t.getMessage());
                Toast.makeText(SignatureActivity.this, "Erreur de connexion: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void searchUIN() {
        b.uin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 14) {
                    String uin = s.toString();
                    searchActorByUin(uin);
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}