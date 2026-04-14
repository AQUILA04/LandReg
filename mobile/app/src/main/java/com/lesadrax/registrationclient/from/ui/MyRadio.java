package com.lesadrax.registrationclient.from.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.lesadrax.registrationclient.R;
import com.lesadrax.registrationclient.from.model.FormField;


// Custom toggleView class
public class MyRadio extends FrameLayout {

    private RadioButton radio1;
    private RadioButton radio2;
    private TextView hintTv;

    private FormField.FormOption[] options;
    private FormField.FormOption selectedItem;
    private String hint;

    private boolean isPasswordVisible = true;


    private OnSwitchedListener onSwitchedListener;

    public MyRadio(Context context) {
        super(context);
    }

    public MyRadio(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MyRadio(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    /**
     * Builds the MyToggleView with the provided options and initializes it with the attributes.
     */
    public void build(FormField.FormOption[] options){
        this.options = options;

        init(null);
    }

    /**
     * Initializes the MyToggleView with the provided attributes.
     *
     * @param attrs The attribute set.
     */
    private void init(@Nullable AttributeSet attrs){

        if (getContext() == null)
            return;

        // Retrieve attribute values from XML


        // Inflate the custom_toggle layout and add it to the view
        View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_radio, null,false);
        addView(view);

        hintTv = view.findViewById(R.id.hint);
        radio1 = view.findViewById(R.id.a);
        radio2 = view.findViewById(R.id.b);

        hintTv.setId(View.generateViewId());
        radio1.setId(View.generateViewId());
        radio2.setId(View.generateViewId());

        radio1.setText(options[0].getName());
        radio2.setText(options[1].getName());
        hintTv.setText(hint);

        radio1.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked)
                if (onSwitchedListener != null) {
                    selectedItem= options[0];
                    onSwitchedListener.onSwitched(selectedItem);
                }

        });

        radio2.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked)
                if (onSwitchedListener != null) {
                    selectedItem = options[1];
                    onSwitchedListener.onSwitched(selectedItem);
                }

        });

    }

    /**
     * Gets the text of the currently selected option.
     *
     * @return The text of the selected option, or null if no option is selected.
     */
    public String getSelectedText(){
        if (radio1.isSelected())
            return options[0].getName();
        if (radio2.isSelected())
            return options[1].getName();

        return null;
    }

    /**
     * Gets the text of the currently selected option.
     *
     * @return The text of the selected option, or null if no option is selected.
     */
    public FormField.FormOption getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(FormField.FormOption selectedItem) {
        this.selectedItem = selectedItem;

        if (selectedItem.getId() == options[0].getId()){
            radio1.setChecked(true);
        } else if (selectedItem.getId() == options[1].getId()){
            radio2.setChecked(true);
        }
    }

    public void setSelectedItem(Boolean value) {

        if (value == null)
            return;

        if (value){
            radio1.setChecked(true);
            selectedItem = options[0];
        } else {
            radio2.setChecked(true);
            selectedItem = options[1];
        }

        if (onSwitchedListener != null)
            onSwitchedListener.onSwitched(selectedItem);
    }

    public void setSelectedItem(int value) {

        if (value == options[0].getId()){
            radio1.setChecked(true);
            selectedItem = options[0];
        } else if (value == options[1].getId()) {
            radio2.setChecked(true);
            selectedItem = options[1];
        }

        if (onSwitchedListener != null)
            onSwitchedListener.onSwitched(selectedItem);
    }

    public void setSelectedItem(String value) {

        if (value == null)
            return;

        if (value.equals(options[0].getName()) || value.equals(options[0].getKey())){
            radio1.setChecked(true);
            selectedItem = options[0];
        } else if (value.equals(options[1].getName()) || value.equals(options[1].getKey())) {
            radio2.setChecked(true);
            selectedItem = options[1];
        }

        if (onSwitchedListener != null)
            onSwitchedListener.onSwitched(selectedItem);
    }

    /**
     * Gets the ID of the currently selected option.
     *
     * @return The ID of the selected option, or -1 if no option is selected.
     */
    public Boolean getSelectedValue(){
        if (radio1.isSelected())
            return true;
        if (radio2.isSelected())
            return false;

        return null;
    }

    public FormField.FormOption[] getOptions() {
        return options;
    }

    public void setOptions(FormField.FormOption[] options) {
        this.options = options;
    }

    //    /**
//     * Sets the selected option based on the provided value.
//     *
//     * @param value The value of the option to select.
//     */
//    public void setValue(String value){
//        if (value.equals(positive))
//            radio1.performClick();
//        if (value.equals(negative))
//            radio2.performClick();
//
//        if (onSwitchedListener != null)
//            onSwitchedListener.onSwitched(getSelectedID(), getSelectedText());
//    }

//    /**
//     * Sets the selected option based on the provided value.
//     *
//     * @param value The value of the option to select.
//     */
//    public void setValue(Boolean value){
//        if (value == null)
//            return;
//
//        if (value)
//            radio1.performClick();
//        if (!value)
//            radio2.performClick();
//
//        if (onSwitchedListener != null)
//            onSwitchedListener.onSwitched(getSelectedValue(), getSelectedText());
//    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public boolean isPasswordVisible() {
        return isPasswordVisible;
    }

    public void setPasswordVisible(boolean passwordVisible) {
        isPasswordVisible = passwordVisible;
    }

    public TextView getHintTv() {
        return hintTv;
    }

    public void setHintTv(TextView hintTv) {
        this.hintTv = hintTv;
    }

    public void setOnSwitchedListener(OnSwitchedListener onSwitchedListener) {
        this.onSwitchedListener = onSwitchedListener;
    }

    /**
     * The listener interface for handling the switch event.
     */
    public interface OnSwitchedListener {
        /**
         * Called when the switch is toggled between options.
         */
        void onSwitched(FormField.FormOption option);
    }

}
