package com.lesadrax.registrationclient.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lesadrax.registrationclient.R;
import com.lesadrax.registrationclient.data.model.ActorListModel;
import com.lesadrax.registrationclient.data.model.OperationModel;
import com.lesadrax.registrationclient.data.model.PagedResponse;
import com.lesadrax.registrationclient.data.network.ApiService;
import com.lesadrax.registrationclient.data.network.RetrofitClient;
import com.lesadrax.registrationclient.databinding.ActivityActorListBinding;
import com.lesadrax.registrationclient.databinding.ActivityOperationValidatedBinding;
import com.lesadrax.registrationclient.sessionManager.SessionManager;
import com.lesadrax.registrationclient.ui.adapter.ActorListAdapter;
import com.lesadrax.registrationclient.ui.adapter.OperationListAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OperationValidatedActivity extends AppCompatActivity {

    private OperationListAdapter operationListAdapter;
    ActivityOperationValidatedBinding b;
    private boolean isLoading = false; // Indicateur pour éviter les requêtes en double
    private int currentPage = 0; // Page actuelle
    private final int pageSize = 20; // Nombre d'éléments par page
    private boolean isLastPage = false; // Indicateur pour arrêter les requêtes si la dernière page est atteinte
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityOperationValidatedBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        operationListAdapter = new OperationListAdapter(this);
        b.rvMembers.setLayoutManager(new LinearLayoutManager(this));
        b.rvMembers.setAdapter(operationListAdapter);

        b.back.setOnClickListener(v->{
            onBackPressed();
        });

        // Charger la première page
        loadActors(currentPage);

        // Ajouter un OnScrollListener pour charger plus de données au défilement
        b.rvMembers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading && !isLastPage &&
                        layoutManager.findLastCompletelyVisibleItemPosition() == operationListAdapter.getItemCount() - 1) {
                    // Charger la page suivante
                    currentPage++;
                    loadActors(currentPage);
                }
            }
        });

    }

    private void loadActors(int page) {
        isLoading = true; // Indiquer que le chargement est en cours
        b.progressBar.setVisibility(View.VISIBLE);
        ApiService operationApi = RetrofitClient.getClient(new SessionManager(OperationValidatedActivity.this).getAccessToken()).create(ApiService.class);
        Call<PagedResponse<OperationModel>> call = operationApi.getOperations(page, pageSize);

        call.enqueue(new Callback<PagedResponse<OperationModel>>() {
            @Override
            public void onResponse(Call<PagedResponse<OperationModel>> call, Response<PagedResponse<OperationModel>> response) {
                isLoading = false;
                b.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("****SUCCESS", "=====> "+ response.body());
                    List<OperationModel> actors = response.body().getData().getContent();
                    operationListAdapter.addOperation(actors);

                    // Vérifier si c'est la dernière page
                    isLastPage = response.body().getData().isLast();
                } else {
                    Toast.makeText(OperationValidatedActivity.this, "Erreur : " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<OperationModel>> call, Throwable t) {
                isLoading = false;
                b.progressBar.setVisibility(View.GONE);
                Toast.makeText(OperationValidatedActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}