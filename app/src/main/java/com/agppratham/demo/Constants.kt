package com.agppratham.demo

import android.os.Build

object Constants {
    const val MONDAY_BIT = 1
    const val TUESDAY_BIT = 2
    const val WEDNESDAY_BIT = 4
    const val THURSDAY_BIT = 8
    const val FRIDAY_BIT = 16
    const val SATURDAY_BIT = 32
    const val SUNDAY_BIT = 64
    const val EVERY_DAY_BIT = MONDAY_BIT or TUESDAY_BIT or WEDNESDAY_BIT or THURSDAY_BIT or FRIDAY_BIT or SATURDAY_BIT or SUNDAY_BIT
    const val WEEK_DAYS_BIT = MONDAY_BIT or TUESDAY_BIT or WEDNESDAY_BIT or THURSDAY_BIT or FRIDAY_BIT
    const val WEEKENDS_BIT = SATURDAY_BIT or SUNDAY_BIT

    const val ALARM_SOUND_TYPE_NOTIFICATION = 2
    const val ALARM_SOUND_TYPE_ALARM = 1
    const val ALARM_ID = "alarm_id"

    const val WAS_ALARM_WARNING_SHOWN = "was_alarm_warning_shown"
    const val DEFAULT_ALARM_MINUTES = 480
    const val APP_ID = "app_id"
    const val PREFS_KEY = "Prefs"

    const val OPEN_ALARMS_TAB_INTENT_ID = 9996
    const val SILENT = "silent"

    fun isMarshmallowPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    fun isNougatPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    fun isNougatMR1Plus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1
    fun isOreoPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    fun isOreoMr1Plus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1
    fun isPiePlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    fun isQPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    fun isRPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R


    const val TEXT_COLOR = "text_color"
    const val BACKGROUND_COLOR = "background_color"
    const val PRIMARY_COLOR = "primary_color_2"

    const val TODAY_BIT = -1
    const val TOMORROW_BIT = -2

    const val HOUR_MINUTES = 60
    const val DAY_MINUTES = 24 * HOUR_MINUTES
    const val WEEK_MINUTES = DAY_MINUTES * 7
    const val MONTH_MINUTES = DAY_MINUTES * 30
    const val YEAR_MINUTES = DAY_MINUTES * 365

    const val MINUTE_SECONDS = 60
    const val HOUR_SECONDS = HOUR_MINUTES * 60
    const val DAY_SECONDS = DAY_MINUTES * 60
    const val WEEK_SECONDS = WEEK_MINUTES * 60
    const val MONTH_SECONDS = MONTH_MINUTES * 60
    const val YEAR_SECONDS = YEAR_MINUTES * 60


    const val USE_24_HOUR_FORMAT = "use_24_hour_format"

    const val ALARM_MAX_REMINDER_SECS = "alarm_max_reminder_secs"
    const val DEFAULT_MAX_ALARM_REMINDER_SECS = 300

    const val ALARM_NOTIF_ID = 9998

    const val YOUR_ALARM_SOUNDS = "your_alarm_sounds"
    // permissions
    const val PERMISSION_READ_STORAGE = 1
    const val PERMISSION_WRITE_STORAGE = 2
    const val PERMISSION_CAMERA = 3
    const val PERMISSION_RECORD_AUDIO = 4
    const val PERMISSION_READ_CONTACTS = 5
    const val PERMISSION_WRITE_CONTACTS = 6
    const val PERMISSION_READ_CALENDAR = 7
    const val PERMISSION_WRITE_CALENDAR = 8
    const val PERMISSION_CALL_PHONE = 9
    const val PERMISSION_READ_CALL_LOG = 10
    const val PERMISSION_WRITE_CALL_LOG = 11
    const val PERMISSION_GET_ACCOUNTS = 12
    const val PERMISSION_READ_SMS = 13
    const val PERMISSION_SEND_SMS = 14
    const val PERMISSION_READ_PHONE_STATE = 15

    const val PICK_AUDIO_FILE_INTENT_ID = 9994
    const val SUNDAY_FIRST = "sunday_first"
    const val ALARM_LAST_CONFIG = "alarm_last_config"
}