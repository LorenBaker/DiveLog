package com.lbconsulting.divelogfirebase.ui.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.lbconsulting.divelogfirebase.utils.MyEvents;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;

import timber.log.Timber;

/**
 * Sets the dive start date
 */
public class dialogDiveLogDatePicker extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private static final String ARG_DIVE_LOG_UID = "diveLogUid";
    private static final String ARG_DIVE_LOG_START = "diveLogStart";
    private static final String ARG_DIVE_SITE_TIMEZONE_ID = "diveSiteTimezoneID";

    private long mDiveStart;
    private String mDiveLogUid;
    private String mDiveSiteTimezoneID;
    private TimeZone mDiveSiteTimeZone;

    private int mHour;
    private int mMinute;

    public static dialogDiveLogDatePicker newInstance(@NonNull String diveLogUid,
                                                      long diveLogStart,
                                                      @NonNull String diveSiteTimeZoneID) {
        dialogDiveLogDatePicker fragment = new dialogDiveLogDatePicker();
        Bundle args = new Bundle();
        args.putString(ARG_DIVE_LOG_UID, diveLogUid);
        args.putLong(ARG_DIVE_LOG_START, diveLogStart);
        args.putString(ARG_DIVE_SITE_TIMEZONE_ID, diveSiteTimeZoneID);
        fragment.setArguments(args);
        return fragment;
    }

    public dialogDiveLogDatePicker() {
        // Default constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Timber.i("onCreate()");

        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_DIVE_LOG_UID)) {
            mDiveLogUid = args.getString(ARG_DIVE_LOG_UID);
            mDiveStart = args.getLong(ARG_DIVE_LOG_START);
            mDiveSiteTimezoneID = args.getString(ARG_DIVE_SITE_TIMEZONE_ID);
            mDiveSiteTimeZone = TimeZone.getTimeZone(mDiveSiteTimezoneID);
        }
        super.onCreate(savedInstanceState);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Timber.i("onCreateDialog()");

        // Start by using the current date as the default date in the picker
        final Calendar c = Calendar.getInstance(mDiveSiteTimeZone);

        if (mDiveStart > 0) {
            c.setTimeInMillis(mDiveStart);
        }

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(Objects.requireNonNull(getActivity()), this, year, month, day);
    }


    @Override
    public void onResume() {
        Timber.i("onResume()");
        super.onResume();
    }

    @Override
    public void onPause() {
        Timber.i("onPause()");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Timber.i("onDestroy()");
        super.onDestroy();
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeZone(mDiveSiteTimeZone);
        calendar.set(year, month, day, mHour, mMinute);
        long diveStart = calendar.getTimeInMillis();
        EventBus.getDefault().post(new MyEvents.updateDiveDate(mDiveLogUid, diveStart));
    }
}