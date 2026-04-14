package com.lesadrax.registrationclient;

import android.util.Log;

import com.morpho.Intent_manager.ReceiverCallback;

/*This class will be used for callback from Multiprotect License service.
 * License service will call LicenseLoaded() once license service is initialized.*/

public class MultiprotectReceiverCallbackImpl implements ReceiverCallback{

    public static volatile boolean MPLicenseServiceInitialized = false;
    @Override
    public void LicenseLoaded()
    {
        MPLicenseServiceInitialized = true;
    }
}