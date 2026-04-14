package com.lesadrax.registrationclient.from;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.lesadrax.registrationclient.R;
import com.lesadrax.registrationclient.from.model.FormField;
import com.lesadrax.registrationclient.from.model.FormValue;
import com.lesadrax.registrationclient.from.model.ItemData;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormPageAdapter extends FragmentStateAdapter {

    private List<FormPageData> data;

    public FormPageAdapter(FragmentActivity fm, List<FormPageData> data) {
        super(fm);
        this.data = data;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return  PageFragment.newInstance(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public Map<String, FormValue> getFormData(){
        Map<String, FormValue> result = new HashMap<>();

        for (FormPageData d : data){
            if (d != null && d.data != null) {
                result.putAll(d.data);
            }
        }

        return result;
    }


    public static class PageFragment extends Fragment {

        private static final String ARG_PAGE = "ARG_PAGE";

        private FormPageData data;


        public static PageFragment newInstance(FormPageData page) {
            Bundle args = new Bundle();
            args.putSerializable(ARG_PAGE, page);
            PageFragment fragment = new PageFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                data = (FormPageData) getArguments().getSerializable(ARG_PAGE);
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            Log.e("OOOOOO", "onResume");
            if (formView != null){
                formView.setupReceiver();
            }
        }

        @Override
        public void onPause() {
            super.onPause();
        }


        FormView formView;
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.pager_form, container, false);

            formView = view.findViewById(R.id.form);

            return view;
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            if (data != null) {
                formView.setRes(data.res);
                formView.build();
                formView.setOnSelectOption((fieldID, option) -> {
                    Log.e("LLLLLLLL", fieldID+" | "+option.getName());
                    if (fieldID.equals("region")) {
                        formView.setOptions("prefecture", ItemData.getOptions(ItemData.Type.PREFECTURE, option.getName()));
                        restoreDynamic("prefecture");
                    }
                    if (fieldID.equals("prefecture")) {
                        formView.setOptions("commune", ItemData.getOptions(ItemData.Type.COMMUNE, option.getName()));
                        restoreDynamic("commune");
                    }
                    if (fieldID.equals("commune")) {
                        formView.setOptions("canton", ItemData.getOptions(ItemData.Type.CANTON, option.getName()));
                        restoreDynamic("canton");
                    }
                });
                System.out.println("DATTA : "+data.data);
                if (data.data != null){
                    System.out.println("DATTA2 : "+data.data);
                    formView.buildField(data.data);
//                    Log.e("UUUUU2",data.data.toString());
                }
            }
        }

        private void restoreDynamic(String key){
            if (data.dynamics != null) {
                if (data.dynamics.containsKey(key)) {
                    if (data.dynamics.get(key) != null) {
                        formView.finMySpinner(key).setSelectedItem(data.dynamics.get(key));
                        data.dynamics.remove(key);
                    }
                }
            }
        }

        public Map<String, FormValue> validate(){
            Map<String, FormValue> result = formView.getFormData();

            if (result == null)
                return null;

            Log.e("UUUUU1", result.toString());

            data.data = result;

            return result;
        }
    }


    public static class FormPageData implements Serializable {
        public int res;
        public Map<String, FormValue> data;
        public Map<String, String> dynamics;

        public FormPageData(int res) {
            this.res = res;
        }

        public FormPageData(int res, Map<String, FormValue> data) {
            this.res = res;
            this.data = data;
        }

        public FormPageData(int res, Map<String, FormValue> data, Map<String, String> dynamics) {
            this.res = res;
            this.data = data;
            this.dynamics = dynamics;
        }
    }

}
