package com.example.notesapp;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.text.MessageFormat;
import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder>  {

    private ArrayList<Note> localDataSet;
    private ItemClickListener clickListener;
    private ConfettiDisplayable confettiDisplayable;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private MaterialTextView title, description, workDone;
        private MaterialButton done;
        private MaterialButton increment, decrement;
        private ProgressBar progressBar;
        private LinearLayout linearLayout;
//        private ConstraintLayout constraintLayout;
        private MaterialCardView noteCardView;
        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.titleNoteCard1);
            description = view.findViewById(R.id.DescriptionNoteCard1);
            workDone = view.findViewById(R.id.workDoneCard1);
            done = view.findViewById(R.id.workDoneButtonCard1);
            increment = view.findViewById(R.id.incCard1);
            decrement = view.findViewById(R.id.decCard1);
            progressBar = view.findViewById(R.id.progressBarCard1);
            linearLayout = view.findViewById(R.id.linearLCard1);
            noteCardView = view.findViewById(R.id.NoteCardMaterial);
//            constraintLayout = view.findViewById(R.id.constraintCard1);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick (view, getLayoutPosition());
                }
            });

            increment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getLayoutPosition();
                    Note currentNote = localDataSet.get(pos);
                    boolean changesMade = currentNote.incrementTask();
                    if (changesMade) {
                        getWorkDone().setText(getWorkDoneMessage(currentNote));
                        getProgressBar().setProgress(currentNote.tasksDone);
                    }

                    if(currentNote.hasProgress && currentNote.tasksDone>= currentNote.maxTask){
                        startTaskDoneEvent();
                        currentNote.isDone= true;
                        notifyItemChanged(pos);
                    }


                }
            });
            decrement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getLayoutPosition();
                    Note currentNote = localDataSet.get(pos);
                    if (currentNote.decrementTask())
                        getWorkDone().setText(getWorkDoneMessage(currentNote));
                        getProgressBar().setProgress(currentNote.tasksDone);
                }
            });

            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    int pos = getLayoutPosition();
                    Note currentNote = localDataSet.get(pos);
                    if(!currentNote.isDone){
                        startTaskDoneEvent();
                        if(currentNote.hasProgress){
                            currentNote.tasksDone = currentNote.maxTask;
                        }
                        currentNote.isDone = true;
                        notifyItemChanged(pos);
                    }
                }
            });
        }

        public MaterialTextView getTitle() {
            return title;
        }

        public MaterialTextView getDescription() {
            return description;
        }

        public MaterialTextView getWorkDone() {
            return workDone;
        }

        public MaterialButton getDone() {
            return done;
        }

        public MaterialButton getIncrement() {
            return increment;
        }

        public MaterialButton getDecrement() {
            return decrement;
        }

        public ProgressBar getProgressBar() {
            return progressBar;
        }

        public LinearLayout getLinearLayout() {
            return linearLayout;
        }

        public MaterialCardView getNoteCardView() { return noteCardView; }
    }

    private String getWorkDoneMessage(Note note) {
        return MessageFormat.format("{0}/{1}", note.tasksDone, note.maxTask);
    }


    public NotesAdapter(ItemClickListener itemClickListener, ArrayList<Note> dataSet) {
        localDataSet = dataSet;
        this.clickListener = itemClickListener;
        this.confettiDisplayable = (ConfettiDisplayable)clickListener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.single_note, viewGroup, false);
        return new ViewHolder(view);
    }

    private void startTaskDoneEvent(){
        // TODO this
        confettiDisplayable.displayConfetti();
        //Toast.makeText((AppCompatActivity)clickListener, "task was done", Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        Note note = localDataSet.get(position);
        viewHolder.getTitle().setText(note.title);
        if(note.description==null || note.description.equals("")){
            viewHolder.getDescription().setVisibility(View.GONE);
        }
        else{
            viewHolder.getDescription().setVisibility(View.VISIBLE);
            viewHolder.getDescription().setText(note.description);
        }
        if (note.hasProgress){
            viewHolder.getProgressBar().setVisibility(View.VISIBLE);
            viewHolder.getProgressBar().setMax(note.maxTask);
            viewHolder.getProgressBar().setProgress(note.tasksDone);
            viewHolder.getIncrement().setVisibility(View.VISIBLE);
            viewHolder.getDecrement().setVisibility(View.VISIBLE);
            viewHolder.getWorkDone().setVisibility(View.VISIBLE);
            viewHolder.getWorkDone().setText(getWorkDoneMessage(note));
//            viewHolder.getDecrement().setBackgroundColor(Color.parseColor("#0000ffff" ));
//            viewHolder.getIncrement().setBackgroundColor(Color.parseColor("#0000ffff" ));
            viewHolder.getLinearLayout().setVisibility(View.VISIBLE);
//            viewHolder.getConstraintLayout().setPadding(0,50,0,0);
        }
        else{
            viewHolder.getProgressBar().setVisibility(View.GONE);
            viewHolder.getIncrement().setVisibility(View.GONE);
            viewHolder.getDecrement().setVisibility(View.GONE);
            viewHolder.getWorkDone().setVisibility(View.GONE);
            viewHolder.getLinearLayout().setVisibility(View.GONE);
//            viewHolder.getConstraintLayout().setPadding(0,50,0,50);

        }
        if (note.isTaskCompleted()){
            viewHolder.getDone().setEnabled(false);


//            viewHolder.getDecrement().setImageAlpha(1);
//            viewHolder.getIncrement().setImageAlpha(1);
            viewHolder.getIncrement().setEnabled(false);
            viewHolder.getDecrement().setEnabled(false);
            viewHolder.getDone().setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor((MainActivity)clickListener, R.color.design_default_color_on_secondary)));
            viewHolder.getIncrement().setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor((MainActivity)clickListener, R.color.design_default_color_on_secondary)));
            viewHolder.getDecrement().setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor((MainActivity)clickListener, R.color.design_default_color_on_secondary)));


        }
        else {
//            viewHolder.getDecrement().se
//            viewHolder.getIncrement().setImageAlpha(255);
//            viewHolder.getIncrement().setImageAlpha(255);
            viewHolder.getDone().setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor((MainActivity)clickListener, R.color.design_default_color_secondary)));
            viewHolder.getIncrement().setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor((MainActivity)clickListener, R.color.design_default_color_secondary)));
            viewHolder.getDecrement().setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor((MainActivity)clickListener, R.color.design_default_color_secondary)));

            viewHolder.getDone().setEnabled(true);
            viewHolder.getIncrement().setEnabled(true);
            viewHolder.getDecrement().setEnabled(true);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
    public interface ConfettiDisplayable {
        void displayConfetti();
    }
}
