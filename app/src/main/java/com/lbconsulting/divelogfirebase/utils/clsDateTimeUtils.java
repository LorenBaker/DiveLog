package com.lbconsulting.divelogfirebase.utils;

import android.content.Context;
import android.content.res.Resources;

import com.lbconsulting.divelogfirebase.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by Loren on 1/5/2015.
 */
public class clsDateTimeUtils {

    private static final long millsPerSecond = 1000;
    private static final long secondsPerMinute = 60;
    private static final long millsPerMinute = millsPerSecond * secondsPerMinute;
    private static final long minutesPerHour = 60;
    private static final long minutesPerDay = 24 * minutesPerHour;
    private static long minutesPerYear = 365 * minutesPerDay;

    public static final int DATE = 0;
    public static final int TIME = 1;

    public static ArrayList<String> getFormattedDateTime(long dateTimeMinutes) {
        ArrayList<String> result = new ArrayList<>();
        long dateTimeMills = ConvertToMills(dateTimeMinutes);
        // Create a DateFormatter object for displaying date in specified format.
        String dateFormat = "EEEE, MMM d, yyyy";
        String timeFormat = "h:mm a";
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat, Locale.US);
        SimpleDateFormat timeFormatter = new SimpleDateFormat(timeFormat, Locale.US);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateTimeMills);

        // Create the formatted date and time
        String formattedDate = dateFormatter.format(calendar.getTime());
        String formattedTime = timeFormatter.format(calendar.getTime());

        // return result
        result.add(formattedDate);
        result.add(formattedTime);
        return result;
    }

    public static String getFormattedDuration(Context context, long durationMinutes) {
        Resources res = context.getResources();

        final int DAY_HOUR_MINUTE_MODE = 10;
        final int MONTH_DAY_MODE = 20;
        final int YEAR_MONTH_MODE = 30;
        int mode = DAY_HOUR_MINUTE_MODE;

        long years = durationMinutes / minutesPerYear;
        if (years > 0) {
            mode = YEAR_MONTH_MODE;
            durationMinutes = durationMinutes - years * minutesPerYear;
        }

        long days = durationMinutes / minutesPerDay;
        if (days > 0) {
            if (mode != YEAR_MONTH_MODE) {
                if (days > 61) {
                    mode = MONTH_DAY_MODE;
                }
            }
            durationMinutes = durationMinutes - days * minutesPerDay;
        }

        long hours = durationMinutes / minutesPerHour;
        if (hours > 0) {
            durationMinutes = durationMinutes - hours * minutesPerHour;
        }
        long minutes = durationMinutes;


        // build the formatted duration string
        StringBuilder sb = new StringBuilder();

        switch (mode) {

            case YEAR_MONTH_MODE:
                if (years > 0) {
                    sb.append(res.getQuantityString(R.plurals.years_abbreviation, (int) years, (int) years));
                }
                long months = days/31;
                if (months > 0) {
                    if (sb.length() > 0) {
                        sb.append(" ").append(res.getQuantityString(R.plurals.months_abbreviation, (int) months, (int) months));
                    } else {
                        sb.append(res.getQuantityString(R.plurals.months_abbreviation, (int) months, (int) months));
                    }
                }
               break;

            case MONTH_DAY_MODE:
                months = days / 31;
                days = days - (months*31);
                if (months > 0) {
                        sb.append(res.getQuantityString(R.plurals.months_abbreviation, (int) months, (int) months));
                }
                if (days > 0) {
                    if (sb.length() > 0) {
                        sb.append(" ").append(res.getQuantityString(R.plurals.days_abbreviation, (int) days, (int) days));
                    } else {
                        sb.append(res.getQuantityString(R.plurals.days_abbreviation, (int) days, (int) days));
                    }
                }

               break;

            case DAY_HOUR_MINUTE_MODE:
                if (years > 0) {
                    sb.append(res.getQuantityString(R.plurals.years_abbreviation, (int) years, (int) years));
                }
                if (days > 0) {
                    if (sb.length() > 0) {
                        sb.append(" ").append(res.getQuantityString(R.plurals.days_abbreviation, (int) days, (int) days));
                    } else {
                        sb.append(res.getQuantityString(R.plurals.days_abbreviation, (int) days, (int) days));
                    }
                }
                if (hours > 0) {
                    if (sb.length() > 0) {
                        sb.append(" ").append(res.getQuantityString(R.plurals.hours_abbreviation, (int) hours, (int) hours));
                    } else {
                        sb.append(res.getQuantityString(R.plurals.hours_abbreviation, (int) hours, (int) hours));
                    }
                }
                if (minutes > 0) {
                    if (sb.length() > 0) {
                        sb.append(" ").append(res.getQuantityString(R.plurals.minutes_abbreviation, (int) minutes, (int) minutes));
                    } else {
                        sb.append(res.getQuantityString(R.plurals.minutes_abbreviation, (int) minutes, (int) minutes));
                    }
                }
                break;
        }

        return sb.toString();
    }

    public static long ConvertToMinutes(long timeMills) {
        long minutes = timeMills / millsPerMinute;
        long remainder = timeMills % millsPerMinute;
        if (remainder > 29999) {
            minutes++;
        }
        return minutes;
    }

    public static long ConvertToMills(long timeMinutes) {
        long result = timeMinutes * millsPerMinute;
        return timeMinutes * millsPerMinute;
    }

    public static long getDateInMin(String diveStartDate) {
        Calendar cal = Calendar.getInstance();
        //String format = "M/d/yyyy HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(fileStringDateFormat, Locale.US);
        // 6/21/1990 14:20
        try {

            cal.setTime(sdf.parse(diveStartDate));
        } catch (ParseException e) {
            Timber.e("getDateInMin(): Exception: %s.", e.getMessage());
            e.printStackTrace();
        }

        long dateInMin = ConvertToMinutes(cal.getTimeInMillis());
        return dateInMin;
    }

    //private static final String fileStringDateFormat = "MMM d, yyyy hh:mm a";
    private static final String fileStringDateFormat = "M/d/yyyy HH:mm";

    public static String getDateString(long diveStartInMin) {
        String formattedDateTime = "";
        long diveStartInMills = ConvertToMills(diveStartInMin);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(diveStartInMills);
        SimpleDateFormat sdf = new SimpleDateFormat(fileStringDateFormat, Locale.US);
        formattedDateTime = sdf.format(cal.getTime());
        return formattedDateTime;
    }

/*    public static long roundToTheMinutes(long timeMills) {
        return ConvertToMills(ConvertToMinutes(timeMills));
    }*/
}
