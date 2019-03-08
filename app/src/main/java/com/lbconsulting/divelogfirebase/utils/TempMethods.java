package com.lbconsulting.divelogfirebase.utils;

/**
 * This class holds static A1List temporary methods
 */
public class TempMethods {
/*

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

//    private static final int ACCUM_BOTTOM_TIME_TO_DATE = 31;
//    private static final int DIVE_END = 32;
//    private static final int SURFACE_INTERVAL = 33;
//    private static final int AIR_USED = 34;
//    private static final int DIVE_SITE_TIMEZONE_ID = 35;
//    private static final int PREVIOUS_DIVE_LOG_UID = 36;
//    private static final int DIVE_LOG_UID = 37;
//    private static final int NEXT_DIVE_LOG_UID = 38;
    //endregion

    public TempMethods() {

    }

    //    private static MyFirebaseArray mDiveSitesArray;
    private static boolean mAreDiveSitesLoaded;
    private static List<DiveSite> mDiveSites;

    //    private static MyFirebaseArray mPeopleArray;
    private static boolean mArePeopleLoaded;
    private static List<Person> mPeople;

    public static void readDiveSitesCsvFile(Context context, final String userUid,
                                            final String fileName) {
        InputStream fis = null;
        final int DIVE_SITE_NAME = 0;
        final int DIVE_SITE_AREA = 1;
        final int DIVE_SITE_STATE = 2;
        final int DIVE_SITE_COUNTRY = 3;

        try {
            fis = context.getAssets().open(fileName);

            if (fis != null) {

                // prepare the file for reading
                InputStreamReader chapterReader = new InputStreamReader(fis);
                BufferedReader buffReader = new BufferedReader(chapterReader);

                String line;
                int diveSiteCount = 0;

                do {
                    line = buffReader.readLine();
                    if (line != null) {
                        String[] fields = line.split(",");
                        if (fields.length == 4) {
                            DiveSite diveSite = new DiveSite();
                            diveSite.setDiveSiteName(fields[DIVE_SITE_NAME]);
                            diveSite.setArea(fields[DIVE_SITE_AREA]);
                            diveSite.setState(fields[DIVE_SITE_STATE]);
                            diveSite.setCountry(fields[DIVE_SITE_COUNTRY]);
                            diveSite.setDiveSiteUid(MySettings.NOT_AVAILABLE);
                            DiveSite.save(userUid, diveSite);
                            diveSiteCount++;
                        }
                    }

                } while (line != null);

                Timber.i("readDiveSitesCsvFile(): created %d DiveSites.", diveSiteCount);

            }
        } catch (Exception e) {
            Timber.e("readDiveSitesCsvFile(): Exception: %s.", e.getMessage());
        } finally {
            // close the file.
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                Timber.e("readDiveSitesCsvFile(): Exception: %s.", e.getMessage());
            }
        }
    }

    public static void readPeopleCsvFile(Context context, final String userUid, final String
            fileName) {
        InputStream fis = null;

        try {
            fis = context.getAssets().open(fileName);

            if (fis != null) {

                // prepare the file for reading
                InputStreamReader chapterReader = new InputStreamReader(fis);
                BufferedReader buffReader = new BufferedReader(chapterReader);

                String line;
                int peopleCount = 0;

                do {
                    line = buffReader.readLine();
                    if (line != null) {
                        Person person = new Person();
                        person.setName(line);
                        person.setPersonUid(MySettings.NOT_AVAILABLE);
                        Person.save(userUid, person);
                        peopleCount++;
                    }

                } while (line != null);

                Timber.i("readPeopleCsvFile(): created %d people.", peopleCount);

            }
        } catch (Exception e) {
            Timber.e("readPeopleCsvFile(): Exception: %s.", e.getMessage());
        } finally {
            // close the file.
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                Timber.e("readPeopleCsvFile(): Exception: %s.", e.getMessage());
            }
        }
    }

    public static void readDiveLogCsvFile(final Context context, final String userUid, final
    String fileName) {

        mAreDiveSitesLoaded = false;
        mArePeopleLoaded = false;
        mDiveSites = new ArrayList<>();
        mPeople = new ArrayList<>();

        Query diveSitesQuery = DiveSite.nodeUserDiveSites(userUid).orderByChild(DiveSite.FIELD_DIVE_SITE_NAME);
        diveSitesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        DiveSite diveSite = snapshot.getValue(DiveSite.class);
                        mDiveSites.add(diveSite);
                    }
                    mAreDiveSitesLoaded = true;
                    Timber.i("onDataChange(): found %d DiveSites.", mDiveSites.size());
                    resumeReadDiveLogCsvFile(context, userUid, fileName);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
            }
        });

        Query peopleQuery = Person.nodeUserPersons(userUid);
        peopleQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Person person = snapshot.getValue(Person.class);
                        mPeople.add(person);
                    }
                    mArePeopleLoaded = true;
                    Timber.i("onDataChange(): found %d People.", mPeople.size());
                    resumeReadDiveLogCsvFile(context, userUid, fileName);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.e("onCancelled(): DatabaseError: %s.", databaseError.getMessage());
            }
        });

    }

    private static void resumeReadDiveLogCsvFile(Context context, String userUid, String fileName) {
        if (!mAreDiveSitesLoaded || !mArePeopleLoaded) {
            return;
        }
        InputStream fis = null;

        try {
            fis = context.getAssets().open(fileName);

            if (fis != null) {

                // prepare the file for reading
                InputStreamReader chapterReader = new InputStreamReader(fis);
                BufferedReader buffReader = new BufferedReader(chapterReader);

                String line;
                int lineCount = 0;
                int diveLogCount = 0;
                String defaultDiveSiteTimezoneId = "Pacific/Honolulu";
//                String defaultDiveSiteTimezoneId = Calendar.getInstance().getTimeZone().getID();

                do {
                    line = buffReader.readLine();
                    lineCount++;
                    if (line != null
                            && lineCount > 3
                            && !line.startsWith("**")) {
                        ArrayList<String> fields = MyMethods.parseDiveLogRecord(line);
                        DiveLog diveLog = new DiveLog();
                        diveLog = fillDiveLogWithFields(diveLog, fields, defaultDiveSiteTimezoneId);
                        String diveLogUid = DiveLog.save(userUid, diveLog);
                        diveLog.setDiveLogUid(diveLogUid);

                        diveLog = fillDiveLogDiveSite(userUid, diveLog, mDiveSites, fields);
                        diveLog = fillDiveLogWithPeople(userUid, diveLog, mPeople, fields);

                        DiveLog.save(userUid, diveLog);
                        diveLogCount++;
                    }

                } while (line != null);

                Timber.i("ReadDiveLogCsvFile(): created %d DiveLogs.", diveLogCount);

            }
        } catch (Exception e) {
            Timber.e("readDiveLogCsvFile(): Exception: %s.", e.getMessage());
        } finally {
            // close the file.
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                Timber.e("readDiveLogCsvFile(): Exception: %s.", e.getMessage());
            }
        }
    }

    private static DiveLog fillDiveLogWithPeople(String userUid, DiveLog diveLog,
                                                 List<Person> people,
                                                 ArrayList<String> fields) {
        String diveBuddy = fields.get(DIVE_BUDDY);
        String diveMaster = fields.get(DIVE_MASTER);
        String diveCompany = fields.get(DIVE_COMPANY);

        Person person = null;
        if (diveBuddy.isEmpty()) {
            diveLog.setDiveBuddyPersonUid(MySettings.NOT_AVAILABLE);
        } else {
            person = Person.findPersonByName(diveBuddy, people);
            if (person != null) {
                diveLog.setDiveBuddyPersonUid(person.getPersonUid());
                if (!person.isBuddy()) {
                    person.setBuddy(true);
                    Person.save(userUid, person);
                }
                Person.addBuddy(userUid, person.getPersonUid(), diveLog.getDiveLogUid());
            } else {
                // Create new Person as buddy
                diveLog.setDiveBuddyPersonUid(MySettings.NOT_AVAILABLE);
                Timber.d("fillDiveLogWithPeople(): Did not find DiveBuddy: \"%s\" for %s",
                         diveBuddy, diveLog.toString());
//                Person buddyPerson = new Person();
//                buddyPerson.setName(diveBuddy);
//                buddyPerson.setBuddy(true);
//                buddyPerson.setCompany(false);
//                buddyPerson.setDiveMaster(false);
//                buddyPerson.setPersonUid(MySettings.NOT_AVAILABLE);
//                String buddyPersonUid = Person.save(userUid, buddyPerson);
//                people.add(buddyPerson);
//                Person.addBuddy(userUid, buddyPersonUid, diveLog.getDiveLogUid());
            }
        }

        person = null;
        if (diveMaster.isEmpty()) {
            diveLog.setDiveMasterPersonUid(MySettings.NOT_AVAILABLE);
        } else {
            person = Person.findPersonByName(diveMaster, people);
            if (person != null) {
                diveLog.setDiveMasterPersonUid(person.getPersonUid());
                if (!person.isDiveMaster()) {
                    person.setDiveMaster(true);
                    Person.save(userUid, person);
                }
                Person.addDiveMaster(userUid, person.getPersonUid(), diveLog.getDiveLogUid());
            } else {
                diveLog.setDiveMasterPersonUid(MySettings.NOT_AVAILABLE);
                Timber.d("fillDiveLogWithPeople(): Did not find DiveMaster: \"%s\" for %s",
                         diveBuddy, diveLog.toString());
                // Create new Person as diveMaster
//                Person masterPerson = new Person();
//                masterPerson.setName(diveMaster);
//                masterPerson.setBuddy(false);
//                masterPerson.setCompany(false);
//                masterPerson.setDiveMaster(true);
//                masterPerson.setPersonUid(MySettings.NOT_AVAILABLE);
//                String diveMasterPersonUid = Person.save(userUid, masterPerson);
//                people.add(masterPerson);
//                Person.addDiveMaster(userUid, diveMasterPersonUid, diveLog.getDiveLogUid());
            }
        }

        person = null;
        if (diveCompany.isEmpty()) {
            diveLog.setDiveCompanyPersonUid(MySettings.NOT_AVAILABLE);
        } else {
            person = Person.findPersonByName(diveCompany, people);
            if (person != null) {
                diveLog.setDiveCompanyPersonUid(person.getPersonUid());
                if (!person.isCompany()) {
                    person.setCompany(true);
                    Person.save(userUid, person);
                }
                Person.addCompany(userUid, person.getPersonUid(), diveLog.getDiveLogUid());
            } else {
                diveLog.setDiveCompanyPersonUid(MySettings.NOT_AVAILABLE);
                Timber.d("fillDiveLogWithPeople(): Did not find DiveCompany: \"%s\" for %s",
                         diveBuddy, diveLog.toString());

                // Create new Person as company
//                Person companyPerson = new Person();
//                companyPerson.setName(diveCompany);
//                companyPerson.setBuddy(false);
//                companyPerson.setCompany(true);
//                companyPerson.setDiveMaster(false);
//                companyPerson.setPersonUid(MySettings.NOT_AVAILABLE);
//                String companyPersonUid = Person.save(userUid, companyPerson);
//                people.add(companyPerson);
//                Person.addCompany(userUid, companyPersonUid, diveLog.getDiveLogUid());
            }
        }


        return diveLog;
    }

    private static DiveLog fillDiveLogDiveSite(String userUid, DiveLog diveLog,
                                               List<DiveSite> diveSites, ArrayList<String> fields) {
        String diveSiteName = fields.get(DIVE_SITE);
        if (diveSiteName.isEmpty()) {
            diveLog.setDiveSiteUid(MySettings.NOT_AVAILABLE);
            return diveLog;
        }

        DiveSite diveSite = DiveSite.findDiveSite(diveSiteName, diveSites,
                                                  diveLog.getArea(), diveLog.getState(), diveLog
                                                          .getCountry());
        if (diveSite != null) {
            diveLog.setDiveSiteUid(diveSite.getDiveSiteUid());
            diveLog.setArea(diveSite.getArea());
            diveLog.setState(diveSite.getState());
            diveLog.setCountry(diveSite.getCountry());
            DiveSite.addDiveLogToDiveSite(userUid, diveSite.getDiveSiteUid(),
                                          diveLog.getDiveLogUid());
            return diveLog;
        } else {
            // set default diveSite fields
            diveLog.setDiveSiteUid(MySettings.NOT_AVAILABLE);
            diveLog.setArea(DiveSite.DEFAULT_AREA);
            diveLog.setState(DiveSite.DEFAULT_STATE);
            diveLog.setCountry(DiveSite.DEFAULT_COUNTRY);
            Timber.d("fillDiveLogDiveSite(): Did not find diveSite \"%s\"", diveSiteName);
            return diveLog;
        }
    }

    private static DiveLog fillDiveLogWithFields(DiveLog diveLog, ArrayList<String> fields,
                                                 String defaultDiveSiteTimezoneId) {
        diveLog.setSequencingRequired(false);

        diveLog.setAirTemperatureDouble(Double.parseDouble(fields.get(AIR_TEMPERATURE)));
        diveLog.setStartingTankPressureDouble(Double.parseDouble(fields.get
                (STARTING_TANK_PRESSURE)));
        diveLog.setEndingTankPressureDouble(Double.parseDouble(fields.get(ENDING_TANK_PRESSURE)));

        int startingPressure = Integer.parseInt(fields.get(STARTING_TANK_PRESSURE));
        int endingPressure = Integer.parseInt(fields.get(ENDING_TANK_PRESSURE));
        int airUsed = startingPressure - endingPressure;
        diveLog.setAirUsedDouble((double) airUsed);

        diveLog.setMaximumDepthDouble(Double.parseDouble(fields.get(MAXIMUM_DEPTH)));
        diveLog.setVisibilityDouble(Double.parseDouble(fields.get(VISIBILITY)));
        diveLog.setWaterTemperatureDouble(Double.parseDouble(fields.get(WATER_TEMPERATURE)));
        diveLog.setWeightUsedDouble(Double.parseDouble(fields.get(WEIGHT_USED)));
        diveLog.setDiveRating(Integer.parseInt(fields.get(DIVE_RATING)));

        diveLog.setDiveNumber(Integer.parseInt(fields.get((DIVE_NUMBER))));
        diveLog.setTissueLoadingValue(Integer.parseInt(fields.get((TISSUE_LOADING_VALUE))));

        String diveStartString = fields.get(DIVE_START);
        long diveStartMills = getDateTimeMilliSeconds(diveStartString);
        diveLog.setDiveStart(diveStartMills);


        long minutesMills = TimeUnit.MINUTES.toMillis(Long.parseLong(fields.get(BOTTOM_TIME)));
        diveLog.setBottomTime(minutesMills);

        diveLog.setDiveEnd(diveLog.getDiveStart() + diveLog.getBottomTime());
        diveLog.setSurfaceInterval(-1);
        diveLog.setAccumulatedBottomTimeToDate(-1);
        diveLog.setDiveSiteTimeZoneID(defaultDiveSiteTimezoneId);

        diveLog.setArea(fields.get(AREA));
        diveLog.setCountry(fields.get(COUNTRY));
        diveLog.setState(fields.get(STATE));

        diveLog.setCurrentCondition(fields.get(CURRENT_CONDITION));
        diveLog.setDiveEntry(fields.get(DIVE_ENTRY));
        diveLog.setDiveLogUid(MySettings.NOT_AVAILABLE);
        diveLog.setDiveNotes(fields.get(DIVE_NOTES));
        diveLog.setDivePhotosUrl(MySettings.NOT_AVAILABLE);
        diveLog.setDiveSiteTimeZoneID(MySettings.NOT_AVAILABLE);
        diveLog.setDiveStyle(fields.get(DIVE_STYLE));
        diveLog.setDiveType(fields.get(DIVE_TYPE));
        diveLog.setEquipmentList(fields.get(EQUIPMENT_LIST));
        diveLog.setEquipmentList(fields.get(MARINE_LIFE));

        diveLog.setNextDiveLogUid(MySettings.NOT_AVAILABLE);
        diveLog.setPreviousDiveLogUid(MySettings.NOT_AVAILABLE);

        diveLog.setSeaCondition(fields.get(SEA_CONDITION));
        diveLog.setTankType(fields.get(TANK_TYPE));
        diveLog.setTissueLoadingColor(fields.get(TISSUE_LOADING_COLOR));
        diveLog.setWeatherCondition(fields.get(WEATHER_CONDITION));

        return diveLog;
    }

    private static long getDateTimeMilliSeconds(String diveStartString) {
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

    public static void fillDiveLogs(final String userUid) {

        DiveLog.nodeUserDiveLogs(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    DiveLog diveLog;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        diveLog = snapshot.getValue(DiveLog.class);
                        if (diveLog != null) {
                            if (diveLog.getCurrentCondition() != null
                                    && !diveLog.getCurrentCondition().equals(MySettings
                                                                                     .NOT_AVAILABLE)
                                    && !diveLog.getCurrentCondition().startsWith("[")) {
                                SelectionValue.addDiveLogToSelectionValue(userUid,
                                                                          SelectionValue
                                                                                  .NODE_CURRENT_VALUES,
                                                                          diveLog.getCurrentCondition(), diveLog.getDiveLogUid());
                            }

                            if (diveLog.getDiveEntry() != null
                                    && !diveLog.getDiveEntry().equals(MySettings.NOT_AVAILABLE)
                                    && !diveLog.getDiveEntry().startsWith("[")) {
                                SelectionValue.addDiveLogToSelectionValue(userUid,
                                                                          SelectionValue
                                                                                  .NODE_DIVE_ENTRY_VALUES,
                                                                          diveLog.getDiveEntry(),
                                                                          diveLog.getDiveLogUid());
                            }
                            if (diveLog.getDiveStyle() != null
                                    && !diveLog.getDiveStyle().equals(MySettings.NOT_AVAILABLE)
                                    && !diveLog.getDiveStyle().startsWith("[")) {
                                SelectionValue.addDiveLogToSelectionValue(userUid,
                                                                          SelectionValue
                                                                                  .NODE_DIVE_STYLE_VALUES,
                                                                          diveLog.getDiveStyle(),
                                                                          diveLog.getDiveLogUid());
                            }
                            if (diveLog.getTankType() != null
                                    && !diveLog.getTankType().equals(MySettings.NOT_AVAILABLE)
                                    && !diveLog.getTankType().startsWith("[")) {
                                String tankType = diveLog.getTankType();
                                if (tankType.equalsIgnoreCase("AL72")) {
                                    tankType = "AL072";
                                } else if (tankType.equalsIgnoreCase("AL80")) {
                                    tankType = "AL080";
                                } else if (tankType.equalsIgnoreCase("HP80")) {
                                    tankType = "HP080";
                                }

                                SelectionValue.addDiveLogToSelectionValue(userUid,
                                                                          SelectionValue
                                                                                  .NODE_DIVE_TANK_VALUES,
                                                                          tankType, diveLog
                                                                                  .getDiveLogUid());
                            }
                            if (diveLog.getDiveType() != null
                                    && !diveLog.getDiveType().equals(MySettings.NOT_AVAILABLE)
                                    && !diveLog.getDiveType().startsWith("[")) {
                                SelectionValue.addDiveLogToSelectionValue(userUid,
                                                                          SelectionValue
                                                                                  .NODE_DIVE_TYPE_VALUES,
                                                                          diveLog.getDiveType(),
                                                                          diveLog.getDiveLogUid());
                            }
                            if (diveLog.getSeaCondition() != null
                                    && !diveLog.getSeaCondition().equals(MySettings.NOT_AVAILABLE)
                                    && !diveLog.getSeaCondition().startsWith("[")) {
                                SelectionValue.addDiveLogToSelectionValue(userUid,
                                                                          SelectionValue
                                                                                  .NODE_SEA_CONDITION_VALUES,
                                                                          diveLog.getSeaCondition
                                                                                  (), diveLog
                                                                                  .getDiveLogUid());
                            }
                            if (diveLog.getWeatherCondition() != null
                                    && !diveLog.getWeatherCondition().equals(MySettings
                                                                                     .NOT_AVAILABLE)
                                    && !diveLog.getWeatherCondition().startsWith("[")) {
                                SelectionValue.addDiveLogToSelectionValue(userUid,
                                                                          SelectionValue
                                                                                  .NODE_WEATHER_CONDITION_VALUES,
                                                                          diveLog.getWeatherCondition(), diveLog.getDiveLogUid());
                            }

                        }
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static void resetDiveRatings(final String userUid) {

        DiveLog.nodeUserDiveLogs(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    DiveLog diveLog;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        diveLog = snapshot.getValue(DiveLog.class);
                        if (diveLog != null) {
                            diveLog.setDiveRating(0);
                            DiveLog.saveDiveRating(userUid, diveLog);
                        }
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static void fillDiveSiteNames(final String userUid) {
        // iterate through all diveLogs
        DiveLog.nodeUserDiveLogs(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DiveLog diveLog;
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        diveLog = snapshot.getValue(DiveLog.class);
                        if (diveLog != null) {
                            fillDiveLogDiveSiteName(diveLog);
                        }
                    }
                }
            }

            private void fillDiveLogDiveSiteName(final DiveLog diveLog) {
                DiveSite.nodeUserDiveSite(userUid, diveLog.getDiveSiteUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                DiveSite diveSite;
                                if (dataSnapshot.getValue() != null) {
                                    diveSite = dataSnapshot.getValue(DiveSite.class);
                                    DiveLog.nodeUserDiveLog(userUid, diveLog.getDiveLogUid())
                                            .child(DiveLog.FIELD_DIVE_SITE_NAME).setValue
                                            (diveSite.getDiveSiteName());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static void fillDiveSites(final String userUid) {
        DiveSite.nodeUserDiveSites(userUid).addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            DiveSite diveSite;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                diveSite = snapshot.getValue(DiveSite.class);
                                if (diveSite != null) {

                                    if (diveSite.getArea() != null
                                            && !diveSite.getArea().equals(MySettings.NOT_AVAILABLE)
                                            && !diveSite.getArea().startsWith("[")) {
                                        SelectionValue.addDiveSiteToSelectionValue(userUid,
                                                                                   SelectionValue
                                                                                           .NODE_AREA_VALUES,

                                                                                   diveSite.getArea(),
                                                                                   diveSite.getDiveSiteUid());
                                    }

                                    if (diveSite.getState() != null
                                            && !diveSite.getState().equals(MySettings.NOT_AVAILABLE)
                                            && !diveSite.getState().startsWith("[")) {
                                        SelectionValue.addDiveSiteToSelectionValue(userUid,
                                                                                   SelectionValue
                                                                                           .NODE_STATE_VALUES,
                                                                                   diveSite.getState(),
                                                                                   diveSite.getDiveSiteUid());
                                    }

                                    if (diveSite.getCountry() != null
                                            && !diveSite.getCountry().equals(MySettings
                                                                                     .NOT_AVAILABLE)
                                            && !diveSite.getCountry().startsWith("[")) {
                                        SelectionValue.addDiveSiteToSelectionValue(userUid,
                                                                                   SelectionValue
                                                                                           .NODE_COUNTRY_VALUES,
                                                                                   diveSite.getCountry(),
                                                                                   diveSite.getDiveSiteUid());
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public static void cleanDiveSites(final String userUid) {
        DiveSite.nodeUserDiveSites(userUid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<String> diveSitesForDeletion = new ArrayList<>();

                        if (dataSnapshot.getValue() != null) {
                            DiveSite diveSite;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                diveSite = snapshot.getValue(DiveSite.class);
                                if (diveSite != null) {
                                    if (diveSite.getDiveLogs() == null) {
                                        diveSitesForDeletion.add(diveSite.getDiveSiteUid());
                                    }
                                }
                            }

                            for (String diveSiteUid : diveSitesForDeletion) {
                                DiveSite.nodeUserDiveSite(userUid, diveSiteUid).removeValue();
                            }

                            int remainingDiveSites = (int) dataSnapshot.getChildrenCount() -
                                    diveSitesForDeletion.size();
                            AppMetrics.nodeDiveLogArraySize(userUid).setValue(remainingDiveSites);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public static void cleanDiveLogSelectionValues(final String userUid) {
        DiveLog.nodeUserDiveLogs(userUid)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void
                            onDataChange
                                    (DataSnapshot dataSnapshot) {

                                if (dataSnapshot
                                        .getValue() != null) {
                                    DiveLog diveLog;
                                    for (DataSnapshot snapshot :
                                            dataSnapshot.getChildren()) {
                                        diveLog = snapshot.getValue
                                                (DiveLog.class);
                                        if (diveLog != null) {
                                            if (diveLog.getArea() == null) {
                                                diveLog.setArea
                                                        (SelectionValue
                                                                 .getDefault(SelectionValue
                                                                                     .NODE_AREA_VALUES).getValue());
                                            }
                                            if (diveLog.getState() ==
                                                    null) {
                                                diveLog.setState
                                                        (SelectionValue
                                                                 .getDefault(SelectionValue
                                                                                     .NODE_STATE_VALUES).getValue());
                                            }
                                            if (diveLog.getCountry() ==
                                                    null) {
                                                diveLog.setCountry
                                                        (SelectionValue
                                                                 .getDefault(SelectionValue
                                                                                     .NODE_COUNTRY_VALUES).getValue());
                                            }
                                            if (diveLog
                                                    .getCurrentCondition
                                                            () == null) {
                                                diveLog.setCurrentCondition(SelectionValue
                                                                                    .getDefault
                                                                                            (SelectionValue
                                                                                                     .NODE_CURRENT_VALUES).getValue());
                                            }
                                            if (diveLog.getDiveEntry() ==
                                                    null) {
                                                diveLog.setDiveEntry
                                                        (SelectionValue
                                                                 .getDefault(SelectionValue
                                                                                     .NODE_DIVE_ENTRY_VALUES).getValue());
                                            }

                                            if (diveLog.getDiveStyle() ==
                                                    null) {
                                                diveLog.setDiveStyle
                                                        (SelectionValue
                                                                 .getDefault(SelectionValue
                                                                                     .NODE_DIVE_STYLE_VALUES).getValue());
                                            }
                                            if (diveLog.getTankType() ==
                                                    null) {
                                                diveLog.setTankType
                                                        (SelectionValue
                                                                 .getDefault(SelectionValue
                                                                                     .NODE_DIVE_TANK_VALUES).getValue());
                                            }
                                            if (diveLog.getDiveType() ==
                                                    null) {
                                                diveLog.setDiveType
                                                        (SelectionValue
                                                                 .getDefault(SelectionValue
                                                                                     .NODE_DIVE_TYPE_VALUES).getValue());
                                            }
                                            if (diveLog.getSeaCondition()
                                                    == null) {
                                                diveLog.setSeaCondition
                                                        (SelectionValue
                                                                 .getDefault(SelectionValue
                                                                                     .NODE_SEA_CONDITION_VALUES).getValue());
                                            }
                                            if (diveLog
                                                    .getWeatherCondition
                                                            () == null) {
                                                diveLog.setWeatherCondition(SelectionValue
                                                                                    .getDefault
                                                                                            (SelectionValue
                                                                                                     .NODE_WEATHER_CONDITION_VALUES)
                                                                                    .getValue());
                                            }

                                            DiveLog.saveDiveRating
                                                    (userUid, diveLog);
                                        }
                                    }
                                }


                            }


                            @Override
                            public void onCancelled(DatabaseError
                                                            databaseError) {

                            }
                        }

                                               );
    }


    public static void removeAllReefGuideItems(final String userUid) {
        DiveLog.nodeUserDiveLogs(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DiveLog diveLog = snapshot.getValue(DiveLog.class);
                    DiveLog.nodeUserDiveLog(userUid, diveLog.getDiveLogUid())
                            .child(DiveLog.FIELD_REEF_GUIDE_ITEMS).removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void addReefGuides(Context context) {

        String[] titles = context.getResources().getStringArray(R.array.hawaii_summaryPageTitles);
        String[] urls = context.getResources().getStringArray(R.array.hawaii_summaryPageUrls);
        String[] uids = context.getResources().getStringArray(R.array.hawaii_summaryPageUids);

        if (titles.length != urls.length || uids.length != urls.length) {
            Timber.e("addReefGuides(): Array length mismatch");
            return;
        }

        ReefGuide reefGuide;
        for (int i = 0; i < titles.length; i++) {
            reefGuide = new ReefGuide(1, i + 1, titles[i], uids[i], urls[i]);
            ReefGuide.save(ReefGuide.NODE_HAWAII, reefGuide);
        }

        // *******
        titles = context.getResources().getStringArray(R.array.caribbean_summaryPageTitles);
        urls = context.getResources().getStringArray(R.array.caribbean_summaryPageUrls);
        uids = context.getResources().getStringArray(R.array.caribbean_summaryPageUids);

        if (titles.length != urls.length || uids.length != urls.length) {
            Timber.e("addReefGuides(): Array length mismatch");
            return;
        }

        for (int i = 0; i < titles.length; i++) {
            reefGuide = new ReefGuide(0, i + 1, titles[i], uids[i], urls[i]);
            ReefGuide.save(ReefGuide.NODE_CARIBBEAN, reefGuide);
        }


        // *******
        titles = context.getResources().getStringArray(R.array.south_florida_summaryPageTitles);
        urls = context.getResources().getStringArray(R.array.south_florida_summaryPageUrls);
        uids = context.getResources().getStringArray(R.array.south_florida_summaryPageUids);

        if (titles.length != urls.length || uids.length != urls.length) {
            Timber.e("addReefGuides(): Array length mismatch");
            return;
        }

        for (int i = 0; i < titles.length; i++) {
            reefGuide = new ReefGuide(2, i + 1, titles[i], uids[i], urls[i]);
            ReefGuide.save(ReefGuide.NODE_SOUTH_FLORIDA, reefGuide);
        }

        // *******
        titles = context.getResources().getStringArray(R.array.tropical_pacific_summaryPageTitles);
        urls = context.getResources().getStringArray(R.array.tropical_pacific_summaryPageUrls);
        uids = context.getResources().getStringArray(R.array.tropical_pacific_summaryPageUids);

        if (titles.length != urls.length || uids.length != urls.length) {
            Timber.e("addReefGuides(): Array length mismatch");
            return;
        }

        for (int i = 0; i < titles.length; i++) {
            reefGuide = new ReefGuide(3, i + 1, titles[i], uids[i], urls[i]);
            ReefGuide.save(ReefGuide.NODE_TROPICAL_PACIFIC, reefGuide);
        }

    }

    public static void parseMarineLifeNotes(final String userUid) {
        DiveLog.nodeUserDiveLogs(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        DiveLog diveLog = snapshot.getValue(DiveLog.class);
                        if (diveLog != null) {
                            if (diveLog.getDiveNotes() != null
                                    && !diveLog.getDiveNotes().isEmpty()
                                    && diveLog.getDiveNotes().contains("+ ")) {
                                String[] bulletNotes = diveLog.getDiveNotes().split("\\+ ");
                                if (bulletNotes.length > 1) {
                                    for (int i = 1; i < bulletNotes.length; i++) {
                                        String bullet = bulletNotes[i].trim();
                                        if (diveLog.getMarineNotes() == null) {
                                            diveLog.setMarineNotes(new HashMap<String,
                                                    MarineNote>());
                                        }
                                        MarineNote marineNote = new MarineNote(userUid, diveLog
                                                .getDiveLogUid(), bullet);
                                        MarineNote.save(userUid, diveLog.getDiveLogUid(),
                                                        marineNote);
                                    }
                                }
                            }
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
*/
}

