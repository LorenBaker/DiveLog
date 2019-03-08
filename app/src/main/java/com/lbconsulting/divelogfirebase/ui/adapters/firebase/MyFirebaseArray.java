/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lbconsulting.divelogfirebase.ui.adapters.firebase;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * This class implements an array-like collection on top of a Firebase location.
 */
public class MyFirebaseArray implements ChildEventListener {
    public interface OnChangedListener {
        enum EventType {ADDED, CHANGED, REMOVED, MOVED}

        void onChanged(EventType type, DataSnapshot dataSnapshot, int index, int oldIndex);

        void onInitialItemsLoaded();

        void onCancelled(DatabaseError databaseError);
    }

    private final DatabaseReference mNodeInitialNumberOfItems;
    private final List<DataSnapshot> mSnapshots = new ArrayList<>();
    private final Query mQuery;
    private boolean mInitialItemsLoaded;
    private boolean mNoItemsToLoad;
    private int mTotalNumberOfItems;
    private OnChangedListener mListener;


    public MyFirebaseArray(@NonNull Query query,
                           @Nullable DatabaseReference nodeInitialNumberOfItems) {
        mQuery = query;
        mTotalNumberOfItems = 0;
        mInitialItemsLoaded = true;
        mNoItemsToLoad = false;
        mNodeInitialNumberOfItems = nodeInitialNumberOfItems;

        if (mNodeInitialNumberOfItems != null) {
            mInitialItemsLoaded = false;
            nodeInitialNumberOfItems.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        mTotalNumberOfItems = dataSnapshot.getValue(Integer.class);
                    } else {
                        mInitialItemsLoaded = true;
                        mNoItemsToLoad = true;
                        mListener.onInitialItemsLoaded();
                    }
                    mQuery.addChildEventListener(MyFirebaseArray.this);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
                }
            });

        } else {
            mQuery.addChildEventListener(MyFirebaseArray.this);
            mListener.onInitialItemsLoaded();
        }

    }

    public void cleanup() {
        mSnapshots.clear();
        mQuery.removeEventListener(this);
    }

    public int getCount() {
        return mSnapshots.size();
    }

    public DataSnapshot getItem(int index) {
        return mSnapshots.get(index);
    }

    public int getIndexForKey(String key) {
        int index = 0;
        for (DataSnapshot snapshot : mSnapshots) {
            if (snapshot.getKey().equals(key)) {
                return index;
            } else {
                index++;
            }
        }
        throw new IllegalArgumentException("Key not found");
    }

    public DataSnapshot getDataSnapshotForKey(String key) {
        for (DataSnapshot snapshot : mSnapshots) {
            if (snapshot.getKey().equals(key)) {
                return snapshot;
            }
        }
        return null;
    }

    @Override
    public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
        int index = 0;
        if (previousChildKey != null) {
            index = getIndexForKey(previousChildKey) + 1;
        }

        if (mNoItemsToLoad) {
            mNoItemsToLoad = false;
            mListener.onInitialItemsLoaded();
        }

        if (mInitialItemsLoaded) {
            mSnapshots.add(index, snapshot);
            if (mNodeInitialNumberOfItems != null) {
                mNodeInitialNumberOfItems.setValue(mSnapshots.size());
            }
            notifyChangedListeners(OnChangedListener.EventType.ADDED, snapshot, index);

        } else {
            mSnapshots.add(index, snapshot);
            if (mSnapshots.size() == mTotalNumberOfItems) {
                mInitialItemsLoaded = true;
                if (mNodeInitialNumberOfItems != null) {
                    mNodeInitialNumberOfItems.setValue(mSnapshots.size());
                }
                mListener.onInitialItemsLoaded();
            }
        }

    }

    @Override
    public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {
        if (mInitialItemsLoaded) {
            int index = getIndexForKey(snapshot.getKey());
            mSnapshots.set(index, snapshot);
            notifyChangedListeners(OnChangedListener.EventType.CHANGED, snapshot, index);
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot snapshot) {
        int index = getIndexForKey(snapshot.getKey());
        mSnapshots.remove(index);
        mTotalNumberOfItems = mSnapshots.size();
        if (mNodeInitialNumberOfItems != null) {
            mNodeInitialNumberOfItems.setValue(mTotalNumberOfItems);
        }
        if (index > 0) {
            index = index - 1;
        } else {
            index = 0;
        }
        notifyChangedListeners(OnChangedListener.EventType.REMOVED, snapshot, index);
    }

    @Override
    public void onChildMoved(DataSnapshot snapshot, String previousChildKey) {
        int oldIndex = getIndexForKey(snapshot.getKey());
        mSnapshots.remove(oldIndex);
        int newIndex = previousChildKey == null ? 0 : (getIndexForKey(previousChildKey) + 1);
        mSnapshots.add(newIndex, snapshot);
        notifyChangedListeners(OnChangedListener.EventType.MOVED, snapshot, newIndex, oldIndex);
    }

    @Override
    public void onCancelled(DatabaseError error) {
        notifyCancelledListeners(error);
    }

    public void setOnChangedListener(OnChangedListener listener) {
        mListener = listener;
    }

    private void notifyChangedListeners(OnChangedListener.EventType type, DataSnapshot dataSnapshot, int index) {
        notifyChangedListeners(type, dataSnapshot, index, -1);
    }

    private void notifyChangedListeners(OnChangedListener.EventType type, DataSnapshot dataSnapshot, int index, int oldIndex) {
        if (mListener != null) {
            mListener.onChanged(type, dataSnapshot, index, oldIndex);
        }
    }

    private void notifyCancelledListeners(DatabaseError databaseError) {
        if (mListener != null) {
            mListener.onCancelled(databaseError);
        }
    }
}
