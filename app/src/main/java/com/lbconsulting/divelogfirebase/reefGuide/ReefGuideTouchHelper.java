package com.lbconsulting.divelogfirebase.reefGuide;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * This call helps implement swipe to delete
 * source: http://androidessence.com/swipe-to-dismiss-recyclerview-items/
 */

public class ReefGuideTouchHelper extends ItemTouchHelper.SimpleCallback {
    private ReefGuideAdapter mReefGuideAdapter;

    public ReefGuideTouchHelper(ReefGuideAdapter reefGuideAdapter) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.mReefGuideAdapter = reefGuideAdapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        //Not implemented in this app
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //Remove item
        mReefGuideAdapter.remove(viewHolder.getAdapterPosition());
    }
}
