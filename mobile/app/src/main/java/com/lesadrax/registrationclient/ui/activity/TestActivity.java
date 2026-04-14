package com.lesadrax.registrationclient.ui.activity;

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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
import com.lesadrax.registrationclient.databinding.ActivityPvactivityBinding;
import com.lesadrax.registrationclient.databinding.ActivityTestBinding;

import java.util.List;

public class TestActivity extends AppCompatActivity {
    private PdfGenerator.XmlToPDFLifecycleObserver xmlToPDFLifecycleObserver;
    ActivityTestBinding b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        checkPermission(this);
        xmlToPDFLifecycleObserver = new PdfGenerator.XmlToPDFLifecycleObserver(this);
        getLifecycle().addObserver(xmlToPDFLifecycleObserver);

        b.print.setOnClickListener(k->{
            LayoutInflater vi = (LayoutInflater) TestActivity.this.getSystemService(TestActivity.this.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.print_pv, null);
            PdfGenerator.getBuilder()
                    .setContext(TestActivity.this)
                    .fromViewSource()
                    .fromView(v)
                    .setFileName("test")
                    .setFolderNameOrPath("CardForder")
                    .savePDFSharedStorage(xmlToPDFLifecycleObserver)
                    .actionAfterPDFGeneration(PdfGenerator.ActionAfterPDFGeneration.OPEN)
                    .build(new PdfGeneratorListener() {
                        @Override
                        public void onFailure(FailureResponse failureResponse) {
                            super.onFailure(failureResponse);
                            Log.d("*L1", "====> "+failureResponse.getErrorMessage());
                        }

                        @Override
                        public void showLog(String log) {
                            super.showLog(log);
                            Log.d("*L2", "====> "+log);

                        }

                        @Override
                        public void onStartPDFGeneration() {

                        }

                        @Override
                        public void onFinishPDFGeneration() {


                        }

                        @Override
                        public void onSuccess(SuccessResponse response) {
                            super.onSuccess(response);
                            Log.d("****Succes", "====> "+response.getPath());
                        }
                    });
        });



    }

    public static   void checkPermission(Context context){
        Dexter.withContext(context)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
                    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}
                    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                }).check();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                context.startActivity(intent);
                return;
            }
        }
    }
}