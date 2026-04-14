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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lesadrax.registrationclient.MyApp;
import com.lesadrax.registrationclient.data.model.Operation;
import com.lesadrax.registrationclient.databinding.FragmentConflitBinding;
import com.lesadrax.registrationclient.databinding.FragmentOpertionsBinding;
import com.lesadrax.registrationclient.ui.activity.OperationActivity;
import com.lesadrax.registrationclient.ui.adapter.ConflitAdapter;
import com.lesadrax.registrationclient.ui.adapter.OperationAdapter;

import java.util.ArrayList;
import java.util.List;

public class ConflitsFragment extends Fragment {

    private ConflitAdapter adapter;
    private final List<Operation> data = new ArrayList<>();

    private FragmentConflitBinding b;

    public ConflitsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        b = FragmentConflitBinding.inflate(inflater, container, false);

        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getView() == null || getContext() == null
                || getActivity() == null)
            return;


//        b.add.setOnClickListener(v -> {
//            Intent intent = new Intent(getContext(), OperationActivity.class);
////            intent.putExtra("DATA", operation);
//            startActivity(intent);
//        });

        adapter = new ConflitAdapter(getContext(), data);
        b.rv.setLayoutManager(new LinearLayoutManager(getContext()));
        b.rv.setAdapter(adapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }

    private void load(){
        AsyncTask.execute(() -> {
            data.clear();
            data.addAll(MyApp.getDatabase().operationDao().getAllOperations());
            requireActivity().runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
//                b.opText.setText((operation == null) ? R.string.bcst : R.string.ccst);
            });
        });
    }
}
