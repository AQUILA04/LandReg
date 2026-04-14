package com.lesadrax.registrationclient.from.ui;

import static com.lesadrax.registrationclient.from.utils.FormIntents.CAMERA_REQUEST;
import static com.lesadrax.registrationclient.from.utils.FormIntents.PICK_FILE_REQUEST;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.lesadrax.registrationclient.BuildConfig;
import com.lesadrax.registrationclient.R;
import com.lesadrax.registrationclient.from.utils.FormUtils;

import java.io.File;


// Custom Spinner class
public class MyFileField extends FrameLayout {

    private View displayPan;
    private View pickerPan;
    private ImageView previewImage;
    private TextView hintTv;
    private TextView change;
    private View close;

    private String name;
    private String mime;
    private String[] extras;

    private String path;
    private String text;
    private String hint;

    private OnSelectListener onSelectListener;

    public MyFileField(Context context) {
        super(context);
    }

    public MyFileField(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MyFileField(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    /**
     * Builds the MySpinner view by initializing it with the provided attributes.
     */
    public void build(String name, String mime, String[] extras){
        this.name = name;
        this.mime = mime;
        this.extras = extras;

        if (this.mime == null){
            this.mime = "*/*";
        }

        init(null);
    }

    /**
     * Initializes the MySpinner view with the provided attributes.
     *
     * @param attrs The attribute set.
     */
    private void init(@Nullable AttributeSet attrs){

        if (getContext() == null)
            return;

        // Inflate the custom_spinner layout and add it to the view
        View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_file_picker, null,false);
        addView(view);

        pickerPan = view.findViewById(R.id.pick_pan);
        displayPan = view.findViewById(R.id.display_pan);
        previewImage = view.findViewById(R.id.display);
        change = view.findViewById(R.id.change);
        hintTv = view.findViewById(R.id.hint);
//        close = view.findViewById(R.id.close_button);

        pickerPan.setId(View.generateViewId());
        displayPan.setId(View.generateViewId());
        previewImage.setId(View.generateViewId());
        change.setId(View.generateViewId());
        hintTv.setId(View.generateViewId());

        pickerPan.setVisibility(VISIBLE);
        displayPan.setVisibility(GONE);

        previewImage.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ImagePreviewActivity.class);
            intent.putExtra("imagePath", path);
            getActivity().startActivity(intent);
        });
        change.setOnClickListener(v -> {
            openFilePicker();
        });
        pickerPan.setOnClickListener(v -> {
            openFilePicker();
        });
//        close.setOnClickListener(v -> {
//            clear();
//        });


        hintTv.setText(getHint());
    }

    private AppCompatActivity getActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof AppCompatActivity) {
                return (AppCompatActivity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }


    private void openFilePicker() {
        Activity activity = getActivity();
        if (activity == null) return;
        FormUtils.openFilePicker(activity, extras, mime, name);
    }



    public void clear(){
        path = null;
        text = null;
        pickerPan.setVisibility(VISIBLE);
        displayPan.setVisibility(GONE);
//        close.setVisibility(GONE);
//        textView.setText("Cliquez pour choisir");
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }



    public String getText() {
        return text;
    }

    public void setFile(String text, String path) {

        if (!TextUtils.isEmpty(text) || !TextUtils.isEmpty(path)) {
            displayPan.setVisibility(VISIBLE);
            pickerPan.setVisibility(GONE);

            if (getActivity() != null && !getActivity().isDestroyed()) {
                if (isUriPath(path)){
                    Glide.with(getContext())
                            .load(path)
                            .into(previewImage);
                } else {
                    byte[] decodedString = Base64.decode(path, Base64.DEFAULT);
                    Glide.with(getContext())
                            .load(decodedString)
                            .into(previewImage);
                }
            }
        }

        this.text = text;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public static boolean isUriPath(String str) {
        // Check if it starts with common URI schemes
        return str.endsWith(".JPEG") || str.endsWith(".jpg")
                || str.endsWith(".PNG") || str.endsWith(".png");
    }

//    public TextInputLayout getHintTv() {
//        return hintTv;
//    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    public interface OnSelectListener {
        void onSelect(String date);
    }
}
