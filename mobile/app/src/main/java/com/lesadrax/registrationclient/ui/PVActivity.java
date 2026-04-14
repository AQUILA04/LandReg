package com.lesadrax.registrationclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gkemon.XMLtoPDF.PdfGenerator;
import com.gkemon.XMLtoPDF.PdfGeneratorListener;
import com.gkemon.XMLtoPDF.model.FailureResponse;
import com.gkemon.XMLtoPDF.model.SuccessResponse;
import com.lesadrax.registrationclient.MyApp;
import com.lesadrax.registrationclient.R;
import com.lesadrax.registrationclient.data.model.ActorModel;
import com.lesadrax.registrationclient.data.model.Operation;
import com.lesadrax.registrationclient.data.model.Role;
import com.lesadrax.registrationclient.databinding.ActivityPvactivityBinding;
import com.lesadrax.registrationclient.from.utils.FormDataUtils;
import com.lesadrax.registrationclient.ui.adapter.ActorInPVAdapter;

import java.util.Calendar;
import java.util.List;

public class PVActivity extends AppCompatActivity {

    private ActivityPvactivityBinding b;
    private String area = "", prefecture = "", canton = "", commune = "", village = "", place = "", pointAttention = "", surface = "", landForm = "";
    private PdfGenerator.XmlToPDFLifecycleObserver xmlToPDFLifecycleObserver;
    private List<ActorModel> actorModels;
    private Operation op;

    // Variables pour le cache des données affichées
    private boolean isDataLoaded = false;
    private String cachedRegion = "";
    private String cachedPrefecture = "";
    private String cachedCommune = "";
    private String cachedVillage = "";
    private String cachedCanton = "";
    private String cachedPlace = "";
    private String cachedSurface = "";
    private String cachedLandForm = "";
    private String cachedPointAttention = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = ActivityPvactivityBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        // Restaurer l'état si l'activité est recréée
        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        } else {
            // Récupération normale depuis l'Intent
            op = (Operation) getIntent().getSerializableExtra("DATA");
            actorModels = op != null ? op.getActors() : null;
        }

        b.title.setText("PV PROVISOIRE");

        b.print.setText("Valider");

        xmlToPDFLifecycleObserver = new PdfGenerator.XmlToPDFLifecycleObserver(this);
        getLifecycle().addObserver(xmlToPDFLifecycleObserver);

        // Charger les données si l'opération existe
        if (op != null) {
            loadData();
            displayData();
        }

        b.back.setOnClickListener(v -> onBackPressed());

        b.print.setOnClickListener(v -> {
            // Stocker l'opération dans MyApp avant de naviguer
            //MyApp.getInstance().setTempOperation(op);
            startActivity(new Intent(PVActivity.this, SignatureActivity.class)
                    .putExtra("before", "after").putExtra("DATA", op));
            finish();
        });
    }

    /**
     * Charge les données depuis l'opération (avec cache)
     */
    private void loadData() {
        if (isDataLoaded) return;

        if (op != null && op.getFormValues() != null) {
            cachedRegion = getSafeFormValue(op, "region");
            cachedPrefecture = getSafeFormValue(op, "prefecture");
            cachedCommune = getSafeFormValue(op, "commune");
            cachedVillage = getSafeFormValue(op, "locality");
            cachedCanton = getSafeFormValue(op, "canton");
            cachedPlace = getSafeFormValue(op, "place");
            cachedSurface = getSafeFormValue(op, "surface");
            cachedLandForm = getSafeFormValue(op, "landForm");
            cachedPointAttention = getSafeFormValue(op, "pointAttention");

            // Assigner aux variables d'instance pour compatibilité
            area = cachedRegion;
            prefecture = cachedPrefecture;
            commune = cachedCommune;
            village = cachedVillage;
            canton = cachedCanton;
            place = cachedPlace;
            surface = cachedSurface;
            landForm = cachedLandForm;
            pointAttention = cachedPointAttention;
        }

        isDataLoaded = true;
    }

    /**
     * Récupère une valeur de formulaire de manière sécurisée
     */
    private String getSafeFormValue(Operation op, String key) {
        String value = FormDataUtils.getFormValueDisplay(op.getFormValues(), key);
        return (value != null && !value.isEmpty() && !value.equals("null")) ? value : "";
    }

    /**
     * Affiche les données dans l'UI avec vérification de nullité
     */
    private void displayData() {
        if (op == null) {
            Log.e("PVActivity", "Opération est null");
            return;
        }

        if (actorModels == null || actorModels.size() < 12) {
            Log.e("PVActivity", "Liste des acteurs invalide");
            return;
        }

        try {
            // Récupérer les acteurs avec vérification des indices
            ActorModel topographe = actorModels.size() > 5 ? actorModels.get(5) : null;
            ActorModel socialLand = actorModels.size() > 7 ? actorModels.get(7) : null;
            ActorModel proprietaire = actorModels.size() > 3 ? actorModels.get(3) : null;
            ActorModel limitropheN = actorModels.size() > 8 ? actorModels.get(8) : null;
            ActorModel limitropheS = actorModels.size() > 9 ? actorModels.get(9) : null;
            ActorModel limitropheE = actorModels.size() > 10 ? actorModels.get(10) : null;
            ActorModel limitropheO = actorModels.size() > 11 ? actorModels.get(11) : null;

            // Remplir les données de base
            b.printPv.region.setText(getSafeString("REGION DE ", cachedRegion));
            b.printPv.prefecture.setText(getSafeString("PREFECTURE DE ", cachedPrefecture));
            b.printPv.commune.setText(getSafeString("COMMUNE DE ", cachedCommune));
            b.printPv.canton.setText(getSafeString("CANTON  ", cachedCanton));
            b.printPv.village.setText(getSafeString("VILLAGE ", cachedVillage));
            b.printPv.pointAttention.setText(getSafeString("", cachedPointAttention));

            Calendar c = Calendar.getInstance();
            int month = c.get(Calendar.MONTH) + 1;

            // Topographe et Agent social
            b.printPv.topoAndSocial.setText(getTopoAndSocialText(topographe, socialLand, c));
            b.printPv.maireAndSocial.setText(getMaireAndSocialText(socialLand, cachedCommune));

            // Propriétaire
            b.printPv.proprietaire.setText(getProprietaireText(proprietaire));
            b.printPv.proprietaire2.setText(getProprietaireDocText(proprietaire));
            b.printPv.emailAddress.setText(getEmailAddressText(proprietaire));
            b.printPv.quality.setText(getQualityText(proprietaire));

            // Limitrophes
            b.printPv.limitrophe.setText(getLimitropheText(proprietaire, limitropheN, limitropheS, limitropheE, limitropheO, cachedLandForm, cachedSurface));

            // RecyclerView
            LinearLayoutManager lm = new LinearLayoutManager(PVActivity.this);
            b.printPv.actorsRecycler.setLayoutManager(lm);

            ActorInPVAdapter adapter = new ActorInPVAdapter(actorModels);
            b.printPv.actorsRecycler.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            Log.e("PVActivity", "Erreur d'affichage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Méthodes utilitaires pour générer les textes avec vérification de nullité

    private String getSafeString(String prefix, String value) {
        if (value == null || value.isEmpty() || value.equals("null")) {
            return prefix + "----";
        }
        return prefix + value;
    }

    private String getActorFullName(ActorModel actor) {
        if (actor == null) return "----";
        String lastName = (actor.getLastname() != null && !actor.getLastname().isEmpty() && !actor.getLastname().equals("null"))
                ? actor.getLastname() : "----";
        String firstName = (actor.getFirstname() != null && !actor.getFirstname().isEmpty() && !actor.getFirstname().equals("null"))
                ? actor.getFirstname() : "----";
        return lastName + " " + firstName;
    }

    private String getActorName(ActorModel actor) {
        if (actor == null) return "----";
        String name = (actor.getName() != null && !actor.getName().isEmpty() && !actor.getName().equals("null"))
                ? actor.getName() : "";
        if (!name.isEmpty()) return name;
        return getActorFullName(actor);
    }

    private String getActorContact(ActorModel actor) {
        if (actor == null) return "----";
        String contact = actor.getContact();
        return (contact != null && !contact.isEmpty() && !contact.equals("null")) ? contact : "----";
    }

    private String getActorEmail(ActorModel actor) {
        if (actor == null) return "----";
        String email = actor.getEmail();
        return (email != null && !email.isEmpty() && !email.equals("null")) ? email : "----";
    }

    private String getActorAddress(ActorModel actor) {
        if (actor == null) return "----";
        String address = actor.getAddress();
        return (address != null && !address.isEmpty() && !address.equals("null")) ? address : "----";
    }

    private String getActorIdentificationDocNumber(ActorModel actor) {
        if (actor == null) return "----";
        String docNumber = actor.getIdentificationDocNumber();
        return (docNumber != null && !docNumber.isEmpty() && !docNumber.equals("null")) ? docNumber : "----";
    }

    private String getActorIdentificationDocType(ActorModel actor) {
        if (actor == null) return "----";
        String docType = actor.getIdentificationDocType();
        return (docType != null && !docType.isEmpty() && !docType.equals("null")) ? docType : "----";
    }

    private String getActorRole(ActorModel actor) {
        if (actor == null) return "----";
        String role = actor.getRole();
        if (role != null && !role.isEmpty() && !role.equals("null")) {
            String roleName = Role.getRoleNameByCode(role);
            return (roleName != null && !roleName.isEmpty()) ? roleName : role;
        }
        return "----";
    }

    private String getTopoAndSocialText(ActorModel topographe, ActorModel socialLand, Calendar c) {
        int month = c.get(Calendar.MONTH) + 1;
        String topoName = getActorFullName(topographe);
        return "L’an " + c.get(Calendar.YEAR) + " et le " + c.get(Calendar.DAY_OF_MONTH) + "/" + month +
                " , nous soussignés, M. / Mme " + topoName;
    }

    private String getMaireAndSocialText(ActorModel socialLand, String commune) {
        String socialName = getActorFullName(socialLand);
        String communeText = (commune != null && !commune.isEmpty() && !commune.equals("null")) ? commune : "----";
        return "Technicien.ne topographe et M. / Mme " + socialName +
                " Agent socio foncier, commissionnés par la mairie de " + communeText;
    }

    private String getProprietaireText(ActorModel proprietaire) {
        String proprietaireName = getActorFullName(proprietaire);
        return "Attendu M. / Mme " + proprietaireName +
                " et ses limitrophes : les intéressés ayant été dûment prévenus de l’heure et du jour des opérations de cartographie, d’abornement des limites, de constatation et d’enregistrement des droits fonciers de sa parcelle.";
    }

    private String getProprietaireDocText(ActorModel proprietaire) {
        String proprietaireName = getActorFullName(proprietaire);
        String docNumber = getActorIdentificationDocNumber(proprietaire);
        String docType = getActorIdentificationDocType(proprietaire);
        return "M. / Mme " + proprietaireName + " Document d’identification N° " + docNumber + " type: " + docType;
    }

    private String getEmailAddressText(ActorModel proprietaire) {
        String address = getActorAddress(proprietaire);
        String contact = getActorContact(proprietaire);
        String email = getActorEmail(proprietaire);
        return "Demeurant à : " + address + "; tél : " + contact + "; Email : " + email;
    }

    private String getQualityText(ActorModel proprietaire) {
        return "Qualité du déclarant : " + getActorRole(proprietaire);
    }

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

    public void generatePDF() {
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(PVActivity.this.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.test, null);

        TextView region = v.findViewById(R.id.region);
        region.setText(getSafeString("REGION DE ", area));

        TextView prefectureP = v.findViewById(R.id.prefecture);
        prefectureP.setText(getSafeString("PREFECTURE DE ", prefecture));

        TextView communeP = v.findViewById(R.id.commune);
        communeP.setText(getSafeString("COMMUNE DE ", commune));

        TextView cantonP = v.findViewById(R.id.canton);
        cantonP.setText(getSafeString("CANTON  ", canton));

        TextView villageP = v.findViewById(R.id.village);
        villageP.setText(getSafeString("VILLAGE ", village));

        TextView pointAttentionP = v.findViewById(R.id.point_attention);
        pointAttentionP.setText(getSafeString("", pointAttention));

        PdfGenerator.getBuilder()
                .setContext(PVActivity.this)
                .fromViewSource()
                .fromView(v)
                .setFileName("test")
                .setFolderNameOrPath("CardForder")
                .savePDFSharedStorage(xmlToPDFLifecycleObserver)
                .actionAfterPDFGeneration(PdfGenerator.ActionAfterPDFGeneration.OPEN)
                .build(new PdfGeneratorListener() {
                    @Override
                    public void onFailure(FailureResponse failureResponse) {
                        super.onFailure(failureResponse);
                        Log.d("*L1", "====> " + failureResponse.getErrorMessage());
                    }

                    @Override
                    public void showLog(String log) {
                        super.showLog(log);
                        Log.d("*L2", "====> " + log);
                    }

                    @Override
                    public void onStartPDFGeneration() {
                    }

                    @Override
                    public void onFinishPDFGeneration() {
                    }

                    @Override
                    public void onSuccess(SuccessResponse response) {
                        super.onSuccess(response);
                        Log.d("****Succes", "====> " + response.getPath());
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyApp.getInstance().clearTempData();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Ne sauvegarder que les données essentielles
        if (op != null) {
            outState.putLong("operation_id", op.getId());  // ✅ CORRECTION: putLong pour un long
        }
        outState.putBoolean("is_data_loaded", isDataLoaded);
        outState.putString("cached_region", cachedRegion);
        outState.putString("cached_prefecture", cachedPrefecture);
        outState.putString("cached_commune", cachedCommune);
        outState.putString("cached_village", cachedVillage);
        outState.putString("cached_canton", cachedCanton);
        outState.putString("cached_place", cachedPlace);
        outState.putString("cached_surface", cachedSurface);
        outState.putString("cached_land_form", cachedLandForm);
        outState.putString("cached_point_attention", cachedPointAttention);
    }

    private void restoreInstanceState(@NonNull Bundle savedInstanceState) {
        long operationId = savedInstanceState.getLong("operation_id", 0);  // ✅ CORRECTION: getLong pour un long
        isDataLoaded = savedInstanceState.getBoolean("is_data_loaded", false);
        cachedRegion = savedInstanceState.getString("cached_region", "");
        cachedPrefecture = savedInstanceState.getString("cached_prefecture", "");
        cachedCommune = savedInstanceState.getString("cached_commune", "");
        cachedVillage = savedInstanceState.getString("cached_village", "");
        cachedCanton = savedInstanceState.getString("cached_canton", "");
        cachedPlace = savedInstanceState.getString("cached_place", "");
        cachedSurface = savedInstanceState.getString("cached_surface", "");
        cachedLandForm = savedInstanceState.getString("cached_land_form", "");
        cachedPointAttention = savedInstanceState.getString("cached_point_attention", "");

        // Assigner aux variables d'instance
        area = cachedRegion;
        prefecture = cachedPrefecture;
        commune = cachedCommune;
        village = cachedVillage;
        canton = cachedCanton;
        place = cachedPlace;
        surface = cachedSurface;
        landForm = cachedLandForm;
        pointAttention = cachedPointAttention;

        // Récupérer l'opération depuis MyApp si nécessaire
        if (operationId != 0 && op == null) {
            op = MyApp.getInstance().getTempOperation();
        }

        actorModels = op != null ? op.getActors() : null;

        // Recharger les données si nécessaire
        if (!isDataLoaded && op != null) {
            loadData();
        }

        displayData();
    }
}