package com.lesadrax.registrationclient.ui.activity;

import static com.lesadrax.registrationclient.data.model.RoleEnum.MAYOR;
import static com.lesadrax.registrationclient.data.model.RoleEnum.RoleType.INFORMAL_GROUP;
import static com.lesadrax.registrationclient.data.model.RoleEnum.RoleType.PHYSICAL_PERSON;
import static com.lesadrax.registrationclient.data.model.RoleEnum.RoleType.PHYSICAL_PERSON2;
import static com.lesadrax.registrationclient.data.model.RoleEnum.RoleType.PRIVATE_LEGAL_ENTITY;
import static com.lesadrax.registrationclient.data.model.RoleEnum.RoleType.PUBLIC_LEGAL_ENTITY;
import static com.lesadrax.registrationclient.from.utils.FormIntents.TEXT_PICKER_REQUEST;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lesadrax.registrationclient.MyApp;
import com.lesadrax.registrationclient.R;
import com.lesadrax.registrationclient.data.model.Actor;
import com.lesadrax.registrationclient.data.model.RoleEnum;
import com.lesadrax.registrationclient.data.model.UpdateModel;
import com.lesadrax.registrationclient.data.model.UpdateResponse;
import com.lesadrax.registrationclient.data.network.ApiService;
import com.lesadrax.registrationclient.data.network.RetrofitClient;
import com.lesadrax.registrationclient.data.utis.DataUtils;
import com.lesadrax.registrationclient.databinding.ActivityAddActorBinding;
import com.lesadrax.registrationclient.from.FormView;
import com.lesadrax.registrationclient.from.model.FormField;
import com.lesadrax.registrationclient.from.model.FormValue;
import com.lesadrax.registrationclient.from.model.ItemData;
import com.lesadrax.registrationclient.from.ui.MyCustomPickerField;
import com.lesadrax.registrationclient.from.utils.FormBackup;
import com.lesadrax.registrationclient.from.utils.FormDataUtils;
import com.lesadrax.registrationclient.from.utils.FormFieldParser;
import com.lesadrax.registrationclient.from.utils.FormUtils;
import com.lesadrax.registrationclient.from.utils.Utils;
import com.lesadrax.registrationclient.sessionManager.SessionManager;
import com.lesadrax.registrationclient.ui.FingerActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddActorActivity extends AppCompatActivity {

    ProgressDialog progressDialog;

    private Actor actor;
    private List<RoleEnum> roles = new ArrayList<>();

    private ActivityAddActorBinding b;
    boolean onlineMode = false;

    private boolean isUpdating = false;
    private boolean isMoralEntity = false;
    int id = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = ActivityAddActorBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        progressDialog = new ProgressDialog(AddActorActivity.this);

        actor = (Actor) getIntent().getSerializableExtra("ACTOR");
        id = getIntent().getIntExtra("ID", 0);

        b.back.setOnClickListener(v -> {
            onBackPressed();
        });
        b.btn.setOnClickListener(v -> {

            b.roleHint.setErrorEnabled(false);
            if (selectedRoleItem == null){
                b.roleHint.setError(getString(R.string.champ_requis));
                return;
            }

            Map<String, FormValue> data = null;


            if (selectedRoleItem.getType() == PHYSICAL_PERSON){
                data = b.formPp.getFormData();
                isMoralEntity = false;
            } else if (selectedRoleItem.getType() == PRIVATE_LEGAL_ENTITY){ /// PP Moral Privé
                data = b.formPrivate.getFormData();
                isMoralEntity = true;
            } else if (selectedRoleItem.getType() == PUBLIC_LEGAL_ENTITY){ /// PP Moral Public
                data = b.formPublic.getFormData();
                isMoralEntity = true;
            } else if (selectedRoleItem.getType() == INFORMAL_GROUP){ /// Groupe Inf
                data = b.formGi.getFormData();
                isMoralEntity = true;
            } else if (selectedRoleItem.getType() == PHYSICAL_PERSON2){ /// Agent
                data = b.formPp2.getFormData();
                isMoralEntity = false;
            }

            if (data != null) {
                save(data);
            }
        });

        if (id == 0){
            build();
        } else {
            b.title.setText("Détail Enregistrement");
            loadData(id);
            onlineMode = true;
        }

    }

    private void loadData(long id) {

        String token = new SessionManager(this).getAccessToken();
        System.out.println(token);
        System.out.println("ID : "+id);
        ApiService apiService = RetrofitClient.getClient(token).create(ApiService.class);

        b.progressPan.setVisibility(View.VISIBLE);
        apiService.getActor(id)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        b.progressPan.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {

                            try {
                                String jsonString = response.body().string();
                                Log.d("****Ret", "=====> "+jsonString);
                                JSONObject jsonObject = new JSONObject(jsonString);

                                // Access "data" key
                                JSONObject dataObject = jsonObject.getJSONObject("data");
                                List<FormField> fields = new ArrayList<>();
                                String role = dataObject.getString("role");
                                String roleType = null;
                                actor = new Actor();

                                if (!FormBackup.isnull(dataObject,"physicalPerson")){
                                      JSONObject physicalObject = dataObject.getJSONObject("physicalPerson");
                                      if(physicalObject.has("id")){
                                          Log.d("****Has", "=====> "+physicalObject.getInt("id"));
                                          actor.setPersonID(physicalObject.getInt("id"));
                                      }
                                      if(physicalObject.has("nationality") ){
                                          Log.d("****Nat", "=====> "+physicalObject.getString("nationality"));
                                      }
                                      if(physicalObject.has("identificationDoc") && !physicalObject.isNull("identificationDoc")){
                                          JSONObject doc = physicalObject.getJSONObject("identificationDoc");
                                          if(doc.has("id"))
                                              actor.setDocID(doc.getInt("id"));
                                      }
//                                    if (FormBackup.isnull(dataObject,"sex")){
                                    if ("SOCIAL_LAND_AGENT".equals(role) || "TOPOGRAPHER".equals(role) ){
                                        roleType = PHYSICAL_PERSON2.name();
                                        fields = FormFieldParser.parseFormFields(AddActorActivity.this, R.raw.form_actor_pp2);

                                    } else {
                                        roleType = PHYSICAL_PERSON.name();
                                        fields = FormFieldParser.parseFormFields(AddActorActivity.this, R.raw.form_actor_pp);
                                    }
                                } else if (!FormBackup.isnull(dataObject,"privateLegalEntity")){
                                    JSONObject privateLegalEntityObject = dataObject.getJSONObject("privateLegalEntity");
                                    if(privateLegalEntityObject.has("id")){
                                        actor.setPersonID(privateLegalEntityObject.getInt("id"));
                                    }
                                    roleType = PRIVATE_LEGAL_ENTITY.name();
                                    fields = FormFieldParser.parseFormFields(AddActorActivity.this, R.raw.form_actor_pm_private);
                                } else if (!FormBackup.isnull(dataObject,"publicLegalEntity")){
                                    JSONObject publicLegalEntityObject = dataObject.getJSONObject("publicLegalEntity");
                                    if(publicLegalEntityObject.has("id")){
                                        actor.setPersonID(publicLegalEntityObject.getInt("id"));
                                    }
                                    roleType = PUBLIC_LEGAL_ENTITY.name();
                                    fields = FormFieldParser.parseFormFields(AddActorActivity.this, R.raw.form_actor_pm_public);
                                } else if (!FormBackup.isnull(dataObject,"informalGroup")){
                                    JSONObject informalGroupObject = dataObject.getJSONObject("informalGroup");
                                    if(informalGroupObject.has("id")){
                                        actor.setPersonID(informalGroupObject.getInt("id"));
                                    }
                                    roleType = INFORMAL_GROUP.name();
                                    fields = FormFieldParser.parseFormFields(AddActorActivity.this, R.raw.form_actor_gi);
                                } else {
                                    Toast.makeText(AddActorActivity.this, "Erreur", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                if(dataObject.has("fingerprintStores") && !dataObject.isNull("fingerprintStores")){
                                    JSONArray fingerprint = dataObject.getJSONArray("fingerprintStores");

                                    for(int i = 0; i < fingerprint.length(); i++){
                                        JSONObject fp = fingerprint.getJSONObject(i);
                                        if(i == 0){
                                            actor.setFinger1ID(fp.getInt("id"));
                                        }

                                        if(i == 1){
                                            actor.setFinger2ID(fp.getInt("id"));
                                        }

                                        if(i == 2){
                                            actor.setFinger1ID(fp.getInt("id"));
                                        }
                                    }
                                }
                                Log.d("***ID", "====> "+actor.getPersonID());
                                Map<String, FormValue> backupData = FormBackup.backupActor(dataObject, fields);

                                System.out.println(roleType+"");
                                backupData.put("roleType", new FormValue(roleType, roleType, roleType));

                                for (Map.Entry<String, FormValue> entry : backupData.entrySet()) {
                                    String key = entry.getKey();
                                    FormValue value = entry.getValue();

                                    Log.d("****MAP_CONTENT", "Clé: " + key + " | Valeur: " + value.getDisplay());
                                }


                                actor.setFormValues(backupData);
                                build();


                            } catch (Exception e){
                                Toast.makeText(AddActorActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                finish();
                                e.printStackTrace();
                            }

                        } else {
                            System.out.println("Erreur : " + response.code());
                            Toast.makeText(AddActorActivity.this, "Erreur", Toast.LENGTH_SHORT).show();
                            finish();
                            try {
                                Log.d("*****Error", "=====> INIT NOT RESPONSE "+response.errorBody().string());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        b.progressPan.setVisibility(View.GONE);
//                        t.printStackTrace();
                        Log.d("PPPPP", t.getMessage());
                        Toast.makeText(AddActorActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

    }

    private void build(){
        b.formPp.setRes(R.raw.form_actor_pp);
        b.formPp2.setRes(R.raw.form_actor_pp2);
        b.formPrivate.setRes(R.raw.form_actor_pm_private);
        b.formPublic.setRes(R.raw.form_actor_pm_public);
        b.formGi.setRes(R.raw.form_actor_gi);

        b.formPp.build();
        b.formPp2.build();
        b.formPrivate.build();
        b.formPublic.build();
        b.formGi.build();

        b.formPp.setOptions("nationality", ItemData.getCountries());
        b.formPp.finMyDateField("birthDate").addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) return;

                if(!s.toString().isEmpty()){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        isUpdating = true;
                        b.formPp.finMyEditText("age").getEditText().setText("");
                        isUpdating = false;
                    }
                }

            }
        });

        b.formPp.finMyEditText("age").addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) return;

                if(!s.toString().isEmpty()){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        try{
                            isUpdating = true;
                            b.formPp.finMyDateField("birthDate").setText(Utils.getDateFromAge(Integer.parseInt(s.toString())));
                            isUpdating = false;
                        }catch (NumberFormatException e){
                            Toast.makeText(AddActorActivity.this, "L'âge doit être entier", Toast.LENGTH_SHORT).show();
                        }


                    }
                }
            }
        });


        buildRoleSpinner();

        if (actor != null){
            FormValue role = FormDataUtils.getFormValue(actor.getFormValues(), "role");
            FormValue roleType = FormDataUtils.getFormValue(actor.getFormValues(), "roleType");
            Log.d("***Role", "=====> "+actor.getFormValues().toString());
            if (roleType != null) {
                Object name = roleType.getValue();
                if (name.equals(PHYSICAL_PERSON.name())){
                    b.formPp.buildField(actor.getFormValues());
                    b.formPp.setVisibility(View.VISIBLE);
                } else if (name.equals(PRIVATE_LEGAL_ENTITY.name())){ /// PP Moral Privé
                    b.formPrivate.buildField(actor.getFormValues());
                    b.formPrivate.setVisibility(View.VISIBLE);
                } else if (name.equals(PUBLIC_LEGAL_ENTITY.name())){ /// PP Moral Public
                    b.formPublic.buildField(actor.getFormValues());
                    b.formPublic.setVisibility(View.VISIBLE);
                } else if (name.equals(INFORMAL_GROUP.name())){ /// Groupe Inf
                    b.formGi.buildField(actor.getFormValues());
                    b.formGi.setVisibility(View.VISIBLE);
                } else if (name.equals(PHYSICAL_PERSON2.name())){ /// Agent
                    b.formPp2.buildField(actor.getFormValues());
                    b.formPp2.setVisibility(View.VISIBLE);
                }

                for (RoleEnum o : roles){
                    if (o.name().equals(role.getValue()))
                        selectedRoleItem = o;
                }

                if (selectedRoleItem == null)
                    selectedRoleItem = MAYOR;

                b.roleEt.setText(selectedRoleItem.getTag(), false);
            }
        }

        settingPicker(b.formGi,"representative");
        settingPicker(b.formGi,"secondaryRepresentative");
        settingPicker(b.formGi,"thirdRepresentative");
        settingPicker(b.formPrivate,"representative");
        settingPicker(b.formPp,"witness");
    }


    private void save(Map<String, FormValue> data){
        AsyncTask.execute(() -> {

            Actor a = new Actor();

            a.setRole(selectedRoleItem.getTag());

            if ((selectedRoleItem.getType() == PHYSICAL_PERSON)
                    || (selectedRoleItem.getType() == PHYSICAL_PERSON2)){
                a.setPerson(true);
                if (data.get("lastname") != null && data.get("firstname") != null) {
                    a.setName(data.get("lastname").getDisplay()
                            +" "+
                            data.get("firstname").getDisplay());
                }
            }

            data.put("role", new FormValue(selectedRoleItem.name(),selectedRoleItem.getTag(),selectedRoleItem.name(), "string"));
            data.put("roleType", new FormValue(selectedRoleItem.getType().name(),selectedRoleItem.getType().name(), selectedRoleItem.getType().name(), "string"));
            //
            if(actor != null && actor.getFormValues() != null){
                Log.d("****V", "======> 4 "+actor.getFormValues().get("fingerFirstName") );
                if(actor.getFormValues().get("fingerFirstName") != null){
                    data.put("fingerFirstName", actor.getFormValues().get("fingerFirstName"));
                    data.put("fingerSecondName", actor.getFormValues().get("fingerSecondName"));
                    data.put("fingerThirdName", actor.getFormValues().get("fingerThirdName"));
                }

                data.put("fingerThirdImage", null);
                data.put("fingerFirstImage", null);
                data.put("fingerSecondImage", null);

                if(data.get("identificationDocPhoto") != null && Utils.isBase64Regex(data.get("identificationDocPhoto").getValue().toString())){
                    Log.d("******Verif", "=======> ");
                   data.put("identificationDocPhoto", null);
                }
            }

            a.setFormValues(data);

            FormValue roleType = FormDataUtils.getFormValue(a.getFormValues(), "roleType");
            Log.d("****Role", "=====> "+roleType.getValue());
            if (roleType != null) {
                Object name = roleType.getValue();
                if (name.equals(PHYSICAL_PERSON.name())) {
                    isMoralEntity = false;
                } else if (name.equals(PRIVATE_LEGAL_ENTITY.name())) { /// PP Moral Privé
                    isMoralEntity = true;
                } else if (name.equals(PUBLIC_LEGAL_ENTITY.name())) { /// PP Moral Public
                    isMoralEntity = true;
                    Log.d("*****Role", "=====> "+isMoralEntity);
                } else if (name.equals(INFORMAL_GROUP.name())) { /// Groupe Inf
                    isMoralEntity = true;
                } else if (name.equals(PHYSICAL_PERSON2.name())) { /// Agent
                    isMoralEntity = false;
                }
            }
            if(actor != null){
                a.setId(actor.getId());
                a.setTag(actor.getTag());
            }
            else{
                String nui = "NIT-"+System.currentTimeMillis();
                a.setTag(nui);
                data.put("nui", new FormValue(nui,nui,nui, "string"));
            }
           /*
            if (actor == null){
                String nui = "LAND-"+System.currentTimeMillis();
                a.setTag(nui);
                data.put("nui", new FormValue(nui,nui,nui));
                a.setFormValues(data);
                MyApp.getDatabase().actorDao().insertActor(a);
                // CALL YOUR ACTIVITY,
            } else {
                a.setId(actor.getId());
                a.setFormValues(data);
                MyApp.getDatabase().actorDao().updateActor(a);
            }
            */

            if(!onlineMode){
                if(!isMoralEntity){
                    Intent intent = new Intent(this, FingerActivity.class).putExtra("ACTOR", a);
                    intent.putExtra("ONLINE_MODE", onlineMode);
                    startActivity(intent);
                }
                else{
                    if(actor == null){

                        AsyncTask.execute(() -> {
                            MyApp.getDatabase().actorDao().insertActor(a);
                            runOnUiThread(() -> Toast.makeText(AddActorActivity.this, "Enregistrement effectué", Toast.LENGTH_LONG).show());
                        });
                    }
                    else{
                        AsyncTask.execute(() -> {
                            a.setMessage("");
                            MyApp.getDatabase().actorDao().updateActor(a);
                            runOnUiThread(() -> Toast.makeText(this, "Mise à jour effectuée", Toast.LENGTH_LONG).show());
                        });
                    }
                    startActivity(new Intent(this, ActorsActivity.class));
                    finishAffinity();
                }

            }
            else{
                a.setId(id);
                a.setPersonID(actor.getPersonID());
                a.setFinger1ID(actor.getFinger1ID());
                a.setFinger2ID(actor.getFinger2ID());
                a.setFinger3ID(actor.getFinger3ID());
                a.setDocID(actor.getDocID());
                if(!isMoralEntity){
                    Intent intent = new Intent(this, FingerActivity.class).putExtra("ACTOR", a);
                    intent.putExtra("ONLINE_MODE", onlineMode);
                    startActivity(intent);
                }
                else{

                    runOnUiThread(()->{
                        progressDialog.setMessage("Modification ...");
                        progressDialog.show();
                    });
                    ApiService apiService = RetrofitClient.getClient(new SessionManager(AddActorActivity.this).getAccessToken()).create(ApiService.class);

                    apiService.updateObject("" + id, DataUtils.actorData(a, "")).enqueue(new Callback<LinkedHashMap<String, Object>>() {
                        @Override
                        public void onResponse(Call<LinkedHashMap<String, Object>> call, Response<LinkedHashMap<String, Object>> response) {

                            if (response.isSuccessful() && response.body() != null) {
                                LinkedHashMap<String, Object> responseMap = response.body();

                                // Vérification si "data" est présent dans la réponse
                                if (responseMap.containsKey("data") && responseMap.get("data") != null) {
                                    String dataStr = responseMap.get("data").toString().toLowerCase();

                                    if (dataStr.contains("success")) {
                                        runOnUiThread(() -> {
                                            if (progressDialog != null && progressDialog.isShowing()) {
                                                progressDialog.dismiss();
                                            }

                                            if (!isFinishing()) {
                                                Toast.makeText(AddActorActivity.this, "Mise à jour effectuée", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(AddActorActivity.this, ActorListActivity.class));
                                                finishAffinity();
                                            }
                                        });
                                    } else {
                                        runOnUiThread(() -> {
                                            if (progressDialog != null && progressDialog.isShowing()) {
                                                progressDialog.dismiss();
                                            }
                                            Toast.makeText(AddActorActivity.this, "Une erreur s'est produite dans la modification.", Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                } else {
                                    runOnUiThread(() -> {
                                        if (progressDialog != null && progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }
                                        Toast.makeText(AddActorActivity.this, "Réponse invalide reçue.", Toast.LENGTH_SHORT).show();
                                    });
                                }
                            } else {
                                runOnUiThread(() -> {
                                    if (progressDialog != null && progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                    try {
                                        Toast.makeText(AddActorActivity.this, "Erreur : " + response.errorBody().string(), Toast.LENGTH_LONG).show();
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
                                Toast.makeText(AddActorActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    });

                }

            }

            //finish();
        });
    }

    private RoleEnum selectedRoleItem;
    private void buildRoleSpinner(){

        roles.addAll(Arrays.asList(RoleEnum.values()));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, roles.stream()
                .map(RoleEnum::getTag)
                .collect(Collectors.toList()));

        b.roleEt.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        b.roleEt.setOnItemClickListener((parent, view1, position, id) -> {

            String selection = (String) parent.getItemAtPosition(position);

            for (int i = 0; i < roles.size(); i++) {

                if (roles.get(i).getTag().equals(selection)) {

                    selectedRoleItem = roles.get(i);

                    hideForms();
                    if (selectedRoleItem.getType() == PHYSICAL_PERSON){
                        b.formPp.setVisibility(View.VISIBLE);
                    } else if (selectedRoleItem.getType() == PRIVATE_LEGAL_ENTITY){ /// PP Moral Privé
                        b.formPrivate.setVisibility(View.VISIBLE);
                    } else if (selectedRoleItem.getType() == PUBLIC_LEGAL_ENTITY){ /// PP Moral Public
                        b.formPublic.setVisibility(View.VISIBLE);
                    } else if (selectedRoleItem.getType() == INFORMAL_GROUP){ /// Groupe Inf
                        b.formGi.setVisibility(View.VISIBLE);
                    } else if (selectedRoleItem.getType() == PHYSICAL_PERSON2){ /// Actor
                        b.formPp2.setVisibility(View.VISIBLE);
                    }

                    break;
                }
            }

        });

    }

    private void hideForms(){
        b.formPp.setVisibility(View.GONE);
        b.formPp2.setVisibility(View.GONE);
        b.formPrivate.setVisibility(View.GONE);
        b.formPublic.setVisibility(View.GONE);
        b.formGi.setVisibility(View.GONE);
    }

    private void settingPicker(FormView formView, String tag){
        MyCustomPickerField pickerField = formView.finCustomPickerField(tag);
        if (pickerField != null) {
            pickerField.setOnRequestPickListener(() -> {
                Intent intent = new Intent(this, ActorPickerActivity.class);
                intent.putExtra("fieldId", tag);
                startActivityForResult(intent, TEXT_PICKER_REQUEST);
            });
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FormUtils.FilePickerResult(this, requestCode, resultCode, data);
    }



    @Override
    protected void onDestroy() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }
}