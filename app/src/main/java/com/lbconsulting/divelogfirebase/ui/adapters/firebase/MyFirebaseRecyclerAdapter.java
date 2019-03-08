package com.lbconsulting.divelogfirebase.ui.adapters.firebase;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by Matteo on 24/08/2015.
 * Source: https://github.com/mmazzarolo/firebase-recyclerview/blob/master/app/src/main/java/com/example/matteo/firebase_recycleview/FirebaseRecyclerAdapter.java
 * Updated on 19/06/2016 following https://firebase.google.com/support/guides/firebase-android.
 * <p>
 * This class is a generic way of backing an Android RecyclerView with a Firebase location.
 * It handles all of the child events at the given Firebase location.
 * It marshals received data into the given class type.
 * Extend this class and provide an implementation of the abstract methods, which will notify when
 * the adapter list changes.
 * <p>
 * This class also simplifies the management of configuration change (e.g.: device rotation)
 * allowing the restore of the list.
 *
 * @param <T> The class type to use as a model for the data contained in the children of the
 *            given Firebase location
 */
public abstract class MyFirebaseRecyclerAdapter<ViewHolder extends RecyclerView.ViewHolder, T>
        extends RecyclerView.Adapter<ViewHolder> {

    private final Query mQuery;
    private final Class<T> mItemClass;

    private final ArrayList<T> mAllItems;
    private final ArrayList<String> mAllKeys;

    private final ArrayList<T> mFilteredItems;
    private final ArrayList<String> mFilteredKeys;

    private final DatabaseReference mNodeInitialNumberOfItems;
    private int mInitialNumberOfItems;
    private boolean mInitialItemsLoaded;
    private int mNumberOfItemsFound;
    private String mPreviousAddedFilteredItemKey;

    private final boolean mIsSortDescending;

//    /**
//     * @param query     The Firebase location to watch for data changes.
//     *                  Can also be a slice of a location, using some combination of
//     *                  <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>.
//     * @param itemClass The class of the items.
//     */
//    public MyFirebaseRecyclerAdapter2(Query query, Class<T> itemClass,
//                                      DatabaseReference nodeInitialNumberOfItems,
//                                      boolean isSortDescending) {
//
//        this(query, itemClass, null, null, nodeInitialNumberOfItems, isSortDescending);
//    }

    //    /**
//     * @param query     The Firebase location to watch for data changes.
//     *                  Can also be a slice of a location, using some combination of
//     *                  <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>.
//     * @param itemClass The class of the items.
//     * @param items     List of items that will load the adapter before starting the listener.
//     *                  Generally null or empty, but this can be useful when dealing with a
//     *                  configuration change (e.g.: reloading the adapter after a device rotation).
//     *                  Be careful: keys must be coherent with this list.
//     * @param keys      List of keys of items that will load the adapter before starting the listener.
//     *                  Generally null or empty, but this can be useful when dealing with a
//     *                  configuration change (e.g.: reloading the adapter after a device rotation).
//     *                  Be careful: items must be coherent with this list.
//     */
    public MyFirebaseRecyclerAdapter(Query query, Class<T> itemClass,
                                     @Nullable DatabaseReference nodeInitialNumberOfItems,
                                     boolean isSortDescending) {
        this.mQuery = query;
//        if (items != null && keys != null) {
//            this.mFilteredItems = items;
//            this.mFilteredKeys = keys;
//        } else {

        mAllItems = new ArrayList<>();
        mAllKeys = new ArrayList<>();

        mFilteredItems = new ArrayList<>();
        mFilteredKeys = new ArrayList<>();
//        }
        this.mItemClass = itemClass;

        mNodeInitialNumberOfItems = nodeInitialNumberOfItems;
        mIsSortDescending = isSortDescending;
        mInitialNumberOfItems = 0;
        mNumberOfItemsFound = 0;
        mInitialItemsLoaded = true;
        mPreviousAddedFilteredItemKey = null;

        if (mNodeInitialNumberOfItems != null) {
            mInitialItemsLoaded = false;
            nodeInitialNumberOfItems.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        mInitialNumberOfItems = dataSnapshot.getValue(Integer.class);
                    } else {
                        onInitialItemsLoaded();
                    }
                    mQuery.addChildEventListener(mListener);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
                }
            });

        } else {
            mQuery.addChildEventListener(mListener);
            onInitialItemsLoaded();
        }
    }

    private final ChildEventListener mListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousItemKey) {
            mNumberOfItemsFound++;
            String key = dataSnapshot.getKey();

            if (!mAllKeys.contains(key)) {
                T item = dataSnapshot.getValue(MyFirebaseRecyclerAdapter.this.mItemClass);
                if (previousItemKey == null) {
                    mAllItems.add(0, item);
                    mAllKeys.add(0, key);
                } else {
                    int previousIndex = mAllKeys.indexOf(previousItemKey);
                    if (mIsSortDescending) {
                        int nextIndex = previousIndex - 1;
                        if (nextIndex < 0) {
                            mAllItems.add(0, item);
                            mAllKeys.add(0, key);
                        } else {
                            mAllItems.add(nextIndex, item);
                            mAllKeys.add(nextIndex, key);
                        }

                    } else {
                        int nextIndex = previousIndex + 1;
                        if (nextIndex == mAllItems.size()) {
                            mAllItems.add(item);
                            mAllKeys.add(key);
                        } else {
                            mAllItems.add(nextIndex, item);
                            mAllKeys.add(nextIndex, key);
                        }
                    }
                }

                if (!mFilteredKeys.contains(key)) {
                    if (okToAddItem(item)) {
                        if (mPreviousAddedFilteredItemKey == null) {
                            mFilteredItems.add(0, item);
                            mFilteredKeys.add(0, key);

                        } else {
                            int previousIndex = mFilteredKeys.indexOf(mPreviousAddedFilteredItemKey);

                            if (mIsSortDescending) {
                                int nextIndex = previousIndex - 1;
                                if (nextIndex < 0) {
                                    mFilteredItems.add(0, item);
                                    mFilteredKeys.add(0, key);
                                } else {
                                    mFilteredItems.add(nextIndex, item);
                                    mFilteredKeys.add(nextIndex, key);
                                }
                            } else {
                                int nextIndex = previousIndex + 1;
                                if (nextIndex == mFilteredItems.size()) {
                                    mFilteredItems.add(item);
                                    mFilteredKeys.add(key);
                                } else {
                                    mFilteredItems.add(nextIndex, item);
                                    mFilteredKeys.add(nextIndex, key);
                                }
                            }
                        }
                        mPreviousAddedFilteredItemKey = key;

                        if (mInitialItemsLoaded) {
                            if (mNodeInitialNumberOfItems != null) {
                                mNodeInitialNumberOfItems.setValue(mNumberOfItemsFound);
                            }
                            int insertedPosition = mFilteredKeys.indexOf(key);
                            notifyItemInserted(insertedPosition);
                            itemAdded(item, key, insertedPosition);
                        }
                    }
                    if (!mInitialItemsLoaded && mNumberOfItemsFound >= mInitialNumberOfItems) {
                        mInitialItemsLoaded = true;
                        notifyDataSetChanged();
                        onInitialItemsLoaded();

                    }
                }
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            String key = dataSnapshot.getKey();

            if (mAllKeys.contains(key)) {

                int index = mAllKeys.indexOf(key);
//                T oldItem = mAllItems.get(index);
                T newItem = dataSnapshot.getValue(MyFirebaseRecyclerAdapter.this.mItemClass);

                mAllItems.set(index, newItem);

                if (mFilteredKeys.contains(key)) {
                    index = mFilteredKeys.indexOf(key);
                    T oldItem = mFilteredItems.get(index);
//                    T newItem = dataSnapshot.getValue(MyFirebaseRecyclerAdapter2.this.mItemClass);

                    mFilteredItems.set(index, newItem);

                    notifyItemChanged(index);
                    itemChanged(oldItem, newItem, key, index);
                }
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String key = dataSnapshot.getKey();

            T item;
            if (mAllKeys.contains(key)) {
                int index = mAllKeys.indexOf(key);
                item = mAllItems.get(index);

                mAllKeys.remove(index);
                mAllItems.remove(index);

                mNumberOfItemsFound--;
                if (mNodeInitialNumberOfItems != null) {
                    mNodeInitialNumberOfItems.setValue(mNumberOfItemsFound);
                }

                if (mFilteredKeys.contains(key)) {
                    index = mFilteredKeys.indexOf(key);
                    mFilteredKeys.remove(index);
                    mFilteredItems.remove(index);

                    notifyItemRemoved(index);
                    itemRemoved(item, key, index);
                }
            }
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String previousItemKey) {
            // Think that the code below works ... but has not been tested.
            // Not need for the dive log app
/*            String key = dataSnapshot.getKey();
            int nextIndex = 0;

            if (mAllKeys.contains(key)) {
                int index = mAllKeys.indexOf(key);
                T item = dataSnapshot.getValue(MyFirebaseRecyclerAdapter2.this.mItemClass);
                mAllItems.remove(index);
                mAllKeys.remove(index);
//                int newPosition;
                int previousAllKeysIndex;
                if (previousItemKey == null) {
                    mAllItems.add(0, item);
                    mAllKeys.add(0, key);
                    previousAllKeysIndex = 0;
                } else {
                    previousAllKeysIndex = mAllKeys.indexOf(previousItemKey);
                    if (mIsSortDescending) {
                        nextIndex = previousAllKeysIndex - 1;
                        if (nextIndex < 0) {
                            mAllItems.add(0, item);
                            mAllKeys.add(0, key);
                        } else {
                            mAllItems.add(nextIndex, item);
                            mAllKeys.add(nextIndex, key);
                        }

                    } else {
                        nextIndex = previousAllKeysIndex + 1;
                        if (nextIndex == mAllItems.size()) {
                            mAllItems.add(item);
                            mAllKeys.add(key);
                        } else {
                            mAllItems.add(nextIndex, item);
                            mAllKeys.add(nextIndex, key);
                        }
                    }
                }

                if (mFilteredKeys.contains(key)) {
                    index = mFilteredKeys.indexOf(key);
//                    T item = dataSnapshot.getValue(MyFirebaseRecyclerAdapter2.this.mItemClass);
                    mFilteredItems.remove(index);
                    mFilteredKeys.remove(index);
                    int newPosition = findNewPosition(nextIndex,
                            mAllKeys, mFilteredKeys, mIsSortDescending);
                    if (newPosition > -1) {
                        mFilteredItems.add(newPosition, item);
                        mFilteredKeys.add(newPosition, key);
                        notifyItemMoved(index, newPosition);
                        itemMoved(item, key, index, newPosition);
                    }
                }
            }*/
        }

        private int findNewPosition(int allKeysNextIndex,
                                    ArrayList<String> mAllKeys, ArrayList<String> mFilteredKeys,
                                    boolean isSortDescending) {

            int newPosition = 0;
            boolean found = false;
            boolean finished = false;
            String key;
            while (!finished) {
                key = mAllKeys.get(allKeysNextIndex);
                if (mFilteredKeys.contains(key)) {
                    newPosition = mFilteredKeys.indexOf(key);
                    found = true;
                    finished = true;
                } else {
                    if (isSortDescending) {
                        allKeysNextIndex++;
                        if (allKeysNextIndex == mAllKeys.size()) {
                            finished = true;
                        }
                    } else {
                        allKeysNextIndex--;
                        if (allKeysNextIndex < 0) {
                            finished = true;
                        }
                    }
                }
            }

            if (!found) {
                Timber.e("findNewPosition(): failed to find new position!");
                newPosition = -1;
            }

            return newPosition;
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Timber.e("onCancelled(): Listen was cancelled, no more updates will occur. Exception: %s.",
                    databaseError.getMessage());
        }

    };

    @Override
    public abstract ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(ViewHolder holder, final int position);

    @Override
    public int getItemCount() {
        return (mFilteredItems != null) ? mFilteredItems.size() : 0;
    }

    /**
     * Clean the adapter.
     * ALWAYS call this method before destroying the adapter to remove the listener.
     */
    public void destroy() {
        mQuery.removeEventListener(mListener);
        mFilteredItems.clear();
        mFilteredKeys.clear();
        mNumberOfItemsFound = 0;
        mInitialItemsLoaded = false;
    }

    // C:\Users\Loren\AppData\Local\Android\sdk\android-platform-tools

    /**
     * Returns the list of items of the adapter: can be useful when dealing with a configuration
     * change (e.g.: a device rotation).
     * Just save this list before destroying the adapter and pass it to the new adapter (in the
     * constructor).
     *
     * @return the list of items of the adapter
     */
    public ArrayList<T> getItems() {
        return mFilteredItems;
    }

    /**
     * Returns the list of keys of the items of the adapter: can be useful when dealing with a
     * configuration change (e.g.: a device rotation).
     * Just save this list before destroying the adapter and pass it to the new adapter (in the
     * constructor).
     *
     * @return the list of keys of the items of the adapter
     */
    public ArrayList<String> getKeys() {
        return mFilteredKeys;
    }

    /**
     * Returns the item in the specified position
     *
     * @param position Position of the item in the adapter
     * @return the item
     */
    public T getItem(int position) {
        return mFilteredItems.get(position);
    }

    /**
     * Returns the position of the item in the adapter
     *
     * @param item Item to be searched
     * @return the position in the adapter if found, -1 otherwise
     */
    public int getPositionForItem(T item) {
        return mFilteredItems != null && mFilteredItems.size() > 0 ? mFilteredItems.indexOf(item) : -1;
    }

    /**
     * Check if the searched item is in the adapter
     *
     * @param item Item to be searched
     * @return true if the item is in the adapter, false otherwise
     */
    public boolean contains(T item) {
        return mFilteredItems != null && mFilteredItems.contains(item);
    }

    /**
     * ABSTRACT METHODS THAT MUST BE IMPLEMENTED BY THE EXTENDING ADAPTER.
     */

    /**
     * Called to filter items added to the adapter
     *
     * @param item proposed item for addition
     * @return true if ok to add item
     */
    protected abstract boolean okToAddItem(T item);

//    /**
//     * @param items presorted items
//     * @return sorted items
//     */
//    protected abstract ArrayList<T> sort(ArrayList<T> items);

    /**
     * Called after all initial items loaded from the firebase database
     */
    protected abstract void onInitialItemsLoaded();


    /**
     * Called after an item has been added to the adapter
     *
     * @param item     Added item
     * @param key      Key of the added item
     * @param position Position of the added item in the adapter
     */
    protected abstract void itemAdded(T item, String key, int position);

    /**
     * Called after an item changed
     *
     * @param oldItem  Old version of the changed item
     * @param newItem  Current version of the changed item
     * @param key      Key of the changed item
     * @param position Position of the changed item in the adapter
     */
    protected abstract void itemChanged(T oldItem, T newItem, String key, int position);

    /**
     * Called after an item has been removed from the adapter
     *
     * @param item     Removed item
     * @param key      Key of the removed item
     * @param position Position of the removed item in the adapter
     */
    protected abstract void itemRemoved(T item, String key, int position);

    /**
     * Called after an item changed position
     *
     * @param item        Moved item
     * @param key         Key of the moved item
     * @param oldPosition Old position of the changed item in the adapter
     * @param newPosition New position of the changed item in the adapter
     */
    protected abstract void itemMoved(T item, String key, int oldPosition, int newPosition);

}