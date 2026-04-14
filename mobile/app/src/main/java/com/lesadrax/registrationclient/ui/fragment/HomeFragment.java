package com.lesadrax.registrationclient.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gkemon.XMLtoPDF.PdfGenerator;
import com.gkemon.XMLtoPDF.PdfGeneratorListener;
import com.gkemon.XMLtoPDF.model.FailureResponse;
import com.gkemon.XMLtoPDF.model.SuccessResponse;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.lesadrax.registrationclient.R;
import com.lesadrax.registrationclient.databinding.FragmentHomeBinding;
import com.lesadrax.registrationclient.ui.LoginActivity;
import com.lesadrax.registrationclient.ui.activity.ActorsActivity;
import com.lesadrax.registrationclient.ui.activity.MainActivity;
import com.lesadrax.registrationclient.ui.activity.OperationsActivity;

import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding b;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        b = FragmentHomeBinding.inflate(inflater, container, false);

        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getView() == null || getContext() == null
                || getActivity() == null)
            return;

        b.op.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), OperationsActivity.class));
        });

        b.actors.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ActorsActivity.class));
        });

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
