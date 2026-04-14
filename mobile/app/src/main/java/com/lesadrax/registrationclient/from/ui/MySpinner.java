package com.lesadrax.registrationclient.from.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.lesadrax.registrationclient.R;
import com.lesadrax.registrationclient.from.model.FormField;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Custom Spinner class
public class MySpinner extends FrameLayout {

    private AutoCompleteTextView autoCompleteTextView;
    private TextInputLayout textInputLayout;

    private String text;
    private String hint;
    private boolean hintVisibility = true;

    private boolean isPasswordVisible = true;


    private List<FormField.FormOption> options;

    private FormField.FormOption selectedItem;

    private OnSelectListener onSelectListener;

    public MySpinner(Context context) {
        super(context);
    }

    public MySpinner(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MySpinner(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

        // Retrieve attribute values from XML

        // Inflate the custom_spinner layout and add it to the view
        View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_spinner, null,false);
        addView(view);

        autoCompleteTextView = view.findViewById(R.id.et);
        textInputLayout = view.findViewById(R.id.hint);

        textInputLayout.setId(View.generateViewId());
        autoCompleteTextView.setId(View.generateViewId());

        buildOptions();

        textInputLayout.setHint(hint);

//        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.exo_semibold);
//        autoCompleteTextView.setTypeface(typeface);

    }

    public void buildOptions(){
        autoCompleteTextView.setText("");
        selectedItem = null;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, options.stream()
                .map(FormField.FormOption::getName)
                .collect(Collectors.toList()));

        autoCompleteTextView.setAdapter(adapter);
//        autoCompleteTextView.setThreshold(0);
        adapter.notifyDataSetChanged();


        autoCompleteTextView.setOnItemClickListener((parent, view1, position, id) -> {
            if (onSelectListener != null) {

                String selection = (String) parent.getItemAtPosition(position);
                int pos = -1;

                for (int i = 0; i < options.size(); i++) {
                    if (options.get(i).getName().equals(selection)) {
                        pos = i;
                        selectedItem = options.get(i);
                        break;
                    }
                }
                onSelectListener.onSelect(pos, options.get(pos));
            }
        });

//        autoCompleteTextView.setDropDownAnchor(1);

        if (selectedRef > 0) {
            setSelectedItem(selectedRef);
            selectedRef = 0;
        }
    }

    public void setSelectedItem(int id) {
        boolean found = false;
        int i = 0;
        for (FormField.FormOption o : options){
            if (o.getId() == id){
                Log.d("UUUUU", o.getName()+"");
                found = true;

                this.selectedItem = o;
//                this.autoCompleteTextView.setListSelection(2);
                this.autoCompleteTextView.setText(selectedItem.getName(), false);

                if (onSelectListener != null)
                    onSelectListener.onSelect(i,o);

                i++;
            }
        }

        if (!found)
            selectedRef = id;
    }

    public void setSelectedItem(String name) {
        int i = 0;
        for (FormField.FormOption o : options){
            if (o.getName().equals(name)){
                autoCompleteTextView.setText(o.getName(), false);
                selectedItem = o;

                if (onSelectListener != null)
                    onSelectListener.onSelect(i,o);

                i++;
            }
        }
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public boolean isHintVisibility() {
        return hintVisibility;
    }

    public void setHintVisibility(boolean hintVisibility) {
        this.hintVisibility = hintVisibility;
    }

    public boolean isPasswordVisible() {
        return isPasswordVisible;
    }

    public void setPasswordVisible(boolean passwordVisible) {
        isPasswordVisible = passwordVisible;
    }

    public AutoCompleteTextView getAutoCompleteTextView() {
        return autoCompleteTextView;
    }

    public TextInputLayout getTextInputLayout() {
        return textInputLayout;
    }

    public void setTextInputLayout(TextInputLayout textInputLayout) {
        this.textInputLayout = textInputLayout;
    }

    public void setAutoCompleteTextView(AutoCompleteTextView autoCompleteTextView) {
        this.autoCompleteTextView = autoCompleteTextView;
    }

    public List<FormField.FormOption> getOptions() {
        return options;
    }

    public void setOptions(List<FormField.FormOption> options) {
        if(options == null)
            this.options = new ArrayList<>();
        else
            this.options = options;
    }

    public String getText(){
        return autoCompleteTextView.getText().toString().trim();
    }

    public void setText(String text){
        autoCompleteTextView.setText(text, false);
    }


    public FormField.FormOption getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(FormField.FormOption selectedItem) {
        this.selectedItem = selectedItem;
    }

    int selectedRef;

    public void setSelectedRef(int selectedRef) {
        this.selectedRef = selectedRef;
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    public interface OnSelectListener {
        void onSelect(int i, FormField.FormOption d);
    }
}
