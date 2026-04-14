package com.lesadrax.registrationclient.from.ui;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;


import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputLayout;
import com.lesadrax.registrationclient.R;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

// Custom Spinner class
public class MyDateField extends FrameLayout {

    private EditText textView;
    private TextInputLayout hintTv;

    private String dateText;
    private String hint;

    private OnSelectListener onSelectListener;

    public MyDateField(Context context) {
        super(context);
    }

    public MyDateField(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MyDateField(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

//    private void request(){
//        final Calendar c = Calendar.getInstance();
//        DatePickerDialog datePickerDialog;
//        datePickerDialog = new DatePickerDialog(getContext(),
//                (view1, year, monthOfYear, dayOfMonth) -> {
//
//                    monthOfYear+=1;
//                    String check = ""+monthOfYear;
//                    String checkDay = ""+dayOfMonth;
//                    if(checkDay.length() == 1)
//                        checkDay = "0"+checkDay;
//                    if(check.length() == 1){
//                        check = "0"+check;
//                    }
//                    dateText = ""+checkDay+"-"+check+"-"+year;
//
//                    if (onSelectListener != null)
//                        onSelectListener.onSelect(dateText);
//
//                    textView.setText(dateText);
//                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
//        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
//        datePickerDialog.show();
//    }

    private void request() {
        // Build constraints to set max date to today
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                .setEnd(MaterialDatePicker.todayInUtcMilliseconds());

        // Create Material Date Picker
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraintsBuilder.build())
                .setTheme(com.google.android.material.R.style.ThemeOverlay_Material3_MaterialCalendar)
                .build();

        // Set up date picker listener
        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Convert the UTC timestamp to local date
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(selection);

            int year = calendar.get(Calendar.YEAR);
            // Month is 0-based, so add 1
            int monthOfYear = calendar.get(Calendar.MONTH) + 1;
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            // Format day and month to ensure 2 digits
            String check = String.valueOf(monthOfYear);
            String checkDay = String.valueOf(dayOfMonth);

            if (checkDay.length() == 1) {
                checkDay = "0" + checkDay;
            }
            if (check.length() == 1) {
                check = "0" + check;
            }

            // Format date string as dd-MM-yyyy
            dateText = year+ "-" + check+"-"+checkDay;

            // Call listener if exists
            if (onSelectListener != null) {
                onSelectListener.onSelect(dateText);
            }

            // Update TextView
            textView.setText(dateText);
        });

        // Show the date picker
        datePicker.show(((FragmentActivity) getContext()).getSupportFragmentManager(), "DATE_PICKER");
    }

    public void clear(){
        dateText = null;
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


    public String getDateText() {
        return dateText;
    }

    public void setDateText(String dateText) {
        this.dateText = dateText;
        textView.setText(dateText);
    }

    public TextInputLayout getHintTv() {
        return hintTv;
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    public interface OnSelectListener {
        void onSelect(String date);
    }

    /**
     * Ajoute un TextWatcher pour écouter les modifications de texte.
     *
     * @param textWatcher Le TextWatcher à attacher à l'EditText interne.
     */
    public void addTextChangedListener(android.text.TextWatcher textWatcher) {
        if (textView != null) {
            textView.addTextChangedListener(textWatcher);
        }
    }

    public String getText(){
        return textView.getText().toString().trim();
    }

    public void setText(String text){
        textView.setText(text);
    }
}
