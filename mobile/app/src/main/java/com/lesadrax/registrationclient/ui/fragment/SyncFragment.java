package com.lesadrax.registrationclient.ui.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lesadrax.registrationclient.databinding.FragmentSyncBinding;
import com.lesadrax.registrationclient.ui.activity.SyncActorActivity;
import com.lesadrax.registrationclient.ui.adapter.ActorAdapter;

import java.util.ArrayList;
import java.util.List;

public class SyncFragment extends Fragment {


    private FragmentSyncBinding b;

    public SyncFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        b = FragmentSyncBinding.inflate(inflater, container, false);

        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getView() == null || getContext() == null
                || getActivity() == null)
            return;

        b.actors.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SyncActorActivity.class);
            startActivity(intent);
        });
        b.op.setOnClickListener(v -> {
//            Intent intent = new Intent(this, OperationActivity.class);
//            intent.putExtra("TASK", task.getId());
//            intent.putExtra("DATA", operation);
//            startActivity(intent);
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }

}
