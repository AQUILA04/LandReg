package com.lesadrax.registrationclient.ui.activity;

import static com.lesadrax.registrationclient.data.model.RoleEnum.RoleType.INFORMAL_GROUP;
import static com.lesadrax.registrationclient.data.model.RoleEnum.RoleType.PHYSICAL_PERSON;
import static com.lesadrax.registrationclient.data.model.RoleEnum.RoleType.PHYSICAL_PERSON2;
import static com.lesadrax.registrationclient.data.model.RoleEnum.RoleType.PRIVATE_LEGAL_ENTITY;
import static com.lesadrax.registrationclient.data.model.RoleEnum.RoleType.PUBLIC_LEGAL_ENTITY;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.lesadrax.registrationclient.MyApp;
import com.lesadrax.registrationclient.R;
import com.lesadrax.registrationclient.data.model.ActorModel;
import com.lesadrax.registrationclient.data.model.Checklist;
import com.lesadrax.registrationclient.data.model.Operation;
import com.lesadrax.registrationclient.data.network.ApiService;
import com.lesadrax.registrationclient.data.network.RetrofitClient;
import com.lesadrax.registrationclient.databinding.ActivityOperationBinding;
import com.lesadrax.registrationclient.from.FormPageAdapter;
import com.lesadrax.registrationclient.from.model.FormField;
import com.lesadrax.registrationclient.from.model.FormValue;
import com.lesadrax.registrationclient.from.utils.FormBackup;
import com.lesadrax.registrationclient.from.utils.FormFieldParser;
import com.lesadrax.registrationclient.from.utils.FormUtils;
import com.lesadrax.registrationclient.from.utils.JsonToChecklistConverter;
import com.lesadrax.registrationclient.sessionManager.SessionManager;
import com.lesadrax.registrationclient.ui.SignatureActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OperationActivity extends AppCompatActivity {

    // Variables d'instance
    private Operation op;
    private List<FormPageAdapter.FormPageData> pageData = new ArrayList<>();
    private int current = 0;
    private boolean isConflit = false;
    private ActivityOperationBinding b;
    private Checklist cbo;
    private int id = 0;
    private String jsonString = "";
    private List<ActorModel> receivedList;

    // Cache pour les formulaires parsés (optimisation performance)
    private static List<FormField> cachedFormPage1 = null;
    private static List<FormField> cachedFormPage2 = null;
    private static List<FormField> cachedFormPage3 = null;

    private FormPageAdapter sliderAdapter;
    private boolean saving;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = ActivityOperationBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        // Restaurer l'état si l'activité est recréée (rotation d'écran)
        if (savedInstanceState != null) {
            id = savedInstanceState.getInt("operation_id", 0);
            current = savedInstanceState.getInt("current_page", 0);
            isConflit = savedInstanceState.getBoolean("is_conflit", false);

            if (id != 0) {
                loadData(id);
            } else {
                build(null);
            }
        } else {
            // Récupération normale depuis l'Intent
            op = (Operation) getIntent().getSerializableExtra("DATA");
            cbo = (Checklist) getIntent().getSerializableExtra("cbo");
            id = getIntent().getIntExtra("ID", 0);
            receivedList = (List<ActorModel>) getIntent().getSerializableExtra("actor_list_key");

            if (id == 0) {
                build(null);
            } else {
                b.title.setText("Modifier");
                loadData(id);
            }
        }

        b.back.setOnClickListener(v -> onBackPressed());
    }

    private void loadData(long id) {
        String token = new SessionManager(this).getAccessToken();
        System.out.println(token);
        System.out.println("ID : " + id);
        ApiService apiService = RetrofitClient.getClient(token).create(ApiService.class);

        b.progressPan.setVisibility(View.VISIBLE);
        apiService.getOp(id)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        b.progressPan.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {

                            try {
                                String jsonString = response.body().string();
                                Log.d("******end", "======> " + jsonString);
                                JSONObject jsonObject = new JSONObject(jsonString);

                                // Access "data" key
                                JSONObject dataObject = jsonObject.getJSONObject("data");

                                // Utiliser les formulaires en cache
                                List<FormField> allFields = getCachedFormFields();

                                Map<String, FormValue> backupData = FormBackup.backup(dataObject, allFields);

                                op = new Operation();
                                op.setId(dataObject.getInt("id"));
                                if (dataObject.has("conflict") && !dataObject.isNull("conflict")) {
                                    JSONObject conflict = dataObject.getJSONObject("conflict");
                                    if (conflict.has("id"))
                                        op.setConflitID(conflict.getInt("id"));
                                }
                                if (dataObject.has("lastCheckListOperation")) {
                                    JSONObject lastCheckListOperationObject = dataObject.getJSONObject("lastCheckListOperation");
                                    Log.d("****Check", "====> " + JsonToChecklistConverter.fromJson(lastCheckListOperationObject).toString());
                                    op.setChecklistAfterOperation(JsonToChecklistConverter.fromJson(lastCheckListOperationObject));
                                }
                                op.setFormValues(backupData);

                                Map<String, String> dynamics = new HashMap<>();
                                dynamics.put("prefecture", dataObject.getString("prefecture"));
                                dynamics.put("commune", dataObject.getString("commune"));
                                dynamics.put("canton", dataObject.getString("canton"));

                                build(dynamics);

                            } catch (Exception e) {
                                Toast.makeText(OperationActivity.this, "Erreur", Toast.LENGTH_SHORT).show();
                                finish();
                                e.printStackTrace();
                            }

                        } else {
                            System.out.println("Erreur : " + response.code());
                            Toast.makeText(OperationActivity.this, "Erreur", Toast.LENGTH_SHORT).show();
                            finish();
                            try {
                                Log.d("*Error", "=====> INIT NOT RESPONSE " + response.errorBody().string());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        b.progressPan.setVisibility(View.GONE);
                        Log.d("PPPPP", t.getMessage());
                        Toast.makeText(OperationActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    /**
     * Récupère les formulaires parsés avec mise en cache
     * Version corrigée : ne retourne jamais null
     */
    private List<FormField> getCachedFormFields() {
        List<FormField> allFields = new ArrayList<>();

        // Chargement page 1 avec protection null
        if (cachedFormPage1 == null) {
            cachedFormPage1 = FormFieldParser.parseFormFields(OperationActivity.this, R.raw.form_op_1);
            if (cachedFormPage1 == null) {
                cachedFormPage1 = new ArrayList<>();
                Log.e("OperationActivity", "form_op_1 a retourné null, initialisation avec liste vide");
            }
        }
        allFields.addAll(cachedFormPage1);

        // Chargement page 2 avec protection null
        if (cachedFormPage2 == null) {
            cachedFormPage2 = FormFieldParser.parseFormFields(OperationActivity.this, R.raw.form_op_2);
            if (cachedFormPage2 == null) {
                cachedFormPage2 = new ArrayList<>();
                Log.e("OperationActivity", "form_op_2 a retourné null, initialisation avec liste vide");
            }
        }
        allFields.addAll(cachedFormPage2);

        // Chargement page 3 avec protection null
        if (cachedFormPage3 == null) {
            cachedFormPage3 = FormFieldParser.parseFormFields(OperationActivity.this, R.raw.form_op_3);
            if (cachedFormPage3 == null) {
                cachedFormPage3 = new ArrayList<>();
                Log.e("OperationActivity", "form_op_3 a retourné null, initialisation avec liste vide");
            }
        }
        allFields.addAll(cachedFormPage3);

        return allFields;
    }

    /**
     * Récupère un formulaire spécifique avec protection null
     */
    private List<FormField> getCachedFormPage1() {
        if (cachedFormPage1 == null) {
            cachedFormPage1 = FormFieldParser.parseFormFields(OperationActivity.this, R.raw.form_op_1);
            if (cachedFormPage1 == null) {
                cachedFormPage1 = new ArrayList<>();
            }
        }
        return cachedFormPage1;
    }

    private List<FormField> getCachedFormPage2() {
        if (cachedFormPage2 == null) {
            cachedFormPage2 = FormFieldParser.parseFormFields(OperationActivity.this, R.raw.form_op_2);
            if (cachedFormPage2 == null) {
                cachedFormPage2 = new ArrayList<>();
            }
        }
        return cachedFormPage2;
    }

    private List<FormField> getCachedFormPage3() {
        if (cachedFormPage3 == null) {
            cachedFormPage3 = FormFieldParser.parseFormFields(OperationActivity.this, R.raw.form_op_3);
            if (cachedFormPage3 == null) {
                cachedFormPage3 = new ArrayList<>();
            }
        }
        return cachedFormPage3;
    }

    private void build(Map<String, String> dynamics) {
        Map<String, FormValue> d1 = null;
        Map<String, FormValue> d2 = null;
        Map<String, FormValue> d3 = null;

        // Version corrigée avec protection contre les null
        if (op != null && op.getFormValues() != null) {
            // Utilisation des getters protégés pour chaque page
            d1 = FormFieldParser.trimData(getCachedFormPage1(), op.getFormValues());
            d2 = FormFieldParser.trimData(getCachedFormPage2(), op.getFormValues());
            d3 = FormFieldParser.trimData(getCachedFormPage3(), op.getFormValues());
        }

        pageData.clear();
        pageData.add(new FormPageAdapter.FormPageData(R.raw.form_op_1, d1, dynamics));
        pageData.add(new FormPageAdapter.FormPageData(R.raw.form_op_2, d2));
        pageData.add(new FormPageAdapter.FormPageData(R.raw.form_op_3, d3));

        sliderAdapter = new FormPageAdapter(this, pageData);
        b.pager.setAdapter(sliderAdapter);

        b.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                current = position;
                Log.d("*****Page", "====> " + current);
                updateUITitleAndButton();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        b.pager.setCurrentItem(current);
        b.pager.setUserInputEnabled(false);

        // Mettre à jour l'UI initiale
        updateUITitleAndButton();

        b.btn.setOnClickListener(v -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentByTag("f" + current);

            if (currentFragment instanceof FormPageAdapter.PageFragment) {
                FormPageAdapter.PageFragment pageFragment = (FormPageAdapter.PageFragment) currentFragment;

                Map<String, FormValue> result = pageFragment.validate();

                if (result != null) {
                    if (current + 1 < pageData.size()) {
                        if (current == 0) {
                            if (result.get("hasConflict") != null) {
                                if (FormUtils.getInt(result.get("hasConflict").getValue()) == 0) {
                                    isConflit = false;
                                    b.pager.setCurrentItem(2);
                                    b.title.setText("Constatation");
                                } else {
                                    isConflit = true;
                                    b.title.setText("Conflit");
                                    b.pager.setCurrentItem(1);
                                }
                            } else {
                                b.title.setText("Constatation");
                                isConflit = false;
                                Toast.makeText(OperationActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            b.pager.setCurrentItem(current + 1);
                        }
                    } else {
                        if (id == 0) {
                            Map<String, FormValue> data = sliderAdapter.getFormData();
                            if (data != null) {
                                Log.d("OOOOOOO", data.toString());
                                save(data);
                            }
                        } else {
                            Log.d("*Enter", "=======> ONLINE");
                            Map<String, FormValue> data = sliderAdapter.getFormData();
                            op.setFormValues(data);
                            MyApp.getInstance().setTempOperation(op);
                            clearLargeData();
                            startActivity(new Intent(OperationActivity.this, SignatureActivity.class)
                                    .putExtra("before", "after")
                                    .putExtra("ONLINE", "YES"));
                            finish();
                        }
                    }
                }
            }
        });
    }

    /**
     * Met à jour le titre et le texte du bouton selon la page courante
     * Garde exactement le comportement original
     */
    private void updateUITitleAndButton() {
        if (current == 0) {
            b.title.setText("Constatation");
            b.btn.setText("Suivant");
        } else if (current == 1) {
            b.title.setText("Conflit");
            b.btn.setText("Suivant");
        } else {
            b.title.setText("Conflit");
            b.btn.setText("Valider");
        }
    }

    private void clearLargeData() {
        op = null;
        cbo = null;
        receivedList = null;
        jsonString = null;
        if (pageData != null) {
            pageData.clear();
        }
        System.gc();
    }

    private void save(Map<String, FormValue> data) {
        if (saving) return;
        saving = true;
        AsyncTask.execute(() -> {
            Operation a = new Operation();
            if (data.get("nup") != null) {
                a.setTag(data.get("nup").getDisplay());
            }
            if (data.get("firstConflictPartyNUP") != null) {
                a.setConflitTag(data.get("firstConflictPartyNUP").getDisplay());
            }
            a.setFormValues(data);
            if (op == null) {
                a.setChecklistBeforeOperation(cbo);
                a.setActors(receivedList);
                MyApp.getDatabase().operationDao().insertOperation(a);
                Log.d("*****Enter", "=======>Here ");
                runOnUiThread(() -> {
                    startActivity(new Intent(OperationActivity.this, OperationsActivity.class));
                    finish();
                });
            } else {
                a.setId(op.getId());
                a.setChecklistBeforeOperation(op.getChecklistBeforeOperation());
                a.setChecklistAfterOperation(op.getChecklistAfterOperation());
                a.setActors(op.getActors());
                MyApp.getDatabase().operationDao().updateOperation(a);
                runOnUiThread(() -> {
                    startActivity(new Intent(OperationActivity.this, OperationsActivity.class));
                    finishAffinity();
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (current <= 0) {
            super.onBackPressed();
        } else if (isConflit) {
            b.pager.setCurrentItem(current - 1);
        } else {
            b.pager.setCurrentItem(0);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FormUtils.FilePickerResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Ne sauvegarder que les petites données
        outState.putInt("operation_id", id);
        outState.putInt("current_page", current);
        outState.putBoolean("is_conflit", isConflit);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        id = savedInstanceState.getInt("operation_id", 0);
        current = savedInstanceState.getInt("current_page", 0);
        isConflit = savedInstanceState.getBoolean("is_conflit", false);
    }
}