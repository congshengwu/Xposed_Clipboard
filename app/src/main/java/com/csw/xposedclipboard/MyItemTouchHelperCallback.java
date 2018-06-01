package com.csw.xposedclipboard;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by 丛 on 2018/5/31 0031.
 */
public class MyItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private BaseRecyclerViewAdapter adapter;

    MyItemTouchHelperCallback(BaseRecyclerViewAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;   //只允许从右向左侧滑
        return makeMovementFlags(0, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
         adapter.remove(viewHolder.getAdapterPosition());
    }
}
