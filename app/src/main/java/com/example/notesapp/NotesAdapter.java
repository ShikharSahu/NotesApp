package com.example.notesapp;

import android.content.res.ColorStateList;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.text.MessageFormat;
import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder>  {

    private final ArrayList<Note> localDataSet;
    private final ItemClickListener clickListener;
    private ItemTouchHelper itemTouchHelper;

    public void notifyAdapterItemInserted(int position) {
        notifyItemInserted(position);
        clickListener.setEmptyViewOnOff();
    }

    public void notifyAdapterItemChanged(int position) {
        notifyItemChanged(position);
        clickListener.setEmptyViewOnOff();
    }

    public void notifyAdapterItemMoved(int to, int from){
        notifyItemMoved(to, from);
    }

    public void notifyAdapterItemRemoved(int position) {
        notifyItemRemoved(position);
        clickListener.setEmptyViewOnOff();
    }

    public void notifyAdapterDataSetChanged() {
        notifyDataSetChanged();
        clickListener.setEmptyViewOnOff();
    }

    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements
        View.OnTouchListener, GestureDetector.OnGestureListener {

        private final MaterialTextView title;
        private final MaterialTextView description;
        private final MaterialTextView workDone;
        private final MaterialButton doneButton;
        private final MaterialButton increment;
        private final MaterialButton decrement;
        private final ProgressBar progressBar;
        private final LinearLayout linearLayout;
        private final ShapeableImageView priorityLabel;
        private final MaterialCardView noteCardView;
        private final GestureDetector gestureDetector;
        private final View completeView;

        public ViewHolder(View view) {
            super(view);
            completeView = view;
            title = view.findViewById(R.id.titleNoteCard1);
            description = view.findViewById(R.id.DescriptionNoteCard1);
            workDone = view.findViewById(R.id.workDoneCard1);
            doneButton = view.findViewById(R.id.workDoneButtonCard1);
            increment = view.findViewById(R.id.incCard1);
            decrement = view.findViewById(R.id.decCard1);
            progressBar = view.findViewById(R.id.progressBarCard1);
            linearLayout = view.findViewById(R.id.linearLCard1);
            noteCardView = view.findViewById(R.id.NoteCardMaterial);
            priorityLabel = view.findViewById(R.id.priorityLabel);

            gestureDetector = new GestureDetector(view.getContext(), this);



            view.setOnTouchListener(this::onTouch);
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    clickListener.onNoteCompleteClick(view, getLayoutPosition());
//                }
//            });


            increment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onIncrementClick(ViewHolder.this, getLayoutPosition());
                }
            });

            decrement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onDecrementClick(ViewHolder.this, getLayoutPosition());
                }
            });


            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClickDone(ViewHolder.this, getLayoutPosition());
                }
            });

        }

        public String getWorkDoneMessage(Note note) {
            return MessageFormat.format("{0}/{1}", note.tasksDone, note.maxTask);
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

        public MaterialButton getDoneButton() {
            return doneButton;
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

        public ShapeableImageView getPriorityLabel() {
            return priorityLabel;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            clickListener.onNoteCompleteClick(completeView, getLayoutPosition());
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            itemTouchHelper.startDrag(this);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            gestureDetector.onTouchEvent(event);
            return true;
        }
    }




    public NotesAdapter(ItemClickListener itemClickListener, ArrayList<Note> dataSet) {
        localDataSet = dataSet;
        this.clickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.single_note, viewGroup, false);
        return new ViewHolder(view);
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
            viewHolder.getWorkDone().setText(viewHolder.getWorkDoneMessage(note));
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
            viewHolder.getDoneButton().setEnabled(false);


//            viewHolder.getDecrement().setImageAlpha(1);
//            viewHolder.getIncrement().setImageAlpha(1);
            viewHolder.getIncrement().setEnabled(false);
            viewHolder.getDecrement().setEnabled(false);
            viewHolder.getDoneButton().setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor((MainActivity)clickListener, R.color.design_default_color_on_secondary)));
            viewHolder.getIncrement().setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor((MainActivity)clickListener, R.color.design_default_color_on_secondary)));
            viewHolder.getDecrement().setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor((MainActivity)clickListener, R.color.design_default_color_on_secondary)));


        }
        else {
//            viewHolder.getDecrement().se
//            viewHolder.getIncrement().setImageAlpha(255);
//            viewHolder.getIncrement().setImageAlpha(255);
            viewHolder.getDoneButton().setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor((MainActivity)clickListener, R.color.design_default_color_secondary)));
            viewHolder.getIncrement().setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor((MainActivity)clickListener, R.color.design_default_color_secondary)));
            viewHolder.getDecrement().setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor((MainActivity)clickListener, R.color.design_default_color_secondary)));

            viewHolder.getDoneButton().setEnabled(true);
            viewHolder.getIncrement().setEnabled(true);
            viewHolder.getDecrement().setEnabled(true);
        }

        clickListener.setPriorityLabelImageView(viewHolder, position);


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public interface ItemTouchHelperAdapter{
        void onItemMove(int fromPosition, int toPosition);
        void onItemSwiped(int position);
    }
    public interface ItemClickListener {
        void onNoteCompleteClick(View view, int position);
        void onIncrementClick(NotesAdapter.ViewHolder currentViewHolderInFocus, int position);
        void onDecrementClick(NotesAdapter.ViewHolder currentViewHolderInFocus, int position);
        void onClickDone(NotesAdapter.ViewHolder currentViewHolderInFocus, int position);
        void setPriorityLabelImageView(NotesAdapter.ViewHolder currentViewHolderInFocus, int position);
        void setEmptyViewOnOff();
    }
    public interface ConfettiDisplayable {
        void displayConfetti();
    }
}
