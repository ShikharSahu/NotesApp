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
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.Serializable;

public class DisplayNote extends AppCompatActivity {


    private TextInputEditText title, description, taskDone, totalTask;
    private SwitchMaterial hasProgressSwitch;
    MaterialButton createNote;
    private boolean hasProgress = true;
    private TextInputLayout taskDoneLayout, totalTaskLayout;
    int indexToReplace = 0 ;

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

                    int td = 0, tt= 0;
                    if(hasProgress) {
                        td = Integer.parseInt(taskDone.getText().toString());
                        tt = Integer.parseInt(totalTask.getText().toString());
                    }
                    Note note = new Note(titleStr,descriptionStr,hasProgress,td,tt);
                    Intent output = new Intent();
                    output.putExtra(MainActivity.NOTE_TO_EDIT_EXTRA_RECEIVE, (Serializable) note);
                    output.putExtra(MainActivity.INDEX_TO_REPLACE_EXTRA, indexToReplace);
                    setResult(RESULT_OK,output);
                    finish();
                }
            }
        });






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