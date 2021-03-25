package com.agppratham.demo.alarm.data.util;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.util.SparseBooleanArray;

import androidx.core.app.ActivityCompat;

import com.agppratham.demo.alarm.data.Alarm;
import com.agppratham.demo.alarm.data.AlarmSong;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static com.agppratham.demo.alarm.data.DatabaseHelper.COL_FRI;
import static com.agppratham.demo.alarm.data.DatabaseHelper.COL_FRI_SONG;
import static com.agppratham.demo.alarm.data.DatabaseHelper.COL_IS_ENABLED;
import static com.agppratham.demo.alarm.data.DatabaseHelper.COL_LABEL;
import static com.agppratham.demo.alarm.data.DatabaseHelper.COL_MON;
import static com.agppratham.demo.alarm.data.DatabaseHelper.COL_MON_SONG;
import static com.agppratham.demo.alarm.data.DatabaseHelper.COL_SAT;
import static com.agppratham.demo.alarm.data.DatabaseHelper.COL_SAT_SONG;
import static com.agppratham.demo.alarm.data.DatabaseHelper.COL_SUN;
import static com.agppratham.demo.alarm.data.DatabaseHelper.COL_SUN_SONG;
import static com.agppratham.demo.alarm.data.DatabaseHelper.COL_THRUS_SONG;
import static com.agppratham.demo.alarm.data.DatabaseHelper.COL_THURS;
import static com.agppratham.demo.alarm.data.DatabaseHelper.COL_TIME;
import static com.agppratham.demo.alarm.data.DatabaseHelper.COL_TUES;
import static com.agppratham.demo.alarm.data.DatabaseHelper.COL_TUE_SONG;
import static com.agppratham.demo.alarm.data.DatabaseHelper.COL_WED;
import static com.agppratham.demo.alarm.data.DatabaseHelper.COL_WED_SONG;
import static com.agppratham.demo.alarm.data.DatabaseHelper._ID;


public final class AlarmUtils {

    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("h:mm", Locale.getDefault());
    private static final SimpleDateFormat AM_PM_FORMAT =
            new SimpleDateFormat("a", Locale.getDefault());

    private static final int REQUEST_ALARM = 1;
    private static final String[] PERMISSIONS_ALARM = {
            Manifest.permission.VIBRATE
    };
    static Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation().create();

    private AlarmUtils() {
        throw new AssertionError();
    }

    public static void checkAlarmPermissions(Activity activity) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        final int permission = ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.VIBRATE
        );

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_ALARM,
                    REQUEST_ALARM
            );
        }

    }

    public static ContentValues toContentValues(Alarm alarm) {

        final ContentValues cv = new ContentValues(10);

        cv.put(COL_TIME, alarm.getTime());
        cv.put(COL_LABEL, alarm.getLabel());
        cv.put(COL_SUN_SONG,alarm.getSUN_SONG());
        cv.put(COL_MON_SONG,alarm.getMON_SONG());
        cv.put(COL_TUE_SONG,alarm.getTUES_SONG());
        cv.put(COL_WED_SONG,alarm.getWED_SONG());
        cv.put(COL_THRUS_SONG,alarm.getTHRUS_SONG());
        cv.put(COL_FRI_SONG,alarm.getFRI_SONG());
        cv.put(COL_SAT_SONG,alarm.getSAT_SONG());

        final SparseBooleanArray days = alarm.getDays();
        cv.put(COL_MON, days.get(Alarm.MON) ? 1 : 0);
        cv.put(COL_TUES, days.get(Alarm.TUES) ? 1 : 0);
        cv.put(COL_WED, days.get(Alarm.WED) ? 1 : 0);
        cv.put(COL_THURS, days.get(Alarm.THURS) ? 1 : 0);
        cv.put(COL_FRI, days.get(Alarm.FRI) ? 1 : 0);
        cv.put(COL_SAT, days.get(Alarm.SAT) ? 1 : 0);
        cv.put(COL_SUN, days.get(Alarm.SUN) ? 1 : 0);

        cv.put(COL_IS_ENABLED, alarm.isEnabled());

        return cv;

    }

    public static ArrayList<Alarm> buildAlarmList(Cursor c) {

        if (c == null) return new ArrayList<>();

        final int size = c.getCount();

        final ArrayList<Alarm> alarms = new ArrayList<>(size);

        if (c.moveToFirst()) {
            do {
                Type type = new TypeToken<ArrayList<AlarmSong>>() {
                }.getType();
                final long id = c.getLong(c.getColumnIndex(_ID));
                final long time = c.getLong(c.getColumnIndex(COL_TIME));
                final String label = c.getString(c.getColumnIndex(COL_LABEL));
                final boolean mon = c.getInt(c.getColumnIndex(COL_MON)) == 1;
                final boolean tues = c.getInt(c.getColumnIndex(COL_TUES)) == 1;
                final boolean wed = c.getInt(c.getColumnIndex(COL_WED)) == 1;
                final boolean thurs = c.getInt(c.getColumnIndex(COL_THURS)) == 1;
                final boolean fri = c.getInt(c.getColumnIndex(COL_FRI)) == 1;
                final boolean sat = c.getInt(c.getColumnIndex(COL_SAT)) == 1;
                final boolean sun = c.getInt(c.getColumnIndex(COL_SUN)) == 1;
                final boolean isEnabled = c.getInt(c.getColumnIndex(COL_IS_ENABLED)) == 1;
                final String MON_SONG = c.getString(c.getColumnIndex(COL_MON_SONG));
                final String TUE_SONG = c.getString(c.getColumnIndex(COL_TUE_SONG));
                final String WED_SONG = c.getString(c.getColumnIndex(COL_WED_SONG));
                final String THRUS_SONG = c.getString(c.getColumnIndex(COL_THRUS_SONG));
                final String FRI_SONG = c.getString(c.getColumnIndex(COL_FRI_SONG));
                final String SAT_SONG = c.getString(c.getColumnIndex(COL_SAT_SONG));
                final String SUN_SONG = c.getString(c.getColumnIndex(COL_SUN_SONG));

                final Alarm alarm = new Alarm(id, time, label, MON_SONG,TUE_SONG,WED_SONG,THRUS_SONG,FRI_SONG,SAT_SONG,SUN_SONG);
                alarm.setDay(Alarm.MON, mon);
                alarm.setDay(Alarm.TUES, tues);
                alarm.setDay(Alarm.WED, wed);
                alarm.setDay(Alarm.THURS, thurs);
                alarm.setDay(Alarm.FRI, fri);
                alarm.setDay(Alarm.SAT, sat);
                alarm.setDay(Alarm.SUN, sun);

                alarm.setIsEnabled(isEnabled);

                alarms.add(alarm);

            } while (c.moveToNext());
        }

        return alarms;

    }

    public static String getReadableTime(long time) {
        return TIME_FORMAT.format(time);
    }

    public static String getAmPm(long time) {
        return AM_PM_FORMAT.format(time);
    }

    public static boolean isAlarmActive(Alarm alarm) {

        final SparseBooleanArray days = alarm.getDays();

        boolean isActive = false;
        int count = 0;
        System.out.println();
        while (count < days.size() && !isActive) {
            isActive = days.valueAt(count);
            count++;
        }

        return isActive;

    }

    public static String getActiveDaysAsString(Alarm alarm) {

        StringBuilder builder = new StringBuilder("Active Days: ");

        if (alarm.getDay(Alarm.MON)) builder.append("Monday, ");
        if (alarm.getDay(Alarm.TUES)) builder.append("Tuesday, ");
        if (alarm.getDay(Alarm.WED)) builder.append("Wednesday, ");
        if (alarm.getDay(Alarm.THURS)) builder.append("Thursday, ");
        if (alarm.getDay(Alarm.FRI)) builder.append("Friday, ");
        if (alarm.getDay(Alarm.SAT)) builder.append("Saturday, ");
        if (alarm.getDay(Alarm.SUN)) builder.append("Sunday.");

        if (builder.substring(builder.length() - 2).equals(", ")) {
            builder.replace(builder.length() - 2, builder.length(), ".");
        }

        return builder.toString();

    }

}
