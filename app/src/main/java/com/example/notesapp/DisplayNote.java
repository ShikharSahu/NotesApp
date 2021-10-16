package com.example.notesapp;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.Serializable;
import java.util.Objects;

public class DisplayNote extends AppCompatActivity {


    private TextInputEditText title, description, taskDone, totalTask;
    private SwitchMaterial hasProgressSwitch;
    private MaterialButton createNote;
    private boolean hasProgress = true;
    private TextInputLayout taskDoneLayout, totalTaskLayout, descriptionLayout;
    private AutoCompleteTextView autoCompleteTextView;
    private TextInputLayout priorityMenuLayout;
    int indexToReplace = 0 ;
    private FloatingActionButton makeEditableButton;
    private ArrayAdapter<String> priorityDropDownAdapter;
    private final String[] priorityDDOptions = new String[]{"High","Mid","Low","No"};


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onStop() {
        super.onStop();
        makeEditableButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_edit_24));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_note);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            customizeActionBarLook();

        }

        title = findViewById(R.id.titleNoteETDisplay1);
        description = findViewById(R.id.descriptionNoteEtDisplay1);
        taskDone = findViewById(R.id.tasksDoneETDisplay1);
        totalTask = findViewById(R.id.totalTaskNoteETDisplay1);
        hasProgressSwitch = findViewById(R.id.hasProgressMatDisplay1);
        createNote = findViewById(R.id.createNoteButtonDisplay1);
        totalTaskLayout = findViewById(R.id.totalTaskNoteLytDisplay1);
        taskDoneLayout = findViewById(R.id.taskDoneLayoutDisplay1);
        autoCompleteTextView = findViewById(R.id.priorityCreateDropDownDisplay1);
        priorityMenuLayout = findViewById(R.id.textFieldDisplay1);
        makeEditableButton = findViewById(R.id.fabEdit);
        descriptionLayout = findViewById(R.id.descriptionNoteLytDisplay1);

        makeEditableButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(!title.isEnabled()){
                    enableAllViews(true);

//                    autoCompleteTextView.setText("");
//                    priorityDropDownAdapter.notifyDataSetChanged();

                    makeEditableButton.setImageDrawable(getDrawable(R.drawable.tick_mark));
                }
                else{
                    createNote.performClick();

                }
            }
        });



        Intent intent = getIntent();
        Note receivedNote = (Note) intent.getSerializableExtra(MainActivity.NOTE_TO_EDIT_EXTRA_SEND);
        indexToReplace = intent.getIntExtra(MainActivity.INDEX_TO_REPLACE_EXTRA,12);
        Log.d("mee",indexToReplace+" indextr");
        if(receivedNote == null){
            Toast.makeText(this, "Error, note not found", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED,new Intent());
            finish();
        }
        assert receivedNote != null;
        hasProgress = receivedNote.hasProgress;
        hasProgressSwitch.setChecked(hasProgress);


        title.setText(receivedNote.title);
        if (receivedNote.description != null ) description.setText(receivedNote.description);
        if(hasProgress){
            taskDone.setText(""+receivedNote.tasksDone);
            totalTask.setText(""+receivedNote.maxTask);
        }


        priorityDropDownAdapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item, priorityDDOptions);
        autoCompleteTextView.setAdapter(priorityDropDownAdapter);


        switch (receivedNote.priority){
            case Note.MAX_PRIORITY:{
                autoCompleteTextView.setText("High");
                break;
            }
            case Note.MID_PRIORITY:{
                autoCompleteTextView.setText("Mid");
                break;
            }
            case Note.MIN_PRIORITY:{
                autoCompleteTextView.setText("Low");
                break;
            }
            case Note.NO_PRIORITY:{
                autoCompleteTextView.setText("No");
                break;
            }
        }




        Log.d("mee","allfinds");
        hasProgressSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    hasProgress= true;
                    taskDoneLayout.setVisibility(View.VISIBLE);
                    totalTaskLayout.setVisibility(View.VISIBLE);
                }
                else{
                    hasProgress = false;
                    taskDoneLayout.setVisibility(View.GONE);
                    totalTaskLayout.setVisibility(View.GONE);
                }
            }
        });

        title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(TextUtils.isEmpty(title.getText())){
                    title.setError("title can't be empty");
                }
                else {
                    title.setError(null);
                }
            }
        });


        View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                TextInputEditText textInputEditText = ((TextInputEditText)v);
                String ed_text = textInputEditText.getText().toString().trim();

                if(TextUtils.isEmpty(ed_text)) {
                    textInputEditText.setError("can't be empty!");
                }
                else {
                    textInputEditText.setError(null);
                }
            }
        };
        Log.d("mee","avove create note");


        totalTask.setOnFocusChangeListener(onFocusChangeListener);
        taskDone.setOnFocusChangeListener(onFocusChangeListener);

        createNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean proceed = true;
                if(TextUtils.isEmpty(title.getText())){
                    proceed = false;
                    title.setError("Error");
                }
                if(!TextUtils.isEmpty(autoCompleteTextView.getText())){
                    autoCompleteTextView.setError(null);
                }else{
                    proceed = false;
                    autoCompleteTextView.setError("Error");
                }
                if(hasProgress){
                    if (TextUtils.isEmpty(totalTask.getText())) {
                        proceed = false;
                        totalTask.setError("Error");
                    }
                    if (TextUtils.isEmpty(taskDone.getText())) {
                        proceed = false;
                        taskDone.setError("Error");
                    }
                    if (!(TextUtils.isEmpty(taskDone.getText())) && !(TextUtils.isEmpty(totalTask.getText()))) {
                        int td = Integer.parseInt(taskDone.getText().toString());
                        int tt = Integer.parseInt(totalTask.getText().toString());
                        if (td >= tt) {
                            Toast.makeText(getBaseContext(), "tasks done is greater than total tasks", Toast.LENGTH_SHORT).show();
                            proceed = false;
                        }
                    }
                }
                if(proceed){

                    String titleStr = title.getText().toString();
                    String descriptionStr = description.getText().toString();
                    String priorityLabelString = autoCompleteTextView.getText().toString();

                    int priorityValueInInt = 0;
                    switch (priorityLabelString){
                        case "High": {
                            priorityValueInInt = Note.MAX_PRIORITY;
                            break;
                        }
                        case "Mid": {
                            priorityValueInInt = Note.MID_PRIORITY;
                            break;
                        }
                        case "Low": {
                            priorityValueInInt = Note.MIN_PRIORITY;
                            break;
                        }
                        case "No": {
                            priorityValueInInt = Note.NO_PRIORITY;
                            break;
                        }
                    }

                    int td = 0, tt= 0;
                    if(hasProgress) {
                        td = Integer.parseInt(taskDone.getText().toString());
                        tt = Integer.parseInt(totalTask.getText().toString());
                    }
                    Note note = new Note(titleStr,descriptionStr,hasProgress,td,tt, priorityValueInInt);
                    Intent output = new Intent();
                    output.putExtra(MainActivity.NOTE_TO_EDIT_EXTRA_RECEIVE, (Serializable) note);
                    output.putExtra(MainActivity.INDEX_TO_REPLACE_EXTRA, indexToReplace);
                    setResult(RESULT_OK,output);
                    finish();
                }
            }
        });

        enableAllViews(false);


    }
    private void enableAllViews(boolean enable){
        title.setEnabled(enable);
        description.setEnabled(enable);
        taskDone.setEnabled(enable);
        totalTask.setEnabled(enable);
        hasProgressSwitch.setEnabled(enable);
        autoCompleteTextView.setEnabled(enable);
        createNote.setEnabled(enable);
//        if(enable) autoCompleteTextView.setThreshold(10);
//        else autoCompleteTextView.setThreshold(0);
        if(!enable){

            createNote.setVisibility(View.GONE);
            if (Objects.requireNonNull(description.getText()).toString().equals("")) {
//                description.setVisibility(View.GONE);
                descriptionLayout.setVisibility(View.GONE);
            }
            autoCompleteTextView.setThreshold(10);

            if (!hasProgressSwitch.isChecked()) {
                taskDoneLayout.setVisibility(View.GONE);
                totalTaskLayout.setVisibility(View.GONE);
            }
        }
        else {
            createNote.setVisibility(View.VISIBLE);
            autoCompleteTextView.setThreshold(0);
            descriptionLayout.setVisibility(View.VISIBLE);
//            description.setVisibility(View.VISIBLE);
            taskDoneLayout.setVisibility(View.VISIBLE);
            totalTaskLayout.setVisibility(View.VISIBLE);
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void customizeActionBarLook(){

        int color = ContextCompat.getColor(this, R.color.design_default_color_secondary);
        ColorDrawable colorDrawable = new ColorDrawable(color);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(colorDrawable);
        Window window = getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.design_default_color_secondary))          ;
    }

}