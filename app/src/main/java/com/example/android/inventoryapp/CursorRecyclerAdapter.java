package com.example.android.inventoryapp;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Dawid on 2017-06-08.
 */

public abstract class CursorRecyclerAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private Cursor cursor;
    private ProductsActivity ac;
    private boolean mDataValid;
    private int mRowIdColumn;
    private DataSetObserver mDataSetObserver;

    public CursorRecyclerAdapter(ProductsActivity context, Cursor c) {
        this.ac = context;
        this.cursor = c;
        this.mDataValid = cursor != null;
        mRowIdColumn = mDataValid ? cursor.getColumnIndex("_id") : -1;
        mDataSetObserver = new NotifyingDataSetObserver();
        if (cursor != null) {
            cursor.registerDataSetObserver(mDataSetObserver);
        }
    }

    public Cursor getCursor() {
        return cursor;
    }

    @Override
    public int getItemCount() {
        if (mDataValid && cursor != null) {
            return cursor.getCount();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        if (mDataValid && cursor != null && cursor.moveToPosition(position)) {
            return cursor.getLong(mRowIdColumn);
        }
        return 0;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    public abstract void onBindViewHolder(VH viewHolder, Cursor cursor);

    @Override
    public void onBindViewHolder(VH holder, int position) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!cursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }

        onBindViewHolder(holder, cursor);
    }

    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == cursor) {
            return null;
        }
        final Cursor oldCursor = cursor;
        if (oldCursor != null && mDataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        cursor = newCursor;
        if (cursor != null) {
            if (mDataSetObserver != null) {
                cursor.registerDataSetObserver(mDataSetObserver);
            }
            mRowIdColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            notifyDataSetChanged();
        } else {
            mRowIdColumn = -1;
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
        return oldCursor;
    }

    private class NotifyingDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
    }
}
