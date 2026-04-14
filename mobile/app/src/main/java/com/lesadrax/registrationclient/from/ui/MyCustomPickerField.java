package com.lesadrax.registrationclient.from.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;
import com.lesadrax.registrationclient.R;
import com.lesadrax.registrationclient.from.model.FormValue;

import java.util.HashMap;
import java.util.Map;

// Custom Spinner class
public class MyCustomPickerField extends FrameLayout {

    private EditText textView;
    private TextInputLayout hintTv;

    private String displayText;
    private String hint;

    private Map<String, FormValue> data = new HashMap<>();

    private OnSelectListener onSelectListener;
    private OnRequestPickListener onRequestPickListener;

    public MyCustomPickerField(Context context) {
        super(context);
    }

    public MyCustomPickerField(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MyCustomPickerField(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    /**
     * Builds the MySpinner view by initializing it with the provided attributes.
     */
    public void build(){
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
        View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_date_picker, null,false);
        addView(view);

        textView = view.findViewById(R.id.et);
        hintTv = view.findViewById(R.id.hint);

        textView.setId(View.generateViewId());
        hintTv.setId(View.generateViewId());

        textView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                request();
            }
        });
        textView.setOnClickListener(v -> {
            request();
        });


        hintTv.setHint(getHint());

//        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.exo_semibold);
//        textView.setTypeface(typeface);

    }

    private void request() {
        if (onRequestPickListener != null)
            onRequestPickListener.onRequest();
    }

    public void clear(){
        displayText = null;
        data = null;
        textView.setText("Cliquez pour choisir");
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public TextView getTextView() {
        return textView;
    }


    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
        textView.setText(displayText);
    }

    public TextInputLayout getHintTv() {
        return hintTv;
    }

    public Map<String, FormValue> getData() {
        return data;
    }

    public void setData(Map<String, FormValue> data) {
        this.data = data;
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    public void setOnRequestPickListener(OnRequestPickListener onRequestPickListener) {
        this.onRequestPickListener = onRequestPickListener;
    }

    public interface OnSelectListener {
        void onSelect(String date);
    }

    public interface OnRequestPickListener {
        void onRequest();
    }
}
