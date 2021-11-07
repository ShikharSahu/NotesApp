package com.example.notesapp;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.color.MaterialColors;

import org.jetbrains.annotations.NotNull;

public class NotesAdapterItemTouchHelperSwipeMove extends ItemTouchHelper.Callback {

    private NotesAdapter.ItemTouchHelperAdapter itemTouchHelperAdapter;

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public void clearView(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
//        viewHolder.itemView.setBackgroundColor(ContextCompat.getColor());
        int color = MaterialColors.getColor((MainActivity)itemTouchHelperAdapter, R.attr.colorSurface, Color.BLACK);
        viewHolder.itemView.setBackgroundColor(color);
        // reset Color, back to orignal
    }

    @Override
    public void onSelectedChanged(@Nullable @org.jetbrains.annotations.Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG){
//            int color = MaterialColors.getColor((MainActivity)itemTouchHelperAdapter, R.attr.colorSurface, Color.BLACK);
            viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
        }
    }

    public NotesAdapterItemTouchHelperSwipeMove(NotesAdapter.ItemTouchHelperAdapter itemTouchHelperAdapter) {
        this.itemTouchHelperAdapter = itemTouchHelperAdapter;
    }

    @Override
    public int getMovementFlags(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder) {
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
        itemTouchHelperAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
        itemTouchHelperAdapter.onItemSwiped(viewHolder.getAdapterPosition());
    }
}
