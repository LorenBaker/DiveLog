package com.lbconsulting.divelogfirebase.models;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.utils.MySettings;

/**
 * Holds Dive Log app settings.
 */
@IgnoreExtraProperties
public class AppSettings {

    public AppSettings() {
        // default constructor
    }

    private static final String NODE_APP_SETTINGS = "appSettings";
    private static final String FIELD_LAST_DIVE_LOG_VIEWED_UID = "lastDiveLogViewedUid";
    private static final String FIELD_IMPERIAL_UNITS = "imperialUnits";
    private static final String FIELD_SORT_DIVE_LOGS_DESCENDING = "sortDiveLogsDescending";
    private static final String FIELD_CREATE_NEW_DIVE_FROM_TODAY = "createNewDiveFromToday";
    private static final String FIELD_REEF_GUIDE_ID = "reefGuideId";

    private static final String DEFAULT_AREA_FILTER = "[Any Area]";
    private static final String DEFAULT_STATE_FILTER = "[Any State]";
    private static final String DEFAULT_COUNTRY_FILTER = "[Any Country]";

    private static final int AREA_STATE_COUNTRY = 1;
    private static final int AREA_STATE = 2;
    private static final int AREA_COUNTRY = 3;
    private static final int AREA = 4;
    private static final int STATE_COUNTRY = 5;
    private static final int STATE = 6;
    private static final int COUNTRY = 7;
    private static final int NO_FILTER = 8;

    private static final DatabaseReference dbReference = FirebaseDatabase.getInstance()
            .getReference();

    private String appSettingsUid;
    private String areaFilter;
    private String countryFilter;
    private boolean createNewDiveFromToday;
    private String displayName;
    private boolean imperialUnits;
    private String lastDiveLogViewedUid;
    private int recyclerViewFirstVisiblePosition;
    private int recyclerViewTop;
    private boolean recyclerViewVisible;
    private int resetViewPagerAdapterValue;
    private boolean sortDiveLogsDescending;
    private String stateFilter;
    private int reefGuideId;


    //<editor-fold desc="Getters and Setters">
    public String getAppSettingsUid() {
        return appSettingsUid;
    }

    public void setAppSettingsUid(String appSettingsUid) {
        this.appSettingsUid = appSettingsUid;
    }

    public String getAreaFilter() {
        return areaFilter;
    }

    public void setAreaFilter(String areaFilter) {
        this.areaFilter = areaFilter;
    }

    public String getCountryFilter() {
        return countryFilter;
    }

    public void setCountryFilter(String countryFilter) {
        this.countryFilter = countryFilter;
    }

    public boolean isCreateNewDiveFromToday() {
        return createNewDiveFromToday;
    }

    public void setCreateNewDiveFromToday(boolean createNewDiveFromToday) {
        this.createNewDiveFromToday = createNewDiveFromToday;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isImperialUnits() {
        return imperialUnits;
    }

    public void setImperialUnits(boolean imperialUnits) {
        this.imperialUnits = imperialUnits;
    }

    public String getLastDiveLogViewedUid() {
        return lastDiveLogViewedUid;
    }

    public void setLastDiveLogViewedUid(String lastDiveLogViewedUid) {
        this.lastDiveLogViewedUid = lastDiveLogViewedUid;
    }

    public int getRecyclerViewFirstVisiblePosition() {
        return recyclerViewFirstVisiblePosition;
    }

    public void setRecyclerViewFirstVisiblePosition(int recyclerViewFirstVisiblePosition) {
        this.recyclerViewFirstVisiblePosition = recyclerViewFirstVisiblePosition;
    }

    public int getRecyclerViewTop() {
        return recyclerViewTop;
    }

    public void setRecyclerViewTop(int recyclerViewTop) {
        this.recyclerViewTop = recyclerViewTop;
    }

    public boolean isRecyclerViewVisible() {
        return recyclerViewVisible;
    }

    public void setRecyclerViewVisible(boolean recyclerViewVisible) {
        this.recyclerViewVisible = recyclerViewVisible;
    }

    public int getResetViewPagerAdapterValue() {
        return resetViewPagerAdapterValue;
    }

    public void setResetViewPagerAdapterValue(int resetViewPagerAdapterValue) {
        this.resetViewPagerAdapterValue = resetViewPagerAdapterValue;
    }

    public boolean isSortDiveLogsDescending() {
        return sortDiveLogsDescending;
    }

    public void setSortDiveLogsDescending(boolean sortDiveLogsDescending) {
        this.sortDiveLogsDescending = sortDiveLogsDescending;
    }

    public String getStateFilter() {
        return stateFilter;
    }

    public void setStateFilter(String stateFilter) {
        this.stateFilter = stateFilter;
    }

    public int getReefGuideId() {
        return reefGuideId;
    }

    public void setReefGuideId(int reefGuideId) {
        this.reefGuideId = reefGuideId;
    }

    @Override
    public String toString() {
        return displayName;
    }
    //</editor-fold> Getters and Setters

    @Exclude
    public static AppSettings getDefaultAppSettings(AppUser appUser) {
        AppSettings defaultAppSettings = new AppSettings();
        defaultAppSettings.setAppSettingsUid(MySettings.NOT_AVAILABLE);

        defaultAppSettings.setDisplayName(appUser.getDisplayName());
        defaultAppSettings.setSortDiveLogsDescending(true);
        defaultAppSettings.setImperialUnits(true);
        defaultAppSettings.setFilter(AppSettings.DEFAULT_AREA_FILTER,
                AppSettings.DEFAULT_STATE_FILTER, AppSettings
                        .DEFAULT_COUNTRY_FILTER);
        defaultAppSettings.setLastDiveLogViewedUid(MySettings.NOT_AVAILABLE);
        defaultAppSettings.setRecyclerViewFirstVisiblePosition(-1);
        defaultAppSettings.setRecyclerViewTop(0);
        defaultAppSettings.setRecyclerViewVisible(true);
        defaultAppSettings.setCreateNewDiveFromToday(true);
        defaultAppSettings.setReefGuideId(ReefGuide.HAWAII);

        return defaultAppSettings;
    }


    //<editor-fold desc="Firebase Helpers">
    public static DatabaseReference nodeUserAppSettings(@NonNull String userUid) {
        return dbReference.child(NODE_APP_SETTINGS).child(userUid);
    }

    private static DatabaseReference nodeUserReefGuideId(@NonNull String userUid) {
        return nodeUserAppSettings(userUid).child(FIELD_REEF_GUIDE_ID);
    }

    public static void save(@NonNull String userUid, @NonNull AppSettings userAppSettings) {
        if (userAppSettings.getAppSettingsUid() == null
                || userAppSettings.getAppSettingsUid().isEmpty()
                || userAppSettings.getAppSettingsUid().equals(MySettings.NOT_AVAILABLE)) {
            String newAppSettingsUid = nodeUserAppSettings(userUid).push().getKey();
            userAppSettings.setAppSettingsUid(newAppSettingsUid);
        }
        nodeUserAppSettings(userUid).setValue(userAppSettings);
    }

    public static void saveReefGuideId(String userUid, int selectedReefGuideId) {
        nodeUserReefGuideId(userUid).setValue(selectedReefGuideId);
    }

    public static void saveIsImperialUnits(@NonNull String userUid, boolean isImperialUnits) {
        nodeUserAppSettings(userUid).child(FIELD_IMPERIAL_UNITS).setValue(isImperialUnits);
    }

    public static void saveLastDiveLogViewedUid(@NonNull String userUid, @NonNull DiveLog diveLog) {
        saveLastDiveLogViewedUid(userUid, diveLog.getDiveLogUid());
    }

    public static void saveLastDiveLogViewedUid(@NonNull String userUid,
                                                @NonNull String diveLogUid) {
        nodeUserAppSettings(userUid).child(FIELD_LAST_DIVE_LOG_VIEWED_UID).setValue(diveLogUid);
    }

    public static void saveSortDiveLogsDescending(@NonNull String userUid, boolean
            isDiveLogsSortedDescending) {
        nodeUserAppSettings(userUid).child(FIELD_SORT_DIVE_LOGS_DESCENDING).setValue
                (isDiveLogsSortedDescending);
    }

    public static void saveCreateNewDiveFromToday(@NonNull String userUid, boolean
            isCreateNewDiveFromToday) {
        nodeUserAppSettings(userUid).child(FIELD_CREATE_NEW_DIVE_FROM_TODAY).setValue
                (isCreateNewDiveFromToday);
    }

    @Exclude
    private void setFilter(@NonNull String areaFilter, @NonNull String stateFilter,
                           @NonNull String countryFilter) {
        this.areaFilter = areaFilter;
        this.stateFilter = stateFilter;
        this.countryFilter = countryFilter;
    }

    public boolean includeDiveLog(DiveLog diveLog) {
        boolean result = false;
        switch (getFilterMethod()) {
            case AREA_STATE_COUNTRY:
                if (diveLog.getArea().equals(getAreaFilter())) {
                    if (diveLog.getState().equals(getStateFilter())) {
                        if (diveLog.getCountry().equals(getCountryFilter())) {
                            result = true;
                        }
                    }
                }
                break;

            case AREA_STATE:
                if (diveLog.getArea().equals(getAreaFilter())) {
                    if (diveLog.getState().equals(getStateFilter())) {
                        result = true;
                    }
                }
                break;

            case AREA_COUNTRY:
                if (diveLog.getArea().equals(getAreaFilter())) {
                    if (diveLog.getCountry().equals(getCountryFilter())) {
                        result = true;
                    }
                }
                break;

            case AREA:
                if (diveLog.getArea().equals(getAreaFilter())) {
                    result = true;
                }
                break;

            case STATE_COUNTRY:
                if (diveLog.getState().equals(getStateFilter())) {
                    if (diveLog.getCountry().equals(getCountryFilter())) {
                        result = true;
                    }
                }
                break;

            case STATE:
                if (diveLog.getState().equals(getStateFilter())) {
                    result = true;
                }
                break;

            case COUNTRY:
                if (diveLog.getCountry().equals(getCountryFilter())) {
                    result = true;
                }
                break;

            case NO_FILTER:
                result = true;
                break;
        }
        return result;
    }

    public boolean includeDiveSite(DiveSite diveSite, int filterMethod) {
        boolean result = false;
        switch (filterMethod) {
            case AREA_STATE_COUNTRY:
                if (diveSite.getArea().equals(getAreaFilter())) {
                    if (diveSite.getState().equals(getStateFilter())) {
                        if (diveSite.getCountry().equals(getCountryFilter())) {
                            result = true;
                        }
                    }
                }
                break;

            case AREA_STATE:
                if (diveSite.getArea().equals(getAreaFilter())) {
                    if (diveSite.getState().equals(getStateFilter())) {
                        result = true;
                    }
                }
                break;

            case AREA_COUNTRY:
                if (diveSite.getArea().equals(getAreaFilter())) {
                    if (diveSite.getCountry().equals(getCountryFilter())) {
                        result = true;
                    }
                }
                break;

            case AREA:
                if (diveSite.getArea().equals(getAreaFilter())) {
                    result = true;
                }
                break;

            case STATE_COUNTRY:
                if (diveSite.getState().equals(getStateFilter())) {
                    if (diveSite.getCountry().equals(getCountryFilter())) {
                        result = true;
                    }
                }
                break;

            case STATE:
                if (diveSite.getState().equals(getStateFilter())) {
                    result = true;
                }
                break;

            case COUNTRY:
                if (diveSite.getCountry().equals(getCountryFilter())) {
                    result = true;
                }
                break;

            case NO_FILTER:
                result = true;
                break;
        }
        return result;
    }

    public int getFilterMethod() {
        int filterMethod;
        if (!getAreaFilter().equals(DEFAULT_AREA_FILTER)) {
            if (!getStateFilter().equals(DEFAULT_STATE_FILTER)) {
                if (!getCountryFilter().equals(DEFAULT_COUNTRY_FILTER)) {
                    filterMethod = AREA_STATE_COUNTRY;
                } else {
                    filterMethod = AREA_STATE;
                }
            } else {
                if (!getCountryFilter().equals(DEFAULT_COUNTRY_FILTER)) {
                    filterMethod = AREA_COUNTRY;
                } else {
                    filterMethod = AREA;
                }
            }
        } else {
            if (!getStateFilter().equals(DEFAULT_STATE_FILTER)) {
                if (!getCountryFilter().equals(DEFAULT_COUNTRY_FILTER)) {
                    filterMethod = STATE_COUNTRY;
                } else {
                    filterMethod = STATE;
                }
            } else {
                if (!getCountryFilter().equals(DEFAULT_COUNTRY_FILTER)) {
                    filterMethod = COUNTRY;
                } else {
                    filterMethod = NO_FILTER;
                }
            }
        }
        return filterMethod;
    }

    public String getFilterMethodDescription(Context context) {
        String description = "";
        switch (getFilterMethod()) {
            case AREA_STATE_COUNTRY:
                description = String.format(context.getString(R.string.filter_triple_values),
                        getAreaFilter(), getStateFilter(), getCountryFilter());
                break;

            case AREA_STATE:
                description = String.format(context.getString(R.string.filter_double_values),
                        getAreaFilter(), getStateFilter());
                break;

            case AREA_COUNTRY:
                description = String.format(context.getString(R.string.filter_double_values),
                        getAreaFilter(), getCountryFilter());
                break;

            case AREA:
                description = String.format(context.getString(R.string.filter_single_value),
                        getAreaFilter());
                break;

            case STATE_COUNTRY:
                description = String.format(context.getString(R.string.filter_double_values),
                        getStateFilter(), getCountryFilter());
                break;

            case STATE:
                description = String.format(context.getString(R.string.filter_single_value),
                        getStateFilter());
                break;

            case COUNTRY:
                description = String.format(context.getString(R.string.filter_single_value),
                        getCountryFilter());
                break;

            case NO_FILTER:
                description = context.getString(R.string.filter_no_value);
                break;
        }

        return description;
    }
    //</editor-fold> Firebase Helpers
}