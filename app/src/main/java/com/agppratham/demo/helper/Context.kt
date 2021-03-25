package com.agppratham.demo.helper

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Looper
import android.os.PowerManager
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.widget.Toast
import androidx.annotation.AnyRes
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.agppratham.demo.AlarmReceiver
import com.agppratham.demo.Constants.ALARM_ID
import com.agppratham.demo.Constants.ALARM_SOUND_TYPE_ALARM
import com.agppratham.demo.Constants.ALARM_SOUND_TYPE_NOTIFICATION
import com.agppratham.demo.Constants.DAY_MINUTES
import com.agppratham.demo.Constants.DAY_SECONDS
import com.agppratham.demo.Constants.FRIDAY_BIT
import com.agppratham.demo.Constants.HOUR_SECONDS
import com.agppratham.demo.Constants.MINUTE_SECONDS
import com.agppratham.demo.Constants.MONDAY_BIT
import com.agppratham.demo.Constants.OPEN_ALARMS_TAB_INTENT_ID
import com.agppratham.demo.Constants.PERMISSION_CALL_PHONE
import com.agppratham.demo.Constants.PERMISSION_CAMERA
import com.agppratham.demo.Constants.PERMISSION_GET_ACCOUNTS
import com.agppratham.demo.Constants.PERMISSION_READ_CALENDAR
import com.agppratham.demo.Constants.PERMISSION_READ_CALL_LOG
import com.agppratham.demo.Constants.PERMISSION_READ_CONTACTS
import com.agppratham.demo.Constants.PERMISSION_READ_PHONE_STATE
import com.agppratham.demo.Constants.PERMISSION_READ_SMS
import com.agppratham.demo.Constants.PERMISSION_READ_STORAGE
import com.agppratham.demo.Constants.PERMISSION_RECORD_AUDIO
import com.agppratham.demo.Constants.PERMISSION_SEND_SMS
import com.agppratham.demo.Constants.PERMISSION_WRITE_CALENDAR
import com.agppratham.demo.Constants.PERMISSION_WRITE_CALL_LOG
import com.agppratham.demo.Constants.PERMISSION_WRITE_CONTACTS
import com.agppratham.demo.Constants.PERMISSION_WRITE_STORAGE
import com.agppratham.demo.Constants.PREFS_KEY
import com.agppratham.demo.Constants.SATURDAY_BIT
import com.agppratham.demo.Constants.SILENT
import com.agppratham.demo.Constants.SUNDAY_BIT
import com.agppratham.demo.Constants.THURSDAY_BIT
import com.agppratham.demo.Constants.TODAY_BIT
import com.agppratham.demo.Constants.TOMORROW_BIT
import com.agppratham.demo.Constants.TUESDAY_BIT
import com.agppratham.demo.Constants.WEDNESDAY_BIT
import com.agppratham.demo.Constants.isOreoPlus
import com.agppratham.demo.MainActivity
import com.agppratham.demo.R
import com.agppratham.demo.alarm.data.service.HideAlarmService
import com.agppratham.demo.model.Alarm
import com.agppratham.demo.model.AlarmSound
import com.agppratham.demo.receiver.HideAlarmReceiver
import java.util.*
import kotlin.math.pow

var player: MediaPlayer? = MediaPlayer()
val Context.baseConfig: BaseConfig get() = BaseConfig.newInstance(this)
fun Context.getSharedPrefs() = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
val Context.dbHelper: DBHelper get() = DBHelper.newInstance(applicationContext)
fun Context.isScreenOn() = (getSystemService(Context.POWER_SERVICE) as PowerManager).isScreenOn
fun Context.getLaunchIntent() = packageManager.getLaunchIntentForPackage(baseConfig.appId)
fun Context.isBlackAndWhiteTheme() =
    baseConfig.textColor == Color.WHITE && baseConfig.primaryColor == Color.BLACK && baseConfig.backgroundColor == Color.BLACK

fun Context.getAdjustedPrimaryColor() =
    if (isBlackAndWhiteTheme()) Color.WHITE else baseConfig.primaryColor

fun Context.getDefaultAlarmSound(type: Int) = AlarmSound(
    0, getDefaultAlarmTitle(type), getDefaultAlarmUri(
        type
    ).toString()
)

fun Context.getPermissionString(id: Int) = when (id) {
    PERMISSION_READ_STORAGE -> Manifest.permission.READ_EXTERNAL_STORAGE
    PERMISSION_WRITE_STORAGE -> Manifest.permission.WRITE_EXTERNAL_STORAGE
    PERMISSION_CAMERA -> Manifest.permission.CAMERA
    PERMISSION_RECORD_AUDIO -> Manifest.permission.RECORD_AUDIO
    PERMISSION_READ_CONTACTS -> Manifest.permission.READ_CONTACTS
    PERMISSION_WRITE_CONTACTS -> Manifest.permission.WRITE_CONTACTS
    PERMISSION_READ_CALENDAR -> Manifest.permission.READ_CALENDAR
    PERMISSION_WRITE_CALENDAR -> Manifest.permission.WRITE_CALENDAR
    PERMISSION_CALL_PHONE -> Manifest.permission.CALL_PHONE
    PERMISSION_READ_CALL_LOG -> Manifest.permission.READ_CALL_LOG
    PERMISSION_WRITE_CALL_LOG -> Manifest.permission.WRITE_CALL_LOG
    PERMISSION_GET_ACCOUNTS -> Manifest.permission.GET_ACCOUNTS
    PERMISSION_READ_SMS -> Manifest.permission.READ_SMS
    PERMISSION_SEND_SMS -> Manifest.permission.SEND_SMS
    PERMISSION_READ_PHONE_STATE -> Manifest.permission.READ_PHONE_STATE
    else -> ""
}

fun Context.hasPermission(permId: Int) = ContextCompat.checkSelfPermission(
    this, getPermissionString(
        permId
    )
) == PackageManager.PERMISSION_GRANTED

fun Context.createNewAlarm(timeInMinutes: Int, weekDays: Int): Alarm {
    val defaultAlarmSound = getDefaultAlarmSound(ALARM_SOUND_TYPE_ALARM)
    return Alarm(
        0,
        timeInMinutes,
        weekDays,
        false,
        false,
        defaultAlarmSound.title,
        defaultAlarmSound.uri,
        ""
    )
}
fun Context.getHideAlarmPendingIntent(context: Context,alarm: com.agppratham.demo.alarm.data.Alarm): PendingIntent {
    val intent = Intent(this, HideAlarmService::class.java)
    intent.putExtra(ALARM_ID, alarm.notificationId())
    return PendingIntent.getBroadcast(this, alarm.notificationId(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
}

fun Context.getDefaultAlarmUri(type: Int) =
    RingtoneManager.getDefaultUri(if (type == ALARM_SOUND_TYPE_NOTIFICATION) RingtoneManager.TYPE_NOTIFICATION else RingtoneManager.TYPE_ALARM)

fun Context.startAlarmSound(context: Context, url: Uri) {
    player = MediaPlayer.create(context, url)
    player!!.start()
}

fun Context.stopAlarmSound() {
    if (player != null) {
        player!!.stop()
    }
}
internal fun Context.getResourceUri(@AnyRes resourceId: Int): Uri = Uri.Builder()
    .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
    .authority(packageName)
    .path(resourceId.toString())
    .build()
fun Context.getDefaultAlarmTitle(type: Int): String {
    val alarmString = getString(R.string.alarm)
    return try {
        RingtoneManager.getRingtone(this, getDefaultAlarmUri(type))?.getTitle(this) ?: alarmString
    } catch (e: Exception) {
        alarmString
    }
}

fun getTomorrowBit(): Int {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_WEEK, 1)
    val dayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7
    return 2.0.pow(dayOfWeek).toInt()
}

fun Context.grantReadUriPermission(uriString: String) {
    try {
        // ensure custom reminder sounds play well
        grantUriPermission(
            "com.android.systemui",
            Uri.parse(uriString),
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    } catch (ignored: Exception) {
    }
}

fun isOnMainThread() = Looper.myLooper() == Looper.getMainLooper()

fun ensureBackgroundThread(callback: () -> Unit) {
    if (isOnMainThread()) {
        Thread {
            callback()
        }.start()
    } else {
        callback()
    }
}

fun Context.getAlarmIntent(alarm: Alarm): PendingIntent {
    val intent = Intent(this, AlarmReceiver::class.java)
    intent.putExtra(ALARM_ID, alarm.id)
    return PendingIntent.getBroadcast(this, alarm.id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
}

fun Context.getNextAlarm(): String {
    val milliseconds =
        (getSystemService(Context.ALARM_SERVICE) as AlarmManager).nextAlarmClock?.triggerTime
            ?: return ""
    val calendar = Calendar.getInstance()
    val isDaylightSavingActive = TimeZone.getDefault().inDaylightTime(Date())
    var offset = calendar.timeZone.rawOffset
    if (isDaylightSavingActive) {
        offset += TimeZone.getDefault().dstSavings
    }

    calendar.timeInMillis = milliseconds
    val dayOfWeekIndex = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7
    val dayOfWeek = resources.getStringArray(R.array.week_days_short)[dayOfWeekIndex]
    val formatted = getFormattedTime(((milliseconds + offset) / 1000L).toInt(), false, false)
    return "$dayOfWeek $formatted"
}

fun Context.cancelAlarmClock(alarm: Alarm) {
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(getAlarmIntent(alarm))
}

fun Context.showAlarmNotification(alarm: Alarm) {
    val pendingIntent = getOpenAlarmTabIntent()
    val notification = getAlarmNotification(pendingIntent, alarm)
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(alarm.id, notification)

    if (alarm.days > 0) {
        scheduleNextAlarm(alarm, false)
    }
}

fun Context.getOpenAlarmTabIntent(): PendingIntent {
    val intent = getLaunchIntent() ?: Intent(this, MainActivity::class.java)
    return PendingIntent.getActivity(
        this,
        OPEN_ALARMS_TAB_INTENT_ID,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
}

@SuppressLint("NewApi")
fun Context.getAlarmNotification(pendingIntent: PendingIntent, alarm: Alarm): Notification {
    val soundUri = alarm.soundUri
    if (soundUri != SILENT) {
        grantReadUriPermission(soundUri)
    }

    val channelId = "simple_alarm_channel_$soundUri"
    val label = if (alarm.label.isNotEmpty()) alarm.label else getString(R.string.alarm)
    if (isOreoPlus()) {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setLegacyStreamType(AudioManager.STREAM_ALARM)
            .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
            .build()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val importance = NotificationManager.IMPORTANCE_HIGH
        NotificationChannel(channelId, label, importance).apply {
            setBypassDnd(true)
            enableLights(true)
            lightColor = getAdjustedPrimaryColor()
            enableVibration(alarm.vibrate)
            setSound(Uri.parse(soundUri), audioAttributes)
            notificationManager.createNotificationChannel(this)
        }
    }

    val dismissIntent = getHideAlarmPendingIntent(alarm)
    val builder = NotificationCompat.Builder(this)
        .setContentTitle(label)
        .setContentText(getFormattedTime(getPassedSeconds(), false, false))
        .setSmallIcon(R.drawable.ic_alarm_vector)
        .setContentIntent(pendingIntent)
        .setPriority(Notification.PRIORITY_HIGH)
        .setDefaults(Notification.DEFAULT_LIGHTS)
        .setAutoCancel(true)
        .setChannelId(channelId)
        // .addAction(R.drawable.ic_snooze_vector, getString(R.string.snooze), getSnoozePendingIntent(alarm))
        .addAction(R.drawable.ic_cross_vector, getString(R.string.dismiss), dismissIntent)
        .setDeleteIntent(dismissIntent)

    builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

    if (soundUri != SILENT) {
        builder.setSound(Uri.parse(soundUri), AudioManager.STREAM_ALARM)
    }

    if (alarm.vibrate) {
        val vibrateArray = LongArray(2) { 500 }
        builder.setVibrate(vibrateArray)
    }

    val notification = builder.build()
    notification.flags = notification.flags or Notification.FLAG_INSISTENT
    return notification
}

fun getCurrentDayMinutes(): Int {
    val calendar = Calendar.getInstance()
    return calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
}

fun Context.setupAlarmClock(alarm: Alarm, triggerInSeconds: Int) {
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val targetMS = System.currentTimeMillis() + triggerInSeconds * 1000
    AlarmManagerCompat.setAlarmClock(
        alarmManager, targetMS, getOpenAlarmTabIntent(), getAlarmIntent(
            alarm
        )
    )
}

fun Context.formatMinutesToTimeString(totalMinutes: Int) =
    formatSecondsToTimeString(totalMinutes * 60)

fun Context.formatSecondsToTimeString(totalSeconds: Int): String {
    val days = totalSeconds / DAY_SECONDS
    val hours = (totalSeconds % DAY_SECONDS) / HOUR_SECONDS
    val minutes = (totalSeconds % HOUR_SECONDS) / MINUTE_SECONDS
    val seconds = totalSeconds % MINUTE_SECONDS
    val timesString = StringBuilder()
    if (days > 0) {
        val daysString = String.format(resources.getQuantityString(R.plurals.days, days, days))
        timesString.append("$daysString, ")
    }

    if (hours > 0) {
        val hoursString = String.format(resources.getQuantityString(R.plurals.hours, hours, hours))
        timesString.append("$hoursString, ")
    }

    if (minutes > 0) {
        val minutesString = String.format(
            resources.getQuantityString(
                R.plurals.minutes,
                minutes,
                minutes
            )
        )
        timesString.append("$minutesString, ")
    }

    if (seconds > 0) {
        val secondsString = String.format(
            resources.getQuantityString(
                R.plurals.seconds,
                seconds,
                seconds
            )
        )
        timesString.append(secondsString)
    }

    var result = timesString.toString().trim().trimEnd(',')
    if (result.isEmpty()) {
        result = String.format(resources.getQuantityString(R.plurals.minutes, 0, 0))
    }
    return result
}

fun Context.showRemainingTimeMessage(totalMinutes: Int) {
    val fullString = String.format(
        getString(R.string.alarm_goes_off_in), formatMinutesToTimeString(
            totalMinutes
        )
    )
    Toast.makeText(this, fullString, Toast.LENGTH_LONG).show()
}

fun Context.scheduleNextAlarm(alarm: Alarm, showToast: Boolean) {
    val calendar = Calendar.getInstance()
    calendar.firstDayOfWeek = Calendar.MONDAY
    val currentTimeInMinutes = getCurrentDayMinutes()

    if (alarm.days == TODAY_BIT) {
        val triggerInMinutes = alarm.timeInMinutes - currentTimeInMinutes
        setupAlarmClock(alarm, triggerInMinutes * 60 - calendar.get(Calendar.SECOND))

        if (showToast) {
            showRemainingTimeMessage(triggerInMinutes)
        }
    } else if (alarm.days == TOMORROW_BIT) {
        val triggerInMinutes = alarm.timeInMinutes - currentTimeInMinutes + DAY_MINUTES
        setupAlarmClock(alarm, triggerInMinutes * 60 - calendar.get(Calendar.SECOND))

        if (showToast) {
            showRemainingTimeMessage(triggerInMinutes)
        }
    } else {
        for (i in 0..7) {
            val currentDay = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7
            val isCorrectDay = alarm.days and 2.0.pow(currentDay).toInt() != 0
            if (isCorrectDay && (alarm.timeInMinutes > currentTimeInMinutes || i > 0)) {
                val triggerInMinutes =
                    alarm.timeInMinutes - currentTimeInMinutes + (i * DAY_MINUTES)
                setupAlarmClock(alarm, triggerInMinutes * 60 - calendar.get(Calendar.SECOND))

                if (showToast) {
                    showRemainingTimeMessage(triggerInMinutes)
                }
                break
            } else {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
        }
    }
}

fun Context.hideNotification(id: Int) {
    val manager =
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.cancel(id)
}

fun Context.getHideAlarmPendingIntent(alarm: Alarm): PendingIntent {
    val intent = Intent(this, HideAlarmReceiver::class.java)
    intent.putExtra(ALARM_ID, alarm.id)
    return PendingIntent.getBroadcast(this, alarm.id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
}

fun Context.formatTo12HourFormat(
    showSeconds: Boolean,
    hours: Int,
    minutes: Int,
    seconds: Int
): String {
    val appendable = getString(if (hours >= 12) R.string.p_m else R.string.a_m)
    val newHours = if (hours == 0 || hours == 12) 12 else hours % 12
    return "${formatTime(showSeconds, false, newHours, minutes, seconds)} $appendable"
}

fun formatTime(
    showSeconds: Boolean,
    use24HourFormat: Boolean,
    hours: Int,
    minutes: Int,
    seconds: Int
): String {
    val hoursFormat = if (use24HourFormat) "%02d" else "%01d"
    var format = "$hoursFormat:%02d"

    return if (showSeconds) {
        format += ":%02d"
        String.format(format, hours, minutes, seconds)
    } else {
        String.format(format, hours, minutes)
    }
}

fun Context.getFormattedTime(
    passedSeconds: Int,
    showSeconds: Boolean,
    makeAmPmSmaller: Boolean
): SpannableString {
    val use24HourFormat = baseConfig.use24HourFormat
    val hours = (passedSeconds / 3600) % 24
    val minutes = (passedSeconds / 60) % 60
    val seconds = passedSeconds % 60

    return if (!use24HourFormat) {
        val formattedTime = formatTo12HourFormat(showSeconds, hours, minutes, seconds)
        val spannableTime = SpannableString(formattedTime)
        val amPmMultiplier = if (makeAmPmSmaller) 0.4f else 1f
        spannableTime.setSpan(
            RelativeSizeSpan(amPmMultiplier),
            spannableTime.length - 5,
            spannableTime.length,
            0
        )
        spannableTime
    } else {
        val formattedTime = formatTime(showSeconds, use24HourFormat, hours, minutes, seconds)
        SpannableString(formattedTime)
    }
}

fun getPassedSeconds(): Int {
    val calendar = Calendar.getInstance()
    val isDaylightSavingActive = TimeZone.getDefault().inDaylightTime(Date())
    var offset = calendar.timeZone.rawOffset
    if (isDaylightSavingActive) {
        offset += TimeZone.getDefault().dstSavings
    }
    return ((calendar.timeInMillis + offset) / 1000).toInt()
}

fun Context.rescheduleEnabledAlarms() {
    dbHelper.getEnabledAlarms().forEach {
        if (it.days != TODAY_BIT || it.timeInMinutes > getCurrentDayMinutes()) {
            scheduleNextAlarm(it, false)
        }
    }
}

fun Context.checkAlarmsWithDeletedSoundUri(uri: String) {
    val defaultAlarmSound = getDefaultAlarmSound(ALARM_SOUND_TYPE_ALARM)
    dbHelper.getAlarmsWithUri(uri).forEach {
        it.soundTitle = defaultAlarmSound.title
        it.soundUri = defaultAlarmSound.uri
        dbHelper.updateAlarm(it)
    }
}

fun Context.getSelectedDaysString(bitMask: Int): String {
    val dayBits = arrayListOf(
        MONDAY_BIT,
        TUESDAY_BIT,
        WEDNESDAY_BIT,
        THURSDAY_BIT,
        FRIDAY_BIT,
        SATURDAY_BIT,
        SUNDAY_BIT
    )
    val weekDays = resources.getStringArray(R.array.week_days_short).toList() as ArrayList<String>

    if (baseConfig.isSundayFirst) {
        dayBits.moveLastItemToFront()
        weekDays.moveLastItemToFront()
    }

    var days = ""
    dayBits.forEachIndexed { index, bit ->
        if (bitMask and bit != 0) {
            days += "${weekDays[index]}, "
        }
    }
    return days.trim().trimEnd(',')
}

fun Context.getAlarmSelectedDaysString(bitMask: Int): String {
    return when (bitMask) {
        TODAY_BIT -> getString(R.string.today)
        TOMORROW_BIT -> getString(R.string.tomorrow)
        else -> getSelectedDaysString(bitMask)
    }
}