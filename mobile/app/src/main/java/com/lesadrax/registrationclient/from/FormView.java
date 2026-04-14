package com.lesadrax.registrationclient.from;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.lesadrax.registrationclient.R;
import com.lesadrax.registrationclient.from.model.FormField;
import com.lesadrax.registrationclient.from.model.FormValue;
import com.lesadrax.registrationclient.from.ui.MyCustomPickerField;
import com.lesadrax.registrationclient.from.ui.MyDateField;
import com.lesadrax.registrationclient.from.ui.MyEditText;
import com.lesadrax.registrationclient.from.ui.MyFileField;
import com.lesadrax.registrationclient.from.ui.MyRadio;
import com.lesadrax.registrationclient.from.ui.MySpinner;
import com.lesadrax.registrationclient.from.utils.FormFieldParser;
import com.lesadrax.registrationclient.from.utils.FormUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint("ViewConstructor")
public class FormView extends LinearLayout {

    private List<FormField> fields;
    private OnSelectOption onSelectOption;
    private OnTextChanged onTextChanged;

    private Activity getActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }


    public FormView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FormView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FormView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     *
     * @param context         The activity context.
     */
    public FormView(Context context, int res) {
        super(context);
        this.fields = FormFieldParser.parseFormFields(getContext(), res);
    }


    public void setRes(int res) {
        this.fields = FormFieldParser.parseFormFields(getContext(), res);
        for (int i = 0; i < this.fields.size(); i++) {
            FormField f = this.fields.get(i);
            if (checkOther(f)) {
                FormField field = new FormField();
                field.setName(f.getName()+"#");
                field.setLabel("Preciser");
                field.setType("text");

                FormField.VisibilityCondition vc = new FormField.VisibilityCondition();
                vc.setRef(f.getName());
                vc.setEvent("selected");
                vc.setElements(new int[]{-1});
                field.setVisibilityCondition(vc);
                this.fields.add(i + 1, field); // Add the new field right after the current one
                i++; // Increment index to skip the newly added field and avoid re-checking
            }
        }
    }

    /**
     * Initializes the form builder by setting orientation,
     * and creating views for each form field.
     */
    public void build() {
        setOrientation(VERTICAL);
        setLayoutTransition(new LayoutTransition());
        for (FormField field : fields) {
            View fieldView = createFieldView(field); // Create a view for each form field
            addView(fieldView);
        }
        setupReceiver();
    }

    /**
     * Initializes the form builder by setting orientation,
     * and creating views for each form field.
     */
    public void rebuild(String fieldID) {

        FormField newField = null;

        for (FormField field : fields){
            if (fieldID.equals(field.getName())){
                newField = field;
            }
        }

        if (newField == null){
            return;
        }

        setOptions(fieldID, newField.getOptions());
    }


    /**
     * Builds the form fields with pre-filled values.
     *
     * @param values The map of field names and corresponding values.
     */
    public void buildField(Map<String, FormValue> values){

        for (Map.Entry<String, FormValue> entry : values.entrySet()) {
//            Log.w("*****UUUUU", entry.getValue()+"");

            if (entry.getValue() == null) {
                continue;
            }

            if (entry.getValue() instanceof Map<?, ?>){
                buildField((Map<String, FormValue>) entry.getValue());
                continue;
            }

            View view = findViewWithTag(entry.getKey()); // Find the view corresponding to the field name
            if (view instanceof MyEditText){
                MyEditText met = ((MyEditText)view);
                met.getEditText().setText(entry.getValue().getDisplay());
                continue;
            }
            if (view instanceof MySpinner){
                if (entry.getValue().isOtherValue()){
                    MyEditText otherField = findViewWithTag(entry.getKey()+"#");
                    if (otherField != null) {
                        otherField.setVisibility(VISIBLE);
                        otherField.setText(entry.getValue().getDisplay());
                    }
                }
                ((MySpinner) view).setSelectedItem(FormUtils.getInt(entry.getValue().getValue()));
                if(((MySpinner)view).getText().isEmpty() && !entry.getValue().getValue().toString().isEmpty())
                    ((MySpinner)view).setText(entry.getValue().getValue().toString());

                continue;
            }
            if (view instanceof MyRadio){
                ((MyRadio) view).setSelectedItem(FormUtils.getInt(entry.getValue().getValue()));
                continue;
            }
            if (view instanceof MyDateField){
                ((MyDateField)view).setDateText(entry.getValue().getDisplay());
                continue;
            }
            if (view instanceof MyCustomPickerField){
                ((MyCustomPickerField)view).setDisplayText(entry.getValue().getDisplay());
                continue;
            }
            if (view instanceof MyFileField){
                if (entry.getValue().getValue() instanceof String) {
                    ((MyFileField) view).setFile(entry.getValue().getDisplay(), (String) entry.getValue().getValue());
                }
                continue;
            }

        }

        // Show or hide fields based on visibility conditions
        for (FormField f : fields){
            if (f.getVisibilityCondition() != null){
                if (values.get(f.getName()) != null){
                    View view = findViewWithTag(f.getName());
                    if (view != null)
                        view.setVisibility(VISIBLE);
                }
            }
        }
    }

    /**
     * Creates a view for a specific form field.
     *
     * @param field The form field to create a view for.
     * @return The created view.
     */
    private View createFieldView(FormField field) {

        View view = new View(getContext());
        switch (field.getType()) {
            case "text":
                view = createTextField(field);
                break;
            case "name":
                view = createNameField(field);
                break;
            case "email":
                view = createEmailField(field);
                break;
            case "number":
                view = createNumericField(field);
                break;
            case "date":
                view = createDateField(field);
                break;
            case "radio":
                view = createRadioField(field);
                break;
            case "spinner":
                view = createSpinnerField(field);
                if (checkOther(field)){
                    MyEditText otherField = (MyEditText) createTextField(field);
                    otherField.setTag(field.getName());
                }
                break;
            case "picker":
                view = createPickerField(field);
                break;
            case "phone":
                view = createPhoneField(field);
                break;
            case "file":
                view = createFileField(field);
                break;
        }
        if (field.getExtras() != null){
            for (String s : field.getExtras()){
                if (s.equals("AUTO_UPPER")){
                    if (view instanceof MyEditText || view instanceof MySpinner){

                        EditText editText = null;

                        if (view instanceof MyEditText)
                            editText = ((MyEditText) view).getEditText();

                        if (view instanceof MySpinner)
                            editText = ((MySpinner) view).getAutoCompleteTextView();

                        EditText finalEditText = editText;
                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                // Not used in this example
                            }
                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                // Convert the text to uppercase as the user types
                                String uppercaseText = charSequence.toString().toUpperCase();
                                finalEditText.removeTextChangedListener(this); // Prevent recursive calls
                                finalEditText.setText(uppercaseText);
                                finalEditText.setSelection(uppercaseText.length()); // Set the cursor at the end
                                finalEditText.addTextChangedListener(this); // Add back the TextWatcher
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                // Not used in this example
                            }
                        });
                    }
                }
                else if (s.equals("DISABLED")){
                    if (view instanceof MyEditText){

                        EditText editText = null;

                        if (view instanceof MyEditText)
                            editText = ((MyEditText) view).getEditText();

                        editText.setEnabled(false);
                    }
                }
            }
        }


        view.setTag(field.getName()); // Set a tag on the view to identify it later

        if (field.getVisibilityCondition() != null){
            if (!field.getVisibilityCondition().isDefaultVisible()){
                view.setVisibility(GONE); // Hide the view if it's not default visible
            }
        }

        return view;
    }

    private boolean checkOther(FormField field) {

        if (field.getOptions() != null) {
            for (FormField.FormOption o : field.getOptions()) {

                if (o.getId() == -1) {
                    return true;
                }

            }
        }

        return false;
    }


    /**
     * Retrieves the form data entered by the user.
     *
     * @return The map of field names and corresponding values.
     */
    public Map<String, FormValue> getFormData() {
        Map<String, FormValue> formData = new HashMap<>();

        MyEditText met;
        Object val = null;
        String value;

        resetViewStatus();

        for(FormField f : fields){
            View v = findViewWithTag(f.getName());
            if (v == null)
                continue;
            if (v.getVisibility() == GONE)
                continue;

            switch (f.getType()) {
                case "name":
                case "email":
                case "phone":
                case "text":
                    met = (MyEditText) v;
                    EditText et = met.getEditText();

                    value = et.getText().toString().trim();

                    met.getTextInputLayout().setErrorEnabled(false);

                    if (f.isRequired() && value.isEmpty()){
                        scrollToView(v);

                        met.getTextInputLayout().setErrorEnabled(true);
                        met.getTextInputLayout().setError(getContext().getString(R.string.champ_requis));

                        return null;
                    }

                    if (!value.isEmpty()) {
                        if (f.getType().equals("email")) {
                            if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
                                scrollToView(v);
                                met.getTextInputLayout().setErrorEnabled(true);
                                met.getTextInputLayout().setError("Veuillez renseigner un email valide");
//                            Toast.makeText(getContext(), "Veuillez renseigner un email valide", Toast.LENGTH_SHORT).show();
                                return null;
                            }
                        }
                    }

                    if (!value.isEmpty()) {
                        if (f.getType().equals("phone")) {
                            String regex = "(90|91|92|93|96|97|98|99|70|79|71)[0-9]{6}";

                            Pattern pattern = Pattern.compile(regex);
                            Matcher matcher = pattern.matcher(value);

                            if (!matcher.matches()) {
                                met.getTextInputLayout().setErrorEnabled(true);
                                met.getTextInputLayout().setError("Veuillez renseigner un numero valide");
                                return null;
                            }
                        }
                    }

                    formData.put(f.getName(), new FormValue(value, value, value, f.getParseType()));
                    break;
                case "number":
                    met = (MyEditText) v;
                    EditText net = met.getEditText();
                    value = net.getText().toString().trim();

                    met.getTextInputLayout().setErrorEnabled(false);
                    if (f.isRequired() && value.isEmpty()){
                        scrollToView(v);

                        met.getTextInputLayout().setErrorEnabled(true);
                        met.getTextInputLayout().setError(getContext().getString(R.string.champ_requis));

                        return null;
                    }
                    int number = 0;
                    try{
                        number = Integer.parseInt(value);
                        formData.put(f.getName(), new FormValue(number, String.valueOf(number), number, f.getParseType()));
                    }catch (NumberFormatException e){
                        e.printStackTrace();
                        formData.put(f.getName(), new FormValue(value, value, value, f.getParseType()));
                    }
                    break;

                case "spinner":
                    MySpinner mySpinner = (MySpinner) v;

                    String valueText = null;
                    long valueID = 0;
                    boolean isOther = false;

                    MyEditText otherField = findViewWithTag(f.getName()+"#");
                    if (otherField != null){
                        if(mySpinner.getSelectedItem() != null){
                            if (mySpinner.getSelectedItem().getId() == -1){
                                valueText = otherField.getText();
                                isOther = true;
                            }
                        }
                    }

                    if (mySpinner.getSelectedItem() != null){
                        valueID = mySpinner.getSelectedItem().getId();
                        if (!isOther)
                            valueText = mySpinner.getSelectedItem().getName();
                    }

                    mySpinner.getTextInputLayout().setErrorEnabled(false);
                    if (f.isRequired() && TextUtils.isEmpty(valueText)){
                        scrollToView(v);

                        mySpinner.getTextInputLayout().setErrorEnabled(true);
                        mySpinner.getTextInputLayout().setError(getContext().getString(R.string.champ_requis));

                        return null;
                    }


                    if (f.getDataType() == FormField.DataType.ID){
                        val = mySpinner.getSelectedItem().getId();
                    } else if (f.getDataType() == FormField.DataType.KEY){
                        val = mySpinner.getSelectedItem().getKey();
                    } else {
                        if(mySpinner.getSelectedItem() != null){
                            val = mySpinner.getSelectedItem().getName();
                        }
                    }

                    formData.put(f.getName(), new FormValue(
                            valueID,
                            valueText,
                            val,
                            isOther,
                            f.getParseType()
                    ));

                    break;

                case "radio":
                    MyRadio myRadio = (MyRadio) v;

                    if (f.isRequired() && myRadio.getSelectedItem() == null){
                        scrollToView(v);
                        Toast.makeText(getContext(), f.getLabel()+ " est requis", Toast.LENGTH_SHORT).show();
                        return null;
                    }

                    if (myRadio.getSelectedItem() == null){
                        break;
                    }

                    if (f.getDataType() == FormField.DataType.ID){
                        val = myRadio.getSelectedItem().getId();
                    } else if (f.getDataType() == FormField.DataType.BOOL){

                        if (myRadio.getSelectedItem().getId() == 0) {
                            val = Boolean.FALSE;
                        } else if(myRadio.getSelectedItem().getId() == 1) {
                            val = Boolean.TRUE;
                        }
                        else
                            val = null;

                    } else if (f.getDataType() == FormField.DataType.KEY){
                        val = myRadio.getSelectedItem().getKey();
                    } else {
                        val = myRadio.getSelectedItem().getName();
                    }

                    formData.put(f.getName(), new FormValue(
                            myRadio.getSelectedItem().getId(),
                            myRadio.getSelectedItem().getName(),
                            val,
                            f.getParseType()
                    ));
                    break;

                case "date":
                    MyDateField myDate = (MyDateField) v;

                    if (f.isRequired() && myDate.getDateText() == null){
                        scrollToView(v);
                        myDate.getHintTv().setErrorEnabled(true);
                        myDate.getHintTv().setError(getContext().getString(R.string.champ_requis));
                        return null;
                    }
                    if(myDate.getDateText() != null)
                        formData.put(f.getName(), new FormValue(myDate.getDateText(), myDate.getDateText(), myDate.getDateText(), f.getParseType()));
                    else if(!myDate.getText().trim().isEmpty())
                        formData.put(f.getName(), new FormValue(myDate.getText(), myDate.getText(), myDate.getText(), f.getParseType()));
                    else
                        formData.put(f.getName(), new FormValue(myDate.getDateText(), myDate.getDateText(), myDate.getDateText(), f.getParseType()));

                    break;
                case "picker":
                    MyCustomPickerField myPicker = (MyCustomPickerField) v;

                    if (f.isRequired() && myPicker.getDisplayText() == null){
                        scrollToView(v);
                        myPicker.getHintTv().setErrorEnabled(true);
                        myPicker.getHintTv().setError(getContext().getString(R.string.champ_requis));
                        return null;
                    }

                    formData.putAll(myPicker.getData());
                    break;
                case "file":

                    MyFileField myFileField = (MyFileField) v;

                    if (f.isRequired() && myFileField.getText() == null){
                        scrollToView(v);
                        Toast.makeText(getContext(), f.getLabel()+ " est requis", Toast.LENGTH_SHORT).show();
                        return null;
                    }

                    formData.put(f.getName(), new FormValue(myFileField.getPath(), myFileField.getText(), myFileField.getPath(), f.getParseType()));
                    break;
            }
        }

        return formData;
    }

    private BroadcastReceiver fileReceiver;
    public void setupReceiver() {
        fileReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();

                Log.d("UUUUU", action + "");
                if ("FILE_SELECTED".equals(action)) {
                    String receivedId = intent.getStringExtra("fieldId");

                    MyFileField field = finMyFileField(receivedId);

                    if (field != null) {
                        String fileName = intent.getStringExtra("fileName");
                        String filePath = intent.getStringExtra("filePath");

                        Log.d("UUUUU", fileName + "");
                        Log.d("UUUUU", filePath + "");
                        field.setFile(fileName, filePath);
                    }
                } else if ("PICKER_SELECTED".equals(action)){
                    String receivedId = intent.getStringExtra("fieldId");

                    MyCustomPickerField field = finCustomPickerField(receivedId);

                    if (field != null) {
                        String display = intent.getStringExtra("DISPLAY");
                        Map<String, FormValue> data = (Map<String, FormValue>) intent.getSerializableExtra("DATA");

                        Log.d("UUUUU", display + "");
                        Log.d("UUUUU", data + "");

                        field.setDisplayText(display);
                        field.setData(data);
                    }
                }

            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("FILE_SELECTED");
        filter.addAction("PICKER_SELECTED");
        LocalBroadcastManager.getInstance(getContext())
                .registerReceiver(fileReceiver, filter);
    }

    public void clearReceiver(){
        if (fileReceiver != null) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(fileReceiver);
            fileReceiver = null;
        }
    }

    public void setVisibility(String tag, boolean visible){

        View v = findViewWithTag(tag);
        if (v != null){
            if (visible)
                v.setVisibility(VISIBLE);
            else
                v.setVisibility(GONE);
        }
    }

    public MyEditText finMyEditText(String tag){

        View v = findViewWithTag(tag);
        if (v != null){
            if (v instanceof MyEditText){
                return (MyEditText) v;
            }
        }

        return null;
    }

    public MyDateField finMyDateField(String tag){

        View v = findViewWithTag(tag);
        if (v != null){
            if (v instanceof MyDateField){
                return (MyDateField) v;
            }
        }

        return null;
    }

    public MyCustomPickerField finCustomPickerField(String tag){

        View v = findViewWithTag(tag);
        if (v != null){
            if (v instanceof MyCustomPickerField){
                return (MyCustomPickerField) v;
            }
        }

        return null;
    }

    public MyFileField finMyFileField(String tag){

        View v = findViewWithTag(tag);
        if (v != null){
            if (v instanceof MyFileField){
                return (MyFileField) v;
            }
        }

        return null;
    }

    public MyRadio finMyRadio(String tag){

        View v = findViewWithTag(tag);
        if (v != null){
            if (v instanceof MyRadio){
                return (MyRadio) v;
            }
        }

        return null;
    }

    public void setOptions(String fieldID, List<FormField.FormOption> options){

        View view = findViewWithTag(fieldID);

        if (view == null){
            return;
        }

        if (view instanceof MySpinner){
            ((MySpinner) view).setOptions(options);
            ((MySpinner) view).buildOptions();
        }

    }

    /**
     * Creates a text field view.
     *
     * @param field The form field.
     * @return The created text field view.
     */
    private View createTextField(FormField field) {
        MyEditText editText = new MyEditText(getContext());
        editText.setHint(field.getLabel());
        editText.setHintVisibility(true);

        if (field.getMaxSize() > 0){
            editText.setMaxLength(field.getMaxSize());
        }else {
            editText.setMaxLength(80);
        }

        editText.build();

        if (field.getLines() > 1){
            editText.getEditText()
                    .setLines(field.getLines());
            editText.getEditText().setGravity(Gravity.TOP);
        }

        addTextWatcher(field.getName(), editText.getEditText());

        return editText;
    }

    /**
     * Creates a name field view.
     *
     * @param field The form field.
     * @return The created name field view.
     */
    private View createNameField(FormField field) {

        MyEditText editText = new MyEditText(getContext());
        editText.setHint(field.getLabel());
        editText.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME);
        editText.setHintVisibility(true);

        if (field.getMaxSize() > 0){
            editText.setMaxLength(field.getMaxSize());
        }else {
            editText.setMaxLength(80);
        }

        editText.build();

        addTextWatcher(field.getName(), editText.getEditText());

        return editText;
    }

    /**
     * Creates an email field view.
     *
     * @param field The form field.
     * @return The created email field view.
     */
    private View createEmailField(FormField field) {
        MyEditText editText = new MyEditText(getContext());
        editText.setHint(field.getLabel());
        editText.setInputType(EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        editText.setHintVisibility(true);

        if (field.getMaxSize() > 0){
            editText.setMaxLength(field.getMaxSize());
        }else {
            editText.setMaxLength(80);
        }

        editText.build();

        addTextWatcher(field.getName(), editText.getEditText());

        return editText;
    }

    /**
     * Creates a numeric field view.
     * @param field The form field.
     * @return The created numeric field view.
     */
    private View createNumericField(FormField field) {
        MyEditText editText = new MyEditText(getContext());
        editText.setHint(field.getLabel());
        editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        editText.setHintVisibility(true);

        if (field.getMaxSize() > 0){
            editText.setMaxLength(field.getMaxSize());
        }else {
            editText.setMaxLength(10);
        }

        editText.build();

        addTextWatcher(field.getName(), editText.getEditText());

        return editText;
    }

    /**
     * Creates a phone field view.
     * @param field The form field.
     * @return The created phone field view.
     */
    private View createPhoneField(FormField field) {
        MyEditText editText = new MyEditText(getContext());
        editText.setHint(field.getLabel());
        editText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        editText.setMaxLength(8);
        editText.setHintVisibility(true);
        editText.build();

        addTextWatcher(field.getName(), editText.getEditText());

        return editText;
    }

    /**
     * Creates a spinner field view.
     * @param field The form field.
     * @return The created spinner field view.
     */
    private View createDateField(FormField field) {

        MyDateField dateField = new MyDateField(getContext());
        dateField.setHint(field.getLabel());
        Log.d("***DateField", "====> "+field.getLabel());


        dateField.setOnSelectListener(date -> {
            if (onTextChanged != null){
                onTextChanged.onSelect(field.getName(), date);
            }
        });

        dateField.build();

        return dateField;
    }

    /**
     * Creates a spinner field view.
     * @param field The form field.
     * @return The created spinner field view.
     */
    private View createPickerField(FormField field) {

        MyCustomPickerField f = new MyCustomPickerField(getContext());
        f.setHint(field.getLabel());


        f.setOnSelectListener(date -> {
            if (onTextChanged != null){
                onTextChanged.onSelect(field.getName(), date);
            }
        });

        f.build();

        return f;
    }

    private View createFileField(FormField field) {

        MyFileField fileField = new MyFileField(getContext());
        fileField.setHint(field.getLabel());

        fileField.build(field.getName(), field.getMime(), field.getExtras());

        return fileField;
    }

    /**
     * Creates a toggle field view.
     *
     * @param field The form field.
     * @return The created toggle field view.
     */
    private View createRadioField(FormField field) {

        MyRadio tw = new MyRadio(getContext());
        tw.setHint(field.getLabel());
        tw.build(field.getOptions().toArray(new FormField.FormOption[0]));

        tw.setOnSwitchedListener((value) -> {
            refreshUpdate(field.getName(), value.getId());

            if (onSelectOption != null){
                onSelectOption.onSelect(field.getName(), value);
            }
        });

        return tw;
    }

    /**
     * Creates a spinner field view.
     *
     * @param field The form field.
     * @return The created spinner field view.
     */
    private View createSpinnerField(FormField field) {

        MySpinner spinner = new MySpinner(getContext());
        spinner.setHint(field.getLabel());
        spinner.setOptions(field.getOptions());

        spinner.build();

        spinner.setOnSelectListener((i, d) -> {
//            spinner.setText(d.getLabel());
            refreshUpdate(field.getName(), d.getId());
            spinner.setSelected(false);

            if (onSelectOption != null){
                onSelectOption.onSelect(field.getName(), d);
            }
        });

        return spinner;
    }


    /**
     * Refreshes the visibility of fields based on a visibility condition.
     *
     * @param ref The reference field name.
     * @param id  The element ID.
     */
    private void refreshUpdate(String ref, long id) {


        for (FormField form : fields){
            FormField.VisibilityCondition c = form.getVisibilityCondition();
            if (c != null){

                if (c.getEvent().equals("unselected")){

                    if (ref.equals(c.getRef())) {
                        boolean contain = false;
                        for (int e : c.getElements()) {
                            if (e == id) {
                                contain = true;
                                break;
                            }
                        }

                        if (!contain) {
                            findViewWithTag(form.getName()).setVisibility(VISIBLE);
                        } else {
                            findViewWithTag(form.getName()).setVisibility(GONE);
                        }
                    }


                } else if (c.getEvent().equals("selected")){

                    if (ref.equals(c.getRef())) {
                        for (int e : c.getElements()) {
                            if (e == id) {
                                findViewWithTag(form.getName()).setVisibility(VISIBLE);
                                break;
                            } else {
                                findViewWithTag(form.getName()).setVisibility(GONE);
                            }
                        }
                    }

                }

//                FormField.VisibilityCondition c = f.getVisibilityCondition();

            }
        }
    }

    /**
     * Resets the view status by deselecting all views.
     */
    public void resetViewStatus(){
        for (FormField field : fields) {
            View view = findViewWithTag(field.getName());
            if (view.isSelected())
                view.setSelected(false);
        }
    }

    private NestedScrollView findParentNestedScrollView(View view) {
        ViewParent parent = view.getParent();
        while (parent != null) {
            if (parent instanceof NestedScrollView) {
                return (NestedScrollView) parent;
            }
            parent = parent.getParent();
        }
        return null;
    }

    /**
     * Used to scroll to the given view.
     *
     * @param view View to which we need to scroll.
     */
    private void scrollToView(final View view) {
        NestedScrollView scrollView = findParentNestedScrollView(view);
        if (scrollView != null) {
            // Get deepChild Offset
            Point childOffset = new Point();
            getDeepChildOffset(scrollView, view.getParent(), view, childOffset);
            // Scroll to child.
            scrollView.smoothScrollTo(0, childOffset.y);
        }
    }

    /**
     * Used to get deep child offset.
     * <p/>
     * 1. We need to scroll to child in scrollview, but the child may not the direct child to scrollview.
     * 2. So to get correct child position to scroll, we need to iterate through all of its parent views till the main parent.
     *
     * @param mainParent        Main Top parent.
     * @param parent            Parent.
     * @param child             Child.
     * @param accumulatedOffset Accumulated Offset.
     */
    private void getDeepChildOffset(final ViewGroup mainParent, final ViewParent parent, final View child, final Point accumulatedOffset) {
        ViewGroup parentGroup = (ViewGroup) parent;
        accumulatedOffset.x += child.getLeft();
        accumulatedOffset.y += child.getTop();
        if (parentGroup.equals(mainParent)) {
            return;
        }
        getDeepChildOffset(mainParent, parentGroup.getParent(), parentGroup, accumulatedOffset);
    }

    private void addTextWatcher(String id, EditText editText){

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (onTextChanged != null){
                    onTextChanged.onSelect(id, s.toString());
                }
            }
        });

    }

    public void setOnSelectOption(OnSelectOption onSelectOption) {
        this.onSelectOption = onSelectOption;
    }

    public void setOnTextChanged(OnTextChanged onTextChanged) {
        this.onTextChanged = onTextChanged;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.e("UUUUUUUU", "55555555555");
        clearReceiver();
    }

    public interface OnSelectOption {
        void onSelect(String fieldID, FormField.FormOption option);
    }

    public interface OnTextChanged {
        void onSelect(String fieldID, String value);
    }

    public MySpinner finMySpinner(String tag){

        View v = findViewWithTag(tag);
        if (v != null){
            if (v instanceof MySpinner){
                return (MySpinner) v;
            }
        }

        return null;
    }

}