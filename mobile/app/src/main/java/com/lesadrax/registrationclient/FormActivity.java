package com.lesadrax.registrationclient;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.lesadrax.registrationclient.from.FormPageAdapter;
import com.lesadrax.registrationclient.from.utils.FormUtils;

import java.util.ArrayList;
import java.util.List;

public class FormActivity extends AppCompatActivity {

    private ViewPager2 pager;
    private TextView btn;

    List<FormPageAdapter.FormPageData> pageData = new ArrayList<>();

    private int current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_form);

        pager = findViewById(R.id.pager);
        btn = findViewById(R.id.btn);

        pageData.add(new FormPageAdapter.FormPageData(R.raw.form_actor_pp));

        FormPageAdapter sliderAdapter = new FormPageAdapter(this, pageData);
        pager.setAdapter(sliderAdapter);

        View child = pager.getChildAt(0);
        if (child instanceof RecyclerView) {
            child.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }

        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                current = position;
//                header.setText(data.get(current).header);
//
//                if (data.size() -1 <= current){
//                    btn.setText(R.string.open_settings);
//                } else {
//                    btn.setText(R.string.continue_);
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
        pager.setCurrentItem(current);


        btn.setOnClickListener(v -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentByTag("f" + current);

            if (currentFragment instanceof FormPageAdapter.PageFragment) {
                FormPageAdapter.PageFragment pageFragment = (FormPageAdapter.PageFragment) currentFragment;

                // Call the validate() method
//                boolean isValid = pageFragment.validate();
//
//                if (isValid) {
//                    // Go to the next page if the form is valid
//                    if (current + 1 < pageData.size()) {
//                        pager.setCurrentItem(current + 1);
//                    } else {
//                        // Handle the case where it's the last page
//                        Toast.makeText(this, "Form is complete!", Toast.LENGTH_SHORT).show();
//                    }
//                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FormUtils.FilePickerResult(this, requestCode, resultCode, data);
    }

}