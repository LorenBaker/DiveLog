package com.lbconsulting.divelogfirebase.ui.dialogs;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.lbconsulting.divelogfirebase.utils.MyEvents;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.TimeZone;

import timber.log.Timber;


/**
 * Sets the time for dive start and end.
 */
public class dialogDiveLogTimePicker extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {
    private static final String ARG_DIVE_LOG_UID = "diveLogUid";
    private static final String ARG_DIVE_LOG_DIVE_START = "diveLogDiveStart";
    private static final String ARG_DIVE_LOG_DIVE_END = "diveLogDiveEnd";
    private static final String ARG_DIVE_SITE_TIMEZONE_ID = "diveSiteTimezoneID";
    private static final String ARG_IS_START_TIME = "isStartTime";

    private long mDiveLogDiveStart;
    private long mDiveLogDiveEnd;
    private String mDiveLogUid;
    private String mDiveSiteTimezoneID;
    private TimeZone mDiveSiteTimeZone;

    private int mYear;
    private int mMonth;
    private int mDay;

    private boolean mIsStartTime;

    public static dialogDiveLogTimePicker newInstance(@NonNull String diveLogUid, long diveLogDiveStart, long diveLogDiveEnd,
                                                      @NonNull String diveSiteTimeZoneID, boolean isStartTime) {
        dialogDiveLogTimePicker fragment = new dialogDiveLogTimePicker();
        Bundle args = new Bundle();
        args.putString(ARG_DIVE_LOG_UID, diveLogUid);
        args.putLong(ARG_DIVE_LOG_DIVE_START, diveLogDiveStart);
        args.putLong(ARG_DIVE_LOG_DIVE_END, diveLogDiveEnd);
        args.putString(ARG_DIVE_SITE_TIMEZONE_ID, diveSiteTimeZoneID);
        args.putBoolean(ARG_IS_START_TIME, isStartTime);
        fragment.setArguments(args);
        return fragment;
    }

    public dialogDiveLogTimePicker() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Timber.i("onCreate()");

        Bundle args = getArguments();
        if (args.containsKey(ARG_DIVE_LOG_UID)) {
            mDiveLogUid = args.getString(ARG_DIVE_LOG_UID);
            mDiveLogDiveStart = args.getLong(ARG_DIVE_LOG_DIVE_START);
            mDiveLogDiveEnd = args.getLong(ARG_DIVE_LOG_DIVE_END);
            mDiveSiteTimezoneID = args.getString(ARG_DIVE_SITE_TIMEZONE_ID);
            mDiveSiteTimeZone = TimeZone.getTimeZone(mDiveSiteTimezoneID);
            mIsStartTime = args.getBoolean(ARG_IS_START_TIME);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Timber.i("onCreateDialog()");
        // Start by using the current date as the default date in the picker
        final Calendar calDiveStart = Calendar.getInstance(mDiveSiteTimeZone);

        if (mDiveLogDiveStart > 0) {
            calDiveStart.setTimeInMillis(mDiveLogDiveStart);
        }

        mYear = calDiveStart.get(Calendar.YEAR);
        mMonth = calDiveStart.get(Calendar.MONTH);
        mDay = calDiveStart.get(Calendar.DAY_OF_MONTH);
        int hour = calDiveStart.get(Calendar.HOUR_OF_DAY);
        int minute = calDiveStart.get(Calendar.MINUTE);

        if(!mIsStartTime){
            final Calendar calDiveEnd = Calendar.getInstance(mDiveSiteTimeZone);
            calDiveEnd.setTimeInMillis(mDiveLogDiveEnd);
             hour = calDiveEnd.get(Calendar.HOUR_OF_DAY);
             minute = calDiveEnd.get(Calendar.MINUTE);
        }

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeZone(mDiveSiteTimeZone);
        calendar.set(mYear, mMonth, mDay, hourOfDay, minute);
        long diveTime = calendar.getTimeInMillis();

        if (mIsStartTime) {
            EventBus.getDefault().post(new MyEvents.updateDiveStart(mDiveLogUid, diveTime));
        } else {
            EventBus.getDefault().post(new MyEvents.updateDiveEnd(mDiveLogUid, diveTime));
        }
    }
}