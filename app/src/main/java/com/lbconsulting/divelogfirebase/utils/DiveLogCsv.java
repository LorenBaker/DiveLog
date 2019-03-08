package com.lbconsulting.divelogfirebase.utils;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Java object for a diveLog entry.
 */
@IgnoreExtraProperties
public class DiveLogCsv {

 /*   public static final Comparator<DiveLog> mAscendingStartTime = new Comparator<DiveLog>() {
        public int compare(DiveLog diveLog1, DiveLog diveLog2) {
            Long diveStart1 = diveLog1.getDiveStart();
            Long diveStart2 = diveLog2.getDiveStart();
            return diveStart1.compareTo(diveStart2);
        }
    };

    public static final Comparator<DiveLog> mDescendingStartTime = new Comparator<DiveLog>() {
        public int compare(DiveLog diveLog1, DiveLog diveLog2) {
            Long diveStart1 = diveLog1.getDiveStart();
            Long diveStart2 = diveLog2.getDiveStart();
            return diveStart2.compareTo(diveStart1);
        }
    };

//    private static final String NODE_DIVE_LOGS = "diveLogs";
//    private static final String NODE_DIVE_LOG_NOTES = "diveNotes";
//    private static final String NODE_DIVE_LOG_EQUIPMENT = "equipmentList";

    //region Field Names
//    public static final String FIELD_DIVE_NUMBER = "diveNumber";
//    public static final String FIELD_DIVE_START = "diveStart";
//    public static final String FIELD_BOTTOM_TIME = "bottomTime";
//    public static final String FIELD_DIVE_SITE = "diveSite";
//    public static final String FIELD_MAXIMUM_DEPTH = "maximumDepth";
//    public static final String FIELD_AREA = "area";
//    public static final String FIELD_STATE = "state";
//    public static final String FIELD_COUNTRY = "country";
//    public static final String FIELD_DIVE_BUDDY = "diveBuddy";
//    public static final String FIELD_DIVE_MASTER = "diveMaster";
//    public static final String FIELD_DIVE_COMPANY = "diveCompany";
//    public static final String FIELD_TISSUE_LOADING_COLOR = "tissueLoadingColor";
//    public static final String FIELD_TISSUE_LOADING_VALUE = "tissueLoadingValue";
//    public static final String FIELD_TANK_TYPE = "tankType";
//    public static final String FIELD_DIVE_RATING = "diveRating";
//    public static final String FIELD_WEIGHT_USED = "weightUsed";
//    public static final String FIELD_STARTING_TANK_PRESSURE = "startingTankPressure";
//    public static final String FIELD_ENDING_TANK_PRESSURE = "endingTankPressure";
//    public static final String FIELD_AIR_TEMPERATURE = "airTemperature";
//    public static final String FIELD_WATER_TEMPERATURE = "waterTemperature";
//    public static final String FIELD_VISIBILITY = "visibility";
//    public static final String FIELD_CURRENT_CONDITION = "currentCondition";
//    public static final String FIELD_DIVE_ENTRY = "diveEntry";
//    public static final String FIELD_DIVE_STYLE = "diveStyle";
//    public static final String FIELD_DIVE_TYPE = "diveType";
//    public static final String FIELD_SEA_CONDITION = "seaCondition";
//    public static final String FIELD_WEATHER_CONDITION = "weatherCondition";
//    public static final String FIELD_DIVE_NOTES = "diveNotes";
//    public static final String FIELD_MARINE_LIFE = "marineLife";
//    public static final String FIELD_EQUIPMENT_LIST = "equipmentList";
//    public static final String FIELD_PHOTO_FOLDER_URL = "divePhotosUrl";
//    public static final String FIELD_ACCUM_BOTTOM_TIME_TO_DATE = "accumulatedBottomTimeToDate";
//    public static final String FIELD_DIVE_END = "diveEnd";
//    public static final String FIELD_SURFACE_INTERVAL = "surfaceInterval";
//    public static final String FIELD_AIR_USED = "airUsed";
//    public static final String FIELD_DIVE_SITE_TIMEZONE_ID = "diveSiteTimeZoneID";
//    public static final String FIELD_PREVIOUS_DIVE_LOG_UID = "previousDiveLogUid";
//    public static final String FIELD_DIVE_LOG_UID = "diveLogUid";
//    public static final String FIELD_NEXT_DIVE_LOG_UID = "nextDiveLogUid";
    //endregion

    //region Csv Column Numbers
    private static final int DIVE_NUMBER = 0;
    private static final int DIVE_START = 1;
    private static final int BOTTOM_TIME = 2;
    private static final int DIVE_SITE = 3;
    private static final int MAXIMUM_DEPTH = 4;
    private static final int AREA = 5;
    private static final int STATE = 6;
    private static final int COUNTRY = 7;
    private static final int DIVE_BUDDY = 8;
    private static final int DIVE_MASTER = 9;
    private static final int DIVE_COMPANY = 10;
    private static final int TISSUE_LOADING_COLOR = 11;
    private static final int TISSUE_LOADING_VALUE = 12;
    private static final int TANK_TYPE = 13;
    private static final int DIVE_RATING = 14;
    private static final int WEIGHT_USED = 15;
    private static final int STARTING_TANK_PRESSURE = 16;
    private static final int ENDING_TANK_PRESSURE = 17;
    private static final int AIR_TEMPERATURE = 18;
    private static final int WATER_TEMPERATURE = 19;
    private static final int VISIBILITY = 20;
    private static final int CURRENT_CONDITION = 21;
    private static final int DIVE_ENTRY = 22;
    private static final int DIVE_STYLE = 23;
    private static final int DIVE_TYPE = 24;
    private static final int SEA_CONDITION = 25;
    private static final int WEATHER_CONDITION = 26;
    private static final int DIVE_NOTES = 27;
    private static final int MARINE_LIFE = 28;
    private static final int EQUIPMENT_LIST = 29;
    private static final int PHOTOS_URL = 30;
    private static final int ACCUM_BOTTOM_TIME_TO_DATE = 31;
    private static final int DIVE_END = 32;
    private static final int SURFACE_INTERVAL = 33;
    private static final int AIR_USED = 34;
    private static final int DIVE_SITE_TIMEZONE_ID = 35;
    private static final int PREVIOUS_DIVE_LOG_UID = 36;
    private static final int DIVE_LOG_UID = 37;
    private static final int NEXT_DIVE_LOG_UID = 38;
    //endregion

    //region Fields
//    private Boolean sequencingRequired;
//    private Double airTemperature;
//    private Double airUsed;
//    private Double endingTankPressure;
//    private Double maximumDepth;
//    private Double startingTankPressure;
//    private Double visibility;
//    private Double waterTemperature;
//    private Double weightUsed;
//    private float diveRating;
//    private int diveNumber;
//    private int tissueLoadingValue;
//    private long accumulatedBottomTimeToDate;
//    private long bottomTime;
//    private long diveEnd;
//    private long diveStart;
//    private long surfaceInterval;
//    private String area;
//    private String country;
//    private String currentCondition;
//    private String diveBuddyPersonUid;
//    private String diveCompanyPersonUid;
//    private String diveEntry;
//    private String diveLogUid;
//    private String diveMasterPersonUid;
//    private String diveNotes;
//    private String divePhotosUrl;
//    private String diveSiteTimeZoneID;
//    private String diveSiteUid;
//    private String diveStyle;
//    private String diveType;
//    private String equipmentList;
//    private String marineLife;
//    private String nextDiveLogUid;
//    private String previousDiveLogUid;
//    private String seaCondition;
//    private String state;
//    private String tankType;
//    private String tissueLoadingColor;
//    private String weatherCondition;


    //endregion

    private static final DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();

    public DiveLogCsv() {
        // Default constructor.
    }

    public DiveLogCsv(ArrayList<String> field) {
        DiveLog diveLog = new DiveLog();
        diveLogUid = MySettings.NOT_AVAILABLE;
        diveNumber = Integer.parseInt(field.get(DIVE_NUMBER));
        diveStart = getDateTimeMilliSeconds(field.get(DIVE_START));

        diveEnd = 0;
        surfaceInterval = 0;

        bottomTime = Integer.parseInt(field.get(BOTTOM_TIME));
//        diveSite = field.get(DIVE_SITE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(diveStart);
        diveSiteTimeZoneID = calendar.getTimeZone().getID();

        maximumDepth = Double.parseDouble(field.get(MAXIMUM_DEPTH));

        nextDiveLogUid = MySettings.NOT_AVAILABLE;
        previousDiveLogUid = MySettings.NOT_AVAILABLE;

        area = field.get(AREA);
        state = field.get(STATE);
        country = field.get(COUNTRY);
//        diveBuddy = field.get(DIVE_BUDDY);
//        diveMaster = field.get(DIVE_MASTER);
//        diveCompany = field.get(DIVE_COMPANY);
        tissueLoadingColor = field.get(TISSUE_LOADING_COLOR);
        tissueLoadingValue = Integer.parseInt(field.get(TISSUE_LOADING_VALUE));
        tankType = field.get(TANK_TYPE);
        diveRating = Integer.parseInt(field.get(DIVE_RATING));
        weightUsed = Double.parseDouble(field.get(WEIGHT_USED));
        startingTankPressure = Double.parseDouble(field.get(STARTING_TANK_PRESSURE));
        endingTankPressure = Double.parseDouble(field.get(ENDING_TANK_PRESSURE));
        airTemperature = Double.parseDouble(field.get(AIR_TEMPERATURE));
        waterTemperature = Double.parseDouble(field.get(WATER_TEMPERATURE));
        visibility = Double.parseDouble(field.get(VISIBILITY));
        currentCondition = field.get(CURRENT_CONDITION);
        diveEntry = field.get(DIVE_ENTRY);
        diveStyle = field.get(DIVE_STYLE);
        diveType = field.get(DIVE_TYPE);
        seaCondition = field.get(SEA_CONDITION);
        weatherCondition = field.get(WEATHER_CONDITION);
        diveNotes = field.get(DIVE_NOTES);
        marineLife = field.get(MARINE_LIFE);
        equipmentList = field.get(EQUIPMENT_LIST);
        divePhotosUrl = MySettings.NOT_AVAILABLE;
    }


    public void cloneValues(DiveLog diveLog) {
        surfaceInterval = diveLog.getSurfaceInterval();
        diveStart = diveLog.getDiveStart();
        bottomTime = diveLog.getBottomTime();
        diveEnd = diveLog.getDiveEnd();
        accumulatedBottomTimeToDate = diveLog.getAccumulatedBottomTimeToDate();

        diveSiteUid = diveLog.getDiveSiteUid();
        diveSiteTimeZoneID = diveLog.getDiveSiteTimeZoneID();

        maximumDepth = diveLog.getMaximumDepth();
        nextDiveLogUid = diveLog.getNextDiveLogUid();
        previousDiveLogUid = diveLog.getPreviousDiveLogUid();

        area = diveLog.getArea();
        state = diveLog.getState();
        country = diveLog.getCountry();

        diveBuddyPersonUid = diveLog.getDiveBuddyPersonUid();
        diveMasterPersonUid = diveLog.getDiveMasterPersonUid();
        diveCompanyPersonUid = diveLog.getDiveCompanyPersonUid();

        tissueLoadingColor = diveLog.getTissueLoadingColor();
        tissueLoadingValue = diveLog.getTissueLoadingValue();

        tankType = diveLog.getTankType();
        diveRating = diveLog.getDiveRating();
        weightUsed = diveLog.getWeightUsed();

        startingTankPressure = diveLog.getStartingTankPressure();
        endingTankPressure = diveLog.getEndingTankPressure();
        airUsed = diveLog.getAirUsed();

        airTemperature = diveLog.getAirTemperature();
        waterTemperature = diveLog.getWaterTemperature();
        visibility = diveLog.getVisibility();
        currentCondition = diveLog.getCurrentCondition();
        diveEntry = diveLog.getDiveEntry();
        diveStyle = diveLog.getDiveStyle();
        diveType = diveLog.getDiveType();
        seaCondition = diveLog.getSeaCondition();
        weatherCondition = diveLog.getWeatherCondition();
        diveNotes = diveLog.getDiveNotes();
        marineLife = diveLog.getMarineLife();
        equipmentList = diveLog.getEquipmentList();
        divePhotosUrl = diveLog.getDivePhotosUrl();

    }

    public static DiveLog cloneNewDiveLog(@NonNull DiveLog lastDiveLog) {
        DiveLog newDiveLog = new DiveLog();
        newDiveLog.setDiveLogUid(MySettings.NOT_AVAILABLE);
        newDiveLog.setDiveNumber(lastDiveLog.getDiveNumber() + 1);
        // Start next dive one hour after the last dive
        newDiveLog.setSurfaceInterval(MyMethods.millsPerHour);
        newDiveLog.setDiveStart(lastDiveLog.getDiveEnd() + MyMethods.millsPerHour);
        // Set bottom time to one hour
        newDiveLog.setBottomTime(MyMethods.millsPerHour);
        newDiveLog.setDiveEnd(newDiveLog.getDiveStart() + MyMethods.millsPerHour);
        newDiveLog.setAccumulatedBottomTimeToDate(lastDiveLog.getAccumulatedBottomTimeToDate() + MyMethods.millsPerHour);

        newDiveLog.setDiveSiteUid(MySettings.NOT_AVAILABLE);
        newDiveLog.setDiveSiteTimeZoneID(lastDiveLog.getDiveSiteTimeZoneID());

        newDiveLog.setMaximumDepth(0d);
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
        if (lastDiveLog.getStartingTankPressure() > 500d) {
            newDiveLog.setEndingTankPressure(500d);
            newDiveLog.setAirUsed(newDiveLog.getStartingTankPressure() - 500d);
        } else {
            newDiveLog.setStartingTankPressure(0d);
            newDiveLog.setEndingTankPressure(0d);
            newDiveLog.setAirUsed(0d);
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

    @Exclude
    public void setRecords(String userUid, ArrayList<ArrayList<String>> records) {
        DiveLog diveLog;
        for (int recordIndex = 1; recordIndex < records.size(); recordIndex++) {
            diveLog = new DiveLog();
            ArrayList<String> record = records.get(recordIndex);
            diveLog.setDiveNumber(Integer.parseInt(record.get(DIVE_NUMBER)));
            diveLog.setDiveStart(Long.parseLong(record.get(DIVE_START)));
            diveLog.setBottomTime(Long.parseLong(record.get(BOTTOM_TIME)));
//            diveLog.setDiveSite(record.get(DIVE_SITE));
            diveLog.setMaximumDepth(Double.parseDouble(record.get(MAXIMUM_DEPTH)));
            diveLog.setArea(record.get(AREA));
            diveLog.setState(record.get(STATE));
            diveLog.setCountry(record.get(COUNTRY));
//            diveLog.setDiveBuddy(record.get(DIVE_BUDDY));
//            diveLog.setDiveMaster(record.get(DIVE_MASTER));
//            diveLog.setDiveCompany(record.get(DIVE_COMPANY));
            diveLog.setTissueLoadingColor(record.get(TISSUE_LOADING_COLOR));
            diveLog.setTissueLoadingValue(Integer.parseInt(record.get(TISSUE_LOADING_VALUE)));
            diveLog.setTankType(record.get(TANK_TYPE));
            diveLog.setDiveRating(Float.parseFloat(record.get(DIVE_RATING)));
            diveLog.setWeightUsed(Double.parseDouble(record.get(WEIGHT_USED)));
            diveLog.setStartingTankPressure(Double.parseDouble(record.get(STARTING_TANK_PRESSURE)));
            diveLog.setEndingTankPressure(Double.parseDouble(record.get(ENDING_TANK_PRESSURE)));
            diveLog.setAirTemperature(Double.parseDouble(record.get(AIR_TEMPERATURE)));
            diveLog.setWaterTemperature(Double.parseDouble(record.get(WATER_TEMPERATURE)));
            diveLog.setVisibility(Double.parseDouble(record.get(VISIBILITY)));
            diveLog.setCurrentCondition(record.get(CURRENT_CONDITION));
            diveLog.setDiveEntry(record.get(DIVE_ENTRY));
            diveLog.setDiveStyle(record.get(DIVE_STYLE));
            diveLog.setDiveType(record.get(DIVE_TYPE));
            diveLog.setSeaCondition(record.get(SEA_CONDITION));
            diveLog.setWeatherCondition(record.get(WEATHER_CONDITION));
            diveLog.setDiveNotes(record.get(DIVE_NOTES));
            diveLog.setMarineLife(record.get(MARINE_LIFE));
            diveLog.setEquipmentList(record.get(EQUIPMENT_LIST));
            diveLog.setDivePhotosUrl(record.get(PHOTOS_URL));
            diveLog.setAccumulatedBottomTimeToDate(Long.parseLong(record.get(BOTTOM_TIME)));
            diveLog.setDiveEnd(Long.parseLong(record.get(DIVE_END)));
            diveLog.setSurfaceInterval(Long.parseLong(record.get(SURFACE_INTERVAL)));
            diveLog.setAirUsed(Double.parseDouble(record.get(AIR_USED)));
            diveLog.setDiveSiteTimeZoneID(record.get(DIVE_SITE_TIMEZONE_ID));
            diveLog.setPreviousDiveLogUid(record.get(PREVIOUS_DIVE_LOG_UID));
            diveLog.setDiveLogUid(record.get(DIVE_LOG_UID));
            diveLog.setNextDiveLogUid(record.get(NEXT_DIVE_LOG_UID));

            save(userUid, diveLog);
        }
    }

    private long getDateTimeMilliSeconds(String diveStartString) {
        Calendar calendar = Calendar.getInstance();

        String[] dateTime = diveStartString.split(" ");
        String[] monthDayYear = dateTime[0].split("/");
        String[] hourMinute = dateTime[1].split(":");

        int year;
        int month;
        int day;
        int hourOfDay;
        int minute;

        if (monthDayYear.length == 3) {
            month = Integer.parseInt(monthDayYear[0]);
            month--;
            day = Integer.parseInt(monthDayYear[1]);
            year = Integer.parseInt(monthDayYear[2]);

            if (hourMinute.length == 2) {
                hourOfDay = Integer.parseInt(hourMinute[0]);
                minute = Integer.parseInt(hourMinute[1]);
                calendar.clear();
                TimeZone defaultTimeZone = TimeZone.getDefault();
                String defaultTimeZoneDisplayName = defaultTimeZone.getDisplayName();
                calendar.setTimeZone(defaultTimeZone);
                calendar.set(year, month, day, hourOfDay, minute);
            } else {
                Timber.e("DiveLog(): Improper hourMinute");
                return -1;
            }

        } else {
            Timber.e("DiveLog(): Improper yearMonthDay");
            return -1;
        }
        return calendar.getTimeInMillis();
    }

    //region Getters and Setters

    public long getAccumulatedBottomTimeToDate() {
        return accumulatedBottomTimeToDate;
    }

    public void setAccumulatedBottomTimeToDate(long accumulatedBottomTimeToDate) {
        this.accumulatedBottomTimeToDate = accumulatedBottomTimeToDate;
    }

    public Double getAirTemperature() {
        return airTemperature;
    }

    public void setAirTemperature(Double airTemperature) {
        this.airTemperature = airTemperature;
    }

    public void setAirUsed(Double airUsed) {
        this.airUsed = airUsed;
    }

    public Double getAirUsed() {
        return airUsed;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public long getBottomTime() {
        return bottomTime;
    }

    public void setBottomTime(long bottomTime) {
        this.bottomTime = bottomTime;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCurrentCondition() {
        return currentCondition;
    }

    public void setCurrentCondition(String currentCondition) {
        this.currentCondition = currentCondition;
    }

//    public String getDiveBuddy() {
//        return diveBuddy;
//    }
//
//    public void setDiveBuddy(String diveBuddy) {
//        this.diveBuddy = diveBuddy;
//    }
//
//    public String getDiveCompany() {
//        return diveCompany;
//    }
//
//    public void setDiveCompany(String diveCompany) {
//        this.diveCompany = diveCompany;
//    }

    public long getDiveEnd() {
        return diveEnd;
    }

    public void setDiveEnd(long diveEnd) {
        this.diveEnd = diveEnd;
    }

    public String getDiveEntry() {
        return diveEntry;
    }

    public void setDiveEntry(String diveEntry) {
        this.diveEntry = diveEntry;
    }

    public String getDiveLogUid() {
        return diveLogUid;
    }

    public void setDiveLogUid(String diveLogUid) {
        this.diveLogUid = diveLogUid;
    }

//    public String getDiveMaster() {
//        return diveMaster;
//    }
//
//    public void setDiveMaster(String diveMaster) {
//        this.diveMaster = diveMaster;
//    }

    public String getDiveNotes() {
        return diveNotes;
    }

    public void setDiveNotes(String diveNotes) {
        this.diveNotes = diveNotes;
    }

    public int getDiveNumber() {
        return diveNumber;
    }

    public void setDiveNumber(int diveNumber) {
        this.diveNumber = diveNumber;
    }

    public String getDivePhotosUrl() {
        return divePhotosUrl;
    }

    public void setDivePhotosUrl(String divePhotosUrl) {
        this.divePhotosUrl = divePhotosUrl;
    }

    public float getDiveRating() {
        return diveRating;
    }

    public void setDiveRating(float diveRating) {
        this.diveRating = diveRating;
    }
//
//    public String getDiveSiteSnapshot() {
//        return diveSite;
//    }
//
//    public void setDiveSite(String diveSite) {
//        this.diveSite = diveSite;
//    }

    public String getDiveSiteUid() {
        return diveSiteUid;
    }

    public void setDiveSiteUid(String diveSiteUid) {
        this.diveSiteUid = diveSiteUid;
    }

//    @Exclude
//    public void setDiveSiteIncludingItsLocation(DiveSite diveSite) {
//        this.diveSiteUid = diveSite.getDiveSiteUid();
//        this.diveSite = diveSite.getDiveSiteName();
//        this.area = diveSite.getArea();
//        this.state = diveSite.getState();
//        this.country = diveSite.getCountry();
//    }

    public String getDiveSiteTimeZoneID() {
        return diveSiteTimeZoneID;
    }

    public void setDiveSiteTimeZoneID(String diveSiteTimeZoneID) {
        this.diveSiteTimeZoneID = diveSiteTimeZoneID;
    }

    public long getDiveStart() {
        return diveStart;
    }

    public void setDiveStart(long diveStart) {
        this.diveStart = diveStart;
    }

    public String getDiveStyle() {
        return diveStyle;
    }

    public void setDiveStyle(String diveStyle) {
        this.diveStyle = diveStyle;
    }

    public String getDiveType() {
        return diveType;
    }

    public void setDiveType(String diveType) {
        this.diveType = diveType;
    }

    public Double getEndingTankPressure() {
        return endingTankPressure;
    }

    public void setEndingTankPressure(Double endingTankPressure) {
        this.endingTankPressure = endingTankPressure;
    }

    public String getEquipmentList() {
        return equipmentList;
    }

    public void setEquipmentList(String equipmentList) {
        this.equipmentList = equipmentList;
    }

    public String getMarineLife() {
        return marineLife;
    }

    public void setMarineLife(String marineLife) {
        this.marineLife = marineLife;
    }

    public Double getMaximumDepth() {
        return maximumDepth;
    }

    public void setMaximumDepth(Double maximumDepth) {
        this.maximumDepth = maximumDepth;
    }

    public String getNextDiveLogUid() {
        return nextDiveLogUid;
    }

    public void setNextDiveLogUid(String nextDiveLogUid) {
        this.nextDiveLogUid = nextDiveLogUid;
    }

    public String getPreviousDiveLogUid() {
        return previousDiveLogUid;
    }

    public void setPreviousDiveLogUid(String previousDiveLogUid) {
        this.previousDiveLogUid = previousDiveLogUid;
    }

    public String getSeaCondition() {
        return seaCondition;
    }

    public void setSeaCondition(String seaCondition) {
        this.seaCondition = seaCondition;
    }

    public Double getStartingTankPressure() {
        return startingTankPressure;
    }

    public void setStartingTankPressure(Double startingTankPressure) {
        this.startingTankPressure = startingTankPressure;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getSurfaceInterval() {
        return surfaceInterval;
    }

    public void setSurfaceInterval(long surfaceInterval) {
        this.surfaceInterval = surfaceInterval;
    }

    public String getTankType() {
        return tankType;
    }

    public void setTankType(String tankType) {
        this.tankType = tankType;
    }

    public String getTissueLoadingColor() {
        return tissueLoadingColor;
    }

    public void setTissueLoadingColor(String tissueLoadingColor) {
        this.tissueLoadingColor = tissueLoadingColor;
    }

    public int getTissueLoadingValue() {
        return tissueLoadingValue;
    }

    public void setTissueLoadingValue(int tissueLoadingValue) {
        this.tissueLoadingValue = tissueLoadingValue;
    }

    public Double getVisibility() {
        return visibility;
    }

    public void setVisibility(Double visibility) {
        this.visibility = visibility;
    }

    public Double getWaterTemperature() {
        return waterTemperature;
    }

    public void setWaterTemperature(Double waterTemperature) {
        this.waterTemperature = waterTemperature;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public Double getWeightUsed() {
        return weightUsed;
    }

    public void setWeightUsed(Double weightUsed) {
        this.weightUsed = weightUsed;
    }

    public String getDiveBuddyPersonUid() {
        return diveBuddyPersonUid;
    }

    public void setDiveBuddyPersonUid(String diveBuddyPersonUid) {
        this.diveBuddyPersonUid = diveBuddyPersonUid;
    }

    public String getDiveCompanyPersonUid() {
        return diveCompanyPersonUid;
    }

    public void setDiveCompanyPersonUid(String diveCompanyPersonUid) {
        this.diveCompanyPersonUid = diveCompanyPersonUid;
    }

    public String getDiveMasterPersonUid() {
        return diveMasterPersonUid;
    }

    public void setDiveMasterPersonUid(String diveMasterPersonUid) {
        this.diveMasterPersonUid = diveMasterPersonUid;
    }

    public Boolean isSequencingRequired() {
        return sequencingRequired;
    }

    public void setSequencingRequired(Boolean sequencingRequired) {
        this.sequencingRequired = sequencingRequired;
    }

    @Override
    public String toString() {
//        return getDisplayName();
        return "Dive Number " + String.valueOf(diveNumber);
    }

    //endregion

    @Exclude
    public String getDiveStartDayDateTime() {
        return MyMethods.getDayDateTimeString(diveStart, diveSiteTimeZoneID);
    }

    @Exclude
    public String getDiveStartDateTime() {
        return MyMethods.getDateTimeString(diveStart, diveSiteTimeZoneID);
    }

    @Exclude
    public String getDiveStartTime() {
        return MyMethods.getTimeString(diveStart, diveSiteTimeZoneID);
    }

    @Exclude
    public String getDiveEndDayDateTime() {
        return MyMethods.getDayDateTimeString(diveEnd, diveSiteTimeZoneID);
    }

    @Exclude
    public String getDiveEndDateTime() {
        return MyMethods.getDateTimeString(diveEnd, diveSiteTimeZoneID);
    }

    @Exclude
    public String getDiveEndTime() {
        return MyMethods.getTimeString(diveEnd, diveSiteTimeZoneID);
    }


//    @Exclude
//    public String getDisplayName() {
//        return "Dive " + diveNumber + ": " + diveSite;
//    }


    @Exclude
    public static DatabaseReference nodeReefGuides(@NonNull String userUid) {
        return dbReference.child(NODE_DIVE_LOGS).child(userUid);
    }

    @Exclude
    public static DatabaseReference nodeUserDiveLog(@NonNull String userUid, @NonNull String diveLogUid) {
        return dbReference.child(NODE_DIVE_LOGS).child(userUid).child(diveLogUid);
    }

    @Exclude
    public static DatabaseReference nodeUserDiveLogNotes(@NonNull String userUid,
                                                         @NonNull String diveLogUid) {
        return dbReference.child(NODE_DIVE_LOGS).child(userUid).child(diveLogUid)
                .child(NODE_DIVE_LOG_NOTES);
    }

    @Exclude
    public static DatabaseReference nodeUserDiveLogEquipmentList(@NonNull String userUid,
                                                                 @NonNull String diveLogUid) {
        return dbReference.child(NODE_DIVE_LOGS).child(userUid).child(diveLogUid)
                .child(NODE_DIVE_LOG_EQUIPMENT);
    }

    @Exclude
    public static String save(@NonNull String userUid, @NonNull DiveLog diveLog) {
        if (diveLog.getDiveLogUid() == null
                || diveLog.getDiveLogUid().isEmpty()
                || diveLog.getDiveLogUid().equals(MySettings.NOT_AVAILABLE)) {
            String newDiveLogUid = nodeReefGuides(userUid).push().getKey();
            diveLog.setDiveLogUid(newDiveLogUid);
            Timber.i("Created diveLog number %d.", diveLog.getDiveNumber());
        }

        nodeReefGuides(userUid).child(diveLog.getDiveLogUid()).setValue(diveLog);
        Timber.i("Saved diveLog number %d.", diveLog.getDiveNumber());
        return diveLog.getDiveLogUid();
    }

    public static void saveTissueLoading(@NonNull final String userUid, @NonNull String activeDiveLogUid,
                                         @NonNull final String tissueLoadingColor, final int tissueLoadingValue) {

        nodeUserDiveLog(userUid, activeDiveLogUid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final DiveLog activeDiveLog = dataSnapshot.getValue(DiveLog.class);
                activeDiveLog.setTissueLoadingColor(tissueLoadingColor);
                activeDiveLog.setTissueLoadingValue(tissueLoadingValue);
                save(userUid, activeDiveLog);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
            }
        });

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

//    @Exclude
//    public String getAirUsed() {
//        int startingPressure = Integer.parseInt(startingTankPressure);
//        int endingPressure = Integer.parseInt(endingTankPressure);
//        int airUsed = startingPressure - endingPressure;
//        return NumberFormat.getNumberInstance(Locale.US).format(airUsed);
//    }

    @Exclude
    public String getTissueLoading() {
        return "Tis: " + String.valueOf(tissueLoadingValue) + " " + tissueLoadingColor;
    }

    //region DiveLog Person Updates
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

    public static void updateDiveLogsWithDefaultPerson(String userUid, Person personBeingDeleted) {

        if (personBeingDeleted.getDiveLogsAsBuddy().size() > 0) {
            Iterator it = personBeingDeleted.getDiveLogsAsBuddy().entrySet().iterator();
            Person defaultBuddy = Person.getDefaultPerson(R.id.btnDiveBuddy);
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                updateDiveLogWithDiveBuddy(userUid, pair.getKey().toString(), defaultBuddy);
                it.remove(); // avoids a ConcurrentModificationException
            }
        }

        if (personBeingDeleted.getDiveLogsAsMaster().size() > 0) {
            Iterator it = personBeingDeleted.getDiveLogsAsMaster().entrySet().iterator();
            Person defaultDiveMaster = Person.getDefaultPerson(R.id.btnDiveMaster);
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                updateDiveLogWithDiveMaster(userUid, pair.getKey().toString(), defaultDiveMaster);
                it.remove(); // avoids a ConcurrentModificationException
            }
        }

        if (personBeingDeleted.getDiveLogsAsCompany().size() > 0) {
            Iterator it = personBeingDeleted.getDiveLogsAsCompany().entrySet().iterator();
            Person defaultCompany = Person.getDefaultPerson(R.id.btnCompany);
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                updateDiveLogWithDiveCompany(userUid, pair.getKey().toString(), defaultCompany);
                it.remove(); // avoids a ConcurrentModificationException
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
//                    diveLog.setDiveBuddy(diveBuddy.getName());
                    diveLog.setDiveBuddyPersonUid(diveBuddy.getPersonUid());
                    DiveLog.save(userUid, diveLog);
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
//                    diveLog.setDiveMaster(diveMaster.getName());
                    diveLog.setDiveMasterPersonUid(diveMaster.getPersonUid());
                    DiveLog.save(userUid, diveLog);
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
//                    diveLog.setDiveCompany(diveCompany.getName());
                    diveLog.setDiveCompanyPersonUid(diveCompany.getPersonUid());
                    DiveLog.save(userUid, diveLog);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
            }
        });
    }
    //endregion DiveLog Person Updates

    //region DiveLog SelectionValue Updates
    public static void updateDiveLogsWithSelectionValue(@NonNull String userUid,
                                                        @NonNull String nodeName,
                                                        @NonNull SelectionValue selectionValue) {

        if (selectionValue.getDiveLogs().size() > 0) {
            Iterator it = selectionValue.getDiveLogs().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                updateDiveLogWithSelectionValue(userUid, pair.getKey().toString(), nodeName, selectionValue);
                it.remove(); // avoids a ConcurrentModificationException
            }
        }
    }


    public static void updateDiveLogsWithDefaultSelectionValue(@NonNull String userUid,
                                                               @NonNull String nodeName,
                                                               @NonNull SelectionValue selectionValue) {
        if (selectionValue.getDiveLogs().size() > 0) {
            SelectionValue defaultSelectionValue = SelectionValue.getDefault(nodeName);
            if (defaultSelectionValue != null) {
                Iterator it = selectionValue.getDiveLogs().entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    updateDiveLogWithSelectionValue(userUid, pair.getKey().toString(), nodeName, defaultSelectionValue);
                    it.remove(); // avoids a ConcurrentModificationException
                }
            }
        }
    }

    private static void updateDiveLogWithSelectionValue(@NonNull final String userUid,
                                                        @NonNull String diveLogUid,
                                                        @NonNull final String nodeName,
                                                        @NonNull final SelectionValue selectionValue) {

        DiveLog.nodeUserDiveLog(userUid, diveLogUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    DiveLog diveLog = dataSnapshot.getValue(DiveLog.class);
                    switch (nodeName) {
                        case SelectionValue.NODE_AREA_VALUES:
                        case SelectionValue.NODE_STATE_VALUES:
                        case SelectionValue.NODE_COUNTRY_VALUES:
                            Timber.e("updateDiveLogWithSelectionValue(): DiveLog area, state, or country must be updated by DiveSite");
                            break;
                        case SelectionValue.NODE_CURRENT_VALUES:
                            diveLog.setCurrentCondition(selectionValue.getValue());
                            break;
                        case SelectionValue.NODE_DIVE_ENTRY_VALUES:
                            diveLog.setDiveEntry(selectionValue.getValue());
                            break;
                        case SelectionValue.NODE_DIVE_STYLE_VALUES:
                            diveLog.setDiveStyle(selectionValue.getValue());
                            break;
                        case SelectionValue.NODE_DIVE_TANK_VALUES:
                            diveLog.setTankType(selectionValue.getValue());
                            break;
                        case SelectionValue.NODE_DIVE_TYPE_VALUES:
                            diveLog.setDiveType(selectionValue.getValue());
                            break;
                        case SelectionValue.NODE_SEA_CONDITION_VALUES:
                            diveLog.setSeaCondition(selectionValue.getValue());
                            break;
                        case SelectionValue.NODE_WEATHER_CONDITION_VALUES:
                            diveLog.setWeatherCondition(selectionValue.getValue());
                            break;
                    }

                    DiveLog.save(userUid, diveLog);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
            }
        });
    }
    //endregion


    @Exclude
    public ArrayList<String> getCsvArrayList() {
        ArrayList<String> csvArray = new ArrayList<>();
        csvArray.add(String.valueOf(diveNumber));
        csvArray.add(String.valueOf(diveStart));
        csvArray.add(String.valueOf(bottomTime));
//        csvArray.add(diveSite);
        csvArray.add(String.valueOf(maximumDepth));
        csvArray.add(area);
        csvArray.add(state);
        csvArray.add(country);
//        csvArray.add(diveBuddy);
//        csvArray.add(diveMaster);
//        csvArray.add(diveCompany);
        csvArray.add(tissueLoadingColor);
        csvArray.add(String.valueOf(tissueLoadingValue));
        csvArray.add(tankType);
        csvArray.add(String.valueOf(diveRating));
        csvArray.add(String.valueOf(weightUsed));
        csvArray.add(String.valueOf(startingTankPressure));
        csvArray.add(String.valueOf(endingTankPressure));
        csvArray.add(String.valueOf(airTemperature));
        csvArray.add(String.valueOf(waterTemperature));
        csvArray.add(String.valueOf(visibility));
        csvArray.add(currentCondition);
        csvArray.add(diveEntry);
        csvArray.add(diveStyle);
        csvArray.add(diveType);
        csvArray.add(seaCondition);
        csvArray.add(weatherCondition);
        csvArray.add(diveNotes);
        csvArray.add(marineLife);
        csvArray.add(equipmentList);
        csvArray.add(divePhotosUrl);
        csvArray.add(String.valueOf(accumulatedBottomTimeToDate));
        csvArray.add(String.valueOf(diveEnd));
        csvArray.add(String.valueOf(surfaceInterval));
        Double airUsed = startingTankPressure - endingTankPressure;
        csvArray.add(String.valueOf(airUsed));
        csvArray.add(diveSiteTimeZoneID);
        csvArray.add(previousDiveLogUid);
        csvArray.add(diveLogUid);
        csvArray.add(nextDiveLogUid);
        return csvArray;
    }


    //region Dive Site Updates
    public static void updateDiveLogsWithDiveSite(String userUid, DiveSite diveSite) {
        if (diveSite.getDiveLogs().size() > 0) {
            Iterator it = diveSite.getDiveLogs().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                updateDiveLogWithDiveSite(userUid, pair.getKey().toString(), diveSite);
                it.remove(); // avoids a ConcurrentModificationException
            }
        }
    }

    public static void updateDiveLogsWithDiveSiteDefaultValues(@NonNull final String userUid,
                                                         @NonNull final DiveSite diveSiteBeingDeleted) {
        if (diveSiteBeingDeleted.getDiveLogs().size() > 0) {
            Iterator it = diveSiteBeingDeleted.getDiveLogs().entrySet().iterator();
            DiveSite defaultDiveSite = DiveSite.getDefaultDiveSite();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                updateDiveLogWithDiveSite(userUid, pair.getKey().toString(), defaultDiveSite);
                it.remove(); // avoids a ConcurrentModificationException
            }
        }
    }


    private static void updateDiveLogWithDiveSite(@NonNull final String userUid,
                                                  @NonNull final String diveLogUid,
                                                  @NonNull final DiveSite diveSite) {
        DiveLog.nodeUserDiveLog(userUid, diveLogUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    DiveLog diveLog = dataSnapshot.getValue(DiveLog.class);
                    if (diveLog != null) {
                        diveLog.setDiveSiteUid(diveSite.getDiveSiteUid());
                        diveLog.setArea(diveSite.getArea());
                        diveLog.setState(diveSite.getState());
                        diveLog.setCountry(diveSite.getCountry());
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
    //endregion Dive Site Updates

    public static void sequenceDiveLogs(@NonNull final String userUid, final String startingDiveLogUid,
                                        final boolean showProgressBar) {

        if (showProgressBar) {
            EventBus.getDefault().post(new MyEvents.showProgressBar("Sequencing Dive Logs ..."));
        }


        DiveLog.nodeReefGuides(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    new RetrieveDiveLogsAsync(userUid, startingDiveLogUid,
                            showProgressBar, dataSnapshot).execute();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
            }
        });

    }

    private static class RetrieveDiveLogsAsync extends AsyncTask<Void, Void, List<DiveLog>> {
        private String mUserUid;
        private String mStartingDiveLogUid;
        private boolean mShowProgressBar;
        private DataSnapshot mDataSnapshot;

        public RetrieveDiveLogsAsync(String userUid, String startingDiveLogUid,
                                     boolean showProgressBar, DataSnapshot dataSnapshot) {
            mUserUid = userUid;
            mStartingDiveLogUid = startingDiveLogUid;
            mShowProgressBar = showProgressBar;
            mDataSnapshot = dataSnapshot;
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
                new SequenceDiveLogsAsync(mUserUid, diveLogs,
                        mStartingDiveLogUid, mShowProgressBar).execute();
            }
        }
    }

    private static class SequenceDiveLogsAsync extends AsyncTask<Void, Void, Void> {
        private String mUserUid;
        private String mStartingDiveLogUid;
        private List<DiveLog> mDiveLogs;
        private boolean mShowProgressBar;

        public SequenceDiveLogsAsync(String userUid, List<DiveLog> diveLogs,
                                     String startingDiveLogUid, boolean showProgressBar) {
            mUserUid = userUid;
            mDiveLogs = diveLogs;
            mStartingDiveLogUid = startingDiveLogUid;
            mShowProgressBar = showProgressBar;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Timber.i("SequenceDiveLogsAsync: doInBackground()");
            Collections.sort(mDiveLogs, DiveLog.mAscendingStartTime);
            sequenceSortedDiveLogs(mUserUid, mDiveLogs, mStartingDiveLogUid);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Timber.i("SequenceDiveLogsAsync: onPostExecute()");
            if (mShowProgressBar) {
                EventBus.getDefault().post(new MyEvents.hideProgressBar(false));
            }
        }

        private void sequenceSortedDiveLogs(String userUid, List<DiveLog> diveLogs, String startingDiveLogUid) {
            boolean isDiveLogDirty;
            long diveEnd;
            DiveLog previousDiveLog;
            DiveLog nextDiveLog;

            int startingDiveLogIndex = 0;
            if (startingDiveLogUid != null) {
                startingDiveLogIndex = MyMethods.findDiveLogPosition(diveLogs, startingDiveLogUid);
            }
            int previousDiveLogIndex = startingDiveLogIndex - 1;
            int nextDiveLogIndex = startingDiveLogIndex + 1;
            long accumulatedBottomTime = 0;
            if (previousDiveLogIndex > -1) {
                accumulatedBottomTime = diveLogs.get(previousDiveLogIndex).getAccumulatedBottomTimeToDate();
            }

            for (int i = startingDiveLogIndex; i < diveLogs.size() - 1; i++) {
                isDiveLogDirty = false;
                DiveLog diveLog = diveLogs.get(i);
                Timber.i("sequenceSortedDiveLogs(): Dive %d.", diveLog.getDiveNumber());
                if (diveLog.getDiveNumber() != nextDiveLogIndex) {
                    diveLog.setDiveNumber(nextDiveLogIndex);
                    isDiveLogDirty = true;
                }

                // accumulate bottom time
                accumulatedBottomTime += diveLog.getBottomTime();
                if (accumulatedBottomTime != diveLog.getAccumulatedBottomTimeToDate()) {
                    diveLog.setAccumulatedBottomTimeToDate(accumulatedBottomTime);
                    isDiveLogDirty = true;
                }

                // calculate surface interval and set previous dive uid
                if (previousDiveLogIndex > -1) {
                    previousDiveLog = diveLogs.get(previousDiveLogIndex);
                    long surfaceInterval = diveLog.getDiveStart() - previousDiveLog.getDiveEnd();
                    if (diveLog.getSurfaceInterval() != surfaceInterval) {
                        diveLog.setSurfaceInterval(surfaceInterval);
                        isDiveLogDirty = true;
                    }
                    if (!diveLog.getPreviousDiveLogUid().equals(previousDiveLog.getDiveLogUid())) {
                        diveLog.setPreviousDiveLogUid(previousDiveLog.getDiveLogUid());
                        isDiveLogDirty = true;
                    }

                } else {
                    if (!diveLog.getPreviousDiveLogUid().equals(MySettings.NOT_AVAILABLE)) {
                        diveLog.setPreviousDiveLogUid(MySettings.NOT_AVAILABLE);
                        diveLog.setSurfaceInterval(0);
                        isDiveLogDirty = true;
                    }
                }

                // calculate dive end time
                diveEnd = diveLog.getDiveStart() + diveLog.getBottomTime();
                if (diveLog.getDiveEnd() != diveEnd) {
                    diveLog.setDiveEnd(diveEnd);
                    isDiveLogDirty = true;
                }

                // set next DiveLog uid
                if (nextDiveLogIndex < diveLogs.size()) {
                    nextDiveLog = diveLogs.get(nextDiveLogIndex);
                    if (!diveLog.getNextDiveLogUid().equals(nextDiveLog.getDiveLogUid())) {
                        diveLog.setNextDiveLogUid(nextDiveLog.getDiveLogUid());
                        isDiveLogDirty = true;
                    }
                } else {
                    if (!diveLog.getNextDiveLogUid().equals(MySettings.NOT_AVAILABLE)) {
                        diveLog.setNextDiveLogUid(MySettings.NOT_AVAILABLE);
                        isDiveLogDirty = true;
                    }
                }

                if (diveLog.isSequencingRequired() == null) {
                    diveLog.setSequencingRequired(false);
                    isDiveLogDirty = true;
                }

                if (isDiveLogDirty) {
                    DiveLog.save(userUid, diveLog);
                }
                previousDiveLogIndex++;
                nextDiveLogIndex++;
            }
        }
    }*/

}
