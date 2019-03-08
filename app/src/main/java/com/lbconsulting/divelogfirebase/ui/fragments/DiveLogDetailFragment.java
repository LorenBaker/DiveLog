package com.lbconsulting.divelogfirebase.ui.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;
import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.customViews.DiveLogButton;
import com.lbconsulting.divelogfirebase.models.DiveLog;
import com.lbconsulting.divelogfirebase.models.DiveSite;
import com.lbconsulting.divelogfirebase.models.MarineNote;
import com.lbconsulting.divelogfirebase.models.Person;
import com.lbconsulting.divelogfirebase.ui.activities.DiveLogPagerActivity;
import com.lbconsulting.divelogfirebase.ui.activities.MarineNotesActivity;
import com.lbconsulting.divelogfirebase.ui.activities.ReefGuideActivity;
import com.lbconsulting.divelogfirebase.ui.dialogs.dialogDiveLogDatePicker;
import com.lbconsulting.divelogfirebase.ui.dialogs.dialogDiveLogEquipmentPicker;
import com.lbconsulting.divelogfirebase.ui.dialogs.dialogDiveLogNotes;
import com.lbconsulting.divelogfirebase.ui.dialogs.dialogDiveSitePicker;
import com.lbconsulting.divelogfirebase.ui.dialogs.dialogTissueLoading;
import com.lbconsulting.divelogfirebase.ui.dialogs.diveConditions.dialogDiveConditions;
import com.lbconsulting.divelogfirebase.ui.dialogs.diveInfo.dialogDiveInfo;
import com.lbconsulting.divelogfirebase.ui.dialogs.diveStats.dialogDiveStats;
import com.lbconsulting.divelogfirebase.ui.dialogs.people.dialogBuddyMasterCompany;
import com.lbconsulting.divelogfirebase.utils.MyEvents;
import com.lbconsulting.divelogfirebase.utils.MyMethods;
import com.lbconsulting.divelogfirebase.utils.MySettings;
import com.lbconsulting.divelogfirebase.utils.clsConvert;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import timber.log.Timber;

/**
 * A fragment showing Dive Log detail.
 */
public class DiveLogDetailFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_USER_UID = "artUserUid";
    private static final String ARG_CURRENT_DIVE_LOG_JSON = "argCurrentDiveLogJason";

    // Here are character code to these different style of bullets: • = \u2022, ● = \u25CF,
    // ○ = \u25CB, ▪ = \u25AA, ■ = \u25A0, □ = \u25A1, ► = \u25BA
    private static final String BULLET = "\u2022" + " ";

    private final int MAX_GREEN_TISSUE_LOADING = 15;
    private final int MAX_YELLOW_TISSUE_LOADING = 5;
    private final int MAX_RED_TISSUE_LOADING = 20;

    private List<TextView> mTissueLoadingTextViews;

    private DiveLog mPreviousDiveLog;
    private DiveLog mCurrentDiveLog;
    private DiveLog mNextDiveLog;
    private String mUserUid;
    private String mCurrentDiveLogUid;
    private int mDiveRating = 0;

    //region Button Ids
    private static final int[] BUTTON_IDS = {
            R.id.btnDiveDate,
            R.id.btnTissueLoading
    };

    private static final int[] DIVE_LOG_BUTTON_IDS = {
            R.id.btnDiveSite,
            R.id.btnBuddyMasterCompany,
            R.id.btnDiveStats,
            R.id.btnDiveEquipment,
            R.id.btnDiveInfo,
            R.id.btnDiveConditions,
            R.id.btnMarineLifeNotes,
            R.id.btnMarineLife,
            R.id.btnDiveNotes
    };
    //endregion  Button Ids

    private SparseArray<Button> mButtons;
    private SparseArray<DiveLogButton> mDiveLogButtons;

    private ImageButton btnEditDiveRating;
    private ImageButton btnSaveDiveRating;
    private RatingBar rbDiveRating;
    private TextView tvBottomTimeSummary;

    private NumberFormat nf;

    public DiveLogDetailFragment() {
    }

    public static DiveLogDetailFragment newInstance(@NonNull String userUid,
                                                    @NonNull String currentDiveLogJson) {
        DiveLogDetailFragment frag = new DiveLogDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_UID, userUid);
        args.putString(ARG_CURRENT_DIVE_LOG_JSON, currentDiveLogJson);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        Bundle args = getArguments();

        if (args != null
                && args.containsKey(ARG_USER_UID)
                && args.containsKey(ARG_CURRENT_DIVE_LOG_JSON)) {
            mUserUid = args.getString(ARG_USER_UID);
            String currentDiveLogJson = args.getString(ARG_CURRENT_DIVE_LOG_JSON);
            Gson gson = new Gson();
            mCurrentDiveLog = gson.fromJson(currentDiveLogJson, DiveLog.class);
            mCurrentDiveLogUid = mCurrentDiveLog.getDiveLogUid();

            nf = NumberFormat.getInstance();
        }

        Timber.i("onCreate(): %s", mCurrentDiveLog.toString());
    }

    @Subscribe
    public void onEvent(MyEvents.populateDiveLogDetailFragmentUI event) {
        if (event.getDiveLog() == null
                || mCurrentDiveLog.getDiveLogUid().equals(event.getDiveLog().getDiveLogUid())) {
            if (event.getDiveLog() != null) {
                mCurrentDiveLog = event.getDiveLog();
                Timber.i("populateDiveLogDetailFragmentUI(): %s; diveSiteUid: %s",
                        mCurrentDiveLog.toString(), mCurrentDiveLog.getDiveSiteUid());
            }
            populateUI();
        }
    }

    @Subscribe
    public void onEvent(MyEvents.saveDiveRating event) {
        if (mCurrentDiveLog.getDiveLogUid().equals(event.getDiveLog().getDiveLogUid())) {
            if (btnSaveDiveRating.getVisibility() == View.VISIBLE) {
                btnSaveDiveRating.performClick();
            }
        }
    }

    @Subscribe
    public void onEvent(MyEvents.updateDiveDate event) {
        if (mCurrentDiveLog != null) {
            if (mCurrentDiveLog.getDiveLogUid().equals(event.getDiveLogUid())) {
                updateDiveDate(event.getDiveStart());
            }
        } else {
            Timber.e("updateDiveDate(): Unable to updateDiveDate. Current diveLog is null! " +
                    "DiveLogUid = %s", event.getDiveLogUid());
        }
    }

    private void updateDiveDate(long diveStart) {
        // Note: this method is the same as that in dialogDiveStats.updateDiveStart
        // A change in the current dive's startTime effects
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

            // Save changes ... this will also populateUI()
            // via the diveLog listener in the DiveLogPagerAdapter

            // Save mNextDiveLog before saving mCurrentDiveLog
            // because updated data from mNextDiveLog is needed
            // if mCurrentDiveLog.setSequencingRequired
            DiveLog.save(mUserUid, mNextDiveLog);
            DiveLog.save(mUserUid, mCurrentDiveLog);

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

            // Save changes ... this will also populateUI()
            // via the diveLog listener in the DiveLogPagerAdapter
            DiveLog.save(mUserUid, mCurrentDiveLog);

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

            DiveLog.save(mUserUid, mNextDiveLog);
            DiveLog.save(mUserUid, mCurrentDiveLog);

        } else {
            // Both previous and next dive logs are null
            // There is only one diveLog
            currentDiveSurfaceInterval = -1;
            currentDiveEnd = diveStart + mCurrentDiveLog.getBottomTime();

            mCurrentDiveLog.setSurfaceInterval(currentDiveSurfaceInterval);
            mCurrentDiveLog.setDiveStart(diveStart);
            mCurrentDiveLog.setDiveEnd(currentDiveEnd);

            DiveLog.save(mUserUid, mCurrentDiveLog);
        }
    }


    @Subscribe
    public void onEvent(MyEvents.setTimeZone event) {
        if (mCurrentDiveLog.getDiveLogUid().equals(event.getDiveLogUid())) {
            mCurrentDiveLog.setDiveSiteTimeZoneID(event.getTimeZoneID());
            DiveLog.save(mUserUid, mCurrentDiveLog);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dive_log_detail, container, false);

        mButtons = new SparseArray<>();
        for (int id : BUTTON_IDS) {
            Button button = rootView.findViewById(id);
            button.setOnClickListener(this);
            mButtons.put(id, button);
        }

        mDiveLogButtons = new SparseArray<>();
        for (int id : DIVE_LOG_BUTTON_IDS) {
            DiveLogButton button = rootView.findViewById(id);
            button.setOnClickListener(this);
            button.setTitleText(getDiveLogButtonTitle(id));
            mDiveLogButtons.put(id, button);
        }

        //region Tissue loading setup
        LinearLayout llTissueLoading = rootView.findViewById(R.id.llTissueLoading);
        mTissueLoadingTextViews = new ArrayList<>();
        for (int i = 0;
             i < MAX_GREEN_TISSUE_LOADING; i++) {
            final TextView tissueLoadingTextView = new TextView(getActivity());
            TableRow.LayoutParams params = new TableRow
                    .LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
            tissueLoadingTextView.setLayoutParams(params);
            tissueLoadingTextView
                    .setBackgroundColor(ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.DarkGreen));
            mTissueLoadingTextViews.add(tissueLoadingTextView);
            llTissueLoading.addView(tissueLoadingTextView);
        }

        for (int i = MAX_GREEN_TISSUE_LOADING;
             i < MAX_GREEN_TISSUE_LOADING + MAX_YELLOW_TISSUE_LOADING; i++) {
            final TextView tissueLoadingTextView = new TextView(getActivity());
            TableRow.LayoutParams params = new TableRow
                    .LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
            tissueLoadingTextView.setLayoutParams(params);
            tissueLoadingTextView
                    .setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.Yellow));
            mTissueLoadingTextViews.add(tissueLoadingTextView);
            llTissueLoading.addView(tissueLoadingTextView);
        }

        for (int i = MAX_GREEN_TISSUE_LOADING + MAX_YELLOW_TISSUE_LOADING;
             i < MAX_GREEN_TISSUE_LOADING + MAX_YELLOW_TISSUE_LOADING + MAX_RED_TISSUE_LOADING;
             i++) {
            final TextView tissueLoadingTextView = new TextView(getActivity());
            TableRow.LayoutParams params = new TableRow
                    .LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
            tissueLoadingTextView.setLayoutParams(params);
            tissueLoadingTextView
                    .setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.Red));
            mTissueLoadingTextViews.add(tissueLoadingTextView);
            llTissueLoading.addView(tissueLoadingTextView);
        }
        //endregion Tissue loading setup

        btnEditDiveRating = rootView.findViewById(R.id.btnEditDiveRating);
        btnSaveDiveRating = rootView.findViewById(R.id.btnSaveDiveRating);
        btnEditDiveRating.setOnClickListener(this);
        btnSaveDiveRating.setOnClickListener(this);

        rbDiveRating = rootView.findViewById(R.id.rbDiveRating);
        rbDiveRating.setNumStars(5);
        rbDiveRating.setMax(5);
        rbDiveRating.setStepSize(1);
        rbDiveRating.setIsIndicator(true);
        rbDiveRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mDiveRating = Math.round(rating);
            }
        });

        tvBottomTimeSummary = rootView.findViewById(R.id.tvBottomTimeSummary);

        return rootView;
    }

    private String getDiveLogButtonTitle(int diveLogButtonId) {
        String title = "";
        switch (diveLogButtonId) {
            case R.id.btnBuddyMasterCompany:
                title = getString(R.string.btnBuddyMasterCompany_title);
                break;

            case R.id.btnDiveStats:
                title = getString(R.string.btnDiveStats_title);
                break;

            case R.id.btnDiveEquipment:
                title = getString(R.string.btnDiveEquipment_title);
                break;

            case R.id.btnDiveInfo:
                title = getString(R.string.btnDiveInfo_title);
                break;

            case R.id.btnDiveConditions:
                title = getString(R.string.btnDiveConditions_title);
                break;

            case R.id.btnMarineLifeNotes:
                title = getString(R.string.btnMarineLifeNotes_title);
                break;

            case R.id.btnMarineLife:
                title = getString(R.string.btnMarineLife_title);
                break;

            case R.id.btnDiveNotes:
                title = getString(R.string.btnDiveNotes_title);
                break;

            default:
                Timber.e("getDiveLogButtonTitle(): Unknown diveLogButtonId: %d", diveLogButtonId);
        }

        return title;
    }

    @Override
    public void onClick(View view) {
        final FragmentManager fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager();

        switch (view.getId()) {
            case R.id.btnDiveSite:
                dialogDiveSitePicker diveSiteDialog = dialogDiveSitePicker
                        .newInstance(mUserUid, mCurrentDiveLog.getDiveSiteUid());
                diveSiteDialog.show(fm, "diveSiteDialog");
                break;

            case R.id.btnDiveDate:
                dialogDiveLogDatePicker diveDateDialog =
                        dialogDiveLogDatePicker
                                .newInstance(mCurrentDiveLog.getDiveLogUid(),
                                        mCurrentDiveLog.getDiveStart(),
                                        mCurrentDiveLog.getDiveSiteTimeZoneID());
                diveDateDialog.show(fm, "dialogDiveLogDatePicker");
                break;

            case R.id.btnDiveStats:
                dialogDiveStats diveStatsDialog = dialogDiveStats
                        .newInstance(mUserUid, mPreviousDiveLog, mCurrentDiveLog, mNextDiveLog);
                diveStatsDialog.show(fm, "dialogDiveStats");
                break;

            case R.id.btnDiveInfo:
                dialogDiveInfo diveInfoDialog = dialogDiveInfo
                        .newInstance(mUserUid, mCurrentDiveLog);
                diveInfoDialog.show(fm, "dialogDiveInfo");
                break;

            case R.id.btnDiveConditions:
                dialogDiveConditions diveConditionsDialog = dialogDiveConditions
                        .newInstance(mUserUid, mCurrentDiveLog);
                diveConditionsDialog.show(fm, "dialogDiveConditions");
                break;

            case R.id.btnMarineLifeNotes:
                Intent intent = MarineNotesActivity.createIntent(getActivity(), mUserUid,
                        mCurrentDiveLog);
                startActivity(intent);
                break;

            case R.id.btnEditDiveRating:
                rbDiveRating.setIsIndicator(false);
                btnEditDiveRating.setVisibility(View.GONE);
                btnSaveDiveRating.setVisibility(View.VISIBLE);
                break;

            case R.id.btnSaveDiveRating:
                mCurrentDiveLog.setDiveRating(mDiveRating);
                DiveLog.saveDiveRating(mUserUid, mCurrentDiveLog);
                rbDiveRating.setIsIndicator(true);
                btnEditDiveRating.setVisibility(View.VISIBLE);
                btnSaveDiveRating.setVisibility(View.GONE);
                break;

            case R.id.btnTissueLoading:
                dialogTissueLoading tissueLoadingDialog = dialogTissueLoading
                        .newInstance(mUserUid, mCurrentDiveLogUid,
                                mCurrentDiveLog.getTissueLoadingColor(),
                                mCurrentDiveLog.getTissueLoadingValue());
                tissueLoadingDialog.show(getActivity().getSupportFragmentManager(),
                        "dialogTissueLoading");
                break;

            case R.id.btnDiveEquipment:
                dialogDiveLogEquipmentPicker diveLogEquipmentPicker = dialogDiveLogEquipmentPicker
                        .newInstance(getActivity(), mUserUid, mCurrentDiveLogUid,
                                mCurrentDiveLog.getEquipmentList());
                diveLogEquipmentPicker.show(fm, "dialogDiveLogEquipmentPicker");

                break;

            case R.id.btnMarineLife:
                Gson gson = new Gson();
                String userAppSettingsJson = gson.toJson(DiveLogPagerActivity.getUserAppSettings());
                String diveLogJson = gson.toJson(mCurrentDiveLog);
                intent = ReefGuideActivity.createIntent(getActivity(), mUserUid,
                        userAppSettingsJson, diveLogJson);
                startActivity(intent);
                break;

            case R.id.btnDiveNotes:
                dialogDiveLogNotes diveLogNotesDialog = dialogDiveLogNotes
                        .newInstance(mUserUid, mCurrentDiveLogUid, mCurrentDiveLog.getDiveNotes());
                diveLogNotesDialog.show(fm, "dialogDiveLogNotes");
                break;

            case R.id.btnBuddyMasterCompany:
                dialogBuddyMasterCompany buddyMasterCompanyDialog = dialogBuddyMasterCompany
                        .newInstance(mUserUid, mCurrentDiveLog);
                buddyMasterCompanyDialog.show(fm, "dialogBuddyMasterCompany");
                break;

            default:
                String msg = String.format(Locale.getDefault(), "Unknown button Clicked. id = " +
                        "%d", view.getId());
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                break;
        }
    }


//    private void showSelectionValuePickerDialog(final FragmentManager fm,
//                                                final String selectionValueNode,
//                                                String value) {
//        if (!value.equals(MySettings.NOT_AVAILABLE) && !value.startsWith("[")) {
//            SelectionValue.nodeUserSelectionValue(mUserUid,
//                    selectionValueNode, value)
//                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            if (dataSnapshot.getValue() != null) {
//                                SelectionValue selectionValue = dataSnapshot.getValue
//                                        (SelectionValue.class);
//                                dialogSelectionValuesPicker selectionValuePickerDialog =
//                                        dialogSelectionValuesPicker
//                                                .newInstance(mUserUid, mCurrentDiveLogUid, null,
//                                                        selectionValueNode, selectionValue);
//                                selectionValuePickerDialog.show(fm, "selectionValuePickerDialog");
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//                            Timber.e("onCancelled(): DatabaseError: %s.", databaseError
//                                    .getMessage());
//                        }
//                    });
//        } else {
//            dialogSelectionValuesPicker selectionValuePickerDialog = dialogSelectionValuesPicker
//                    .newInstance(mUserUid, mCurrentDiveLogUid, null,
//                            selectionValueNode, null);
//            selectionValuePickerDialog.show(fm, "selectionValuePickerDialog");
//        }
//    }

    //region populateUI Methods
    private void populateUI() {
        populateBottomTimeSummary();
        populateDiveDate();

        populateDiveBuddyMasterCompany();
        populateDiveStats();
        populateDiveInfo();
        populateDiveConditions();

        populateDiveEquipment();
        populateMarineLifeNotes();
        populateDiveNotes();
        populateDiveRating();
        populateDiveSiteName();

        populateMarineLife();
        populateTissueLoading();
    }

    private String getAirTemperature() {
        String airTempText = null;
        String suffix = getString(R.string.degrees_F);
        double value = mCurrentDiveLog.getAirTemperatureDouble();
        if (value > 0) {
            nf.setMinimumIntegerDigits(1);
            nf.setMaximumIntegerDigits(3);
            nf.setMinimumFractionDigits(0);
            nf.setMaximumFractionDigits(0);
            if (!DiveLogPagerActivity.getUserAppSettings().isImperialUnits()) {
                value = clsConvert.fahrenheitToCelsius(value);
                suffix = getString(R.string.degrees_C);
                nf.setMinimumFractionDigits(1);
                nf.setMaximumFractionDigits(1);
            }
            airTempText = String.format(getString(R.string.prefix_airTemp),
                    nf.format(value), suffix);
        }
        return airTempText;
    }

    private void populateBottomTimeSummary() {
        nf.setMinimumIntegerDigits(1);
        nf.setMaximumIntegerDigits(5);
        nf.setMinimumFractionDigits(0);
        nf.setMaximumFractionDigits(0);

        String bottomTimeTotalToDate = MyMethods
                .formatMilliseconds(getActivity(), mCurrentDiveLog.getAccumulatedBottomTimeToDate(),
                        MyMethods.FORMAT_DURATION_TO_HOURS_MINUTES);

        String accumulatedBottomTimeToDate = MyMethods
                .formatMilliseconds(getActivity(), DiveLogPagerActivity
                                .getAccumulatedBottomTimeToDate(),
                        MyMethods.FORMAT_DURATION_TO_HOURS_MINUTES);

        String bottomTimeSummary = String.format(getString(R.string.bottomTimeSummary),
                bottomTimeTotalToDate, accumulatedBottomTimeToDate,
                nf.format(DiveLogPagerActivity
                        .getTotalNumberOfDives()));
        tvBottomTimeSummary.setText(bottomTimeSummary);
    }

    private void populateDiveDate() {
        String diveStartDayDate = MyMethods.getDayDateString(mCurrentDiveLog.getDiveStart(),
                mCurrentDiveLog
                        .getDiveSiteTimeZoneID());
        mButtons.get(R.id.btnDiveDate).setText(diveStartDayDate);
    }

    private void populateDiveStats() {
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

        String diveStartTimeText = String.format(getString(R.string.prefix_startTime),
                diveStartTime);
        String diveSIText = String.format(getString(R.string.prefix_surfaceInterval), currentSI);
        String diveMaxDeptText = getDiveMaxDepth();

        String diveEndTimeText = String.format(getString(R.string.prefix_endTime), diveEndTime);
        String currentBTText = String.format(getString(R.string.prefix_bottomTime), currentBT);
        String diveWeightUsed = getDiveWeight();

        String col1Text = diveSIText + "\n" + currentBTText + "\n" + diveWeightUsed;
        String col2Text = diveStartTimeText + "\n" + diveEndTimeText + "\n" + diveMaxDeptText;

        DiveLogButton button = mDiveLogButtons.get(R.id.btnDiveStats);
        button.setColumn1Text(col1Text);
        button.setColumn2Text(col2Text);
        button.hideColumn3();
    }

    private void populateDiveInfo() {
        String column1Text = getStartingPressure() + "\n"
                + getEndingPressure() + "\n"
                + getAirUsed() + "\n"
                + getDiveTankType();

        String column2Text = getVisibility() + "\n"
                + getAirTemperature() + "\n"
                + getWaterTemperature();

        DiveLogButton button = mDiveLogButtons.get(R.id.btnDiveInfo);
        button.setColumn1Text(column1Text);
        button.setColumn2Text(column2Text);
        button.hideColumn3();
    }

    private void populateDiveConditions() {
        String column1Text = getDiveType() + "\n"
                + getDiveStyle() + "\n"
                + getDiveEntry();

        String column2Text = getWeatherCondition() + "\n"
                + getCurrentCondition() + "\n"
                + getSeaCondition();

        DiveLogButton button = mDiveLogButtons.get(R.id.btnDiveConditions);
        button.setColumn1Text(column1Text);
        button.setColumn2Text(column2Text);
        button.hideColumn3();
    }

    private String getDiveMaxDepth() {
        String maxDepth = "";

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
            maxDepth = String.format(getString(R.string.prefix_maxDepth), nf.format(value), suffix);
        }

        return maxDepth;
    }

    private String getDiveWeight() {
        String diveWeight = "";

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
            diveWeight = String.format(getString(R.string.prefix_weight), nf.format(value), suffix);
        }
        return diveWeight;
    }

    private void populateDiveBuddyMasterCompany() {
        String buddyName = null;
        String masterName = null;
        String companyName = null;

        String buddyPersonUid = mCurrentDiveLog.getDiveBuddyPersonUid();
        String masterPersonUid = mCurrentDiveLog.getDiveMasterPersonUid();
        String companyPersonUid = mCurrentDiveLog.getDiveCompanyPersonUid();

        if (buddyPersonUid != null && !buddyPersonUid.isEmpty()
                && !buddyPersonUid.equals(MySettings.NOT_AVAILABLE)) {
            DataSnapshot diveBuddySnapshot = DiveLogPagerActivity.getPersonSnapshot(buddyPersonUid);
            if (diveBuddySnapshot != null) {
                buddyName = Objects.requireNonNull(diveBuddySnapshot.getValue(Person.class)).getName();
            }
        }

        if (masterPersonUid != null && !masterPersonUid.isEmpty()
                && !masterPersonUid.equals(MySettings.NOT_AVAILABLE)) {
            DataSnapshot diveMasterSnapshot = DiveLogPagerActivity.getPersonSnapshot
                    (masterPersonUid);
            if (diveMasterSnapshot != null) {
                masterName = Objects.requireNonNull(diveMasterSnapshot.getValue(Person.class)).getName();
            }
        }

        if (companyPersonUid != null && !companyPersonUid.isEmpty()
                && !companyPersonUid.equals(MySettings.NOT_AVAILABLE)) {
            DataSnapshot diveCompanySnapshot = DiveLogPagerActivity.getPersonSnapshot
                    (companyPersonUid);
            if (diveCompanySnapshot != null) {
                companyName = Objects.requireNonNull(diveCompanySnapshot.getValue(Person.class)).getName();
            }
        }

        if (buddyName != null) {
            buddyName = String.format(getString(R.string.prefix_buddy), buddyName);
        } else {
            buddyName = String.format(getString(R.string.prefix_buddy), "");
        }

        if (masterName != null) {
            masterName = String.format(getString(R.string.prefix_master), masterName);
        } else {
            masterName = String.format(getString(R.string.prefix_master), "");
        }

        if (companyName != null) {
            companyName = String.format(getString(R.string.prefix_company), companyName);
        } else {
            companyName = String.format(getString(R.string.prefix_company), "");
        }

        mDiveLogButtons.get(R.id.btnBuddyMasterCompany).setColumn1Text(buddyName);
        mDiveLogButtons.get(R.id.btnBuddyMasterCompany).setColumn2Text(masterName);
        mDiveLogButtons.get(R.id.btnBuddyMasterCompany).setColumn3Text(companyName);

    }

    private void populateMarineLifeNotes() {
        DiveLogButton button = mDiveLogButtons.get(R.id.btnMarineLifeNotes);
        if (mCurrentDiveLog.getMarineNotes() == null
                || mCurrentDiveLog.getMarineNotes().size() == 0) {
            button.setColumn1Text(getString(R.string.no_marineLifeNotes_entries));
            button.hideColumn2();
            button.hideColumn3();
            return;
        }
        HashMap<String, MarineNote> marineNotesMap = mCurrentDiveLog.getMarineNotes();
        ArrayList<MarineNote> marineNotes = MarineNote.sort(new ArrayList<>(marineNotesMap.values()));
        StringBuilder value = new StringBuilder(BULLET + marineNotes.get(0).getNote());
        for (int i = 1; i < marineNotes.size(); i++) {
            value.append("\n").append(BULLET).append(marineNotes.get(i).getNote());
        }
        button.setColumn1Text(value.toString());
        button.hideColumn2();
        button.hideColumn3();
    }

    private void populateDiveEquipment() {
        String value = mCurrentDiveLog.getEquipmentList();
        DiveLogButton button = mDiveLogButtons.get(R.id.btnDiveEquipment);
        button.setColumn1Text(value);
        button.hideColumn2();
        button.hideColumn3();
    }

    private void populateDiveNotes() {
        String value = mCurrentDiveLog.getDiveNotes();
        String diveNotesText = getString(R.string.no_dive_notes);
        if (value != null
                && !value.isEmpty()
                && !value.equals(MySettings.NOT_AVAILABLE)
                && !value.startsWith("[")) {
            diveNotesText = value;
        }
        DiveLogButton button = mDiveLogButtons.get(R.id.btnDiveNotes);
        button.setColumn1Text(diveNotesText);
        button.hideColumn2();
        button.hideColumn3();
    }

    private void populateDiveRating() {
        mDiveRating = mCurrentDiveLog.getDiveRating();
        rbDiveRating.setRating(mDiveRating);
    }

    private void populateDiveSiteName() {
        String diveSiteUid = mCurrentDiveLog.getDiveSiteUid();
        String diveSiteButtonTitleText = null;
        String diveSiteButtonColumn1Text = null;

        if (diveSiteUid != null && !diveSiteUid.isEmpty()
                && !diveSiteUid.equals(MySettings.NOT_AVAILABLE)) {

            DataSnapshot dataSnapshot = DiveLogPagerActivity.getDiveSiteSnapshot(diveSiteUid);
            if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                DiveSite diveSite = dataSnapshot.getValue(DiveSite.class);
                if (diveSite != null) {
                    diveSiteButtonTitleText = diveSite.getDiveSiteName();
                    diveSiteButtonColumn1Text = diveSite.getLocationDescription();
                }
            }
        }
        DiveLogButton button = mDiveLogButtons.get(R.id.btnDiveSite);
        button.setTitleHint(getString(R.string.btnDiveSite_hint));
        button.setTitleText(diveSiteButtonTitleText);
        if (diveSiteButtonColumn1Text != null) {
            button.setColumn1Text(diveSiteButtonColumn1Text);
        } else {
            button.hideColumn1();
        }
        button.hideColumn2();
        button.hideColumn3();
    }

    private String getDiveTankType() {
        String tankTypeText = String.format(getString(R.string.prefix_tank), "");
        String value = mCurrentDiveLog.getTankType();
        if (value != null && !value.isEmpty()
                && !value.equals(MySettings.NOT_AVAILABLE) && !value.startsWith("[")) {
            tankTypeText = String.format(getString(R.string.prefix_tank), value);
        }
        return tankTypeText;
    }

    private void populateMarineLife() {
        String value = mCurrentDiveLog.getMarineLife();
        String marineLifeText = getString(R.string.no_marineLife_entries);
        if (value != null
                && !value.isEmpty()
                && !value.equals(MySettings.NOT_AVAILABLE)
                && !value.startsWith("[")) {
            marineLifeText = String.format(getString(R.string.prefix_marineLife), value);
        }

        DiveLogButton button = mDiveLogButtons.get(R.id.btnMarineLife);
        button.setColumn1Text(marineLifeText);
        button.hideColumn2();
        button.hideColumn3();
    }

    private String getStartingPressure() {
        String startingPressureText = String.format(
                getString(R.string.prefix_startingTankPressure), "", "");
        String suffix = getString(R.string.suffix_psi);
        double startingPressure = mCurrentDiveLog.getStartingTankPressureDouble();
        if (startingPressure > 0) {
            nf.setMinimumIntegerDigits(1);
            nf.setMaximumIntegerDigits(4);
            nf.setMinimumFractionDigits(0);
            nf.setMaximumFractionDigits(0);
            if (!DiveLogPagerActivity.getUserAppSettings().isImperialUnits()) {
                startingPressure = clsConvert.psiToBars(startingPressure);
                suffix = getString(R.string.suffix_bars);
                nf.setMinimumFractionDigits(0);
                nf.setMaximumFractionDigits(0);
            }
            startingPressureText = String.format(getString(R.string.prefix_startingTankPressure),
                    nf.format(startingPressure), suffix);
        }
        return startingPressureText;
    }

    private String getEndingPressure() {
        String endingPressureText = String.format(
                getString(R.string.prefix_endingTankPressure), "", "");
        String suffix = getString(R.string.suffix_psi);
        double endingPressure = mCurrentDiveLog.getEndingTankPressureDouble();
        if (endingPressure > 0) {
            nf.setMinimumIntegerDigits(1);
            nf.setMaximumIntegerDigits(4);
            nf.setMinimumFractionDigits(0);
            nf.setMaximumFractionDigits(0);
            if (!DiveLogPagerActivity.getUserAppSettings().isImperialUnits()) {
                endingPressure = clsConvert.psiToBars(endingPressure);
                suffix = getString(R.string.suffix_bars);
                nf.setMinimumFractionDigits(0);
                nf.setMaximumFractionDigits(0);
            }
            endingPressureText = String.format(getString(R.string.prefix_endingTankPressure),
                    nf.format(endingPressure), suffix);
        }
        return endingPressureText;
    }

    private String getAirUsed() {
        String airUsedText = String.format(
                getString(R.string.prefix_airUsed), "", "");
        String suffix = getString(R.string.suffix_psi);
        double airUsed = mCurrentDiveLog.getAirUsedDouble();
        if (airUsed > 0) {
            nf.setMinimumIntegerDigits(1);
            nf.setMaximumIntegerDigits(4);
            nf.setMinimumFractionDigits(0);
            nf.setMaximumFractionDigits(0);
            if (!DiveLogPagerActivity.getUserAppSettings().isImperialUnits()) {
                airUsed = clsConvert.psiToBars(airUsed);
                suffix = getString(R.string.suffix_bars);
                nf.setMinimumFractionDigits(0);
                nf.setMaximumFractionDigits(0);
            }
            airUsedText = String.format(getString(R.string.prefix_airUsed),
                    nf.format(airUsed), suffix);
        }
        return airUsedText;
    }

    private void populateTissueLoading() {
        String tissueLoading = getString(R.string.tissueLoading_none);
        int diveLogTissueLoadingValue = mCurrentDiveLog.getTissueLoadingValue();
        int tissueLoadingIndex = 0;
        if (diveLogTissueLoadingValue > 0) {
            String tissueLoadingNumberString = String.valueOf(diveLogTissueLoadingValue);
            String tissueLoadingColor;
            switch (mCurrentDiveLog.getTissueLoadingColor()) {

                case "Yellow":
                    tissueLoadingColor = getString(R.string.tissueLoading_Yellow);
                    tissueLoadingIndex = MAX_GREEN_TISSUE_LOADING + diveLogTissueLoadingValue;
                    break;

                case "Red":
                    tissueLoadingColor = getString(R.string.tissueLoading_Red);
                    tissueLoadingIndex = MAX_GREEN_TISSUE_LOADING + MAX_YELLOW_TISSUE_LOADING
                            + diveLogTissueLoadingValue;
                    break;

                default:
                    tissueLoadingColor = getString(R.string.tissueLoading_green);
                    tissueLoadingIndex = diveLogTissueLoadingValue;
            }
            tissueLoading = String.format(getString(R.string.tissueLoading),
                    tissueLoadingNumberString, tissueLoadingColor);
        }
        mButtons.get(R.id.btnTissueLoading).setText(tissueLoading);

        // show the tissue loading graph
        for (int i = 0; i < tissueLoadingIndex; i++) {
            mTissueLoadingTextViews.get(i).setVisibility(View.VISIBLE);
        }
        for (int i = tissueLoadingIndex; i < MAX_GREEN_TISSUE_LOADING
                + MAX_YELLOW_TISSUE_LOADING + MAX_RED_TISSUE_LOADING; i++) {
            mTissueLoadingTextViews.get(i).setVisibility(View.INVISIBLE);
        }
    }

    private String getVisibility() {
        String visibilityText = String.format(
                getString(R.string.prefix_visibility), "", "");
        String suffix = getString(R.string.suffix_ft);
        double value = mCurrentDiveLog.getVisibilityDouble();
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
            visibilityText = String.format(getString(R.string.prefix_visibility),
                    nf.format(value), suffix);
        }
        return visibilityText;
    }

    private String getWaterTemperature() {
        String waterTempText = String.format(
                getString(R.string.prefix_waterTemp), "", "");
        String suffix = getString(R.string.degrees_F);
        double value = mCurrentDiveLog.getWaterTemperatureDouble();
        if (value > 0) {
            nf.setMinimumIntegerDigits(1);
            nf.setMaximumIntegerDigits(3);
            nf.setMinimumFractionDigits(0);
            nf.setMaximumFractionDigits(0);
            if (!DiveLogPagerActivity.getUserAppSettings().isImperialUnits()) {
                value = clsConvert.fahrenheitToCelsius(value);
                suffix = getString(R.string.degrees_C);
                nf.setMinimumFractionDigits(1);
                nf.setMaximumFractionDigits(1);
            }
            waterTempText = String.format(getString(R.string.prefix_waterTemp),
                    nf.format(value), suffix);
        }
        return waterTempText;
    }

    private String getCurrentCondition() {
        String currentConditionText = String.format(getString(R.string.prefix_currents), "");
        String value = mCurrentDiveLog.getCurrentCondition();
        if (value != null && !value.isEmpty()
                && !value.equals(MySettings.NOT_AVAILABLE) && !value.startsWith("[")) {
            currentConditionText = value;
        }
        return currentConditionText;
    }

    private String getDiveEntry() {
        String diveEntryText = String.format(getString(R.string.prefix_diveEntry), "");
        String value = mCurrentDiveLog.getDiveEntry();
        if (value != null && !value.isEmpty()
                && !value.equals(MySettings.NOT_AVAILABLE)
                && !value.startsWith("[")) {
            diveEntryText = value;

        }
        return diveEntryText;
    }

    private String getDiveStyle() {
        String diveStyleText = String.format(getString(R.string.prefix_diveStyle), "");
        String value = mCurrentDiveLog.getDiveStyle();
        if (value != null && !value.isEmpty()
                && !value.equals(MySettings.NOT_AVAILABLE)
                && !value.startsWith("[")) {
            diveStyleText = value;

        }
        return diveStyleText;
    }

    private String getDiveType() {
        String diveTypeText = String.format(getString(R.string.prefix_diveType), "");
        String value = mCurrentDiveLog.getDiveType();
        if (value != null && !value.isEmpty()
                && !value.equals(MySettings.NOT_AVAILABLE)
                && !value.startsWith("[")) {
            diveTypeText = value;

        }
        return diveTypeText;
    }

    private String getSeaCondition() {
        String seaConditionText = String.format(getString(R.string.prefix_seas), "");
        String value = mCurrentDiveLog.getSeaCondition();
        if (value != null && !value.isEmpty()
                && !value.equals(MySettings.NOT_AVAILABLE)
                && !value.startsWith("[")) {
            seaConditionText = value;
        }
        return seaConditionText;
    }

    private String getWeatherCondition() {
        String weatherConditionText = String.format(getString(R.string.prefix_weather), "");
        String value = mCurrentDiveLog.getWeatherCondition();
        if (value != null && !value.isEmpty()
                && !value.equals(MySettings.NOT_AVAILABLE) && !value.startsWith("[")) {
//            weatherConditionText = String.format(getString(R.string.prefix_weather), value);
            weatherConditionText = value;
        }
        return weatherConditionText;
    }
    //endregion populateUI Methods

    @Override
    public void onResume() {
        super.onResume();
        Timber.i("onResume(): %s", mCurrentDiveLog.toString());
        new RetrievePreviousAndNextDiveLogs().execute(mCurrentDiveLog);
    }

    @Override
    public void onPause() {
        super.onPause();
        Timber.i("onPause(): %s", mCurrentDiveLog.toString());
        if (btnSaveDiveRating.getVisibility() == View.VISIBLE) {
            btnSaveDiveRating.performClick();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCurrentDiveLog != null) {
            Timber.i("onDestroy(): %s", mCurrentDiveLog.toString());
        } else {
            Timber.d("onDestroy()");
        }
        EventBus.getDefault().unregister(this);
    }

    private class RetrievePreviousAndNextDiveLogs extends AsyncTask<DiveLog, Void,
            ArrayList<DiveLog>> {

        @Override
        protected ArrayList<DiveLog> doInBackground(DiveLog... diveLogs) {
            DiveLog currentDiveLog = diveLogs[0];
            return retrievePreviousAndNextDiveLogs(currentDiveLog);
        }

        @Override
        protected void onPostExecute(ArrayList<DiveLog> diveLogs) {
//            super.onPostExecute(diveLogs);
//            Timber.i("onPostExecute()");
            // if(isAdded()) to prohibit  java.lang.IllegalStateException: Fragment not attached
            // to Activity
            // http://stackoverflow.com/questions/10919240/fragment-myfragment-not-attached-to
            // -activity
            // https://developer.android.com/reference/android/app/Fragment.html#isAdded()
            if (isAdded()) {
                mPreviousDiveLog = diveLogs.get(0);
                mNextDiveLog = diveLogs.get(1);
                populateUI();
            }
        }
    }

    private ArrayList<DiveLog> retrievePreviousAndNextDiveLogs(DiveLog diveLog) {
        DiveLog previousDiveLog = null;
        if (diveLog.getPreviousDiveLogUid() != null
                && !diveLog.getPreviousDiveLogUid().isEmpty()
                && !diveLog.getPreviousDiveLogUid().equals(MySettings.NOT_AVAILABLE)) {

            previousDiveLog = DiveLogPagerActivity.getDiveLog(diveLog.getPreviousDiveLogUid());
        }

        DiveLog nextDiveLog = null;
        if (diveLog.getNextDiveLogUid() != null
                && !diveLog.getNextDiveLogUid().isEmpty()
                && !diveLog.getNextDiveLogUid().equals(MySettings.NOT_AVAILABLE)) {

            nextDiveLog = DiveLogPagerActivity.getDiveLog(diveLog.getNextDiveLogUid());
        }

        ArrayList<DiveLog> result = new ArrayList<>();
        result.add(previousDiveLog);
        result.add(nextDiveLog);

        return result;
    }

}
