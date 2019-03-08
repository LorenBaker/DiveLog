package com.lbconsulting.divelogfirebase.ui.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filterable;

import com.lbconsulting.divelogfirebase.models.SelectionValue;
import com.lbconsulting.divelogfirebase.utils.MyMethods;
import com.lbconsulting.divelogfirebase.utils.MySettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * An adapter to display SelectionValues
 */
public class SelectionValueAdapter extends ArrayAdapter<SelectionValue> implements Filterable {

    private final List<SelectionValue> mOriginalList;
    private final List<SelectionValue> mFilteredList;
    private final HashMap<String, Integer> mIdMap;

    public SelectionValueAdapter(Context context, int textViewResourceId,
                                 List<SelectionValue> selectionValues) {
        super(context, textViewResourceId, selectionValues);
        mOriginalList = new ArrayList<>();
        mOriginalList.addAll(selectionValues);
        mFilteredList = new ArrayList<>();
        mIdMap = new HashMap<>();
        for (int i = 0; i < selectionValues.size(); ++i) {
            mIdMap.put(selectionValues.get(i).toString(), i);
        }
    }

    public void filter(String filter) {
        clear();
        mIdMap.clear();

        if (!filter.isEmpty()) {
            mFilteredList.clear();
            int index = -1;
            for (SelectionValue selectionValue : mOriginalList) {
                String selectionValueString = MyMethods.removePunctuation(selectionValue.toString
                        ());
                String filterString = MyMethods.removePunctuation(filter);
                if (selectionValueString.toLowerCase().contains(filterString.toLowerCase())) {
                    mFilteredList.add(selectionValue);
                    index++;
                    mIdMap.put(selectionValue.toString(), index);
                }
            }
            addAll(mFilteredList);

        } else {
            for (int i = 0; i < mOriginalList.size(); ++i) {
                mIdMap.put(mOriginalList.get(i).toString(), i);
            }
            addAll(mOriginalList);
        }
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        SelectionValue item = getItem(position);
        String itemId = MySettings.NOT_AVAILABLE;
        if (item != null) {
            itemId = item.toString();
        }
        return mIdMap.get(itemId);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
