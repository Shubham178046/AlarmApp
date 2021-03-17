package com.agppratham.demo.helper

import android.content.Context
import android.text.format.DateFormat
import com.agppratham.demo.Constants.ALARM_LAST_CONFIG
import com.agppratham.demo.Constants.ALARM_MAX_REMINDER_SECS
import com.agppratham.demo.Constants.APP_ID
import com.agppratham.demo.Constants.BACKGROUND_COLOR
import com.agppratham.demo.Constants.DEFAULT_MAX_ALARM_REMINDER_SECS
import com.agppratham.demo.Constants.PRIMARY_COLOR
import com.agppratham.demo.Constants.SUNDAY_FIRST
import com.agppratham.demo.Constants.TEXT_COLOR
import com.agppratham.demo.Constants.USE_24_HOUR_FORMAT
import com.agppratham.demo.Constants.WAS_ALARM_WARNING_SHOWN
import com.agppratham.demo.Constants.YOUR_ALARM_SOUNDS
import com.agppratham.demo.R
import com.agppratham.demo.model.Alarm
import java.util.*

open class BaseConfig(val context: Context) {
    protected val prefs = context.getSharedPrefs()

    companion object {
        fun newInstance(context: Context) = BaseConfig(context)
    }
    var appId: String
        get() = prefs.getString(APP_ID, "")!!
        set(appId) = prefs.edit().putString(APP_ID, appId).apply()

    var textColor: Int
        get() = prefs.getInt(TEXT_COLOR, context.resources.getColor(R.color.default_text_color))
        set(textColor) = prefs.edit().putInt(TEXT_COLOR, textColor).apply()

    var backgroundColor: Int
        get() = prefs.getInt(BACKGROUND_COLOR, context.resources.getColor(R.color.default_background_color))
        set(backgroundColor) = prefs.edit().putInt(BACKGROUND_COLOR, backgroundColor).apply()

    var primaryColor: Int
        get() = prefs.getInt(PRIMARY_COLOR, context.resources.getColor(R.color.color_primary))
        set(primaryColor) = prefs.edit().putInt(PRIMARY_COLOR, primaryColor).apply()
    var use24HourFormat: Boolean
        get() = prefs.getBoolean(USE_24_HOUR_FORMAT, DateFormat.is24HourFormat(context))
        set(use24HourFormat) = prefs.edit().putBoolean(USE_24_HOUR_FORMAT, use24HourFormat).apply()

    var alarmMaxReminderSecs: Int
        get() = prefs.getInt(ALARM_MAX_REMINDER_SECS, DEFAULT_MAX_ALARM_REMINDER_SECS)
        set(alarmMaxReminderSecs) = prefs.edit().putInt(ALARM_MAX_REMINDER_SECS, alarmMaxReminderSecs).apply()
    var yourAlarmSounds: String
        get() = prefs.getString(YOUR_ALARM_SOUNDS, "")!!
        set(yourAlarmSounds) = prefs.edit().putString(YOUR_ALARM_SOUNDS, yourAlarmSounds).apply()
    var isSundayFirst: Boolean
        get() {
            val isSundayFirst = Calendar.getInstance(Locale.getDefault()).firstDayOfWeek == Calendar.SUNDAY
            return prefs.getBoolean(SUNDAY_FIRST, isSundayFirst)
        }
        set(sundayFirst) = prefs.edit().putBoolean(SUNDAY_FIRST, sundayFirst).apply()

    var alarmLastConfig: Alarm?
        get() = prefs.getString(ALARM_LAST_CONFIG, null)?.let { lastAlarm ->
            gson.fromJson(lastAlarm, Alarm::class.java)
        }
        set(alarm) = prefs.edit().putString(ALARM_LAST_CONFIG, gson.toJson(alarm)).apply()

    var wasAlarmWarningShown: Boolean
        get() = prefs.getBoolean(WAS_ALARM_WARNING_SHOWN, false)
        set(wasAlarmWarningShown) = prefs.edit().putBoolean(WAS_ALARM_WARNING_SHOWN, wasAlarmWarningShown).apply()

}