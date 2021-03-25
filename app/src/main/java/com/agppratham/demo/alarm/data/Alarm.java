package com.agppratham.demo.alarm.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseBooleanArray;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

public final class Alarm implements Parcelable {

    private Alarm(Parcel in) {
        id = in.readLong();
        time = in.readLong();
        label = in.readString();
        allDays = in.readSparseBooleanArray();
        isEnabled = in.readByte() != 0;
        MON_SONG= in.readString();
        TUES_SONG= in.readString();
        WED_SONG= in.readString();
        THRUS_SONG= in.readString();
        FRI_SONG= in.readString();
        SAT_SONG= in.readString();
        SUN_SONG= in.readString();
    }

    public static final Creator<Alarm> CREATOR = new Creator<Alarm>() {
        @Override
        public Alarm createFromParcel(Parcel in) {
            return new Alarm(in);
        }

        @Override
        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeLong(time);
        parcel.writeString(label);
        parcel.writeSparseBooleanArray(allDays);
        parcel.writeByte((byte) (isEnabled ? 1 : 0));
        parcel.writeString(MON_SONG);
        parcel.writeString(TUES_SONG);
        parcel.writeString(WED_SONG);
        parcel.writeString(THRUS_SONG);
        parcel.writeString(FRI_SONG);
        parcel.writeString(SAT_SONG);
        parcel.writeString(SUN_SONG);
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MON,TUES,WED,THURS,FRI,SAT,SUN})
    @interface Days{}
    public static final int MON = 2;
    public static final int TUES = 3;
    public static final int WED = 4;
    public static final int THURS = 5;
    public static final int FRI = 6;
    public static final int SAT = 7;
    public static final int SUN = 1;

    private static final long NO_ID = -1;

    private final long id;
    private long time;
    private String label;
    private SparseBooleanArray allDays;
    private boolean isEnabled;
    private String MON_SONG;
    private String TUES_SONG;
    private String WED_SONG;
    private String THRUS_SONG;
    private String FRI_SONG;
    private String SAT_SONG;
    private String SUN_SONG;

    public Alarm() {
        this(NO_ID);
    }

    public Alarm(long id) {
        this(id, System.currentTimeMillis());
    }

    public Alarm(long id, long time, @Days int... days) {
        this(id, time, null,null,null,null,null,null,null,null ,days);
    }

    public Alarm(long id, long time, String label, String mon_song,String tue_song,String wed_song,String thrus_song,String fri_song,String sat_song,String sun_song,@Days int... days) {
        this.id = id;
        this.time = time;
        this.label = label;
        this.allDays = buildDaysArray(days);
        this.SUN_SONG = sun_song;
        this.MON_SONG = mon_song;
        this.TUES_SONG = tue_song;
        this.WED_SONG = wed_song;
        this.THRUS_SONG = thrus_song;
        this.FRI_SONG = fri_song;
        this.SAT_SONG = sat_song;
    }

    public long getId() {
        return id;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public String getMON_SONG() {
        return MON_SONG;
    }

    public void setMON_SONG(String MON_SONG) {
        this.MON_SONG = MON_SONG;
    }

    public String getTUES_SONG() {
        return TUES_SONG;
    }

    public void setTUES_SONG(String TUES_SONG) {
        this.TUES_SONG = TUES_SONG;
    }

    public String getWED_SONG() {
        return WED_SONG;
    }

    public void setWED_SONG(String WED_SONG) {
        this.WED_SONG = WED_SONG;
    }

    public String getTHRUS_SONG() {
        return THRUS_SONG;
    }

    public void setTHRUS_SONG(String THRUS_SONG) {
        this.THRUS_SONG = THRUS_SONG;
    }

    public String getFRI_SONG() {
        return FRI_SONG;
    }

    public void setFRI_SONG(String FRI_SONG) {
        this.FRI_SONG = FRI_SONG;
    }

    public String getSAT_SONG() {
        return SAT_SONG;
    }

    public void setSAT_SONG(String SAT_SONG) {
        this.SAT_SONG = SAT_SONG;
    }

    public String getSUN_SONG() {
        return SUN_SONG;
    }

    public void setSUN_SONG(String SUN_SONG) {
        this.SUN_SONG = SUN_SONG;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setDay(@Days int day, boolean isAlarmed) {
        allDays.append(day, isAlarmed);
    }

    public SparseBooleanArray getDays() {
        return allDays;
    }

    public boolean getDay(@Days int day){
        return allDays.get(day);
    }

    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public int notificationId() {
        final long id = getId();
        return (int) (id^(id>>>32));
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "id=" + id +
                ", time=" + time +
                ", label='" + label + '\'' +
                ", allDays=" + allDays +
                ", isEnabled=" + isEnabled +
                '}';
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (int) (id^(id>>>32));
        result = 31 * result + (int) (time^(time>>>32));
        result = 31 * result + label.hashCode();
        for(int i = 0; i < allDays.size(); i++) {
            result = 31 * result + (allDays.valueAt(i)? 1 : 0);
        }
        return result;
    }

    private static SparseBooleanArray buildDaysArray(@Days int... days) {

        final SparseBooleanArray array = buildBaseDaysArray();

        for (@Days int day : days) {
            array.append(day, true);
        }

        return array;

    }

    private static SparseBooleanArray buildBaseDaysArray() {

        final int numDays = 7;

        final SparseBooleanArray array = new SparseBooleanArray(numDays);

        array.put(SUN, false);
        array.put(MON, false);
        array.put(TUES, false);
        array.put(WED, false);
        array.put(THURS, false);
        array.put(FRI, false);
        array.put(SAT, false);

        return array;

    }

}
