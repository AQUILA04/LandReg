package com.lesadrax.registrationclient.from.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.lesadrax.registrationclient.R;


// Custom edittext class
public class MyEditText extends FrameLayout {

    private EditText editText;
    private TextInputLayout textInputLayout;
//    private ImageView icView;

    private String text;
    private String hint;
    private int inputType;
    private int maxLength;
    private String ic;
    private boolean hintVisibility;

    private boolean isPasswordVisible = true;

    public MyEditText(Context context) {
        super(context);
    }

    public MyEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MyEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    /**
     * Builds the MyEditText view by initializing it with the provided attributes.
     */
    public void build(){
        init(null);
    }

    /**
     * Initializes the MyEditText view with the provided attributes.
     *
     * @param attrs The attribute set.
     */
    private void init(@Nullable AttributeSet attrs){

        if (getContext() == null)
            return;

        // Inflate the custom_edittext layout and add it to the view
        View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_edittext, null,false);
        addView(view);

        editText = view.findViewById(R.id.et);
        textInputLayout = view.findViewById(R.id.hint);

        textInputLayout.setId(View.generateViewId());
        editText.setId(View.generateViewId());

        editText.setText(text);
        textInputLayout.setHint(hint);

        if (inputType != 0) {
            editText.setInputType(inputType);
        }

        if (maxLength > 0) {
            InputFilter[] filters = new InputFilter[1];
            filters[0] = new InputFilter.LengthFilter(maxLength);
            editText.setFilters(filters);
        }

//        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.exo_semibold);
//        editText.setTypeface(typeface);

    }


    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public int getInputType() {
        return inputType;
    }

    public void setInputType(int inputType) {
        this.inputType = inputType;
    }

    public boolean isHintVisibility() {
        return hintVisibility;
    }

    public void setHintVisibility(boolean hintVisibility) {
        this.hintVisibility = hintVisibility;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public String getIc() {
        return ic;
    }

    public void setIc(String ic) {
        this.ic = ic;
    }

    public boolean isPasswordVisible() {
        return isPasswordVisible;
    }

    public void setPasswordVisible(boolean passwordVisible) {
        isPasswordVisible = passwordVisible;
    }

    public EditText getEditText() {
        return editText;
    }

    public TextInputLayout getTextInputLayout() {
        return textInputLayout;
    }

    public String getText(){
        return editText.getText().toString().trim();
    }

    public void setText(String text){
        editText.setText(text);
    }

    /**
     * Ajoute un TextWatcher pour écouter les modifications de texte.
     *
     * @param textWatcher Le TextWatcher à attacher à l'EditText interne.
     */
    public void addTextChangedListener(TextWatcher textWatcher) {
        if (editText != null) {
            editText.addTextChangedListener(textWatcher);
        }
    }

}
