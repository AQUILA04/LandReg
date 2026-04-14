package com.lesadrax.registrationclient.ui.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.lesadrax.registrationclient.R;
import com.lesadrax.registrationclient.data.model.ActorModel;
import com.lesadrax.registrationclient.data.model.Role;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ActorInPVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ActorModel> actorList;
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private Random random = new Random();
    private List<Integer> assignedFingerprintIndices; // Liste des indices d'empreintes assignés

    private boolean showFingerprints; // Boolean pour contrôler l'affichage des empreintes

    // Liste complète des 12 empreintes disponibles
    private final int[] FINGERPRINT_DRAWABLES = {
            R.drawable.fingerprint_1,
            R.drawable.fingerprint_2,
            R.drawable.fingerprint_3,
            R.drawable.fingerprint_4,
            R.drawable.fingerprint_5,
            R.drawable.fingerprint_6,
            R.drawable.fingerprint_7,
            R.drawable.fingerprint_8,
            R.drawable.fingerprint_9,
            R.drawable.fingerprint_10,
            R.drawable.fingerprint_11,
            R.drawable.fingerprint_12
    };

    // Constructeur sans boolean (par défaut, afficher les empreintes)
    public ActorInPVAdapter(List<ActorModel> actorList) {
        this(actorList, false); // Appelle le constructeur avec boolean
    }

    // Constructeur avec boolean
    public ActorInPVAdapter(List<ActorModel> actorList, boolean showFingerprints) {
        this.actorList = actorList;
        this.showFingerprints = showFingerprints;
        this.assignedFingerprintIndices = new ArrayList<>();

        if (showFingerprints) {
            assignUniqueFingerprints();
        }
    }

    /**
     * Assigne de manière unique et aléatoire les empreintes aux acteurs
     */
    private void assignUniqueFingerprints() {
        // Créer une liste des index disponibles (0 à 11)
        List<Integer> availableIndexes = new ArrayList<>();
        for (int i = 0; i < FINGERPRINT_DRAWABLES.length; i++) {
            availableIndexes.add(i);
        }

        // Mélanger aléatoirement la liste des index disponibles
        Collections.shuffle(availableIndexes, random);

        // Prendre les N premiers index pour N acteurs (max 12)
        int count = Math.min(actorList.size(), FINGERPRINT_DRAWABLES.length);
        for (int i = 0; i < count; i++) {
            int fingerprintIndex = availableIndexes.get(i);
            assignedFingerprintIndices.add(fingerprintIndex);
        }

        // Si plus d'acteurs que d'empreintes, on complète avec -1 (pas d'empreinte)
        if (actorList.size() > FINGERPRINT_DRAWABLES.length) {
            for (int i = 0; i < actorList.size() - FINGERPRINT_DRAWABLES.length; i++) {
                assignedFingerprintIndices.add(-1); // -1 indique pas d'empreinte disponible
            }
        }
    }

    /**
     * Met à jour l'état d'affichage des empreintes
     * @param showFingerprints true pour afficher les empreintes, false pour afficher du texte
     */
    public void setShowFingerprints(boolean showFingerprints) {
        if (this.showFingerprints != showFingerprints) {
            this.showFingerprints = showFingerprints;

            // Si on active les empreintes et qu'elles n'ont pas encore été assignées
            if (showFingerprints && assignedFingerprintIndices.isEmpty()) {
                assignUniqueFingerprints();
            }

            notifyDataSetChanged(); // Rafraîchir l'affichage
        }
    }

    /**
     * Vérifie si les empreintes sont affichées
     * @return true si les empreintes sont affichées
     */
    public boolean isShowFingerprints() {
        return showFingerprints;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.actor_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.actor_row, parent, false);
            return new ActorViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ActorViewHolder) {
            ActorModel actor = actorList.get(position - 1); // Adjust for header
            ActorViewHolder actorHolder = (ActorViewHolder) holder;

            // Informations texte
            actorHolder.fullNameTextView.setText(actor.getFirstname() + " " + actor.getLastname());
            actorHolder.titleTextView.setText(Role.getRoleNameByCode(actor.getRole()));
            actorHolder.contactTextView.setText(actor.getContact() != null ? actor.getContact() : "");

            // Gestion de l'affichage selon le boolean showFingerprints
            if (showFingerprints) {
                // Mode empreintes
                int actorPosition = position - 1; // Position réelle dans la liste
                if (actorPosition < assignedFingerprintIndices.size() &&
                        assignedFingerprintIndices.get(actorPosition) != -1) {
                    // Empreinte disponible
                    int fingerprintIndex = assignedFingerprintIndices.get(actorPosition);
                    Drawable fingerprint = ContextCompat.getDrawable(holder.itemView.getContext(),
                            FINGERPRINT_DRAWABLES[fingerprintIndex]);
                    actorHolder.signatureImageView.setImageDrawable(fingerprint);
                    actorHolder.signatureImageView.setVisibility(View.VISIBLE);
                    actorHolder.signatureTextView.setVisibility(View.GONE);
                } else {
                    // Pas d'empreinte disponible (au-delà de 12 personnes)
                    actorHolder.signatureImageView.setVisibility(View.GONE);
                    actorHolder.signatureTextView.setVisibility(View.VISIBLE);
                    actorHolder.signatureTextView.setText("Signé");
                }
            } else {
                // Mode texte uniquement
                actorHolder.signatureImageView.setVisibility(View.GONE);
                actorHolder.signatureTextView.setVisibility(View.VISIBLE);
                actorHolder.signatureTextView.setText("");
            }
        }
    }

    @Override
    public int getItemCount() {
        return actorList.size() + 1; // +1 for header
    }

    /**
     * Retourne l'index de l'empreinte assignée à une position donnée
     * @param position Position dans la liste (sans le header)
     * @return Index de l'empreinte ou -1 si non disponible ou si showFingerprints est false
     */
    public int getAssignedFingerprintIndex(int position) {
        if (!showFingerprints) {
            return -1;
        }
        if (position >= 0 && position < assignedFingerprintIndices.size()) {
            return assignedFingerprintIndices.get(position);
        }
        return -1;
    }

    /**
     * Retourne le drawable ID de l'empreinte assignée à une position
     * @param position Position dans la liste (sans le header)
     * @return Resource ID du drawable ou 0 si non disponible ou si showFingerprints est false
     */
    public int getAssignedFingerprintDrawable(int position) {
        if (!showFingerprints) {
            return 0;
        }
        int index = getAssignedFingerprintIndex(position);
        if (index >= 0 && index < FINGERPRINT_DRAWABLES.length) {
            return FINGERPRINT_DRAWABLES[index];
        }
        return 0;
    }

    /**
     * Réassigne les empreintes (nouveau tirage aléatoire)
     */
    public void reshuffleFingerprints() {
        if (showFingerprints) {
            assignedFingerprintIndices.clear();
            assignUniqueFingerprints();
            notifyDataSetChanged();
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    static class ActorViewHolder extends RecyclerView.ViewHolder {
        TextView fullNameTextView, titleTextView, contactTextView, signatureTextView;
        ImageView signatureImageView; // Ajout de l'ImageView pour l'empreinte

        public ActorViewHolder(@NonNull View itemView) {
            super(itemView);
            fullNameTextView = itemView.findViewById(R.id.fullNameTextView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            contactTextView = itemView.findViewById(R.id.contactTextView);
            signatureTextView = itemView.findViewById(R.id.signatureTextView);

            // Initialisation de l'ImageView pour la signature
            signatureImageView = itemView.findViewById(R.id.signatureImageView);
        }
    }
}