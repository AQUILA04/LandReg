package com.lesadrax.registrationclient.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.idemia.mtops.android.devicemanagermtops.DeviceManagerMtops;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.lesadrax.registrationclient.MultiprotectReceiverCallbackImpl;
import com.lesadrax.registrationclient.R;
import com.lesadrax.registrationclient.databinding.ActivityMainBinding;
import com.lesadrax.registrationclient.ui.LoginActivity;
import com.lesadrax.registrationclient.ui.fragment.ConflitsFragment;
import com.lesadrax.registrationclient.ui.fragment.HomeFragment;
import com.lesadrax.registrationclient.ui.fragment.OperationsFragment;

import com.lesadrax.registrationclient.ui.fragment.SyncFragment;
import com.morpho.Intent_manager.IntentManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import morpho.morphoKit.api.MorphoKitConfig;
import morpho.morphoKit.api.MorphoKitException;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding b;
    public static boolean nfiq2_available = true;
    public static boolean isArmv8Supported = false;

    private DeviceManagerMtops mDeviceManagerMTops;
    private String inputFilePath = "";

    // Load native libraries
    static {
        try {
            if(Build.SUPPORTED_64_BIT_ABIS.length > 0)
            {
                isArmv8Supported = true;
            }
            System.loadLibrary("usb1.0");
            System.loadLibrary("morphokit5.42.7");
            System.loadLibrary("morphokitjavawrapper5.42.7");
            if(isArmv8Supported)
            {
                System.loadLibrary("MPJNIAndroid");
            }
        } catch (UnsatisfiedLinkError e) {
            Log.e("MainActivity", "Exception in loadLibrary: " + e);
            e.printStackTrace();
        }

        try {
            System.loadLibrary("c++_shared");
            System.loadLibrary("Snfiq2");
        } catch (UnsatisfiedLinkError e) {
            Log.e("MainActivity", "Exception in loadLibrary: " + e);
            e.printStackTrace();
            nfiq2_available = false;
        }
    }

    IntentManager intentManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        checkPermission(this);
        if (savedInstanceState != null){
            String tag = savedInstanceState.getString("CUR_FRAG");
            replace_fragment_by_tag(tag);
        }else {
            replace_fragment(new HomeFragment());
        }

        if(isArmv8Supported)
        {
            //Creating MultiprotectReceiverCallbackImpl object which will be passed as a callback to Multiprotect IntentManager.
            //MultiprotectReceiverCallbackImpl.LicenseLoaded() will be called once the license service is initialized.
            MultiprotectReceiverCallbackImpl multiprotectCallback = new MultiprotectReceiverCallbackImpl();
            //Initializing IntentManager. This will be responsible for handling the license requests to Multiprotect.
            intentManager = new IntentManager();
            intentManager.setContext(getApplicationContext());
            try{
                intentManager.initManager(multiprotectCallback);
                if(mDeviceManagerMTops == null) {
                    mDeviceManagerMTops = new DeviceManagerMtops();
                    mDeviceManagerMTops.initialize(this);
                }

            }catch (Exception e){
            }

            intentManager.start();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v("****F","Permission is granted");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        if(mDeviceManagerMTops == null) {
            mDeviceManagerMTops = new DeviceManagerMtops();
            mDeviceManagerMTops.initialize(this);
        }

        try
        {
            File[] mediaExternal = getExternalMediaDirs();
            if( mediaExternal != null &&  mediaExternal.length > 0 && mediaExternal[0] != null )
            {
                inputFilePath = mediaExternal[0].getPath() ;
            }
            else
            {
                inputFilePath = "/sdcard/Android/media/" +getApplicationContext().getPackageName();
            }

            File dir = new File(inputFilePath);

            if(!dir.exists())
            {
                dir.mkdirs();
            }
            int ret = MorphoKitConfig.setMainFolderPath(inputFilePath);
            copyConfigFileParam(inputFilePath);
        }
        catch ( MorphoKitException e)
        {
            Log.e("MainActivity", "MorphoKitException while setting main folder path: " + e.getMessage());
            e.printStackTrace();
        }
        catch (Exception e)
        {
            Log.e("MainActivity", "Exception while setting main folder path: " + e.getMessage());
            e.printStackTrace();
        }

        b.nav.nvView.setNavigationItemSelectedListener(this);

        b.menu.setOnClickListener(v -> {
            b.drawerLayout.openDrawer(GravityCompat.START);
        });



    }

    public void replace_fragment(Fragment fragment) {
        String tag = fragment.getClass().getSimpleName();
        FragmentTransaction tr = getSupportFragmentManager().beginTransaction();

        Fragment curFrag = getSupportFragmentManager().getPrimaryNavigationFragment();
        Fragment cacheFrag = getSupportFragmentManager().findFragmentByTag(tag);

        if (curFrag != null) {
            curFrag.onPause();
            tr.hide(curFrag);
        }

        if (cacheFrag == null) {
            tr.add(R.id.frame, fragment, tag);
        } else {
            tr.show(cacheFrag);
            if (cacheFrag.getView() != null)
                cacheFrag.onResume();
            fragment = cacheFrag;
        }

        tr.setPrimaryNavigationFragment(fragment);
        tr.commit();
    }


    public void replace_fragment_by_tag(String tag) {

        if (tag == null){
            replace_fragment(new HomeFragment());
            return;
        }

        if (tag.equals(OperationsFragment.class.getSimpleName())) {
            replace_fragment(new OperationsFragment());
        } else if (tag.equals(SyncFragment.class.getSimpleName())) {
            replace_fragment(new SyncFragment());
        } else {
            replace_fragment(new HomeFragment());
        }

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_op){
            startActivity(new Intent(this, OperationsActivity.class));
        } else if (id == R.id.nav_actors){
            startActivity(new Intent(this, ActorsActivity.class));
        } else if (id == R.id.nav_conflit){
            replace_fragment(new ConflitsFragment());
        } else if (id == R.id.nav_sync){
            startActivity(new Intent(this, SyncActivity.class));
        }else if(id == R.id.nav_disconnect) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finishAffinity();
        }
        else if(id == R.id.nv_validateted_actor){
            startActivity(new Intent(MainActivity.this, ActorListActivity.class));
        }
        else if(id == R.id.nv_validateted_operation){
            startActivity(new Intent(MainActivity.this, OperationValidatedActivity.class));
        }
        else if(id == R.id.nav_pending){
            startActivity(new Intent(MainActivity.this, PendingActivity.class));
        }
        else {
            replace_fragment(new HomeFragment());
        }

        b.drawerLayout.closeDrawer(GravityCompat.START);

        return true;
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

    private void copyConfigFileParam(String filePath)
    {
        String filename = "initBlock.dat";
        try {
            File configFileParam = new File(filePath + "/Idemia/Conf/MorphoKit/", filename);
            if (!configFileParam.exists()) {
                AssetManager assetManager = getAssets();
                InputStream in = null;
                OutputStream out = null;

                in = assetManager.open(filename);
                out = new FileOutputStream(filePath + "/Idemia/Conf/MorphoKit/" + filename);
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            }
        } catch (IOException e) {
            Log.e("tag", "Failed to copy asset file: " + filename, e);
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, read);
        }
    }

}