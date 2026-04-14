package com.lesadrax.registrationclient.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lesadrax.registrationclient.R;
import com.lesadrax.registrationclient.data.model.OperationModel;
import com.lesadrax.registrationclient.ui.activity.OperationActivity;
import com.lesadrax.registrationclient.ui.activity.VerbalProcessActivity;

import java.util.ArrayList;
import java.util.List;

public class OperationListAdapter extends RecyclerView.Adapter<OperationListAdapter.OperationListViewHolder>{
    private  List<OperationModel> operationList = new ArrayList<>();
    private  String type;
    private Context context;

    public OperationListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public OperationListAdapter.OperationListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new OperationListAdapter.OperationListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OperationListAdapter.OperationListViewHolder holder, int position) {
        OperationModel data = operationList.get(position);
        holder.header.setText(data.getNup());
        holder.pv.setVisibility(View.VISIBLE);
        holder.check.setVisibility(View.GONE);

        holder.container.setOnClickListener(v -> {
            Intent intent = new Intent(context, OperationActivity.class);
            intent.putExtra("ID", data.getId());
            context.startActivity(intent);
        });

        holder.pv.setOnClickListener(v->{
            Intent intent = new Intent(context, VerbalProcessActivity.class);
            intent.putExtra("ID", data.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return operationList.size();
    }

    public class OperationListViewHolder extends RecyclerView.ViewHolder {

        TextView header;
        Button pv;
        View check;
        View container;
        public OperationListViewHolder(@NonNull View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.header);
            pv = itemView.findViewById(R.id.pv);
            check = itemView.findViewById(R.id.check);
            container = itemView.findViewById(R.id.container);
        }
    }

    public void addOperation(List<OperationModel> operations) {
        operationList.addAll(operations);
        notifyDataSetChanged();
    }
}
