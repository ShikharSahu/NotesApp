package com.example.notesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class MainActivity extends AppCompatActivity implements NotesAdapter.ItemClickListener,
        NotesAdapter.ConfettiDisplayable{

    private RecyclerView recyclerView;
    private ArrayList<Note> noteArrayList;
    private KonfettiView konfettiView ;
    private NotesAdapter notesAdapter;
    boolean dirtiedNotes = false;
    private final String keyToStoreStuffInSharedPreferences = "KeyToStoreStuffInSharedPreferences";
    private FloatingActionButton floatingActionButton;

    final static public String INDEX_TO_REPLACE_EXTRA = "INDEX_TO_REPLACE_EXTRA";
    final static public String NOTE_TO_EDIT_EXTRA_SEND = "NOTE_TO_EDIT_EXTRA_SEND";
    final static public String NOTE_TO_EDIT_EXTRA_RECEIVE = "NOTE_TO_EDIT_EXTRA_RECEIVE";

    final static public String NOTE_TO_CREATE_EXTRA = "NOTE_TO_CREATE_EXTRA";
    final static public String nameForSharedPreferences = "nameForSharedPreference.NotesApp";

    private ActivityResultLauncher<Intent> addNoteActivityLauncher, editNoteActivityLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            customizeActionBarLook();
        }

//        noteArrayList = new ArrayList<>();
        konfettiView = findViewById(R.id.viewKonfetti);
        floatingActionButton = findViewById(R.id.addNoteFab);
        noteArrayList = getArrayListFromSP();


//
//        Log.d("mee", "hello");
//        Note n1 = new Note("note 1","long des",  true, 12 ,26);
//        Note n2 = new Note("note 2","some des", false, 12 ,26);
//        Note n3 = new Note("note 3","bad des",  false, 12 ,11);
//        Note n4 = new Note("note 4",null ,  true, 74 ,91);
//
//        Log.d("mee", "hello");
//
//
//        noteArrayList.add(n1);
//        noteArrayList.add(n2);
//        noteArrayList.add(n3);
//        noteArrayList.add(n4);
//


        recyclerView = findViewById(R.id.recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        notesAdapter = new NotesAdapter(this , noteArrayList);
        recyclerView.setAdapter(notesAdapter);

        addNoteActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()== RESULT_OK && result.getData() != null){
                    Bundle bundle = result.getData().getExtras();
                    dirtiedNotes = true;
                    Note newNote = (Note) bundle.get(NOTE_TO_CREATE_EXTRA);
                    noteArrayList.add(0,newNote);
                    notesAdapter.notifyItemInserted(0);
                    recyclerView.scrollToPosition(0);
                }
            }
        });

        editNoteActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()== RESULT_OK && result.getData() != null){
                    Bundle bundle = result.getData().getExtras();
                    dirtiedNotes = true;
                    Note newNote = (Note) bundle.get(NOTE_TO_EDIT_EXTRA_RECEIVE);
                    int pos = bundle.getInt(INDEX_TO_REPLACE_EXTRA,0);
                    noteArrayList.set(pos,newNote);
                    notesAdapter.notifyItemChanged(pos);
                    recyclerView.scrollToPosition(0);
                }
            }
        });

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback
                = new ItemTouchHelper.
                SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
                | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //Toast.makeText(getBaseContext(), "on Move", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                int position = viewHolder.getAdapterPosition();
                noteArrayList.remove(position);
                Toast.makeText(getBaseContext(), "Note on position "+ position+" deleted",
                        Toast.LENGTH_SHORT).show();

                notesAdapter.notifyItemRemoved(position);
                dirtiedNotes= true;

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addANoteAndLaunchActivity();
            }
        });
//        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),((LinearLayoutManager)layoutManager).getOrientation());
//        recyclerView.addItemDecoration(mDividerItemDecoration);
//
//        mDividerItemDecoration.setDrawable(getBaseContext().getResources().getDrawable(R.drawable.divider_rv_line));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_task_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.addNote) {// User chose the "Settings" item, show the app settings UI...
            addANoteAndLaunchActivity();
            return true;
        }
        if (item.getItemId() == R.id.sortByDateAdded) {// User chose the "Settings" item, show the app settings UI...
            noteArrayList.sort((p1, p2) -> p2.timeCreated.compareTo(p1.timeCreated));
            notesAdapter.notifyDataSetChanged();
            dirtiedNotes = true;
            return true;
        }
        if (item.getItemId() == R.id.sortByPriority) {// User chose the "Settings" item, show the app settings UI...
            noteArrayList.sort((p1, p2) -> (p2.priority-p1.priority)==0?p2.timeCreated.compareTo(p1.timeCreated):(p2.priority-p1.priority));
            notesAdapter.notifyDataSetChanged();
            dirtiedNotes = true;
            return true;
        }
        Toast.makeText(this, "Error in selection", Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNoteCompleteClick(View view, int position) {
        Intent intent = new Intent(this, DisplayNote.class);
        intent.putExtra(INDEX_TO_REPLACE_EXTRA,position);
        Note noteToEdit = noteArrayList.get(position);
        intent.putExtra(NOTE_TO_EDIT_EXTRA_SEND, noteToEdit);
        editNoteActivityLauncher.launch(intent);
        //Toast.makeText(this, "Click on pos" + position+ " "+ noteArrayList.get(position).title, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onIncrementClick(NotesAdapter.ViewHolder currentViewHolderInFocus, int position) {

        Note currentNote = noteArrayList.get(position);
        boolean changesMade = currentNote.incrementTask();
        if (changesMade) {
            currentViewHolderInFocus.getWorkDone()
                    .setText(currentViewHolderInFocus.getWorkDoneMessage(currentNote));
            currentViewHolderInFocus.getProgressBar().setProgress(currentNote.tasksDone);
            dirtiedNotes = true;
        }

        if(currentNote.hasProgress && currentNote.tasksDone>= currentNote.maxTask){
            startTaskDoneEvent();
            currentNote.isDone= true;
            notesAdapter.notifyItemChanged(position);
        }


    }

    @Override
    public void onDecrementClick(NotesAdapter.ViewHolder currentViewHolderInFocus, int position) {

        Note currentNote = noteArrayList.get(position);
        if (currentNote.decrementTask()) {
            currentViewHolderInFocus.getWorkDone()
                    .setText(currentViewHolderInFocus.getWorkDoneMessage(currentNote));;
            currentViewHolderInFocus.getProgressBar().setProgress(currentNote.tasksDone);
            dirtiedNotes = true;
        }

    }

    @Override
    public void onClickDone(NotesAdapter.ViewHolder currentViewHolderInFocus, int position) {
        Note currentNote = noteArrayList.get(position);
        if(!currentNote.isDone){
            startTaskDoneEvent();
            if(currentNote.hasProgress){
                currentNote.tasksDone = currentNote.maxTask;
            }
            currentNote.isDone = true;
            notesAdapter.notifyItemChanged(position);
            dirtiedNotes = true;
        }
    }

    @Override
    public void setPriorityLabelImageView(NotesAdapter.ViewHolder currentViewHolderInFocus, int position) {
        Note currentNote = noteArrayList.get(position);
        String pLabelName ;

        currentViewHolderInFocus.getPriorityLabel().setVisibility(View.VISIBLE);
        if(noteArrayList.get(position).priority==Note.NO_PRIORITY){
            currentViewHolderInFocus.getPriorityLabel().setVisibility(View.GONE);
            return;
        }

        switch (currentNote.priority){
            case Note.MAX_PRIORITY: {
                pLabelName = "circle_red";
                break;
            }
            case Note.MID_PRIORITY: {
                pLabelName = "circle_yellow";
                break;
            }
            default: {
                pLabelName = "circle_green";
                break;
            }

        }

        Drawable labelImageViewDrawable = getResources().getDrawable(getResources()
                .getIdentifier(pLabelName, "drawable", getPackageName()));
        currentViewHolderInFocus.getPriorityLabel().setImageDrawable(labelImageViewDrawable);
    }


    public ArrayList<Note> getArrayListFromSP(){
        SharedPreferences sharedPreferences = getSharedPreferences(nameForSharedPreferences, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String response=sharedPreferences.getString(keyToStoreStuffInSharedPreferences, "");
        if (response.equals("")){
            return new ArrayList<>();
        }
        ArrayList<Note> lstArrayList = gson.fromJson(response, new TypeToken<List<Note>>(){}.getType());
        return lstArrayList;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(dirtiedNotes){
            saveToSP(noteArrayList);
            dirtiedNotes = false;
        }

    }

    private void addANoteAndLaunchActivity(){
        Intent intent = new Intent(this, CreateNote.class);
        addNoteActivityLauncher.launch(intent);
    }
    private void saveToSP(ArrayList<Note> notes){

        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        sharedPreferences = getSharedPreferences(nameForSharedPreferences, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = gson.toJson(notes);
        editor = sharedPreferences.edit();
        editor.remove(keyToStoreStuffInSharedPreferences).apply();
        editor.putString(keyToStoreStuffInSharedPreferences, json);
        editor.apply();
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

    private void startTaskDoneEvent(){
        displayConfetti();
    }

    @Override
    public void displayConfetti() {

        konfettiView.build()
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                .addSizes(new Size(12, 5f))
                .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                .streamFor(300, 5000L);
    }
}