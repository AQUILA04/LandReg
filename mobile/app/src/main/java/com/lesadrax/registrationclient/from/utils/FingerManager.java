package com.lesadrax.registrationclient.from.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import morpho.morphoKit.api.AcquisitionDevice;
import morpho.morphoKit.api.AcquisitionDeviceInfoVector;
import morpho.morphoKit.api.Blob;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FingerManager {
    private static final String TAG = "FingerManager";

    private AcquisitionDevice acquisitionDevice;
    private Context context;

    public FingerManager(Context context) {
        this.context = context;
        initializeDevice();
    }

    private void initializeDevice() {
        try {
            acquisitionDevice = new AcquisitionDevice(context);
            Log.d("***Finger", "Device initialized successfully.");
        } catch (Exception e) {
            Log.e("***Finger", "Failed to initialize device: " + e.getMessage());
            Toast.makeText(context, "Device initialization failed!", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isDeviceAvailable() {
        try {
            return acquisitionDevice != null && !acquisitionDevice.enumerateDevices().isEmpty();
        } catch (Exception e) {
            Log.e(TAG, "Device availability check failed: " + e.getMessage());
            return false;
        }
    }

    public void captureFingerprint(OnFingerprintCapturedListener listener) {
        if (acquisitionDevice == null) {
            Toast.makeText(context, "Device not initialized!", Toast.LENGTH_SHORT).show();
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                Blob blob = new Blob();
                int[] width = new int[1];
                int[] height = new int[1];
                AcquisitionDeviceInfoVector vector = acquisitionDevice.enumerateDevices();

                if (!vector.isEmpty()) {
                    try{
                        int result = acquisitionDevice.acquire(vector.get(0).getSerialNumber(), blob, width, height);
                        Log.d("***F", vector.get(0).getSerialNumber());
                        if (result == 0) { // ACQUISITION_STATUS_OK
                            SimpleImageData imageData = new SimpleImageData("", "", blob.getData(), width[0], height[0], 0f, "");
                            Bitmap fingerprintImage = Utils.convertRAWToBitmap(imageData.getPixels(), width[0], height[0], true);
                            listener.onSuccess(fingerprintImage);
                        } else {
                            listener.onError("Acquisition failed with status: " + result);
                        }
                    }
                    catch (Exception e){
                        Log.d("*****F", "======> "+e.getMessage());
                    }
                } else {
                    Utils.showMessage(context, "Information", "No device detected !");
                }


            } catch (Exception e) {
                listener.onError("Acquisition error: " + e.getMessage());
                Log.e(TAG, "Error during acquisition: ", e);
            }
        });
    }

    public interface OnFingerprintCapturedListener {
        void onSuccess(Bitmap fingerprintImage);

        void onError(String errorMessage);
    }
}
