package com.lbconsulting.divelogfirebase.models;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.ui.activities.DiveLogPagerActivity;
import com.lbconsulting.divelogfirebase.ui.adapters.firebase.MyFirebaseArray;
import com.lbconsulting.divelogfirebase.ui.dialogs.dialogTissueLoading;
import com.lbconsulting.divelogfirebase.utils.MyEvents;
import com.lbconsulting.divelogfirebase.utils.MyMethods;
import com.lbconsulting.divelogfirebase.utils.MySettings;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * Java object for a diveLog entry.
 */
//@IgnoreExtraProperties
public class DiveLog {

    private static final Comparator<DiveLog> mAscendingStartTime = new Comparator<DiveLog>() {
        public int compare(DiveLog diveLog1, DiveLog diveLog2) {
            Long diveStart1 = diveLog1.getDiveStart();
            Long diveStart2 = diveLog2.getDiveStart();
            return diveStart1.compareTo(diveStart2);
        }
    };

    //<editor-fold desc="Fields">
    private static final String NODE_DIVE_EQUIPMENT = "equipmentList";
    private static final String NODE_DIVE_NOTES = "diveNotes";
    private static final String NODE_MARINE_NOTES = "marineNotes";
    private static final String NODE_DIVE_MARINE_LIFE = "marineLife";
    private static final String NODE_DIVE_LOGS = "diveLogs";
    private static final String NODE_REEF_GUIDE_ITEMS = "reefGuideItems";

    public static final String FIELD_DIVE_SITE_NAME = "diveSiteName";
    public static final String FIELD_DIVE_START = "diveStart";
    public static final String FIELD_REEF_GUIDE_ITEMS = "reefGuideItems";

    private static final String FIELD_AREA = "area";
    private static final String FIELD_STATE = "state";
    private static final String FIELD_COUNTRY = "country";

    private static final String FIELD_CURRENT_CONDITION = "currentCondition";
    private static final String FIELD_DIVE_ENTRY = "diveEntry";
    private static final String FIELD_DIVE_RATING = "diveRating";
    private static final String FIELD_DIVE_STYLE = "diveStyle";
    private static final String FIELD_DIVE_TYPE = "diveType";
    private static final String FIELD_SEA_CONDITION = "seaCondition";
    private static final String FIELD_TANK_TYPE = "tankType";
    private static final String FIELD_WEATHER_CONDITION = "weatherCondition";

    private static final DatabaseReference dbReference = FirebaseDatabase.getInstance()
            .getReference();

    private static final double scaleFactor = 1000000;
    private static UserDiveLogsValueEventListener mUserDiveLogsValueEventListener;


    private Boolean sequencingRequired;

    private long airTemperature;
    private long airUsed;
    private long endingTankPressure;
    private long maximumDepth;
    private long startingTankPressure;
    private long visibility;
    private long waterTemperature;
    private long weightUsed;
    private int diveRating;

    private int diveNumber;
    private int tissueLoadingValue;

    private long accumulatedBottomTimeToDate;
    private long bottomTime;
    private long diveEnd;
    private long diveStart;
    private long surfaceInterval;

    private String diveSiteUid;
    private String diveSiteName;
    private String area;
    private String country;
    private String state;

    private String diveBuddyPersonUid;
    private String diveMasterPersonUid;
    private String diveCompanyPersonUid;

    private String currentCondition;
    private String diveEntry;
    private String diveLogUid;
    private String diveNotes;
    private String divePhotosUrl;
    private String diveSiteTimeZoneID;
    private String diveStyle;
    private String diveType;
    private String equipmentList;
    private String marineLife;
    private String nextDiveLogUid;
    private String previousDiveLogUid;
    private String seaCondition;
    private String tankType;
    private String tissueLoadingColor;
    private String weatherCondition;

    private ArrayList<ReefGuideItem> reefGuideItems;
    private HashMap<String, MarineNote> marineNotes;
    //</editor-fold> Fields


    public DiveLog() {
        // Default constructor.
    }

    public static DiveLog createNextDiveLog(@NonNull String userUid, @Nullable DiveLog lastDiveLog,
                                            @NonNull AppSettings userAppSettings) {
        DiveLog newDiveLog;
        if (lastDiveLog != null) {
            newDiveLog = cloneNewDiveLog(lastDiveLog, userAppSettings);
            if (!userAppSettings.getAreaFilter().startsWith("[")) {
                newDiveLog.setArea(userAppSettings.getAreaFilter());
            }
            if (!userAppSettings.getStateFilter().startsWith("[")) {
                newDiveLog.setState(userAppSettings.getStateFilter());
            }
            if (!userAppSettings.getCountryFilter().startsWith("[")) {
                newDiveLog.setCountry(userAppSettings.getCountryFilter());
            }
            String newDiveLogUid = nodeUserDiveLogs(userUid).push().getKey();
            newDiveLog.setDiveLogUid(newDiveLogUid);
            storeSelectedValues(userUid, newDiveLog);
            storePersons(userUid, newDiveLog);

            // update the previous last diveLog's nextDiveLogUid
            if (lastDiveLog.isSequencingRequired() == null) {
                lastDiveLog.setSequencingRequired(false);
            }
            lastDiveLog.setNextDiveLogUid(newDiveLogUid);
            Timber.i("createNextDiveLog(): %s created.", newDiveLog.toString());
            save(userUid, lastDiveLog);
            save(userUid, newDiveLog);
        } else {
            newDiveLog = getDefaultDiveLog();
            Timber.i("createNextDiveLog(): default diveLog %s created.", newDiveLog.toString());
            save(userUid, newDiveLog);
        }

        return newDiveLog;
    }

    private static void storePersons(String userUid, DiveLog newDiveLog) {
        if (!newDiveLog.getDiveBuddyPersonUid().equals(MySettings.NOT_AVAILABLE)) {
            Person.addBuddy(userUid,
                    newDiveLog.getDiveBuddyPersonUid(),
                    newDiveLog.getDiveLogUid());
        }

        if (!newDiveLog.getDiveMasterPersonUid().equals(MySettings.NOT_AVAILABLE)) {
            Person.addDiveMaster(userUid,
                    newDiveLog.getDiveMasterPersonUid(),
                    newDiveLog.getDiveLogUid());
        }

        if (!newDiveLog.getDiveCompanyPersonUid().equals(MySettings.NOT_AVAILABLE)) {
            Person.addCompany(userUid,
                    newDiveLog.getDiveCompanyPersonUid(),
                    newDiveLog.getDiveLogUid());
        }
    }

    private static void storeSelectedValues(String userUid, DiveLog newDiveLog) {
        if (!newDiveLog.getCurrentCondition().startsWith("[")) {
            SelectionValue.addDiveLogToSelectionValue(userUid,
                    SelectionValue.NODE_CURRENT_VALUES,
                    newDiveLog.getCurrentCondition(),
                    newDiveLog.getDiveLogUid());
        }

        if (!newDiveLog.getDiveEntry().startsWith("[")) {
            SelectionValue.addDiveLogToSelectionValue(userUid,
                    SelectionValue.NODE_DIVE_ENTRY_VALUES,
                    newDiveLog.getDiveEntry(),
                    newDiveLog.getDiveLogUid());
        }

        if (!newDiveLog.getDiveStyle().startsWith("[")) {
            SelectionValue.addDiveLogToSelectionValue(userUid,
                    SelectionValue.NODE_DIVE_STYLE_VALUES,
                    newDiveLog.getDiveStyle(),
                    newDiveLog.getDiveLogUid());
        }

        if (!newDiveLog.getDiveType().startsWith("[")) {
            SelectionValue.addDiveLogToSelectionValue(userUid,
                    SelectionValue.NODE_DIVE_TYPE_VALUES,
                    newDiveLog.getDiveType(),
                    newDiveLog.getDiveLogUid());
        }

        if (!newDiveLog.getSeaCondition().startsWith("[")) {
            SelectionValue.addDiveLogToSelectionValue(userUid,
                    SelectionValue.NODE_SEA_CONDITION_VALUES,
                    newDiveLog.getSeaCondition(),
                    newDiveLog.getDiveLogUid());
        }

        if (!newDiveLog.getTankType().startsWith("[")) {
            SelectionValue.addDiveLogToSelectionValue(userUid,
                    SelectionValue.NODE_DIVE_TANK_VALUES,
                    newDiveLog.getTankType(),
                    newDiveLog.getDiveLogUid());
        }

        if (!newDiveLog.getWeatherCondition().startsWith("[")) {
            SelectionValue.addDiveLogToSelectionValue(userUid,
                    SelectionValue.NODE_WEATHER_CONDITION_VALUES,
                    newDiveLog.getWeatherCondition(),
                    newDiveLog.getDiveLogUid());
        }

    }

    public static void deleteDiveLog(@NonNull final String userUid,
                                     @NonNull DiveLog diveLog) {
        EventBus.getDefault().post(new MyEvents.removeUserAppSettingListener());
        removeDiveLogFromDiveSites(userUid, diveLog);
        removeDiveLogFromPersons(userUid, diveLog);
        removeDiveLogFromSelectedValues(userUid, diveLog);
        remove(userUid, diveLog);
    }

    private static void removeDiveLogFromDiveSites(String userUid, DiveLog diveLog) {
        MyFirebaseArray diveSitesArray = DiveLogPagerActivity.getDiveSitesArray();
        if (diveSitesArray != null) {
            for (int i = 0; i < diveSitesArray.getCount(); i++) {
                DiveSite diveSite = diveSitesArray.getItem(i).getValue(DiveSite.class);
                if (diveSite != null && diveSite.getDiveLogs() != null) {
                    if (diveSite.getDiveLogs().containsKey(diveLog.getDiveLogUid())) {
                        DiveSite.removeDiveLogFromDiveSite(userUid, diveSite.getDiveSiteUid(),
                                diveLog.getDiveLogUid());
                    }
                }
            }
        }
    }

    private static void removeDiveLogFromPersons(String userUid, DiveLog diveLog) {
        MyFirebaseArray personsArray = DiveLogPagerActivity.getPersonsArray();
        if (personsArray != null) {
            for (int i = 0; i < personsArray.getCount(); i++) {
                Person person = personsArray.getItem(i).getValue(Person.class);
                if (person != null) {
                    if (person.getDiveLogsAsBuddy() != null) {
                        if (person.getDiveLogsAsBuddy().containsKey(diveLog.getDiveLogUid())) {
                            Person.removeBuddy(userUid, person.getPersonUid(), diveLog.getDiveLogUid());
                        }
                    }
                    if (person.getDiveLogsAsMaster() != null) {
                        if (person.getDiveLogsAsBuddy().containsKey(diveLog.getDiveLogUid())) {
                            Person.removeDiveMaster(userUid, person.getPersonUid(), diveLog
                                    .getDiveLogUid());
                        }
                    }
                    if (person.getDiveLogsAsCompany() != null) {
                        if (person.getDiveLogsAsBuddy().containsKey(diveLog.getDiveLogUid())) {
                            Person.removeCompany(userUid, person.getPersonUid(), diveLog
                                    .getDiveLogUid());
                        }
                    }
                }
            }
        }
    }

    private static void removeDiveLogFromSelectedValues(String userUid, DiveLog diveLog) {
        SelectionValue.removeDiveLogFromSelectionValues(userUid, SelectionValue.NODE_CURRENT_VALUES,
                diveLog.getDiveLogUid());
        SelectionValue.removeDiveLogFromSelectionValues(userUid, SelectionValue
                        .NODE_DIVE_ENTRY_VALUES,
                diveLog.getDiveLogUid());
        SelectionValue.removeDiveLogFromSelectionValues(userUid, SelectionValue
                        .NODE_DIVE_STYLE_VALUES,
                diveLog.getDiveLogUid());
        SelectionValue.removeDiveLogFromSelectionValues(userUid, SelectionValue
                        .NODE_DIVE_TANK_VALUES,
                diveLog.getDiveLogUid());
        SelectionValue.removeDiveLogFromSelectionValues(userUid, SelectionValue
                        .NODE_DIVE_TYPE_VALUES,
                diveLog.getDiveLogUid());
        SelectionValue.removeDiveLogFromSelectionValues(userUid, SelectionValue
                        .NODE_SEA_CONDITION_VALUES,
                diveLog.getDiveLogUid());
        SelectionValue.removeDiveLogFromSelectionValues(userUid, SelectionValue
                        .NODE_WEATHER_CONDITION_VALUES,
                diveLog.getDiveLogUid());
    }

    private static DiveLog getDefaultDiveLog() {
        DiveLog newDiveLog = new DiveLog();
        newDiveLog.setSequencingRequired(false);
        newDiveLog.setDiveLogUid(MySettings.NOT_AVAILABLE);
        newDiveLog.setDiveNumber(1);

        newDiveLog.setSurfaceInterval(-1);
        newDiveLog.setDiveStart(Calendar.getInstance().getTimeInMillis());
        // Set bottom time to one hour
        newDiveLog.setBottomTime(TimeUnit.HOURS.toMillis(1));
        newDiveLog.setDiveEnd(newDiveLog.getDiveStart() + TimeUnit.HOURS.toMillis(1));
        newDiveLog.setAccumulatedBottomTimeToDate(0);

        newDiveLog.setDiveSiteUid(MySettings.NOT_AVAILABLE);
        newDiveLog.setDiveSiteName(MySettings.NOT_AVAILABLE);
        newDiveLog.setDiveSiteTimeZoneID(Calendar.getInstance().getTimeZone().getID());

        newDiveLog.setMaximumDepth(0L);
        newDiveLog.setNextDiveLogUid(MySettings.NOT_AVAILABLE);
        newDiveLog.setPreviousDiveLogUid(MySettings.NOT_AVAILABLE);

        newDiveLog.setArea(SelectionValue.DEFAULT_AREA);
        newDiveLog.setState(SelectionValue.DEFAULT_STATE);
        newDiveLog.setCountry(SelectionValue.DEFAULT_COUNTRY);

        newDiveLog.setDiveBuddyPersonUid(MySettings.NOT_AVAILABLE);
        newDiveLog.setDiveMasterPersonUid(MySettings.NOT_AVAILABLE);
        newDiveLog.setDiveCompanyPersonUid(MySettings.NOT_AVAILABLE);

        newDiveLog.setTissueLoadingColor(dialogTissueLoading.TISSUE_LOADING_GREEN);
        newDiveLog.setTissueLoadingValue(0);

        newDiveLog.setDiveRating(0);
        newDiveLog.setWeightUsed(-1L);

        newDiveLog.setStartingTankPressure(-1L);
        newDiveLog.setEndingTankPressure(-1L);
        newDiveLog.setAirUsed(-1L);

        newDiveLog.setAirTemperature(-1L);
        newDiveLog.setWaterTemperature(-1L);
        newDiveLog.setVisibility(-1L);

        newDiveLog.setCurrentCondition(SelectionValue.DEFAULT_CURRENT);
        newDiveLog.setDiveEntry(SelectionValue.DEFAULT_DIVE_ENTRY);
        newDiveLog.setDiveStyle(SelectionValue.DEFAULT_DIVE_STYLE);
        newDiveLog.setDiveType(SelectionValue.DEFAULT_DIVE_TYPE);
        newDiveLog.setTankType(SelectionValue.DEFAULT_DIVE_TANK);
        newDiveLog.setSeaCondition(SelectionValue.DEFAULT_SEA_CONDITION);
        newDiveLog.setWeatherCondition(SelectionValue.DEFAULT_WEATHER_CONDITION);

        newDiveLog.setDiveNotes("");
        newDiveLog.setMarineLife(MySettings.NOT_AVAILABLE);
        newDiveLog.setEquipmentList(MySettings.NOT_AVAILABLE);
        newDiveLog.setDivePhotosUrl(MySettings.NOT_AVAILABLE);
        return newDiveLog;
    }

    private static DiveLog cloneNewDiveLog(@NonNull DiveLog lastDiveLog, @NonNull AppSettings
            userAppSettings) {
        DiveLog newDiveLog = new DiveLog();
        newDiveLog.setSequencingRequired(false);
        newDiveLog.setDiveLogUid(MySettings.NOT_AVAILABLE);
        newDiveLog.setDiveNumber(lastDiveLog.getDiveNumber() + 1);

        if (userAppSettings.isCreateNewDiveFromToday()) {
            // Start next dive at today's date
            long now = System.currentTimeMillis();
            newDiveLog.setDiveStart(now);
            newDiveLog.setSurfaceInterval(now - lastDiveLog.getDiveEnd());
            newDiveLog.setDiveSiteTimeZoneID(TimeZone.getDefault().getID());
        } else {
            // Start next dive one hour after the last dive
            newDiveLog.setSurfaceInterval(TimeUnit.HOURS.toMillis(1));
            newDiveLog.setDiveStart(lastDiveLog.getDiveEnd() + TimeUnit.HOURS.toMillis(1));
            newDiveLog.setDiveSiteTimeZoneID(lastDiveLog.getDiveSiteTimeZoneID());
        }
        // Set bottom time to one hour
        newDiveLog.setBottomTime(TimeUnit.HOURS.toMillis(1));
        newDiveLog.setDiveEnd(newDiveLog.getDiveStart() + TimeUnit.HOURS.toMillis(1));
        newDiveLog.setAccumulatedBottomTimeToDate(lastDiveLog.getAccumulatedBottomTimeToDate() +
                TimeUnit.HOURS.toMillis(1));

        newDiveLog.setDiveSiteUid(MySettings.NOT_AVAILABLE);
        newDiveLog.setDiveSiteName(MySettings.NOT_AVAILABLE);

        newDiveLog.setMaximumDepth(0L);
        newDiveLog.setNextDiveLogUid(MySettings.NOT_AVAILABLE);
        newDiveLog.setPreviousDiveLogUid(lastDiveLog.getDiveLogUid());

        newDiveLog.setArea(lastDiveLog.getArea());
        newDiveLog.setState(lastDiveLog.getState());
        newDiveLog.setCountry(lastDiveLog.getCountry());

        newDiveLog.setDiveBuddyPersonUid(lastDiveLog.getDiveBuddyPersonUid());
        newDiveLog.setDiveMasterPersonUid(lastDiveLog.getDiveMasterPersonUid());
        newDiveLog.setDiveCompanyPersonUid(lastDiveLog.getDiveCompanyPersonUid());

        newDiveLog.setTissueLoadingColor(dialogTissueLoading.TISSUE_LOADING_GREEN);
        newDiveLog.setTissueLoadingValue(0);
        newDiveLog.setTankType(lastDiveLog.getTankType());
        newDiveLog.setDiveRating(0);
        newDiveLog.setWeightUsed(lastDiveLog.getWeightUsed());

        newDiveLog.setStartingTankPressure(lastDiveLog.getStartingTankPressure());
        if (lastDiveLog.getStartingTankPressureDouble() > 500d) {
            newDiveLog.setEndingTankPressureDouble(500d);
            newDiveLog.setAirUsedDouble(newDiveLog.getStartingTankPressureDouble() - 500d);
        } else {
            newDiveLog.setStartingTankPressure(0L);
            newDiveLog.setEndingTankPressure(0L);
            newDiveLog.setAirUsed(0L);
        }
        newDiveLog.setAirTemperature(lastDiveLog.getAirTemperature());
        newDiveLog.setWaterTemperature(lastDiveLog.getWaterTemperature());
        newDiveLog.setVisibility(lastDiveLog.getVisibility());

        newDiveLog.setCurrentCondition(lastDiveLog.getCurrentCondition());
        newDiveLog.setDiveEntry(lastDiveLog.getDiveEntry());
        newDiveLog.setDiveStyle(lastDiveLog.getDiveStyle());
        newDiveLog.setDiveType(lastDiveLog.getDiveType());
        newDiveLog.setSeaCondition(lastDiveLog.getSeaCondition());
        newDiveLog.setWeatherCondition(lastDiveLog.getWeatherCondition());

        newDiveLog.setDiveNotes("");
        newDiveLog.setMarineLife(MySettings.NOT_AVAILABLE);
        newDiveLog.setEquipmentList(lastDiveLog.getEquipmentList());
        newDiveLog.setDivePhotosUrl(MySettings.NOT_AVAILABLE);
        return newDiveLog;
    }


    //<editor-fold desc="Getters and Setters">
    public int getDiveNumber() {
        return diveNumber;
    }

    public void setDiveNumber(int diveNumber) {
        this.diveNumber = diveNumber;
    }

    public long getDiveEnd() {
        return diveEnd;
    }

    public void setDiveEnd(long diveEnd) {
        this.diveEnd = diveEnd;
    }

    public long getDiveStart() {
        return diveStart;
    }

    public void setDiveStart(long diveStart) {
        this.diveStart = diveStart;
    }

    public long getAccumulatedBottomTimeToDate() {
        return accumulatedBottomTimeToDate;
    }

    public void setAccumulatedBottomTimeToDate(long accumulatedBottomTimeToDate) {
        this.accumulatedBottomTimeToDate = accumulatedBottomTimeToDate;
    }

    public String getDiveSiteTimeZoneID() {
        return diveSiteTimeZoneID;
    }

    public void setDiveSiteTimeZoneID(@NonNull String diveSiteTimeZoneID) {
        if (diveSiteTimeZoneID.isEmpty()) {
            diveSiteTimeZoneID = MySettings.NOT_AVAILABLE;
        }
        this.diveSiteTimeZoneID = diveSiteTimeZoneID;
    }

    public String getDiveLogUid() {
        return diveLogUid;
    }

    public void setDiveLogUid(@NonNull String diveLogUid) {
        if (diveLogUid.isEmpty()) {
            diveLogUid = MySettings.NOT_AVAILABLE;
        }
        this.diveLogUid = diveLogUid;
    }

    public String getArea() {
        return area;
    }

    public void setArea(@NonNull String area) {
        if (area.isEmpty()) {
            area = MySettings.NOT_AVAILABLE;
        }
        this.area = area;
    }

    public String getState() {
        return state;
    }

    public void setState(@NonNull String state) {
        if (state.isEmpty()) {
            state = MySettings.NOT_AVAILABLE;
        }
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(@NonNull String country) {
        if (country.isEmpty()) {
            country = MySettings.NOT_AVAILABLE;
        }
        this.country = country;
    }

    public String getDiveBuddyPersonUid() {
        return diveBuddyPersonUid;
    }

    public void setDiveBuddyPersonUid(@NonNull String diveBuddyPersonUid) {
        if (diveBuddyPersonUid.isEmpty()) {
            diveBuddyPersonUid = MySettings.NOT_AVAILABLE;
        }
        this.diveBuddyPersonUid = diveBuddyPersonUid;
    }

    public String getDiveMasterPersonUid() {
        return diveMasterPersonUid;
    }

    public void setDiveMasterPersonUid(@NonNull String diveMasterPersonUid) {
        if (diveMasterPersonUid.isEmpty()) {
            diveMasterPersonUid = MySettings.NOT_AVAILABLE;
        }
        this.diveMasterPersonUid = diveMasterPersonUid;
    }

    public String getDiveCompanyPersonUid() {
        return diveCompanyPersonUid;
    }

    public void setDiveCompanyPersonUid(@NonNull String diveCompanyPersonUid) {
        if (diveCompanyPersonUid.isEmpty()) {
            diveCompanyPersonUid = MySettings.NOT_AVAILABLE;
        }
        this.diveCompanyPersonUid = diveCompanyPersonUid;
    }

    public String getTankType() {
        return tankType;
    }

    public void setTankType(@NonNull String tankType) {
        if (tankType.isEmpty()) {
            tankType = MySettings.NOT_AVAILABLE;
        }
        this.tankType = tankType;
    }

    public long getWeightUsed() {
        return weightUsed;
    }

    public void setWeightUsed(long weightUsed) {
        this.weightUsed = weightUsed;
    }

    public long getStartingTankPressure() {
        return startingTankPressure;
    }

    public void setStartingTankPressure(long startingTankPressure) {
        this.startingTankPressure = startingTankPressure;
    }

    public long getAirTemperature() {
        return airTemperature;
    }

    public void setAirTemperature(long airTemperature) {
        this.airTemperature = airTemperature;
    }

    public long getWaterTemperature() {
        return waterTemperature;
    }

    public void setWaterTemperature(long waterTemperature) {
        this.waterTemperature = waterTemperature;
    }

    public long getVisibility() {
        return visibility;
    }

    public void setVisibility(long visibility) {
        this.visibility = visibility;
    }

    public String getCurrentCondition() {
        return currentCondition;
    }

    public void setCurrentCondition(@NonNull String currentCondition) {
        if (currentCondition.isEmpty()) {
            currentCondition = MySettings.NOT_AVAILABLE;
        }
        this.currentCondition = currentCondition;
    }

    public String getDiveEntry() {
        return diveEntry;
    }

    public void setDiveEntry(@NonNull String diveEntry) {
        if (diveEntry.isEmpty()) {
            diveEntry = MySettings.NOT_AVAILABLE;
        }
        this.diveEntry = diveEntry;
    }

    public String getDiveStyle() {
        return diveStyle;
    }

    public void setDiveStyle(@NonNull String diveStyle) {
        if (diveStyle.isEmpty()) {
            diveStyle = MySettings.NOT_AVAILABLE;
        }
        this.diveStyle = diveStyle;
    }

    public String getDiveType() {
        return diveType;
    }

    public void setDiveType(@NonNull String diveType) {
        if (diveType.isEmpty()) {
            diveType = MySettings.NOT_AVAILABLE;
        }
        this.diveType = diveType;
    }

    public String getSeaCondition() {
        return seaCondition;
    }

    public void setSeaCondition(@NonNull String seaCondition) {
        if (seaCondition.isEmpty()) {
            seaCondition = MySettings.NOT_AVAILABLE;
        }
        this.seaCondition = seaCondition;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(@NonNull String weatherCondition) {
        if (weatherCondition.isEmpty()) {
            weatherCondition = MySettings.NOT_AVAILABLE;
        }
        this.weatherCondition = weatherCondition;
    }

    public String getEquipmentList() {
        return equipmentList;
    }

    public void setEquipmentList(@NonNull String equipmentList) {
        if (equipmentList.isEmpty()) {
            equipmentList = MySettings.NOT_AVAILABLE;
        }
        this.equipmentList = equipmentList;
    }

    public long getSurfaceInterval() {
        return surfaceInterval;
    }

    public void setSurfaceInterval(long surfaceInterval) {
        this.surfaceInterval = surfaceInterval;
    }

    public long getBottomTime() {
        return bottomTime;
    }

    public void setBottomTime(long bottomTime) {
        this.bottomTime = bottomTime;
    }

    public String getDiveSiteUid() {
        return diveSiteUid;
    }

    public void setDiveSiteUid(@NonNull String diveSiteUid) {
        if (diveSiteUid.isEmpty()) {
            diveSiteUid = MySettings.NOT_AVAILABLE;
        }
        this.diveSiteUid = diveSiteUid;
    }

    public String getDiveSiteName() {
        return diveSiteName;
    }

    public void setDiveSiteName(String diveSiteName) {
        this.diveSiteName = diveSiteName;
    }

    public long getMaximumDepth() {
        return maximumDepth;
    }

    public void setMaximumDepth(long maximumDepth) {
        this.maximumDepth = maximumDepth;
    }

    public String getNextDiveLogUid() {
        return nextDiveLogUid;
    }

    public void setNextDiveLogUid(@NonNull String nextDiveLogUid) {
        if (nextDiveLogUid.isEmpty()) {
            nextDiveLogUid = MySettings.NOT_AVAILABLE;
        }
        this.nextDiveLogUid = nextDiveLogUid;
    }

    public String getPreviousDiveLogUid() {
        return previousDiveLogUid;
    }

    public void setPreviousDiveLogUid(@NonNull String previousDiveLogUid) {
        if (previousDiveLogUid.isEmpty()) {
            previousDiveLogUid = MySettings.NOT_AVAILABLE;
        }
        this.previousDiveLogUid = previousDiveLogUid;
    }

    public String getTissueLoadingColor() {
        return tissueLoadingColor;
    }

    public void setTissueLoadingColor(@NonNull String tissueLoadingColor) {
        if (tissueLoadingColor.isEmpty()) {
            tissueLoadingColor = MySettings.NOT_AVAILABLE;
        }
        this.tissueLoadingColor = tissueLoadingColor;
    }

    public int getTissueLoadingValue() {
        return tissueLoadingValue;
    }

    public void setTissueLoadingValue(int tissueLoadingValue) {
        this.tissueLoadingValue = tissueLoadingValue;
    }

    public int getDiveRating() {
        return diveRating;
    }

    public void setDiveRating(int diveRating) {
        this.diveRating = diveRating;
    }

    public long getEndingTankPressure() {
        return endingTankPressure;
    }

    public void setEndingTankPressure(long endingTankPressure) {
        this.endingTankPressure = endingTankPressure;
    }

    public long getAirUsed() {
        return airUsed;
    }

    public void setAirUsed(long airUsed) {
        this.airUsed = airUsed;
    }

    public String getDiveNotes() {
        return diveNotes;
    }

    public void setDiveNotes(@NonNull String diveNotes) {
        if (diveNotes.isEmpty()) {
            diveNotes = MySettings.NOT_AVAILABLE;
        }
        this.diveNotes = diveNotes;
    }

    public String getMarineLife() {
        return marineLife;
    }

    public void setMarineLife(@NonNull String marineLife) {
        if (marineLife.isEmpty()) {
            marineLife = MySettings.NOT_AVAILABLE;
        }
        this.marineLife = marineLife;
    }


    public String getDivePhotosUrl() {
        return divePhotosUrl;
    }

    public void setDivePhotosUrl(@NonNull String divePhotosUrl) {
        if (divePhotosUrl.isEmpty()) {
            divePhotosUrl = MySettings.NOT_AVAILABLE;
        }
        this.divePhotosUrl = divePhotosUrl;
    }

    public Boolean isSequencingRequired() {
        return sequencingRequired;
    }

    public void setSequencingRequired(Boolean sequencingRequired) {
        this.sequencingRequired = sequencingRequired;
    }

    public ArrayList<ReefGuideItem> getReefGuideItems() {
        return reefGuideItems;
    }

    public void setReefGuideItems(ArrayList<ReefGuideItem> reefGuideItems) {
        this.reefGuideItems = reefGuideItems;
    }

    public HashMap<String, MarineNote> getMarineNotes() {
        return marineNotes;
    }

    public void setMarineNotes(HashMap<String, MarineNote> marineNotes) {
        this.marineNotes = marineNotes;
    }

    @Override
    public String toString() {
        return "Dive-" + String.valueOf(diveNumber);
    }

    @Exclude
    public String getShortTitle() {
        return String.format(toString() + ": %s", diveSiteName);
    }
    //</editor-fold> Getters and Setters

    //<editor-fold desc="Double conversion Getters and Setters">
    // These conversions are the work around for the Firebase bug
    // where saving one double value without a decimal value results in two change events
    @Exclude
    public double getWeightUsedDouble() {
        return Long.valueOf(weightUsed).doubleValue() / scaleFactor;
    }

    @Exclude
    public void setWeightUsedDouble(double weightUsed) {
        Double result = weightUsed * scaleFactor;
        this.weightUsed = result.longValue();
    }

    @Exclude
    public double getStartingTankPressureDouble() {
        return Long.valueOf(startingTankPressure).doubleValue() / scaleFactor;
    }

    @Exclude
    public void setStartingTankPressureDouble(double startingTankPressure) {
        Double result = startingTankPressure * scaleFactor;
        this.startingTankPressure = result.longValue();
    }

    @Exclude
    public double getAirTemperatureDouble() {
        return Long.valueOf(airTemperature).doubleValue() / scaleFactor;
    }

    @Exclude
    public void setAirTemperatureDouble(double airTemperature) {
        Double result = airTemperature * scaleFactor;
        this.airTemperature = result.longValue();
    }

    @Exclude
    public double getWaterTemperatureDouble() {
        return Long.valueOf(waterTemperature).doubleValue() / scaleFactor;
    }

    @Exclude
    public void setWaterTemperatureDouble(double waterTemperature) {
        Double result = waterTemperature * scaleFactor;
        this.waterTemperature = result.longValue();
    }

    @Exclude
    public double getVisibilityDouble() {
        return Long.valueOf(visibility).doubleValue() / scaleFactor;
    }

    @Exclude
    public void setVisibilityDouble(double visibility) {
        Double result = visibility * scaleFactor;
        this.visibility = result.longValue();
    }

    @Exclude
    public double getMaximumDepthDouble() {
        return Long.valueOf(maximumDepth).doubleValue() / scaleFactor;
    }

    @Exclude
    public void setMaximumDepthDouble(double maximumDepth) {
        Double result = maximumDepth * scaleFactor;
        this.maximumDepth = result.longValue();
    }

    @Exclude
    public double getEndingTankPressureDouble() {
        return Long.valueOf(endingTankPressure).doubleValue() / scaleFactor;
    }

    @Exclude
    public void setEndingTankPressureDouble(double endingTankPressure) {
        Double result = endingTankPressure * scaleFactor;
        this.endingTankPressure = result.longValue();
    }

    @Exclude
    public double getAirUsedDouble() {
        return Long.valueOf(airUsed).doubleValue() / scaleFactor;
    }

    @Exclude
    public void setAirUsedDouble(double airUsed) {
        Double result = airUsed * scaleFactor;
        this.airUsed = result.longValue();
    }
    //</editor-fold> Double conversion Getters and Setters

    //<editor-fold desc="Excluded Getters">
    @Exclude
    public String getDiveStartDayDateTime() {
        return MyMethods.getDayDateTimeString(diveStart, diveSiteTimeZoneID);
    }

    @Exclude
    public String getLocationDescription() {
        String locationDescription = "";

        if (area != null && !area.equals(DiveSite.DEFAULT_AREA)) {
            locationDescription = getArea();
        }

        if (state != null && !state.equals(DiveSite.DEFAULT_STATE)) {
            if (locationDescription.isEmpty()) {
                locationDescription = getState();
            } else {
                locationDescription = locationDescription + ", " + getState();
            }
        }
        if (country != null && !country.equals(DiveSite.DEFAULT_COUNTRY)) {
            if (locationDescription.isEmpty()) {
                locationDescription = getCountry();
            } else {
                locationDescription = locationDescription + ", " + getCountry();
            }
        }
        return locationDescription;
    }

    @Exclude
    public String getTissueLoading() {
        return "Tis: " + String.valueOf(tissueLoadingValue) + " " + tissueLoadingColor;
    }
    //</editor-fold> Excluded Getters

    //<editor-fold desc="Save Methods">
    public static String save(@NonNull String userUid, @NonNull DiveLog diveLog) {
        if (diveLog.getDiveLogUid() == null
                || diveLog.getDiveLogUid().isEmpty()
                || diveLog.getDiveLogUid().equals(MySettings.NOT_AVAILABLE)) {
            String newDiveLogUid = nodeUserDiveLogs(userUid).push().getKey();
            diveLog.setDiveLogUid(newDiveLogUid);
            Timber.i("Created %s with uid: %s.", diveLog.toString(), diveLog.getDiveLogUid());
        }
        nodeUserDiveLogs(userUid).child(diveLog.getDiveLogUid()).setValue(diveLog);
        Timber.i("Saved %s with uid: %s.", diveLog.toString(), diveLog.getDiveLogUid());
        return diveLog.getDiveLogUid();
    }

    private static void remove(@NonNull String userUid, @NonNull DiveLog diveLog) {
        if (diveLog.getDiveLogUid() != null
                && !diveLog.getDiveLogUid().isEmpty()
                && !diveLog.getDiveLogUid().equals(MySettings.NOT_AVAILABLE)) {
            nodeUserDiveLogs(userUid).child(diveLog.getDiveLogUid()).removeValue();
            Timber.i("remove(): %s", diveLog.toString());
        }
    }

    public static void saveTissueLoading(@NonNull final String userUid,
                                         @NonNull String activeDiveLogUid,
                                         @NonNull final String tissueLoadingColor,
                                         final int tissueLoadingValue) {

        nodeUserDiveLog(userUid, activeDiveLogUid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final DiveLog activeDiveLog = dataSnapshot.getValue(DiveLog.class);
                if (activeDiveLog != null) {
                    activeDiveLog.setTissueLoadingColor(tissueLoadingColor);
                    activeDiveLog.setTissueLoadingValue(tissueLoadingValue);
                    save(userUid, activeDiveLog);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
            }
        });

    }

    public static void saveDiveRating(String userUid, DiveLog diveLog) {
        nodeUserDiveLog(userUid, diveLog.getDiveLogUid())
                .child(FIELD_DIVE_RATING)
                .setValue(diveLog.getDiveRating());
    }

    public static void saveUserDiveReefGuideItems(@NonNull Context context,
                                                  @NonNull String userUid,
                                                  @NonNull String diveLogUid,
                                                  @NonNull ArrayList<ReefGuideItem> reefGuideItems) {
        ReefGuideItem.sortReefGuideItemsByTitle(reefGuideItems);
        nodeUserDiveReefGuideItems(userUid, diveLogUid).setValue(reefGuideItems);
        String marineLife = context.getResources()
                .getQuantityString(R.plurals.marineLife, reefGuideItems.size(), reefGuideItems.size());
        nodeUserDiveMarineLife(userUid, diveLogUid).setValue(marineLife);
    }

    public static void removeReefGuideItems(String userUid, @NonNull String diveLogUid) {
        nodeUserDiveReefGuideItems(userUid, diveLogUid).removeValue();
        nodeUserDiveMarineLife(userUid, diveLogUid).setValue(MySettings.NOT_AVAILABLE);
    }

    public static void saveMarineNotes(@NonNull String userUid,
                                       @NonNull String diveLogUid,
                                       @NonNull ArrayList<MarineNote> marineNotes) {

        nodeUserMarineNotes(userUid, diveLogUid).removeValue();

        if (marineNotes.size() > 0) {
            HashMap<String, MarineNote> marineNoteHashMap = new HashMap<>();
            for (MarineNote marineNote : marineNotes) {
                marineNoteHashMap.put(marineNote.getMarineNoteUid(), marineNote);
            }
            nodeUserMarineNotes(userUid, diveLogUid).setValue(marineNoteHashMap);
        }

    }

    public static void saveEquipmentList(@NonNull String userUid,
                                         @NonNull String diveLogUid,
                                         @NonNull String equipmentList) {
        nodeUserDiveEquipmentList(userUid, diveLogUid).setValue(equipmentList);
    }

    //</editor-fold> Save Methods

    //<editor-fold desc="Nodes">
    public static DatabaseReference nodeUserDiveLogs(@NonNull String userUid) {
        return dbReference.child(NODE_DIVE_LOGS).child(userUid);
    }

    public static DatabaseReference nodeUserDiveLog(@NonNull String userUid,
                                                    @NonNull String diveLogUid) {
        return dbReference.child(NODE_DIVE_LOGS).child(userUid).child(diveLogUid);
    }

    public static DatabaseReference nodeUserDiveNotes(@NonNull String userUid,
                                                      @NonNull String diveLogUid) {
        return nodeUserDiveLog(userUid, diveLogUid).child(NODE_DIVE_NOTES);
    }

    public static DatabaseReference nodeUserMarineNotes(@NonNull String userUid,
                                                        @NonNull String diveLogUid) {
        return nodeUserDiveLog(userUid, diveLogUid).child(NODE_MARINE_NOTES);
    }

    private static DatabaseReference nodeUserDiveMarineLife(@NonNull String userUid,
                                                            @NonNull String diveLogUid) {
        return nodeUserDiveLog(userUid, diveLogUid).child(NODE_DIVE_MARINE_LIFE);
    }

    public static DatabaseReference nodeUserDiveEquipmentList(@NonNull String userUid,
                                                              @NonNull String diveLogUid) {
        return nodeUserDiveLog(userUid, diveLogUid).child(NODE_DIVE_EQUIPMENT);
    }

    private static DatabaseReference nodeUserDiveReefGuideItems(@NonNull String userUid,
                                                                @NonNull String diveLogUid) {
        return nodeUserDiveLog(userUid, diveLogUid).child(NODE_REEF_GUIDE_ITEMS);
    }

    public static DatabaseReference nodeUserDiveReefGuideItem(@NonNull String userUid,
                                                              @NonNull String diveLogUid,
                                                              @NonNull String reefGuideItemUid) {
        return nodeUserDiveLog(userUid, diveLogUid).child(NODE_REEF_GUIDE_ITEMS)
                .child(reefGuideItemUid);
    }
    //</editor-fold> Nodes

    //<editor-fold desc="Update Dive Logs">
    public static void updateDiveLogsWithPerson(@NonNull String userUid, @NonNull Person person) {

        if (person.getDiveLogsAsBuddy().size() > 0) {
            Iterator it = person.getDiveLogsAsBuddy().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                updateDiveLogWithDiveBuddy(userUid, pair.getKey().toString(), person);
                it.remove(); // avoids a ConcurrentModificationException
            }
        }

        if (person.getDiveLogsAsMaster().size() > 0) {
            Iterator it = person.getDiveLogsAsMaster().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                updateDiveLogWithDiveMaster(userUid, pair.getKey().toString(), person);
                it.remove(); // avoids a ConcurrentModificationException
            }
        }

        if (person.getDiveLogsAsCompany().size() > 0) {
            Iterator it = person.getDiveLogsAsCompany().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                updateDiveLogWithDiveCompany(userUid, pair.getKey().toString(), person);
                it.remove(); // avoids a ConcurrentModificationException
            }
        }
    }

    public static void updateDiveLogsWithDefaultPerson(@NonNull String userUid,
                                                       @NonNull Person personBeingDeleted) {

        Person defaultBuddy = Person.getDefaultPerson(R.id.btnDiveBuddy);
        if (personBeingDeleted.getDiveLogsAsBuddy().size() > 0) {
            for (String diveLogUid : personBeingDeleted.getDiveLogsAsBuddy().keySet()) {
                updateDiveLogWithDiveBuddy(userUid, diveLogUid, defaultBuddy);
            }
        }

        Person defaultDiveMaster = Person.getDefaultPerson(R.id.btnDiveMaster);
        if (personBeingDeleted.getDiveLogsAsMaster().size() > 0) {
            for (String diveLogUid : personBeingDeleted.getDiveLogsAsMaster().keySet()) {
                updateDiveLogWithDiveMaster(userUid, diveLogUid, defaultDiveMaster);
            }
        }

        Person defaultCompany = Person.getDefaultPerson(R.id.btnCompany);
        if (personBeingDeleted.getDiveLogsAsCompany().size() > 0) {
            for (String diveLogUid : personBeingDeleted.getDiveLogsAsCompany().keySet()) {
                updateDiveLogWithDiveCompany(userUid, diveLogUid, defaultCompany);
            }
        }
    }

    private static void updateDiveLogWithDiveBuddy(@NonNull final String userUid,
                                                   @NonNull final String diveLogUid,
                                                   @NonNull final Person diveBuddy) {
        DiveLog.nodeUserDiveLog(userUid, diveLogUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    DiveLog diveLog = dataSnapshot.getValue(DiveLog.class);
                    if (diveLog != null) {
                        diveLog.setDiveBuddyPersonUid(diveBuddy.getPersonUid());
                        DiveLog.save(userUid, diveLog);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
            }
        });
    }

    private static void updateDiveLogWithDiveMaster(@NonNull final String userUid,
                                                    @NonNull final String diveLogUid,
                                                    @NonNull final Person diveMaster) {
        DiveLog.nodeUserDiveLog(userUid, diveLogUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    DiveLog diveLog = dataSnapshot.getValue(DiveLog.class);
                    if (diveLog != null) {
                        diveLog.setDiveMasterPersonUid(diveMaster.getPersonUid());
                        DiveLog.save(userUid, diveLog);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
            }
        });
    }

    private static void updateDiveLogWithDiveCompany(@NonNull final String userUid,
                                                     @NonNull final String diveLogUid,
                                                     @NonNull final Person diveCompany) {
        DiveLog.nodeUserDiveLog(userUid, diveLogUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    DiveLog diveLog = dataSnapshot.getValue(DiveLog.class);
                    if (diveLog != null) {
                        diveLog.setDiveCompanyPersonUid(diveCompany.getPersonUid());
                        DiveLog.save(userUid, diveLog);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
            }
        });
    }


    public static void updateDiveLogsWithSelectionValue(String userUid, SelectionValue
            selectionValue) {

        if (selectionValue.getDiveLogs() != null && selectionValue.getDiveLogs().size() > 0) {
            for (String diveLogUid : selectionValue.getDiveLogs().keySet()) {
                updateDiveLogWithSelectionValue(userUid, diveLogUid, selectionValue.getNodeName(),
                        selectionValue.getValue());
            }
        }

    }

    private static void updateDiveLogWithSelectionValue(@NonNull String userUid,
                                                        @NonNull String diveLogUid,
                                                        @NonNull String selectionValueField,
                                                        @Nullable String value) {
        if (value == null || value.startsWith("[")) {
            value = MySettings.NOT_AVAILABLE;
        }

        String field = "";
        switch (selectionValueField) {
            case SelectionValue.NODE_AREA_VALUES:
                field = FIELD_AREA;
                break;
            case SelectionValue.NODE_STATE_VALUES:
                field = FIELD_STATE;
                break;
            case SelectionValue.NODE_COUNTRY_VALUES:
                field = FIELD_COUNTRY;
                break;
            case SelectionValue.NODE_CURRENT_VALUES:
                field = FIELD_CURRENT_CONDITION;
                break;
            case SelectionValue.NODE_DIVE_ENTRY_VALUES:
                field = FIELD_DIVE_ENTRY;
                break;
            case SelectionValue.NODE_DIVE_STYLE_VALUES:
                field = FIELD_DIVE_STYLE;
                break;
            case SelectionValue.NODE_DIVE_TANK_VALUES:
                field = FIELD_TANK_TYPE;
                break;
            case SelectionValue.NODE_DIVE_TYPE_VALUES:
                field = FIELD_DIVE_TYPE;
                break;
            case SelectionValue.NODE_SEA_CONDITION_VALUES:
                field = FIELD_SEA_CONDITION;
                break;
            case SelectionValue.NODE_WEATHER_CONDITION_VALUES:
                field = FIELD_WEATHER_CONDITION;
                break;
        }

        if (!field.isEmpty()) {
            nodeUserDiveLog(userUid, diveLogUid).child(field).setValue(value);
        } else {
            Timber.e("updateDiveLogWithSelectionValue(): Unknown diveLog field!");
        }
    }

    public static void updateDiveLogsWithDefaultSelectionValue(@NonNull String userUid,
                                                               @NonNull String selectionValueField,
                                                               @NonNull SelectionValue
                                                                       selectionValue) {
        if (selectionValue.getDiveLogs() != null) {
            for (String diveLogUid : selectionValue.getDiveLogs().keySet()) {
                updateDiveLogWithSelectionValue(userUid, diveLogUid, selectionValueField,
                        MySettings.NOT_AVAILABLE);
            }
        }
    }

    public static void updateDiveLogsWithDiveSiteDefaultValues(@NonNull final String userUid,
                                                               @NonNull final DiveSite
                                                                       diveSiteBeingDeleted) {

        if (diveSiteBeingDeleted.getDiveLogs() != null && diveSiteBeingDeleted.getDiveLogs().size
                () > 0) {
            for (String diveLogUid : diveSiteBeingDeleted.getDiveLogs().keySet()) {
                updateDiveLogWithDiveSiteDefaultValues(userUid, diveLogUid);
            }
        }
    }

    private static void updateDiveLogWithDiveSiteDefaultValues(@NonNull final String userUid,
                                                               @NonNull String diveLogUid) {
        nodeUserDiveLog(userUid, diveLogUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    DiveLog diveLog = dataSnapshot.getValue(DiveLog.class);
                    if (diveLog != null) {
                        diveLog.setDiveSiteUid(MySettings.NOT_AVAILABLE);
                        diveLog.setDiveSiteName(MySettings.NOT_AVAILABLE);
                        diveLog.setArea(DiveSite.DEFAULT_AREA);
                        diveLog.setState(DiveSite.DEFAULT_STATE);
                        diveLog.setCountry(DiveSite.DEFAULT_COUNTRY);
                        DiveLog.save(userUid, diveLog);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
            }
        });
    }
    //</editor-fold> Update Dive Logs

    //<editor-fold desc="Update Dive Logs">
    public static void sequenceDiveLogs(@NonNull final String userUid,
                                        final String returningDiveLogUid) {

        EventBus.getDefault().post(new MyEvents.destroyDiveLogsAdapter());
        EventBus.getDefault().post(new MyEvents.showProgressBar("Accumulating bottom times and sequencing dive logs ..."));
        EventBus.getDefault().post(new MyEvents.setDiveLogsSequenced(true));

        nodeUserDiveLogs(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    new RetrieveDiveLogsAsync(userUid, dataSnapshot, returningDiveLogUid).execute();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
            }
        });
    }

    private static class RetrieveDiveLogsAsync extends AsyncTask<Void, Void, List<DiveLog>> {
        private final String mUserUid;
        private final String mReturningDiveLogUid;
        private final DataSnapshot mDataSnapshot;

        private RetrieveDiveLogsAsync(@NonNull String userUid, DataSnapshot dataSnapshot,
                                      String returningDiveLogUid) {
            mUserUid = userUid;
            mDataSnapshot = dataSnapshot;
            mReturningDiveLogUid = returningDiveLogUid;
        }

        @Override
        protected List<DiveLog> doInBackground(Void... voids) {
            List<DiveLog> diveLogs = new ArrayList<>();
            for (DataSnapshot snapshot : mDataSnapshot.getChildren()) {
                DiveLog diveLog = snapshot.getValue(DiveLog.class);
                if (diveLog != null) {
                    diveLogs.add(diveLog);
                }
            }
            return diveLogs;
        }

        @Override
        protected void onPostExecute(List<DiveLog> diveLogs) {
            if (diveLogs.size() > 0) {
                new SequenceDiveLogsAsync(mUserUid, diveLogs, mReturningDiveLogUid).execute();
            }
        }
    }

    private static class SequenceDiveLogsAsync extends AsyncTask<Void, Void, Void> {
        private final String mUserUid;
        private final List<DiveLog> mDiveLogs;
        private final Handler handler = new Handler();
        private final String mReturningDiveLogUid;

        SequenceDiveLogsAsync(@NonNull String userUid, List<DiveLog> diveLogs,
                              String returningDiveLogUid) {
            mUserUid = userUid;
            mDiveLogs = diveLogs;
            mReturningDiveLogUid = returningDiveLogUid;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Timber.i("SequenceDiveLogsAsync: doInBackground()");
            Collections.sort(mDiveLogs, DiveLog.mAscendingStartTime);
            sequenceSortedDiveLogs(mUserUid, mDiveLogs);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Timber.i("SequenceDiveLogsAsync: onPostExecute()");
            handler.postDelayed(runnable, 100);
        }

        private final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mUserDiveLogsValueEventListener.getNumberOfDiveLogsChangedInFirebase()
                        == mUserDiveLogsValueEventListener.getNumberOfDiveLogsSavedToFirebase()) {
                    Timber.i("SequenceDiveLogsAsync: all diveLog changes implemented.");
                    removeUserDiveLogsValueEventListener(mUserUid);
                    handler.removeCallbacksAndMessages(null);
                    EventBus.getDefault().post(
                            new MyEvents.resetViewPagerAdapter(mReturningDiveLogUid));
                } else {
                    handler.postDelayed(this, 100);
                    Timber.i("run(); SequenceDiveLogsAsync: onPostExecute");
                }
            }
        };

        private void sequenceSortedDiveLogs(@NonNull String userUid, List<DiveLog> diveLogs) {
            addUserDiveLogsValueEventListener(mUserUid);

            boolean isDiveLogDirty;
            long diveEnd;
            DiveLog previousDiveLog;
            DiveLog currentDiveLog;
            DiveLog nextDiveLog;

            int previousDiveLogIndex = -1;
            int startingDiveLogIndex = 0;
            int nextDiveLogIndex = 1;
            long accumulatedBottomTime = 0;

            for (int i = startingDiveLogIndex; i < diveLogs.size(); i++) {
                isDiveLogDirty = false;

                // retrieve current, previous, and next diveLogs
                currentDiveLog = diveLogs.get(i);
                if (previousDiveLogIndex > -1) {
                    previousDiveLog = diveLogs.get(previousDiveLogIndex);
                } else {
                    previousDiveLog = null;
                }

                if (nextDiveLogIndex < diveLogs.size()) {
                    nextDiveLog = diveLogs.get(nextDiveLogIndex);
                } else {
                    nextDiveLog = null;
                }

                // if needed, reset isSequencingRequired
                if (currentDiveLog.isSequencingRequired()) {
                    currentDiveLog.setSequencingRequired(false);
                    Timber.i("sequenceSortedDiveLogs(): isSequencingRequired now = false");
                    isDiveLogDirty = true;
                }

                // set dive log number
                int diveNumber = i + 1;
                if (currentDiveLog.getDiveNumber() != diveNumber) {
                    currentDiveLog.setDiveNumber(diveNumber);
                    Timber.i("sequenceSortedDiveLogs(): setDiveNumber now = %d", diveNumber);
                    isDiveLogDirty = true;
                }

                // accumulate bottom time
                accumulatedBottomTime += currentDiveLog.getBottomTime();
                if (currentDiveLog.getAccumulatedBottomTimeToDate() != accumulatedBottomTime) {
                    currentDiveLog.setAccumulatedBottomTimeToDate(accumulatedBottomTime);
                    Timber.i("sequenceSortedDiveLogs(): setAccumulatedBottomTimeToDate now = %d",
                            accumulatedBottomTime);
                    isDiveLogDirty = true;
                }

                // calculate diveEnd
                diveEnd = currentDiveLog.getDiveStart() + currentDiveLog.getBottomTime();
                if (currentDiveLog.getDiveEnd() != diveEnd) {
                    currentDiveLog.setDiveEnd(diveEnd);
                    Timber.i("sequenceSortedDiveLogs(): setDiveEnd now = %d", diveEnd);
                    isDiveLogDirty = true;
                }

                // make sure that the next diveStart is after
                // the current diveEnd
                if (nextDiveLog != null) {
                    if (diveEnd > nextDiveLog.diveStart) {
                        nextDiveLog.setDiveStart(diveEnd + 1);
                        Timber.i("sequenceSortedDiveLogs(): nextDiveLog.setDiveStart to diveEnd +" +
                                " 1 mills");
                        isDiveLogDirty = true;
                    }
                }

                // calculate surface interval
                if (previousDiveLog != null) {
                    long surfaceInterval = currentDiveLog.getDiveStart() - previousDiveLog
                            .getDiveEnd();
                    if (currentDiveLog.getSurfaceInterval() != surfaceInterval) {
                        currentDiveLog.setSurfaceInterval(surfaceInterval);
                        Timber.i("sequenceSortedDiveLogs(): setSurfaceInterval now = %d",
                                surfaceInterval);
                        isDiveLogDirty = true;
                    }
                } else {
                    if (currentDiveLog.getSurfaceInterval() != -1) {
                        currentDiveLog.setSurfaceInterval(-1);
                        Timber.i("sequenceSortedDiveLogs(): setSurfaceInterval now = %d", -1);
                        isDiveLogDirty = true;
                    }
                }

                //set previous diveLog Uid
                if (previousDiveLog != null) {
                    if (!currentDiveLog.getPreviousDiveLogUid().equals(previousDiveLog
                            .getDiveLogUid())) {
                        currentDiveLog.setPreviousDiveLogUid(previousDiveLog.getDiveLogUid());
                        Timber.i("sequenceSortedDiveLogs(); setPreviousDiveLogUid now = %s",
                                previousDiveLog.getDiveLogUid());
                        isDiveLogDirty = true;
                    }
                } else {
                    if (!currentDiveLog.getPreviousDiveLogUid().equals(MySettings.NOT_AVAILABLE)) {
                        currentDiveLog.setPreviousDiveLogUid(MySettings.NOT_AVAILABLE);
                        Timber.i("sequenceSortedDiveLogs(); setPreviousDiveLogUid now = %s",
                                MySettings.NOT_AVAILABLE);
                        isDiveLogDirty = true;
                    }
                }

                // set next DiveLog uid
                if (nextDiveLog != null) {
                    if (!currentDiveLog.getNextDiveLogUid().equals(nextDiveLog.getDiveLogUid())) {
                        currentDiveLog.setNextDiveLogUid(nextDiveLog.getDiveLogUid());
                        Timber.i("sequenceSortedDiveLogs(): setNextDiveLogUid now = %s",
                                nextDiveLog.getDiveLogUid());
                        isDiveLogDirty = true;
                    }
                } else {
                    if (!currentDiveLog.getNextDiveLogUid().equals(MySettings.NOT_AVAILABLE)) {
                        currentDiveLog.setNextDiveLogUid(MySettings.NOT_AVAILABLE);
                        Timber.i("sequenceSortedDiveLogs(): setNextDiveLogUid now = %s",
                                MySettings.NOT_AVAILABLE);
                        isDiveLogDirty = true;
                    }
                }

                if (isDiveLogDirty) {
                    DiveLog.save(userUid, currentDiveLog);
                    mUserDiveLogsValueEventListener.incrementNumberOfDiveLogsSavedToFirebase();
                }
                previousDiveLogIndex++;
                nextDiveLogIndex++;
            }
        }
    }

    private static void addUserDiveLogsValueEventListener(@NonNull String userUid) {
        Timber.i("addUserDiveLogsValueEventListener()");
        mUserDiveLogsValueEventListener = new UserDiveLogsValueEventListener();
        nodeUserDiveLogs(userUid).addValueEventListener(mUserDiveLogsValueEventListener);
    }

    private static void removeUserDiveLogsValueEventListener(@NonNull String userUid) {
        Timber.i("removeUserDiveLogsValueEventListener()");
        if (mUserDiveLogsValueEventListener != null) {
            nodeUserDiveLogs(userUid).removeEventListener(mUserDiveLogsValueEventListener);
        }
    }

    private static class UserDiveLogsValueEventListener implements ValueEventListener {

        private int mNumberOfDiveLogsSavedToFirebase = 0;
        private int mNumberOfDiveLogsChangedInFirebase = -1;

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mNumberOfDiveLogsChangedInFirebase++;
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
        }

        private void incrementNumberOfDiveLogsSavedToFirebase() {
            mNumberOfDiveLogsSavedToFirebase++;
        }

        private int getNumberOfDiveLogsSavedToFirebase() {
            return mNumberOfDiveLogsSavedToFirebase;
        }

        private int getNumberOfDiveLogsChangedInFirebase() {
            return mNumberOfDiveLogsChangedInFirebase;
        }
    }
    //</editor-fold> Update Dive Logs

}
