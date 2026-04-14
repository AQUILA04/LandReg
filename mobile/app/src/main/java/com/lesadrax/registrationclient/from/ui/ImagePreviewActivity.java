package com.lesadrax.registrationclient.from.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.lesadrax.registrationclient.R;

public class ImagePreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_image_preview);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView imagePreview = findViewById(R.id.image_preview);
        ImageButton closeButton = findViewById(R.id.close_button);

        // Get the image path from Intent
        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("imagePath");

        // Load the image using Glide
        if (imagePath != null) {
            Glide.with(this)
                    .load(imagePath)
                    .into(imagePreview);

            if (isUriPath(imagePath)){
                Glide.with(this)
                        .load(imagePath)
                        .into(imagePreview);
            } else {
                byte[] decodedString = Base64.decode(imagePath, Base64.DEFAULT);
                Glide.with(this)
                        .load(decodedString)
                        .into(imagePreview);
            }
        }

        // Set close button listener
        closeButton.setOnClickListener(v -> finish());
    }

    public static boolean isUriPath(String str) {
        // Check if it starts with common URI schemes
        return str.endsWith(".JPEG") || str.endsWith(".jpg")
                || str.endsWith(".PNG") || str.endsWith(".png");
    }
}
