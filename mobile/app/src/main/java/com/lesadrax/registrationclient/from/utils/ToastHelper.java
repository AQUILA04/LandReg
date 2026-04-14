package com.lesadrax.registrationclient.from.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lesadrax.registrationclient.R;

public class ToastHelper {

    public static void showSuccessToast(Context context, String message) {
        showCustomToast(context, message, R.layout.toast_succes, R.id.toast_message);
    }

    public static void showErrorToast(Context context, String message) {
        showCustomToast(context, message, R.layout.toast_error, R.id.toast_message);
    }

    private static void showCustomToast(Context context, String message, int layoutResId, int textViewId) {
        // Inflater le layout personnalisé
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(layoutResId, null);

        // Définir le message
        TextView textView = layout.findViewById(textViewId);
        textView.setText(message);

        // Créer et configurer le Toast
        Toast toast = new Toast(context.getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 200); // En haut de l'écran
        // Alternative : Gravity.BOTTOM pour en bas
        toast.show();
    }
}
