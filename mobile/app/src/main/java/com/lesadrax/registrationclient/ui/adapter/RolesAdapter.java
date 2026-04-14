package com.lesadrax.registrationclient.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lesadrax.registrationclient.R;
import com.lesadrax.registrationclient.data.model.Role;

import java.util.ArrayList;

public class RolesAdapter extends RecyclerView.Adapter<RolesAdapter.RoleViewHolder> {

    private ArrayList<Role> roles;
    private OnRoleDeleteListener onRoleDeleteListener;

    // Interface pour notifier l'activité
    public interface OnRoleDeleteListener {
        void onDeleteRole(int position);
    }

    public RolesAdapter(ArrayList<Role> roles, OnRoleDeleteListener onRoleDeleteListener) {
        this.roles = roles;
        this.onRoleDeleteListener = onRoleDeleteListener;
    }

    @NonNull
    @Override
    public RoleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_role, parent, false);
        return new RoleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoleViewHolder holder, int position) {
        holder.tvText.setText(roles.get(position).getName());
        holder.ivDelete.setOnClickListener(v -> {
            if (onRoleDeleteListener != null) {
                onRoleDeleteListener.onDeleteRole(position); // Appelle l'activité
            }
        });
    }

    @Override
    public int getItemCount() {
        return roles.size();
    }

    public static class RoleViewHolder extends RecyclerView.ViewHolder {
        TextView tvText;
        ImageView ivDelete;

        public RoleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tv_text);
            ivDelete = itemView.findViewById(R.id.iv_delete);
        }
    }
}
