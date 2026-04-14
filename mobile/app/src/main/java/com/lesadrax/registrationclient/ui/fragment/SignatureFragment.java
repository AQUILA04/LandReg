package com.lesadrax.registrationclient.ui.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lesadrax.registrationclient.MyApp;
import com.lesadrax.registrationclient.R;
import com.lesadrax.registrationclient.data.model.Operation;
import com.lesadrax.registrationclient.databinding.FragmentOpertionsBinding;
import com.lesadrax.registrationclient.ui.adapter.OperationAdapter;

import java.util.ArrayList;
import java.util.List;

public class SignatureFragment extends Fragment {


    private OperationAdapter adapter;
    private final List<Operation> data = new ArrayList<>();

    private FragmentOpertionsBinding b;

    public SignatureFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        b = FragmentOpertionsBinding.inflate(inflater, container, false);

        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }


}