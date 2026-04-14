package com.lesadrax.registrationclient.ui.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gkemon.XMLtoPDF.PdfGenerator;
import com.gkemon.XMLtoPDF.PdfGeneratorListener;
import com.gkemon.XMLtoPDF.model.FailureResponse;
import com.gkemon.XMLtoPDF.model.SuccessResponse;
import com.google.gson.Gson;
import com.lesadrax.registrationclient.R;
import com.lesadrax.registrationclient.data.model.ActorModel;
import com.lesadrax.registrationclient.data.model.Role;
import com.lesadrax.registrationclient.data.model.Operation;
import com.lesadrax.registrationclient.data.model.OperationDetail;
import com.lesadrax.registrationclient.data.model.UinDetailRequest;
import com.lesadrax.registrationclient.data.model.UinDetailResponse;
import com.lesadrax.registrationclient.data.network.ApiService;
import com.lesadrax.registrationclient.data.network.RetrofitClient;
import com.lesadrax.registrationclient.databinding.ActivityPvactivityBinding;
import com.lesadrax.registrationclient.sessionManager.SessionManager;
import com.lesadrax.registrationclient.ui.adapter.ActorInPVAdapter;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerbalProcessActivity extends AppCompatActivity {

    private ActivityPvactivityBinding b;
    private PdfGenerator.XmlToPDFLifecycleObserver xmlToPDFLifecycleObserver;
    private Operation op;
    private List<String> uin;
    private List<ActorModel> checklistActors;
    private OperationDetail operationDetail;
    private ProgressDialog progressDialog;
    private int ID = 0;

    // Cache pour éviter les appels multiples
    private boolean isDataLoaded = false;
    private boolean isGeneratingPDF = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityPvactivityBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        // Restaurer l'état si nécessaire
        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        } else {
            ID = getIntent().getIntExtra("ID", 0);
        }

        progressDialog = new ProgressDialog(this);
        xmlToPDFLifecycleObserver = new PdfGenerator.XmlToPDFLifecycleObserver(this);
        getLifecycle().addObserver(xmlToPDFLifecycleObserver);

        uin = new ArrayList<>();
        checklistActors = new ArrayList<>();

        if (ID != 0) {
            getDetail();
        } else {
            Toast.makeText(this, "ID d'opération manquant", Toast.LENGTH_SHORT).show();
            finish();
        }

        b.back.setOnClickListener(v -> onBackPressed());
        b.print.setOnClickListener(v -> {
            if (!isGeneratingPDF && operationDetail != null && checklistActors != null && !checklistActors.isEmpty()) {
                generatePDF();
            } else {
                Toast.makeText(this, "Chargement des données en cours...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void generatePDF() {
        isGeneratingPDF = true;

        LayoutInflater vi = (LayoutInflater) VerbalProcessActivity.this
                .getSystemService(VerbalProcessActivity.this.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.print_pv, null);

        TextView region = v.findViewById(R.id.region);
        TextView prefectureP = v.findViewById(R.id.prefecture);
        TextView communeP = v.findViewById(R.id.commune);
        TextView cantonP = v.findViewById(R.id.canton);
        TextView villageP = v.findViewById(R.id.village);
        TextView pointAttentionP = v.findViewById(R.id.point_attention);
        TextView topoAndSocial = v.findViewById(R.id.topo_and_social);
        TextView maireAndSocial = v.findViewById(R.id.maire_and_social);
        TextView owner = v.findViewById(R.id.proprietaire);
        TextView owner2 = v.findViewById(R.id.proprietaire_2);
        TextView limitrophe = v.findViewById(R.id.limitrophe);
        TextView emailAddress = v.findViewById(R.id.email_address);
        TextView quality = v.findViewById(R.id.quality);
        RecyclerView actorsRecycler = v.findViewById(R.id.actors_recycler);

        // Remplir les données avec vérification de nullité
        region.setText(getSafeString("REGION DE ", operationDetail.getRegion()));
        prefectureP.setText(getSafeString("PREFECTURE DE ", operationDetail.getPrefecture()));
        communeP.setText(getSafeString("COMMUNE DE ", operationDetail.getCommune()));
        cantonP.setText(getSafeString("CANTON  ", operationDetail.getCanton()));
        villageP.setText(getSafeString("VILLAGE ", operationDetail.getLocality()));

        String pointAttentionText = operationDetail.getConflict() != null ?
                operationDetail.getConflict().getPointOfAttention() : "";
        pointAttentionP.setText(getSafeString("", pointAttentionText));

        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH) + 1;

        // Récupérer les acteurs avec vérification
        ActorModel topographe = getSpecificActorSafely(getUinAtIndex(5));
        ActorModel socialLand = getSpecificActorSafely(getUinAtIndex(7));
        ActorModel proprietaire = getSpecificActorSafely(getUinAtIndex(3));
        ActorModel limitropheN = getSpecificActorSafely(getUinAtIndex(8));
        ActorModel limitropheS = getSpecificActorSafely(getUinAtIndex(9));
        ActorModel limitropheE = getSpecificActorSafely(getUinAtIndex(10));
        ActorModel limitropheO = getSpecificActorSafely(getUinAtIndex(11));

        // Générer les textes
        topoAndSocial.setText(getTopoAndSocialText(topographe, c));
        maireAndSocial.setText(getMaireAndSocialText(socialLand, operationDetail.getCommune()));
        owner.setText(getProprietaireText(proprietaire));
        owner2.setText(getProprietaireDocText(proprietaire));
        limitrophe.setText(getLimitropheText(proprietaire, limitropheN, limitropheS, limitropheE, limitropheO,
                operationDetail.getLandForm(), operationDetail.getSurface()));
        emailAddress.setText(getEmailAddressText(proprietaire));
        quality.setText("Qualité du déclarant : " + getActorRole(proprietaire));

        // RecyclerView
        LinearLayoutManager lm = new LinearLayoutManager(VerbalProcessActivity.this);
        actorsRecycler.setLayoutManager(lm);

        ActorInPVAdapter adapter = new ActorInPVAdapter(checklistActors, true);
        actorsRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        PdfGenerator.getBuilder()
                .setContext(VerbalProcessActivity.this)
                .fromViewSource()
                .fromView(v)
                .setFileName("PV_" + ID + "_" + System.currentTimeMillis())
                .setFolderNameOrPath("PV_Documents")
                .savePDFSharedStorage(xmlToPDFLifecycleObserver)
                .actionAfterPDFGeneration(PdfGenerator.ActionAfterPDFGeneration.OPEN)
                .build(new PdfGeneratorListener() {
                    @Override
                    public void onFailure(FailureResponse failureResponse) {
                        super.onFailure(failureResponse);
                        isGeneratingPDF = false;
                        Log.e("PDF_ERROR", "====> " + failureResponse.getErrorMessage());
                        runOnUiThread(() ->
                                Toast.makeText(VerbalProcessActivity.this,
                                        "Erreur de génération PDF", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void showLog(String log) {
                        super.showLog(log);
                        Log.d("PDF_LOG", "====> " + log);
                    }

                    @Override
                    public void onStartPDFGeneration() {
                        Log.d("PDF", "Début génération PDF");
                    }

                    @Override
                    public void onFinishPDFGeneration() {
                        Log.d("PDF", "Fin génération PDF");
                        isGeneratingPDF = false;
                    }

                    @Override
                    public void onSuccess(SuccessResponse response) {
                        super.onSuccess(response);
                        Log.d("PDF_SUCCESS", "====> " + response.getPath());
                        runOnUiThread(() ->
                                Toast.makeText(VerbalProcessActivity.this,
                                        "PDF généré avec succès", Toast.LENGTH_SHORT).show());
                    }
                });
    }

    /**
     * Récupère un UIN par index avec vérification
     */
    private String getUinAtIndex(int index) {
        if (uin != null && uin.size() > index && uin.get(index) != null) {
            return uin.get(index);
        }
        return "";
    }

    /**
     * Récupère un acteur spécifique de manière sécurisée
     */
    private ActorModel getSpecificActorSafely(String uinValue) {
        ActorModel actor = getSpecificActor(uinValue);
        return actor != null ? actor : createEmptyActor();
    }

    /**
     * Crée un acteur vide pour éviter les null
     */
    private ActorModel createEmptyActor() {
        ActorModel empty = new ActorModel("", "", "", "", "");
        empty.setLastname("----");
        empty.setFirstname("----");
        empty.setContact("----");
        empty.setEmail("----");
        empty.setAddress("----");
        empty.setIdentificationDocNumber("----");
        empty.setIdentificationDocType("----");
        empty.setRole("----");
        return empty;
    }

    /**
     * Retourne le nom complet d'un acteur
     */
    private String getActorFullName(ActorModel actor) {
        if (actor == null) return "----";
        String lastName = (actor.getLastname() != null && !actor.getLastname().isEmpty() && !actor.getLastname().equals("null"))
                ? actor.getLastname() : "----";
        String firstName = (actor.getFirstname() != null && !actor.getFirstname().isEmpty() && !actor.getFirstname().equals("null"))
                ? actor.getFirstname() : "----";
        return lastName + " " + firstName;
    }

    /**
     * Récupère le contact d'un acteur
     */
    private String getActorContact(ActorModel actor) {
        if (actor == null) return "----";
        if (actor.getPrimaryPhone() != null && !actor.getPrimaryPhone().isEmpty() && !actor.getPrimaryPhone().equals("null")) {
            return actor.getPrimaryPhone();
        }
        if (actor.getContact() != null && !actor.getContact().isEmpty() && !actor.getContact().equals("null")) {
            return actor.getContact();
        }
        return "----";
    }

    /**
     * Récupère l'email d'un acteur
     */
    private String getActorEmail(ActorModel actor) {
        if (actor == null) return "----";
        String email = actor.getEmail();
        return (email != null && !email.isEmpty() && !email.equals("null")) ? email : "----";
    }

    /**
     * Récupère l'adresse d'un acteur
     */
    private String getActorAddress(ActorModel actor) {
        if (actor == null) return "----";
        String address = actor.getAddress();
        return (address != null && !address.isEmpty() && !address.equals("null")) ? address : "----";
    }

    /**
     * Récupère le numéro de document d'identité
     */
    private String getActorIdentificationDocNumber(ActorModel actor) {
        if (actor == null) return "----";
        String docNumber = actor.getIdentificationDocNumber();
        return (docNumber != null && !docNumber.isEmpty() && !docNumber.equals("null")) ? docNumber : "----";
    }

    /**
     * Récupère le type de document d'identité
     */
    private String getActorIdentificationDocType(ActorModel actor) {
        if (actor == null) return "----";
        String docType = actor.getIdentificationDocType();
        return (docType != null && !docType.isEmpty() && !docType.equals("null")) ? docType : "----";
    }

    /**
     * Récupère le rôle d'un acteur
     */
    private String getActorRole(ActorModel actor) {
        if (actor == null) return "----";
        String role = actor.getRole();
        if (role != null && !role.isEmpty() && !role.equals("null")) {
            String roleName = Role.getRoleNameByCode(role);
            return (roleName != null && !roleName.isEmpty()) ? roleName : role;
        }
        return "----";
    }

    /**
     * Retourne une chaîne sécurisée avec préfixe
     */
    private String getSafeString(String prefix, String value) {
        if (value == null || value.isEmpty() || value.equals("null")) {
            return prefix + "----";
        }
        return prefix + value;
    }

    /**
     * Génère le texte pour topographe et agent social
     */
    private String getTopoAndSocialText(ActorModel topographe, Calendar c) {
        int month = c.get(Calendar.MONTH) + 1;
        String topoName = getActorFullName(topographe);
        return "L’an " + c.get(Calendar.YEAR) + " et le " + c.get(Calendar.DAY_OF_MONTH) + "/" + month +
                " , nous soussignés, M. / Mme " + topoName;
    }

    /**
     * Génère le texte pour maire et agent social
     */
    private String getMaireAndSocialText(ActorModel socialLand, String commune) {
        String socialName = getActorFullName(socialLand);
        String communeText = (commune != null && !commune.isEmpty() && !commune.equals("null")) ? commune : "----";
        return "Technicien.ne topographe et M. / Mme " + socialName +
                " Agent socio foncier, commissionnés par la mairie de " + communeText;
    }

    /**
     * Génère le texte pour le propriétaire
     */
    private String getProprietaireText(ActorModel proprietaire) {
        String proprietaireName = getActorFullName(proprietaire);
        return "Attendu M. / Mme " + proprietaireName +
                " et ses limitrophes : les intéressés ayant été dûment prévenus de l’heure et du jour des opérations de cartographie, d’abornement des limites, de constatation et d’enregistrement des droits fonciers de sa parcelle.";
    }

    /**
     * Génère le texte du document d'identité du propriétaire
     */
    private String getProprietaireDocText(ActorModel proprietaire) {
        String proprietaireName = getActorFullName(proprietaire);
        String docNumber = getActorIdentificationDocNumber(proprietaire);
        String docType = getActorIdentificationDocType(proprietaire);
        return "M. / Mme " + proprietaireName + " Document d’identification N° " + docNumber + " type: " + docType;
    }

    /**
     * Génère le texte des limitrophes
     */
    private String getLimitropheText(ActorModel proprietaire, ActorModel limitropheN, ActorModel limitropheS,
                                     ActorModel limitropheE, ActorModel limitropheO, String landForm, String surface) {
        String proprietaireName = getActorFullName(proprietaire);
        String limitropheNName = getActorFullName(limitropheN);
        String limitropheSName = getActorFullName(limitropheS);
        String limitropheEName = getActorFullName(limitropheE);
        String limitropheOName = getActorFullName(limitropheO);

        String landFormText = (landForm != null && !landForm.isEmpty() && !landForm.equals("null")) ? landForm : "----";
        String surfaceText = (surface != null && !surface.isEmpty() && !surface.equals("null")) ? surface : "----";

        return "Après avoir reconnu les limites et la consistance générale de la parcelle qui est un terrain de forme " + landFormText +
                ", limité au Nord par " + limitropheNName +
                "; au Sud par " + limitropheSName +
                "; à l’Est par " + limitropheEName +
                "; et à l’Ouest par " + limitropheOName +
                " avec une superficie de " + surfaceText + " mètres carré";
    }

    /**
     * Génère le texte de l'adresse email et contact
     */
    private String getEmailAddressText(ActorModel proprietaire) {
        String address = getActorAddress(proprietaire);
        String phoneNumber = getActorContact(proprietaire);
        String email = getActorEmail(proprietaire);
        return "Demeurant à : " + address + "; tél : " + phoneNumber + "; Email : " + email;
    }

    public void getDetail() {
        if (isDataLoaded) return;

        progressDialog.setMessage("Chargement des données...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String token = new SessionManager(this).getAccessToken();
        Log.d("****Token", "=====> " + token);

        ApiService apiService = RetrofitClient.getClient(token).create(ApiService.class);

        apiService.getOp(ID)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                String jsonString = response.body().string();
                                JSONObject jsonObject = new JSONObject(jsonString);
                                JSONObject dataObject = jsonObject.getJSONObject("data");

                                Gson gson = new Gson();
                                operationDetail = gson.fromJson(dataObject.toString(), OperationDetail.class);

                                if (operationDetail != null && operationDetail.getFirstCheckListOperation() != null) {
                                    // Remplir la liste des UIN
                                    uin.clear();
                                    uin.add(operationDetail.getFirstCheckListOperation().getMayorUIN());
                                    uin.add(operationDetail.getFirstCheckListOperation().getTraditionalChiefUIN());
                                    uin.add(operationDetail.getFirstCheckListOperation().getNotableUIN());
                                    uin.add(operationDetail.getFirstCheckListOperation().getOwnerUIN());
                                    uin.add(operationDetail.getFirstCheckListOperation().getGeometerUIN());
                                    uin.add(operationDetail.getFirstCheckListOperation().getTopographerUIN());
                                    uin.add(operationDetail.getFirstCheckListOperation().getSocialLandAgentUIN());
                                    uin.add(operationDetail.getFirstCheckListOperation().getInterestedThirdPartyUIN());

                                    if (operationDetail.getFirstCheckListOperation().getBorderingList() != null) {
                                        for (int i = 0; i < operationDetail.getFirstCheckListOperation().getBorderingList().size(); i++) {
                                            String borderingUin = operationDetail.getFirstCheckListOperation().getBorderingList().get(i).getUin();
                                            if (borderingUin != null && !borderingUin.isEmpty()) {
                                                uin.add(borderingUin);
                                            }
                                        }
                                    }

                                    Log.d("******", "====> Taille UIN: " + uin.size());

                                    // Appel pour les détails des acteurs
                                    apiService.uinDetailInfo(new UinDetailRequest(uin)).enqueue(new Callback<UinDetailResponse>() {
                                        @Override
                                        public void onResponse(Call<UinDetailResponse> call, Response<UinDetailResponse> response) {
                                            if (response.isSuccessful() && response.body() != null) {
                                                checklistActors = response.body().getData();
                                                Log.d("*****R", "=====> Acteurs reçus: " + (checklistActors != null ? checklistActors.size() : 0));
                                                buildPV(operationDetail);
                                            } else {
                                                handleUinDetailError();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<UinDetailResponse> call, Throwable t) {
                                            handleNetworkError(t);
                                        }
                                    });
                                } else {
                                    handleDataError();
                                }

                            } catch (Exception e) {
                                handleException(e);
                            }
                        } else {
                            handleResponseError(response);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        handleNetworkError(t);
                    }
                });
    }

    private void handleUinDetailError() {
        runOnUiThread(() -> {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Toast.makeText(VerbalProcessActivity.this, "Erreur lors de la récupération des détails des acteurs", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void handleDataError() {
        runOnUiThread(() -> {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Toast.makeText(VerbalProcessActivity.this, "Données d'opération invalides", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void handleException(Exception e) {
        runOnUiThread(() -> {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Toast.makeText(VerbalProcessActivity.this, "Erreur de traitement", Toast.LENGTH_SHORT).show();
            finish();
        });
        e.printStackTrace();
    }

    private void handleResponseError(Response<ResponseBody> response) {
        System.out.println("Erreur : " + response.code());
        runOnUiThread(() -> {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Toast.makeText(VerbalProcessActivity.this, "Erreur dans la réception des données", Toast.LENGTH_SHORT).show();
            finish();
        });
        try {
            if (response.errorBody() != null) {
                Log.d("*****Error", "=====> " + response.errorBody().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleNetworkError(Throwable t) {
        runOnUiThread(() -> {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Toast.makeText(VerbalProcessActivity.this, "Erreur réseau: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        });
        Log.d("PPPPP", t.getMessage());
    }

    public void buildPV(OperationDetail operationDetail) {
        if (operationDetail == null || checklistActors == null) {
            return;
        }

        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH) + 1;

        ActorModel topographe = getSpecificActorSafely(getUinAtIndex(5));
        ActorModel socialLand = getSpecificActorSafely(getUinAtIndex(7));
        ActorModel proprietaire = getSpecificActorSafely(getUinAtIndex(3));
        ActorModel limitropheN = getSpecificActorSafely(getUinAtIndex(8));
        ActorModel limitropheS = getSpecificActorSafely(getUinAtIndex(9));
        ActorModel limitropheE = getSpecificActorSafely(getUinAtIndex(10));
        ActorModel limitropheO = getSpecificActorSafely(getUinAtIndex(11));

        // Remplir l'UI avec vérifications
        b.printPv.region.setText(getSafeString("REGION DE ", operationDetail.getRegion()));
        b.printPv.prefecture.setText(getSafeString("PREFECTURE DE ", operationDetail.getPrefecture()));
        b.printPv.commune.setText(getSafeString("COMMUNE DE ", operationDetail.getCommune()));
        b.printPv.canton.setText(getSafeString("Canton ", operationDetail.getCanton()));
        b.printPv.village.setText(getSafeString("Village ", operationDetail.getLocality()));

        String pointAttentionText = operationDetail.getConflict() != null ?
                operationDetail.getConflict().getPointOfAttention() : "";
        b.printPv.pointAttention.setText(getSafeString("", pointAttentionText));

        b.printPv.topoAndSocial.setText(getTopoAndSocialText(topographe, c));
        b.printPv.maireAndSocial.setText(getMaireAndSocialText(socialLand, operationDetail.getCommune()));
        b.printPv.proprietaire.setText(getProprietaireText(proprietaire));
        b.printPv.proprietaire2.setText(getProprietaireDocText(proprietaire));
        b.printPv.limitrophe.setText(getLimitropheText(proprietaire, limitropheN, limitropheS, limitropheE, limitropheO,
                operationDetail.getLandForm(), operationDetail.getSurface()));
        b.printPv.emailAddress.setText(getEmailAddressText(proprietaire));
        b.printPv.quality.setText("Qualité du déclarant : " + getActorRole(proprietaire));

        LinearLayoutManager lm = new LinearLayoutManager(VerbalProcessActivity.this);
        b.printPv.actorsRecycler.setLayoutManager(lm);

        ActorInPVAdapter adapter = new ActorInPVAdapter(checklistActors, true);
        b.printPv.actorsRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        isDataLoaded = true;

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public ActorModel getSpecificActor(String uinValue) {
        if (uinValue == null || uinValue.isEmpty() || checklistActors == null) {
            return null;
        }
        for (ActorModel actor : checklistActors) {
            if (actor != null && actor.getUin() != null && actor.getUin().equals(uinValue)) {
                return actor;
            }
        }
        return null;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("operation_id", ID);
        outState.putBoolean("is_data_loaded", isDataLoaded);
    }

    private void restoreInstanceState(@NonNull Bundle savedInstanceState) {
        ID = savedInstanceState.getInt("operation_id", 0);
        isDataLoaded = savedInstanceState.getBoolean("is_data_loaded", false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}