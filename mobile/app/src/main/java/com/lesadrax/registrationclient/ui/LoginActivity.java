package com.lesadrax.registrationclient.ui;

import static com.morpho.android.usb.USBManager.context;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.lesadrax.registrationclient.MyApp;
import com.lesadrax.registrationclient.R;
import com.lesadrax.registrationclient.data.model.LoginRequest;
import com.lesadrax.registrationclient.data.model.LoginResponse;
import com.lesadrax.registrationclient.data.model.UserResponse;
import com.lesadrax.registrationclient.data.model.User;
import com.lesadrax.registrationclient.data.network.ApiService;
import com.lesadrax.registrationclient.data.network.NetworkUtils;
import com.lesadrax.registrationclient.data.network.RetrofitClient;
import com.lesadrax.registrationclient.data.network.RetrofitClientSimple;
import com.lesadrax.registrationclient.databinding.ActivityLoginBinding;
import com.lesadrax.registrationclient.sessionManager.SessionManager;
import com.lesadrax.registrationclient.ui.activity.MainActivity;

import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = new SessionManager(LoginActivity.this);

        binding.btn.setOnClickListener(v->{
            connect();
        });
    }

    public void connect(){
        String id = binding.et.getText().toString().trim();
        String password = binding.password.getText().toString().trim();

        if(id.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_LONG).show();
        }
        else if(NetworkUtils.isInternetAvailable(LoginActivity.this)){
            sendRequest(id, password);
        }
        else {
            offlineLogin(id, password);
        }
    }

    public void sendRequest(String username, String password){
        ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Connexion en cours... ");
        progressDialog.setCancelable(false);
        progressDialog.show();

        LoginRequest request = new LoginRequest(username, password);

        ApiService apiService = RetrofitClientSimple.getClient().create(ApiService.class);
        Call<LoginResponse> call = apiService.login(request);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    if(loginResponse.getAccessToken() != null && !loginResponse.getAccessToken().isEmpty()){
                        String token = loginResponse.getAccessToken();
                        Log.d("*******Token", "=======> " + token);

                        // ✅ Sauvegarde du token avec vérification
                        sessionManager.saveAccessToken(token);

                        // ✅ Vérification immédiate que le token est bien sauvegardé
                        String savedToken = sessionManager.getAccessToken();
                        if (savedToken != null && !savedToken.isEmpty()) {
                            Log.d("****Session", "====> Token sauvegardé avec succès");
                        } else {
                            Log.e("****Session", "====> ERREUR: Token non sauvegardé!");
                        }

                        if(sessionManager.isFirstConnection()){
                            // ✅ Utilisation du token fraîchement sauvegardé
                            ApiService apiService1 = RetrofitClient.getClient(sessionManager.getAccessToken()).create(ApiService.class);

                            apiService1.getUsers().enqueue(new Callback<UserResponse<User>>() {
                                @Override
                                public void onResponse(Call<UserResponse<User>> call, Response<UserResponse<User>> response) {
                                    if(response.isSuccessful() && response.body() != null){
                                        List<User> userList = response.body().getData();
                                        Log.d("*****Enter", "=====> " + (userList != null ? userList.size() : 0));

                                        AsyncTask.execute(() -> {
                                            if(userList != null && !userList.isEmpty()){
                                                MyApp.getDatabase().userDao().insertUsers(userList);
                                            }
                                            runOnUiThread(() -> {
                                                progressDialog.dismiss();
                                                sessionManager.setFirstConnection(false);
                                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                finish();
                                            });
                                        });
                                    }
                                    else{
                                        runOnUiThread(() -> {
                                            progressDialog.dismiss();
                                            Toast.makeText(LoginActivity.this, "Un problème s'est produit durant la récuperation des masterData", Toast.LENGTH_LONG).show();
                                        });
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                    }
                                }

                                @Override
                                public void onFailure(Call<UserResponse<User>> call, Throwable t) {
                                    runOnUiThread(() -> {
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                }
                            });
                        }
                        else{
                            runOnUiThread(progressDialog::dismiss);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                    else{
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Une Erreur s'est produite dans la réception du Token", Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    runOnUiThread(progressDialog::dismiss);
                    Log.d("****T", "====> "+response.code());
                    Toast.makeText(LoginActivity.this, "Identifiants incorrects", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                runOnUiThread(progressDialog::dismiss);
                Toast.makeText(LoginActivity.this, "Échec: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", t.getMessage(), t);
            }
        });
    }

    private void login(String username, String password) {
        if ("joseph".equals(username) && "123456".equals(password)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Identifiants incorrects", Toast.LENGTH_LONG).show();
        }
    }

    private void offlineLogin(String username, String password) {
        ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Connexion en cours... ");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                User user = MyApp.getDatabase().userDao().getUserByUsername(username);
                Log.d("****User", "====> " + (user != null ? user.getUsername() : "null") + " " + (user != null ? user.getPassword() : "null"));

                if (user == null || user.getPassword() == null || password == null) {
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Identifiants incorrects", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                if (BCrypt.checkpw(password, user.getPassword())) {
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    });
                } else {
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Identifiants incorrects", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Erreur de connexion : " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}