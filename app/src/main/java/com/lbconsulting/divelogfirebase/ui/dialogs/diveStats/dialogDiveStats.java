package com.lbconsulting.divelogfirebase.ui.dialogs.diveStats;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.models.DiveLog;
import com.lbconsulting.divelogfirebase.ui.activities.DiveLogPagerActivity;
import com.lbconsulting.divelogfirebase.ui.dialogs.dialogSelectTimeZone;
import com.lbconsulting.divelogfirebase.utils.MyEvents;
import com.lbconsulting.divelogfirebase.utils.MyMethods;
import com.lbconsulting.divelogfirebase.utils.clsConvert;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.NumberFormat;
import java.util.TimeZone;

import timber.log.Timber;

/**
 * This dialog allows a user to update a dive stats:
 * Start time, bottom time, end time, surface interval, max depth, weight used, and timezone
 */

public class dialogDiveStats extends DialogFragment implements View.OnClickListener {

    public static final String ARG_USER_UID = "argUserUid";
    public static final String ARG_PREVIOUS_DIVE_LOG_JSON = "argPreviousDiveLogJson";
    public static final String ARG_CURRENT_DIVE_LOG_JSON = "argCurrentDiveLogJson";
    public static final String ARG_NEXT_DIVE_LOG_JSON = "argNextDiveLogJson";

    private AlertDialog mDiveStatsDialog;
    private String mUserUid;
    private DiveLog mPreviousDiveLog;
    private DiveLog mCurrentDiveLog;
    private DiveLog mNextDiveLog;
    private String mTitle;

    private Button btnSurfaceInterval;
    private Button btnStartTime;
    private Button btnBottomTime;
    private Button btnEndTime;
    private Button btnMaximumDepth;
    private Button btnWeightUsed;
    private Button btnTimeZone;

    private NumberFormat nf;

    public dialogDiveStats() {
    }

    public static dialogDiveStats newInstance(@NonNull String userUid,
                                              @Nullable DiveLog previousDiveLog,
                                              @NonNull DiveLog currentDiveLog,
                                              @Nullable DiveLog nextDiveLog) {
        dialogDiveStats frag = new dialogDiveStats();
        Bundle args = new Bundle();
        args.putString(ARG_USER_UID, userUid);
        Gson gson = new Gson();

        if (previousDiveLog != null) {
            String previousDiveLogJson = gson.toJson(previousDiveLog);
            args.putString(ARG_PREVIOUS_DIVE_LOG_JSON, previousDiveLogJson);
        }

        String currentDiveLogJson = gson.toJson(currentDiveLog);
        args.putString(ARG_CURRENT_DIVE_LOG_JSON, currentDiveLogJson);

        if (nextDiveLog != null) {
            String nextDiveLogJson = gson.toJson(nextDiveLog);
            args.putString(ARG_NEXT_DIVE_LOG_JSON, nextDiveLogJson);
        }

        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        Gson gson = new Gson();
        if (args.containsKey(ARG_USER_UID)) {
            mUserUid = args.getString(ARG_USER_UID);
        }

        mPreviousDiveLog = null;
        if (args.containsKey(ARG_PREVIOUS_DIVE_LOG_JSON)) {
            String diveLogJson = args.getString(ARG_PREVIOUS_DIVE_LOG_JSON);
            mPreviousDiveLog = gson.fromJson(diveLogJson, DiveLog.class);
        }

        mCurrentDiveLog = null;
        if (args.containsKey(ARG_CURRENT_DIVE_LOG_JSON)) {
            String diveLogJson = args.getString(ARG_CURRENT_DIVE_LOG_JSON);
            mCurrentDiveLog = gson.fromJson(diveLogJson, DiveLog.class);
        }

        mNextDiveLog = null;
        if (args.containsKey(ARG_NEXT_DIVE_LOG_JSON)) {
            String diveLogJson = args.getString(ARG_NEXT_DIVE_LOG_JSON);
            mNextDiveLog = gson.fromJson(diveLogJson, DiveLog.class);
        }

        mTitle = "Set Dive Statistics";
        nf = NumberFormat.getInstance();

        EventBus.getDefault().register(this);

        Timber.i("onCreate(): %s", mCurrentDiveLog.getShortTitle());
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.i("onActivityCreated(): %s", mCurrentDiveLog.getShortTitle());

        mDiveStatsDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button saveButton = mDiveStatsDialog.getButton(Dialog.BUTTON_POSITIVE);
                saveButton.setTextSize(16);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(getActivity(), "Button Save Clicked.", Toast.LENGTH_SHORT).show();
                        DiveLog.save(mUserUid, mCurrentDiveLog);
                        if (mNextDiveLog != null) {
                            DiveLog.save(mUserUid, mNextDiveLog);
                        }
                        dismiss();
                    }
                });

                Button cancelButton = mDiveStatsDialog.getButton(Dialog.BUTTON_NEGATIVE);
                cancelButton.setTextSize(16);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Cancel
                        dismiss();
                    }
                });
            }
        });
    }

    //region  surfaceInterval, diveStart, bottomTime, and diveEnd changes
    @Subscribe
    public void onEvent(MyEvents.updateDiveStart event) {
        if (mCurrentDiveLog != null) {
            if (mCurrentDiveLog.getDiveLogUid().equals(event.getDiveLogUid())) {
                updateDiveStart(event.getDiveStart());
            }
        } else {
            Timber.e("updateDiveStart(): Unable to updateDiveStart. Current diveLog is null! " +
                    "DiveLogUid = %s", event.getDiveLogUid());
        }
    }

    @Subscribe
    public void onEvent(MyEvents.updateDiveEnd event) {
        if (mCurrentDiveLog != null) {
            if (mCurrentDiveLog.getDiveLogUid().equals(event.getDiveLogUid())) {
                updateDiveEnd(event.getDiveEnd());
            }
        } else {
            Timber.e("updateDiveEnd(): Unable to updateDiveEnd. Current diveLog is null! " +
                    "DiveLogUid = %s", event.getDiveLogUid());
        }
    }

    @Subscribe
    public void onEvent(MyEvents.updateActiveDiveLogNumericValues event) {
        if (mCurrentDiveLog.getDiveLogUid().equals(event.getDiveLogUid())) {

            switch (event.getButtonID()) {
                case R.id.btnBottomTime:
                    long bottomTime = Math.round(event.getSelectedValue());
                    updateBottomTime(bottomTime);
                    break;

                case R.id.btnSurfaceInterval:
                    long surfaceInterval = Math.round(event.getSelectedValue());
                    updateSurfaceInterval(surfaceInterval);
                    break;

                case R.id.btnMaximumDepth:
                    mCurrentDiveLog.setMaximumDepthDouble(event.getSelectedValue());
                    populateMaxDepth();
                    break;

                case R.id.btnWeightUsed:
                    mCurrentDiveLog.setWeightUsedDouble(event.getSelectedValue());
                    populateWeightUsed();
                    break;

                default:
                    Timber.e("onEvent(): Unknown ButtonID=%d", event.getButtonID());
            }
        }
    }

    private void updateDiveStart(long diveStart) {
        // A change int the current dive's startTime effects
        // current dive's surface interval, and endTime; and the
        // next dives's surface interval.
        //
        // |--- Previous ----|---- Current ----|------ Next ------|
        // |---SI---|---BT---|---SI---|---BT---|---SI---|---BT----|
        // end      start    end      start    end      start     end

        long currentDiveSurfaceInterval;
        long currentDiveEnd;
        long nextDiveSurfaceInterval;

        // calculate current dive's surface interval and diveEnd, and next dive's surface interval
        if (mPreviousDiveLog != null && mNextDiveLog != null) {
            currentDiveSurfaceInterval = diveStart - mPreviousDiveLog.getDiveEnd();
            currentDiveEnd = diveStart + mCurrentDiveLog.getBottomTime();
            nextDiveSurfaceInterval = mNextDiveLog.getDiveStart() - currentDiveEnd;

            mCurrentDiveLog.setSurfaceInterval(currentDiveSurfaceInterval);
            mCurrentDiveLog.setDiveStart(diveStart);
            mCurrentDiveLog.setDiveEnd(currentDiveEnd);
            mNextDiveLog.setSurfaceInterval(nextDiveSurfaceInterval);

            // check that the surface intervals are valid
            if (currentDiveSurfaceInterval < 0 || nextDiveSurfaceInterval < 0) {
                mCurrentDiveLog.setSequencingRequired(true);
            }

        } else if (mPreviousDiveLog != null) {
            // We are changing the last dive's startTime
            // which has no effect on the next dive's surface interval
            currentDiveSurfaceInterval = diveStart - mPreviousDiveLog.getDiveEnd();
            currentDiveEnd = diveStart + mCurrentDiveLog.getBottomTime();

            mCurrentDiveLog.setSurfaceInterval(currentDiveSurfaceInterval);
            mCurrentDiveLog.setDiveStart(diveStart);
            mCurrentDiveLog.setDiveEnd(currentDiveEnd);

            // check that the surface interval is valid
            if (currentDiveSurfaceInterval < 0) {
                mCurrentDiveLog.setSequencingRequired(true);
            }

        } else if (mNextDiveLog != null) {
            // We are changing the first dive's startTime
            // which has no effect on the current dive's surface interval
            currentDiveSurfaceInterval = -1;
            currentDiveEnd = diveStart + mCurrentDiveLog.getBottomTime();
            nextDiveSurfaceInterval = mNextDiveLog.getDiveStart() - currentDiveEnd;

            mCurrentDiveLog.setSurfaceInterval(currentDiveSurfaceInterval);
            mCurrentDiveLog.setDiveStart(diveStart);
            mCurrentDiveLog.setDiveEnd(currentDiveEnd);
            mNextDiveLog.setSurfaceInterval(nextDiveSurfaceInterval);

            // check that the surface interval is valid
            if (nextDiveSurfaceInterval < 0) {
                mCurrentDiveLog.setSequencingRequired(true);
            }

        } else {
            // Both previous and next dive logs are null
            // There is only one diveLog
            currentDiveSurfaceInterval = -1;
            currentDiveEnd = diveStart + mCurrentDiveLog.getBottomTime();

            mCurrentDiveLog.setSurfaceInterval(currentDiveSurfaceInterval);
            mCurrentDiveLog.setDiveStart(diveStart);
            mCurrentDiveLog.setDiveEnd(currentDiveEnd);
        }
        populateTimesAndDurations();
    }

    private void updateSurfaceInterval(long currentDiveSurfaceInterval) {
        // A change int the current dive's surface interval effects
        // current dive's startTime, and endTime; and the
        // next dives's surface interval.

        long currentDiveStart;
        long currentDiveEnd;
        long nextDiveSurfaceInterval;

        // calculate current dive's diveStart and diveEnd, and next dive's surface interval
        if (mPreviousDiveLog != null && mNextDiveLog != null) {
            currentDiveStart = mPreviousDiveLog.getDiveEnd() + currentDiveSurfaceInterval;
            currentDiveEnd = currentDiveStart + mCurrentDiveLog.getBottomTime();
            nextDiveSurfaceInterval = mNextDiveLog.getDiveStart() - currentDiveEnd;
            mCurrentDiveLog.setSurfaceInterval(currentDiveSurfaceInterval);
            mCurrentDiveLog.setDiveStart(currentDiveStart);
            mCurrentDiveLog.setDiveEnd(currentDiveEnd);
            mNextDiveLog.setSurfaceInterval(nextDiveSurfaceInterval);

            if (nextDiveSurfaceInterval < 0) {
                mCurrentDiveLog.setSequencingRequired(true);
            }

        } else if (mPreviousDiveLog != null) {
            // We are changing the last dive's surfaceInterval
            // which has no effect on the next dive's surface interval
            currentDiveStart = mPreviousDiveLog.getDiveEnd() + currentDiveSurfaceInterval;
            currentDiveEnd = currentDiveStart + mCurrentDiveLog.getBottomTime();

            mCurrentDiveLog.setSurfaceInterval(currentDiveSurfaceInterval);
            mCurrentDiveLog.setDiveStart(currentDiveStart);
            mCurrentDiveLog.setDiveEnd(currentDiveEnd);

        }
        populateTimesAndDurations();
    }

    private void updateDiveEnd(long diveEnd) {
        // A change int the current dive's endTime effects
        // current dive's bottom time, and the
        // next dives's surface interval.

        long currentDiveBottomTime;
        long nextDiveSurfaceInterval;

        // calculate current dive's bottom time and next dive's surface interval
        if (mNextDiveLog != null) {
            currentDiveBottomTime = diveEnd - mCurrentDiveLog.getDiveStart();
            nextDiveSurfaceInterval = mNextDiveLog.getDiveStart() - diveEnd;

            mCurrentDiveLog.setBottomTime(currentDiveBottomTime);
            mCurrentDiveLog.setDiveEnd(diveEnd);
            mNextDiveLog.setSurfaceInterval(nextDiveSurfaceInterval);

            // Since the dive's bottom time has changed ... sequence the dive logs
            // to update the accumulative bottom time.
            mCurrentDiveLog.setSequencingRequired(true);

        } else {
            // We are changing the last dive's endTime
            currentDiveBottomTime = diveEnd - mCurrentDiveLog.getDiveStart();
            mCurrentDiveLog.setBottomTime(currentDiveBottomTime);
            mCurrentDiveLog.setDiveEnd(diveEnd);

            long accumulatedBottomTimeToDate = mPreviousDiveLog.getAccumulatedBottomTimeToDate()
                    + currentDiveBottomTime;
            mCurrentDiveLog.setAccumulatedBottomTimeToDate(accumulatedBottomTimeToDate);

            // While the bottom time has changed, there are no subsequent diveLogs.
            // So there is no need to sequence the DiveLogs.
        }
        populateTimesAndDurations();
    }

    private void updateBottomTime(long currentDiveBottomTime) {
        // A change int the current dive's bottomTime effects
        // current dive's diveEnd time, and the
        // next dives's surface interval.

        long currentDiveEnd;
        long nextDiveSurfaceInterval;

        // calculate current dive's endTime and next dive's surface interval
        if (mNextDiveLog != null) {
            currentDiveEnd = mCurrentDiveLog.getDiveStart() + currentDiveBottomTime;
            nextDiveSurfaceInterval = mNextDiveLog.getDiveStart() - currentDiveEnd;
            mCurrentDiveLog.setBottomTime(currentDiveBottomTime);
            mCurrentDiveLog.setDiveEnd(currentDiveEnd);
            mNextDiveLog.setSurfaceInterval(nextDiveSurfaceInterval);

            // Since the dive's bottom time has changed ... sequence the dive logs
            // to update the accumulative bottom time.
            mCurrentDiveLog.setSequencingRequired(true);

        } else {
            // We are changing the last dive's bottomTime; calculate its endTime
            currentDiveEnd = mCurrentDiveLog.getDiveStart() + currentDiveBottomTime;
            mCurrentDiveLog.setBottomTime(currentDiveBottomTime);
            mCurrentDiveLog.setDiveEnd(currentDiveEnd);

            long accumulatedBottomTimeToDate;
            if (mPreviousDiveLog != null) {
                accumulatedBottomTimeToDate = mPreviousDiveLog.getAccumulatedBottomTimeToDate()
                        + currentDiveBottomTime;
            } else {
                // There is only one diveLog, so it's both the first and last dive
                accumulatedBottomTimeToDate = currentDiveBottomTime;
            }
            mCurrentDiveLog.setAccumulatedBottomTimeToDate(accumulatedBottomTimeToDate);

            // While the bottom time has changed, there are no subsequent diveLogs.
            // So there is no need to sequence the DiveLogs.
        }

        populateTimesAndDurations();
    }
    //endregion surfaceInterval, diveStart, bottomTime, and diveEnd changes

    @Subscribe
    public void onEvent(MyEvents.setTimeZone event) {
        if (mCurrentDiveLog.getDiveLogUid().equals(event.getDiveLogUid())) {
            mCurrentDiveLog.setDiveSiteTimeZoneID(event.getTimeZoneID());
            populateTimezone();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.i("onResume(): %s", mCurrentDiveLog.getShortTitle());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // inflate the xml layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_dive_stats, null, false);

        // find the dialog's views
        btnSurfaceInterval = (Button) view.findViewById(R.id.btnSurfaceInterval);
        btnStartTime = (Button) view.findViewById(R.id.btnStartTime);
        btnBottomTime = (Button) view.findViewById(R.id.btnBottomTime);
        btnEndTime = (Button) view.findViewById(R.id.btnEndTime);
        btnMaximumDepth = (Button) view.findViewById(R.id.btnMaximumDepth);
        btnWeightUsed = (Button) view.findViewById(R.id.btnWeightUsed);
        btnTimeZone = (Button) view.findViewById(R.id.btnTimeZone);

        populateTimesAndDurations();
        populateMaxDepth();
        populateWeightUsed();
        populateTimezone();

        btnSurfaceInterval.setOnClickListener(this);
        btnStartTime.setOnClickListener(this);
        btnBottomTime.setOnClickListener(this);
        btnEndTime.setOnClickListener(this);
        btnMaximumDepth.setOnClickListener(this);
        btnWeightUsed.setOnClickListener(this);
        btnTimeZone.setOnClickListener(this);

        // build the dialog
        mDiveStatsDialog = new AlertDialog.Builder(getActivity())
                .setTitle(mTitle)
                .setView(view)
                .setPositiveButton(R.string.btnSave_title, null)
                .setNegativeButton(R.string.btnCancel_title, null)
                .create();

        return mDiveStatsDialog;
    }

    private void populateTimesAndDurations() {

        String diveStartTime = MyMethods.getTimeString(mCurrentDiveLog.getDiveStart(),
                mCurrentDiveLog.getDiveSiteTimeZoneID());
        String diveEndTime = MyMethods.getTimeString(mCurrentDiveLog.getDiveEnd(),
                mCurrentDiveLog.getDiveSiteTimeZoneID());
        String currentSI = MyMethods
                .formatMilliseconds(getActivity(), mCurrentDiveLog.getSurfaceInterval(),
                        MyMethods.FORMAT_DURATION_TO_YEARS_DAYS_HOURS_MINUTES);
        String currentBT = MyMethods
                .formatMilliseconds(getActivity(), mCurrentDiveLog.getBottomTime(),
                        MyMethods.FORMAT_DURATION_TO_HOURS_MINUTES);

        btnStartTime.setText(String.format(getString(R.string.prefix_startTime), diveStartTime));
        btnBottomTime.setText(String.format(getString(R.string.prefix_bottomTime), currentBT));
        btnEndTime.setText(String.format(getString(R.string.prefix_endTime), diveEndTime));
        btnSurfaceInterval.setText(String.format(getString(R.string.prefix_surfaceInterval), currentSI));
    }

    private void populateMaxDepth() {
        String suffix = getString(R.string.suffix_ft);
        double value = mCurrentDiveLog.getMaximumDepthDouble();
        if (value > 0) {
            nf.setMinimumIntegerDigits(1);
            nf.setMaximumIntegerDigits(3);
            nf.setMinimumFractionDigits(0);
            nf.setMaximumFractionDigits(0);
            if (!DiveLogPagerActivity.getUserAppSettings().isImperialUnits()) {
                value = clsConvert.feetToMeters(value);
                suffix = getString(R.string.suffix_m);
                nf.setMinimumFractionDigits(1);
                nf.setMaximumFractionDigits(1);
            }
            btnMaximumDepth.setText(String.format(getString(R.string.prefix_maxDepth),
                    nf.format(value), suffix));
        } else {
            btnMaximumDepth.setText(null);
        }
    }

    private void populateWeightUsed() {
        String suffix = getString(R.string.suffix_lbs);
        double value = mCurrentDiveLog.getWeightUsedDouble();
        if (value > 0) {
            nf.setMinimumIntegerDigits(1);
            nf.setMaximumIntegerDigits(3);
            nf.setMinimumFractionDigits(0);
            nf.setMaximumFractionDigits(0);
            if (!DiveLogPagerActivity.getUserAppSettings().isImperialUnits()) {
                value = clsConvert.poundsToKg(value);
                suffix = getString(R.string.suffix_kg);
                nf.setMinimumFractionDigits(1);
                nf.setMaximumFractionDigits(1);
            }
            btnWeightUsed.setText(String.format(getString(R.string.prefix_weight),
                    nf.format(value), suffix));
        } else {
            btnWeightUsed.setText(null);
        }
    }

    private void populateTimezone() {
        TimeZone timeZone = TimeZone.getTimeZone(mCurrentDiveLog.getDiveSiteTimeZoneID());
        String timezoneText = String.format("%s\n%s",
                mCurrentDiveLog.getDiveSiteTimeZoneID(), timeZone.getDisplayName());
        btnTimeZone.setText(timezoneText);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.i("onDestroyView(): %s", mCurrentDiveLog.getShortTitle());
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {

        FragmentManager fm = getActivity().getSupportFragmentManager();
        dialogDiveLogTimePicker diveTimeDialog;
        dialogNumberPicker numberPickerDialog;

        switch (v.getId()) {

            case R.id.btnStartTime:
                diveTimeDialog = dialogDiveLogTimePicker
                        .newInstance(mCurrentDiveLog.getDiveLogUid(),
                                mCurrentDiveLog.getDiveStart(),
                                mCurrentDiveLog.getDiveEnd(),
                                mCurrentDiveLog.getDiveSiteTimeZoneID(), true);
                diveTimeDialog.show(fm, "dialogDiveLogTimePicker");
                break;

            case R.id.btnEndTime:
                diveTimeDialog = dialogDiveLogTimePicker
                        .newInstance(mCurrentDiveLog.getDiveLogUid(),
                                mCurrentDiveLog.getDiveStart(),
                                mCurrentDiveLog.getDiveEnd(),
                                mCurrentDiveLog.getDiveSiteTimeZoneID(), false);
                diveTimeDialog.show(fm, "dialogDiveLogTimePicker");
                break;


            case R.id.btnBottomTime:
                Long bottomTime = mCurrentDiveLog.getBottomTime();
                double bottomTimeDouble = bottomTime.doubleValue();
                numberPickerDialog = dialogNumberPicker
                        .newInstance(mCurrentDiveLog.getDiveLogUid(),
                                R.id.btnBottomTime, bottomTimeDouble,
                                DiveLogPagerActivity.getUserAppSettings().isImperialUnits());
                numberPickerDialog.show(fm, "dialogNumberPicker");
                break;

            case R.id.btnSurfaceInterval:
                Long surfaceInterval = mCurrentDiveLog.getSurfaceInterval();
                double surfaceIntervalDouble = surfaceInterval.doubleValue();
                double surfaceIntervalMinutes = clsConvert.millisToMinutes(surfaceIntervalDouble);
                if (surfaceIntervalMinutes < 1000) {
                    numberPickerDialog = dialogNumberPicker
                            .newInstance(mCurrentDiveLog.getDiveLogUid(),
                                    R.id.btnSurfaceInterval, surfaceIntervalDouble,
                                    DiveLogPagerActivity.getUserAppSettings().isImperialUnits());
                    numberPickerDialog.show(fm, "dialogNumberPicker");

                } else {
                    String title = "Excessive Surface Interval";
                    String msg = "The starting surface interval is too large to set using a " +
                            "minute picker. Try changing a combination of the dive's date and " +
                            "start time.";
                    MyMethods.showOkDialog(getActivity(), title, msg);
                }
                break;

            case R.id.btnMaximumDepth:
                numberPickerDialog = dialogNumberPicker
                        .newInstance(mCurrentDiveLog.getDiveLogUid(),
                                R.id.btnMaximumDepth, mCurrentDiveLog.getMaximumDepthDouble(),
                                DiveLogPagerActivity.getUserAppSettings().isImperialUnits());
                numberPickerDialog.show(fm, "dialogNumberPicker");
                break;


            case R.id.btnWeightUsed:
                numberPickerDialog = dialogNumberPicker
                        .newInstance(mCurrentDiveLog.getDiveLogUid(),
                                R.id.btnWeightUsed, mCurrentDiveLog.getWeightUsedDouble(),
                                DiveLogPagerActivity.getUserAppSettings().isImperialUnits());
                numberPickerDialog.show(fm, "dialogNumberPicker");
                break;


            case R.id.btnTimeZone:
                dialogSelectTimeZone selectTimeZoneDialog = dialogSelectTimeZone
                        .newInstance(mCurrentDiveLog.getDiveLogUid(), mCurrentDiveLog.getDiveSiteTimeZoneID());
                selectTimeZoneDialog.show(fm, "dialogSelectTimeZone");
                break;
        }


    }
}
