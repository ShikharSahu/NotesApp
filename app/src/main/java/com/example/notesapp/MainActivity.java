package com.example.notesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class MainActivity extends AppCompatActivity implements NotesAdapter.ItemClickListener,
        NotesAdapter.ItemTouchHelperAdapter,
        NotesAdapter.ConfettiDisplayable{

    private RecyclerView recyclerView;
    private View emptyView1, emptyView2;
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

    final static public String KEY_FOR_CHART_ARRAYLIST_IN_SERIALIZABLE = "NOTESAPP_ARRAYLIST_IN_SERIALIZABLE";
    final static public String KEY_FOR_CHART_ARRAYLIST_IN_ARGS = "NOTESAPP_BUNDLE_WITH_THE_SERIALIZABLE_ARGS";

    private ActivityResultLauncher<Intent> addNoteActivityLauncher, editNoteActivityLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        noteArrayList = new ArrayList<>();
        konfettiView = findViewById(R.id.viewKonfetti);
        floatingActionButton = findViewById(R.id.addNoteFab);
        noteArrayList = getArrayListFromSP();


//        for(int i = 0 ; i < 25; i++){
//            noteArrayList.add(new Note("Note: "+i,"long des", true,i,30,i%4));
//        }


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


        emptyView1 = findViewById(R.id.empty_image);
        emptyView2 = findViewById(R.id.emptyMessage);

        recyclerView = findViewById(R.id.recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        notesAdapter = new NotesAdapter(this , noteArrayList);
        recyclerView.setAdapter(notesAdapter);

        setEmptyViewOnOff();

        addNoteActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()== RESULT_OK && result.getData() != null){
                    Bundle bundle = result.getData().getExtras();
                    dirtiedNotes = true;
                    Note newNote = (Note) bundle.get(NOTE_TO_CREATE_EXTRA);
                    noteArrayList.add(0,newNote);
                    notesAdapter.notifyAdapterItemInserted(0);
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
                    notesAdapter.notifyAdapterItemChanged(pos);
                    recyclerView.scrollToPosition(0);
                }
            }
        });


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addANoteAndLaunchActivity();
            }
        });

        ItemTouchHelper.Callback callback = new NotesAdapterItemTouchHelperSwipeMove(this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        notesAdapter.setItemTouchHelper(itemTouchHelper);
        itemTouchHelper.attachToRecyclerView(recyclerView);
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
        if (item.getItemId() == R.id.addNoteMenuOption) {
            addANoteAndLaunchActivity();
            return true;
        }
        if (item.getItemId() == R.id.showChartMenuOption){
            Intent intent = new Intent(this, DisplayChart.class);
            Bundle args = new Bundle();
            args.putSerializable(KEY_FOR_CHART_ARRAYLIST_IN_SERIALIZABLE,(Serializable)noteArrayList);
            intent.putExtra(KEY_FOR_CHART_ARRAYLIST_IN_ARGS,args);
            startActivity(intent);
            return true;
        }
        if (item.getItemId() == R.id.sortByDateAdded) {
            noteArrayList.sort((p1, p2) -> p2.timeCreated.compareTo(p1.timeCreated));
            notesAdapter.notifyAdapterDataSetChanged();
            dirtiedNotes = true;
            return true;
        }
        if (item.getItemId() == R.id.sortByPriority) {
            noteArrayList.sort((p1, p2) -> (p2.priority-p1.priority)==0?p2.timeCreated.compareTo(p1.timeCreated):(p2.priority-p1.priority));
            notesAdapter.notifyAdapterDataSetChanged();
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
            notesAdapter.notifyAdapterItemChanged(position);
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
            notesAdapter.notifyAdapterItemChanged(position);
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

    @Override
    public void setEmptyViewOnOff() {
        if(notesAdapter.getItemCount()==0){
//            Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
            emptyView1.setVisibility(View.VISIBLE);
            emptyView2.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        else{
            emptyView1.setVisibility(View.GONE);
            emptyView2.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
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

    private void startTaskDoneEvent(){
        displayConfetti();
    }

    @Override
    public void displayConfetti() {

        konfettiView.build()
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.RED)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                .addSizes(new Size(12, 5f))
                .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                .streamFor(300, 5000L);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Note fromElement = noteArrayList.get(fromPosition);
        noteArrayList.remove(fromPosition);
        noteArrayList.add(toPosition,fromElement);
        notesAdapter.notifyAdapterItemMoved(fromPosition,toPosition);
    }

    @Override
    public void onItemSwiped(int position) {
        noteArrayList.remove(position);
        notesAdapter.notifyAdapterItemRemoved(position);
        Toast.makeText(this, "Item Deleted!", Toast.LENGTH_SHORT).show();
    }
}