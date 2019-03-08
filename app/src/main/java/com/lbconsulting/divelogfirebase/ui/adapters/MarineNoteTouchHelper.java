package com.lbconsulting.divelogfirebase.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * This call helps implement swipe to delete and move actions
 * source: http://androidessence.com/swipe-to-dismiss-recyclerview-items/
 */

public class MarineNoteTouchHelper extends ItemTouchHelper.SimpleCallback {
    private MarineNotesRecyclerAdapter mMarineNotesAdapter;

    public MarineNoteTouchHelper(MarineNotesRecyclerAdapter marineNotesAdapter) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.mMarineNotesAdapter = marineNotesAdapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        //Not implemented in this app
//        return false;

        mMarineNotesAdapter.swap(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //Remove item
        mMarineNotesAdapter.remove(viewHolder.getAdapterPosition());
    }
}
