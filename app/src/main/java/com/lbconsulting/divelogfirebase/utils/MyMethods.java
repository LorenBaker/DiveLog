package com.lbconsulting.divelogfirebase.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;

import com.lbconsulting.divelogfirebase.R;
import com.lbconsulting.divelogfirebase.models.DiveLog;
import com.lbconsulting.divelogfirebase.models.DiveSite;
import com.lbconsulting.divelogfirebase.models.Person;

import org.jetbrains.annotations.Contract;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * This class holds static A1List common methods
 */
public class MyMethods {

    public static final int FORMAT_DURATION_TO_MINUTES = 0;
    public static final int FORMAT_DURATION_TO_HOURS_MINUTES = 1;
    public static final int FORMAT_DURATION_TO_YEARS_DAYS_HOURS_MINUTES = 2;

    private static final SimpleDateFormat mDayDateTimeFormatter = new SimpleDateFormat("EEEE, MMM d, yyyy, h:mm a z", Locale.US);
    private static final SimpleDateFormat mDayDateFormatter = new SimpleDateFormat("EEEE, MMM d, yyyy", Locale.US);
    private static final SimpleDateFormat mDateTimeFormatter = new SimpleDateFormat("M/d/yyyy h:mm a z", Locale.US);
    private static final SimpleDateFormat mTimeFormatter = new SimpleDateFormat("h:mm a z", Locale.US);


//    private static final long millsPerSecond = 1000;
//    private static final long secondsPerMinute = 60;
//    private static final long minutesPerHour = 60;
//    public static final long millsPerHour = minutesPerHour * secondsPerMinute * millsPerSecond;
//    public static final long millsPerMinute = millsPerSecond * secondsPerMinute;
//    private static final long minutesPerDay = 24 * minutesPerHour;
//    private static long minutesPerYear = 365 * minutesPerDay;


//    public static final int DURATION_IN_MILLISECONDS = 1;
//    public static final int DURATION_IN_SECONDS = 2;
//    public static final int DURATION_IN_MINUTES = 3;
//    public static final int DURATION_IN_HOURS = 4;

//    private static final int AREA_STATE_COUNTRY = 1;
//    private static final int AREA_STATE = 2;
//    private static final int AREA_COUNTRY = 3;
//    private static final int AREA = 4;
//    private static final int STATE_COUNTRY = 5;
//    private static final int STATE = 6;
//    private static final int COUNTRY = 7;
//    private static final int NO_FILTER = 8;

    public MyMethods() {

    }

    public static String removePunctuation(String value) {
        ArrayList<String> punctuation = new ArrayList<>();
        String replacementValue = "";
        punctuation.add(".");
        punctuation.add("'");
        punctuation.add("-");

        String result = value;
        for (String mark : punctuation) {
            result = result.replace(mark, replacementValue);
        }
        return result;
    }

    @Contract("null, _ -> null")
    public static String readFromFile(Context context, String fileName) {
        if (context == null) {
            return null;
        }

        String fileString = "";

        try {
            InputStream inputStream = context.openFileInput(fileName);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                int size = inputStream.available();
                char[] buffer = new char[size];

                inputStreamReader.read(buffer);

                inputStream.close();
                fileString = new String(buffer);
            }
        } catch (Exception e) {
            Timber.e("readFromFile(): Exception: %s.", e.getMessage());
            e.printStackTrace();
        }

        return fileString;
    }

    public static String readAssetsFile(Context context, @NonNull String inFilename) {
        String fileContents = "";

        try {
            InputStream inputStream = context.getAssets().open(inFilename);
            if (inputStream != null) {
                int size = inputStream.available();
                byte[] buffer = new byte[size];
                inputStream.read(buffer);
                inputStream.close();
                fileContents = new String(buffer);
            }
        } catch (IOException e) {
            Timber.e("readAssetsFile(): IOException: %s.", e.getMessage());
        }
        return fileContents;
    }


    //region Search Methods
    public static DiveLog getDiveLog(@NonNull List<DiveLog> diveLogs, @NonNull String diveLogUid) {
        DiveLog foundDiveLog = null;
        for (DiveLog diveLog : diveLogs) {
            if (diveLog.getDiveLogUid().equals(diveLogUid)) {
                foundDiveLog = diveLog;
                break;
            }
        }
        return foundDiveLog;
    }

    public static int findPersonPosition(@NonNull List<Person> people, @NonNull String soughtPersonUid) {
        int index = 0;
        boolean foundPerson = false;
        for (Person person : people) {
            if (person.getPersonUid().equals(soughtPersonUid)) {
                foundPerson = true;
                break;
            } else
                index++;
        }
        if (!foundPerson) {
            index = -1;
        }
        return index;
    }


    public static int findDiveLogPosition(@NonNull List<DiveLog> diveLogs, @NonNull String soughtDiveLogUid) {
        int index = 0;
        boolean foundDiveLog = false;
        for (DiveLog diveLog : diveLogs) {
            if (diveLog.getDiveLogUid().equals(soughtDiveLogUid)) {
                foundDiveLog = true;
                break;
            } else
                index++;
        }
        if (!foundDiveLog) {
            index = -1;
        }
        return index;
    }


    public static int findDiveSitePosition(List<DiveSite> diveSites, String soughtDiveSiteUid) {
        int index = 0;
        boolean foundDiveSite = false;
        for (DiveSite diveSite : diveSites) {
            if (diveSite.getDiveSiteUid() != null) {
                if (diveSite.getDiveSiteUid().equals(soughtDiveSiteUid)) {
                    foundDiveSite = true;
                    break;
                }
            }
            index++;
        }
        if (!foundDiveSite) {
            index = -1;
        }

        return index;
    }
    //endregion Search Methods

    //region Day Date Time Duration Methods

    public static String getDayDateTimeString(long diveDate, String diveSiteTimeZoneID) {
        String diveDateString = "";
        try {
            TimeZone timeZone = TimeZone.getTimeZone(diveSiteTimeZoneID);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(diveDate);
            Date date = calendar.getTime();
            mDayDateTimeFormatter.setTimeZone(timeZone);
            diveDateString = mDayDateTimeFormatter.format(date);
        } catch (Exception e) {
            Timber.e("getDayDateTimeString(): Exception: %s.", e.getMessage());
        }

        return diveDateString;
    }

    public static String getDayDateString(long diveDate, String diveSiteTimeZoneID) {
        String diveDateString = "";
        try {
            TimeZone timeZone = TimeZone.getTimeZone(diveSiteTimeZoneID);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(diveDate);
            Date date = calendar.getTime();
            mDayDateFormatter.setTimeZone(timeZone);
            diveDateString = mDayDateFormatter.format(date);
        } catch (Exception e) {
            Timber.e("getDayDateTimeString(): Exception: %s.", e.getMessage());
        }

        return diveDateString;
    }

    public static String getDateTimeString(long diveDate, String diveSiteTimeZoneID) {
        String diveDateString = "";
        try {
            TimeZone timeZone = TimeZone.getTimeZone(diveSiteTimeZoneID);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(diveDate);
            Date date = calendar.getTime();
            mDateTimeFormatter.setTimeZone(timeZone);
            diveDateString = mDateTimeFormatter.format(date);
        } catch (Exception e) {
            Timber.e("getDateTimeString(): Exception: %s.", e.getMessage());
        }

        return diveDateString;
    }

    public static String getTimeString(long diveDate, String diveSiteTimeZoneID) {
        String diveDateString = "";
        try {
            TimeZone timeZone = TimeZone.getTimeZone(diveSiteTimeZoneID);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(diveDate);
            Date date = calendar.getTime();
            mTimeFormatter.setTimeZone(timeZone);
            diveDateString = mTimeFormatter.format(date);
        } catch (Exception e) {
            Timber.e("getTimeString(): Exception: %s.", e.getMessage());
        }

        return diveDateString;
    }

    public static long hoursMinutesToMilliseconds(int hours, int minutes) {
        return TimeUnit.HOURS.toMillis(hours) + TimeUnit.MINUTES.toMillis(minutes);
    }

    public static String formatMilliseconds(Context context, long duration, int formattingMode) {

        switch (formattingMode) {
            case FORMAT_DURATION_TO_MINUTES:
                return millisToMinutes(context, duration);

            case FORMAT_DURATION_TO_HOURS_MINUTES:
                return millisToHoursMinutes(context, duration);

            case FORMAT_DURATION_TO_YEARS_DAYS_HOURS_MINUTES:
                return millisToYearsDaysHoursMinutes(context, duration);

            default:
                return "N/A";
        }
    }

    private static String millisToMinutes(Context context, long durationMillis) {
        Resources res = context.getResources();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis);
        return res.getQuantityString(R.plurals.minutes_abbreviation, (int) minutes, (int) minutes);
    }

    private static String millisToHoursMinutes(Context context, long durationMillis) {
        Resources res = context.getResources();

        long hours = TimeUnit.MILLISECONDS.toHours(durationMillis);
        durationMillis = durationMillis - TimeUnit.HOURS.toMillis(hours);

        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis);

        String hoursString;
        String minutesString;
        if (hours > 0) {
            hoursString = res.getQuantityString(R.plurals.hours_abbreviation, (int) hours, (int) hours);
            minutesString = res.getQuantityString(R.plurals.minutes_abbreviation, (int) minutes, (int) minutes);
            return hoursString + " " + minutesString;

        } else {
            minutesString = res.getQuantityString(R.plurals.minutes_abbreviation, (int) minutes, (int) minutes);
            return minutesString;
        }
    }

    private static String millisToYearsDaysHoursMinutes(Context context, long durationMillis) {
        Resources res = context.getResources();


        long days = TimeUnit.MILLISECONDS.toDays(durationMillis);
        long years = days / 365;
        days = days - years * 365;
        durationMillis = durationMillis - TimeUnit.DAYS.toMillis(days);

        long hours = TimeUnit.MILLISECONDS.toHours(durationMillis);
        durationMillis = durationMillis - TimeUnit.HOURS.toMillis(hours);

        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis);

        String yearsString;
        String daysString;
        String hoursString;
        String minutesString;
        if (years > 0) {
            yearsString = res.getQuantityString(R.plurals.years_abbreviation, (int) years, (int) years);
            daysString = res.getQuantityString(R.plurals.days_abbreviation, (int) days, (int) days);
            return yearsString + " " + daysString;
        } else {
            if (days > 0) {
                if (days > 1) {
                    daysString = res.getQuantityString(R.plurals.days_abbreviation, (int) days, (int) days);
                    hoursString = res.getQuantityString(R.plurals.hours_abbreviation, (int) hours, (int) hours);
                    minutesString = res.getQuantityString(R.plurals.minutes_abbreviation, (int) minutes, (int) minutes);
                    return daysString + " " + hoursString;
                } else {
                    hours = hours + TimeUnit.DAYS.toHours(days);
                    hoursString = res.getQuantityString(R.plurals.hours_abbreviation, (int) hours, (int) hours);
                    minutesString = res.getQuantityString(R.plurals.minutes_abbreviation, (int) minutes, (int) minutes);
                    return hoursString + " " + minutesString;
                }

            } else if (hours > 0) {
                hoursString = res.getQuantityString(R.plurals.hours_abbreviation, (int) hours, (int) hours);
                minutesString = res.getQuantityString(R.plurals.minutes_abbreviation, (int) minutes, (int) minutes);
                return hoursString + " " + minutesString;

            } else {
                minutesString = res.getQuantityString(R.plurals.minutes_abbreviation, (int) minutes, (int) minutes);
                return minutesString;
            }
        }
    }
    //endregion Day Date Time Duration Methods

    public static GradientDrawable getBackgroundDrawable(String startColor, String endColor) {
        int colors[] = new int[]{Color.parseColor(startColor), Color.parseColor(endColor)};
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        drawable.setCornerRadius(0f);
        return drawable;
    }

    public static void showSnackbar(View view, String message, int length) {
        Snackbar.make(view, message, length)
                .setAction("Action", null).show();
    }

    public static void showOkDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        String btnOkTitle = context.getResources().getString(R.string.btnOk_title);
        // set dialog title and message
        alertDialogBuilder
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(btnOkTitle, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnOK = alertDialog.getButton(Dialog.BUTTON_POSITIVE);
                btnOK.setTextSize(18);
            }
        });

        // show it
        alertDialog.show();
    }

    public static ArrayList<String> parseDiveLogRecord(@NonNull String diveLogRecord) {
        final int SIMPLE_FIELD = 100;
        final int QUALIFIED_FIELD = 101;

        final String mDelimiter = ",";
        final String mQualifier = "\"";
        final String mDoubleQualifier = mQualifier + mQualifier;
        final String mCrLf = "\r\n";
        final String mLineSeparator = System.getProperty("line.separator");
        final String mCr = "\r";
        final String mLf = "\n";
        final String mTab = "\t";
        final String mSpace = " ";

        Character currentChar;
        ArrayList<Character> currentField = new ArrayList<>();
        String fieldString = "";
        ArrayList<String> currentRecord = new ArrayList<>();

        // Initialize starting status mode and file pointer position.
        int status = SIMPLE_FIELD;
        int mFilePointer = -1;
        try {
            while (!EOF(mFilePointer, diveLogRecord.length())) {
                mFilePointer++;
                currentChar = ReadChar(mFilePointer, diveLogRecord);
                if (currentChar.equals(mDelimiter.charAt(0))) {
                    switch (status) {
                        case SIMPLE_FIELD:
                            // reached the end of the field
                            fieldString = chars2String(currentField);
                            currentRecord.add(fieldString);
                            currentField = new ArrayList<>();
                            break;

                        case QUALIFIED_FIELD:
                            // have not reached the end of the field
                            // add the delimiter to the field
                            currentField.add(mDelimiter.charAt(0));
                            break;
                        default:
                            break;
                    }

                } else if (currentChar.equals(mQualifier.charAt(0))) {
                    switch (status) {
                        case SIMPLE_FIELD:
                            // beginning of a QualifiedField
                            // ignore the quote mark
                            status = QUALIFIED_FIELD;
                            break;

                        case QUALIFIED_FIELD:
                            mFilePointer++;
                            if (EOF(mFilePointer, diveLogRecord.length())) {
                                // reached the end of the string
                                if (currentField.size() > 0) {
                                    fieldString = chars2String(currentField);
                                    currentRecord.add(fieldString);
                                    currentField = new ArrayList<Character>();
                                }
                                break;
                            }
                            currentChar = ReadChar(mFilePointer, diveLogRecord);
                            if (currentChar.equals(mQualifier.charAt(0))) {
                                // double quote marks
                                // include one of the quote marks in the field
                                currentField.add(mQualifier.charAt(0));

                            } else if (currentChar.equals(mDelimiter.charAt(0))) {
                                // Qualifier - Delimiter pair
                                // quote mark - comma pair
                                // reached the end of QualifiedField
                                fieldString = chars2String(currentField);
                                currentRecord.add(fieldString);
                                currentField = new ArrayList<Character>();
                                status = SIMPLE_FIELD;

                            } else {
                                currentField.add(currentChar);
                            }

                            break;
                        default:
                            break;
                    }

                } else if (currentChar.equals(mSpace.charAt(0)) || currentChar.equals(mTab.charAt(0))) {
                    switch (status) {
                        case SIMPLE_FIELD:
                            // if you're at the start of a field,
                            // then ignore the space or tab
                            if (currentField.size() > 0) {
                                // field characters have been added
                                // so include the space or tab
                                currentField.add(currentChar);
                            }
                            break;

                        case QUALIFIED_FIELD:
                            // add the space or tab to all Qualified fields
                            currentField.add(currentChar);
                            break;
                        default:
                            break;
                    }
                } else {
                    currentField.add(currentChar);
                }

            }

        } catch (Exception e) {
            Timber.e("parseDiveLogRecord(): Exception: %s.", e.getMessage());
        }


        return currentRecord;
    }


    private static Character ReadChar(int filePointer, @NonNull String diveLogRecord) {
        return diveLogRecord.charAt(filePointer);
    }

    @Contract(pure = true)
    private static boolean EOF(int filePointer, int fileLength) {
        return filePointer >= fileLength;
    }


    private static String chars2String(@NonNull ArrayList<Character> field) {
        StringBuilder builder = new StringBuilder(field.size());
        for (Character ch : field) {
            builder.append(ch);
        }
        return builder.toString();
    }

    private static final String[] mInvalidCharacters = {"[", "]", ".", "$", "#", "/"};

    public static boolean containsInvalidCharacters(CharSequence text) {
        return containsInvalidCharacters(text.toString());
    }

    public static boolean containsInvalidCharacters(String text) {
        for (int i = 0; i < mInvalidCharacters.length; i++) {
            if (text.contains(mInvalidCharacters[i])) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInvalidCharacter(char c) {
        for (int i = 0; i < mInvalidCharacters.length; i++) {
            if (mInvalidCharacters[i].charAt(0) == c) {
                return true;
            }
        }
        return false;
    }

//    public static void refreshReefGuide(@NonNull Context context,
//                                        @NonNull final String fileName) {
//
//        ReefGuide.removeAllReefGuides();
//
//        InputStream fis = null;
//        try {
//            fis = context.getAssets().open(fileName);
//
//            if (fis != null) {
//
//                // prepare the file for reading
//                InputStreamReader inputStreamReader = new InputStreamReader(fis);
//                BufferedReader buffReader = new BufferedReader(inputStreamReader);
//
//                String csvReefGuideSummary;
//                ReefGuide reefGuide;
//                ArrayList<ReefGuide> reefGuides = new ArrayList<>();
//
//                do {
//                    csvReefGuideSummary = buffReader.readLine();
//                    if (csvReefGuideSummary != null) {
//                        reefGuide = new ReefGuide(csvReefGuideSummary);
//                        if (!csvReefGuideSummary.startsWith("Line")) {
//                            ReefGuide.save(reefGuide);
//                            reefGuides.add(reefGuide);
//                        }
//                    }
//
//                } while (csvReefGuideSummary != null);
//                Timber.i("refreshReefGuide(): created %d ReefGuides", reefGuides.size());
//
//                for (ReefGuide guide : reefGuides) {
//                    new ReefGuideWebPageReader(context,
//                            guide.getSummaryPageUrl(),
//                            guide.getSummaryPageUid()).execute();
//                }
//
//
//            }
//        } catch (Exception e) {
//            Timber.e("readDiveSitesCsvFile(): Exception: %s.", e.getMessage());
//        } finally {
//            // close the file.
//            try {
//                if (fis != null) {
//                    fis.close();
//                }
//            } catch (IOException e) {
//                Timber.e("readDiveSitesCsvFile(): Exception: %s.", e.getMessage());
//            }
//        }
//    }

}
