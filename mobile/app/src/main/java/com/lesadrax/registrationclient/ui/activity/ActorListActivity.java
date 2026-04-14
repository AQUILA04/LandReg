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
import com.lesadrax.registrationclient.data.model.PagedResponse;
import com.lesadrax.registrationclient.data.network.ApiService;
import com.lesadrax.registrationclient.data.network.RetrofitClient;
import com.lesadrax.registrationclient.databinding.ActivityActorListBinding;
import com.lesadrax.registrationclient.databinding.ActivityActorsBinding;
import com.lesadrax.registrationclient.sessionManager.SessionManager;
import com.lesadrax.registrationclient.ui.adapter.ActorListAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActorListActivity extends AppCompatActivity {

    private ActorListAdapter actorAdapter;
    ActivityActorListBinding b;
    private boolean isLoading = false; // Indicateur pour éviter les requêtes en double
    private int currentPage = 0; // Page actuelle
    private final int pageSize = 20; // Nombre d'éléments par page
    private boolean isLastPage = false; // Indicateur pour arrêter les requêtes si la dernière page est atteinte

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityActorListBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        actorAdapter = new ActorListAdapter(this);
        b.rvMembers.setLayoutManager(new LinearLayoutManager(this));
        b.rvMembers.setAdapter(actorAdapter);

        // Charger la première page
        loadActors(currentPage);

        b.back.setOnClickListener(v->{
            onBackPressed();
        });

        // Ajouter un OnScrollListener pour charger plus de données au défilement
        b.rvMembers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading && !isLastPage &&
                        layoutManager.findLastCompletelyVisibleItemPosition() == actorAdapter.getItemCount() - 1) {
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
        ApiService actorApi = RetrofitClient.getClient(new SessionManager(ActorListActivity.this).getAccessToken()).create(ApiService.class);
        Call<PagedResponse<ActorListModel>> call = actorApi.getActors(page, pageSize);

        call.enqueue(new Callback<PagedResponse<ActorListModel>>() {
            @Override
            public void onResponse(Call<PagedResponse<ActorListModel>> call, Response<PagedResponse<ActorListModel>> response) {
                isLoading = false;
                b.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("****SUCCESS", "=====> "+ response.body().getData().getTotalElements()+" ==== "+response.body().getData().getSize()+" ==="+" "+response.body().getData().getContent().size());
                    List<ActorListModel> actors = response.body().getData().getContent();
                    Log.d("*****List", "====> "+actors.size());
                    actorAdapter.addActors(actors);

                    // Vérifier si c'est la dernière page
                    isLastPage = response.body().getData().isLast();
                } else {
                    Toast.makeText(ActorListActivity.this, "Erreur : " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<ActorListModel>> call, Throwable t) {
                isLoading = false;
                b.progressBar.setVisibility(View.GONE);
                Toast.makeText(ActorListActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}