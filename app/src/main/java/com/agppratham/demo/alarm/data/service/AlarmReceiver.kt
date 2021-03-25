package com.agppratham.demo.alarm.data.service

import android.R.attr.name
import android.app.*
import android.app.AlarmManager.AlarmClockInfo
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import androidx.annotation.AnyRes
import androidx.annotation.NonNull
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.agppratham.demo.BuildConfig
import com.agppratham.demo.R
import com.agppratham.demo.alarm.data.Alarm
import com.agppratham.demo.alarm.data.activity.AlarmLandingPageActivity.Companion.launchIntent
import com.agppratham.demo.alarm.data.util.AlarmUtils
import com.agppratham.demo.helper.getHideAlarmPendingIntent
import com.agppratham.demo.helper.getResourceUri
import com.agppratham.demo.helper.startAlarmSound
import java.io.File
import java.util.*


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        var path: String? = ""
        val alarm: Alarm? = intent!!.getBundleExtra(BUNDLE_EXTRA)!!.getParcelable(ALARM_KEY)
        val mUri = Uri.parse(
            ("android.resource://"
                    + context!!.packageName) + "/raw/" + "ring"
        )
        if (alarm == null) {
            Log.e(
                TAG,
                "Alarm is null",
                NullPointerException()
            )
            return
        }
        val calendar = Calendar.getInstance()
        val day = calendar[Calendar.DAY_OF_WEEK]
        if (day == Alarm.MON) {
            if (alarm.moN_SONG != null && !alarm.moN_SONG.equals("") && !alarm.moN_SONG.isNullOrEmpty()) {
                path = alarm.moN_SONG
            }
        } else if (day == Alarm.TUES) {
            if (alarm.tueS_SONG != null && !alarm.tueS_SONG.equals("") && !alarm.tueS_SONG.isNullOrEmpty()) {
                path = alarm.tueS_SONG
            }
        } else if (day == Alarm.WED) {
            if (alarm.weD_SONG != null && !alarm.weD_SONG.equals("") && !alarm.weD_SONG.isNullOrEmpty()) {
                path = alarm.weD_SONG
            }
        } else if (day == Alarm.THURS) {
            if (alarm.thruS_SONG != null && !alarm.thruS_SONG.equals("") && !alarm.thruS_SONG.isNullOrEmpty()) {
                path = alarm.thruS_SONG
            }
        } else if (day == Alarm.FRI) {
            if (alarm.frI_SONG != null && !alarm.frI_SONG.equals("") && !alarm.frI_SONG.isNullOrEmpty()) {
                path = alarm.frI_SONG
            }
        } else if (day == Alarm.SAT) {
            if (alarm.saT_SONG != null && !alarm.saT_SONG.equals("") && !alarm.saT_SONG.isNullOrEmpty()) {
                path = alarm.saT_SONG
            }
        }
        /*RingtoneManager.setActualDefaultRingtoneUri(context,RingtoneManager.TYPE_ALARM,
           Uri.parse(path))*/
        var notification: Uri? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (path != null && !path.equals("") && !path.isNullOrEmpty()) {
                notification =
                    FileProvider.getUriForFile(
                        context!!,
                        BuildConfig.APPLICATION_ID + ".provider",
                        File(path!!)
                    )
            } else {
                notification = context.getResourceUri(R.raw.ring)
            }
        } else {
            if (path != null && !path.equals("") && !path.isNullOrEmpty()) {
                notification = Uri.fromFile(File(path!!))
            } else {
                notification = context.getResourceUri(R.raw.ring)
            }
        }
        if (notification != null) {
            context!!.startAlarmSound(context, notification)
        }

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        val id = alarm.notificationId()
        val manager: NotificationManager =
            context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(context)
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, CHANNEL_ID)
        builder.setSmallIcon(R.drawable.ic_alarm_vector)
        builder.setColor(ContextCompat.getColor(context, R.color.accent))
        builder.setContentTitle(context.getString(R.string.app_name))
        builder.setContentText(alarm.label)
        builder.setTicker(alarm.label)
        builder.setVibrate(longArrayOf(1000, 500, 1000, 500, 1000, 500))
        builder.setSound(Uri.parse(path))
        builder.setContentIntent(
            launchAlarmLandingPage(
                context,
                alarm
            )
        )
        val dismissIntent = context.getHideAlarmPendingIntent(context, alarm)
        builder.setAutoCancel(true)
        builder.setPriority(Notification.PRIORITY_HIGH)
        builder.addAction(
            R.drawable.ic_cross_vector,
            context.resources.getString(R.string.dismiss),
            dismissIntent
        )
        builder.setDeleteIntent(dismissIntent)
        builder.setSound(notification, AudioManager.STREAM_ALARM)
        manager.notify(id, builder.build())

        //Reset Alarm manually

        //Reset Alarm manually
        setReminderAlarm(context, alarm)
    }

    companion object {
        private val TAG: String =
            AlarmReceiver::class.java.getSimpleName()
        private val CHANNEL_ID = "alarm_channel"

        private val BUNDLE_EXTRA = "bundle_extra"
        private val ALARM_KEY = "alarm_key"
        fun setReminderAlarm(context: Context?, alarm: Alarm) {

            //Check whether the alarm is set to run on any days
            if (!AlarmUtils.isAlarmActive(alarm)) {
                //If alarm not set to run on any days, cancel any existing notifications for this alarm
                cancelReminderAlarm(
                    context!!,
                    alarm
                )
                return
            }
            val nextAlarmTime: Calendar? =
                getTimeForNextAlarm(alarm)
            alarm.time = nextAlarmTime!!.timeInMillis
            val intent =
                Intent(context, AlarmReceiver::class.java)
            val bundle = Bundle()
            bundle.putParcelable(
                ALARM_KEY,
                alarm
            )
            intent.putExtra(
                BUNDLE_EXTRA,
                bundle
            )
            val pIntent = PendingIntent.getBroadcast(
                context,
                alarm.notificationId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            ScheduleAlarm.with(context!!).schedule(alarm, pIntent)
        }

        fun setReminderAlarms(context: Context?, alarms: List<Alarm>) {
            for (alarm in alarms) {
                setReminderAlarm(context, alarm)
            }
        }

        private fun getTimeForNextAlarm(alarm: Alarm): Calendar? {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = alarm.time
            val currentTime = System.currentTimeMillis()
            val startIndex: Int =
                getStartIndexFromTime(calendar)
            var count = 0
            var isAlarmSetForDay: Boolean
            val daysArray = alarm.days
            do {
                val index = (startIndex + count) % 7
                isAlarmSetForDay = daysArray.valueAt(index) && calendar.timeInMillis > currentTime
                if (!isAlarmSetForDay) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                    count++
                }
            } while (!isAlarmSetForDay && count < 7)
            return calendar
        }

        fun cancelReminderAlarm(context: Context, alarm: Alarm) {
            val intent =
                Intent(context, AlarmReceiver::class.java)
            val pIntent = PendingIntent.getBroadcast(
                context,
                alarm.notificationId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            manager.cancel(pIntent)
        }

        private fun getStartIndexFromTime(c: Calendar): Int {
            val dayOfWeek = c[Calendar.DAY_OF_WEEK]
            var startIndex = 0
            when (dayOfWeek) {
                Calendar.MONDAY -> startIndex = 1
                Calendar.TUESDAY -> startIndex = 2
                Calendar.WEDNESDAY -> startIndex = 3
                Calendar.THURSDAY -> startIndex = 4
                Calendar.FRIDAY -> startIndex = 5
                Calendar.SATURDAY -> startIndex = 6
                Calendar.SUNDAY -> startIndex = 0
            }
            return startIndex
        }

        private fun createNotificationChannel(ctx: Context) {
            if (VERSION.SDK_INT < VERSION_CODES.O) return
            val mgr = ctx.getSystemService(NotificationManager::class.java)
                ?: return
            val name = ctx.getString(R.string.channel_name)
            if (mgr.getNotificationChannel(name) == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    name,
                    NotificationManager.IMPORTANCE_HIGH
                )
                channel.enableVibration(true)
                channel.vibrationPattern = longArrayOf(1000, 500, 1000, 500, 1000, 500)
                channel.setBypassDnd(true)
                mgr.createNotificationChannel(channel)
            }
        }


        private fun launchAlarmLandingPage(ctx: Context, alarm: Alarm): PendingIntent? {
            return PendingIntent.getActivity(
                ctx, alarm.notificationId(), launchIntent(ctx), PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

    private class ScheduleAlarm private constructor(
        @NonNull private val am: AlarmManager,
        @NonNull private val ctx: Context
    ) {
        fun schedule(alarm: Alarm, pi: PendingIntent?) {
            if (VERSION.SDK_INT > VERSION_CODES.LOLLIPOP) {
                am.setAlarmClock(
                    AlarmClockInfo(
                        alarm.time,
                        launchAlarmLandingPage(
                            ctx, alarm
                        )
                    ), pi
                )
            } else if (VERSION.SDK_INT > VERSION_CODES.KITKAT) {
                am.setExact(AlarmManager.RTC_WAKEUP, alarm.time, pi)
            } else {
                am[AlarmManager.RTC_WAKEUP, alarm.time] = pi
            }
        }

        companion object {
            fun with(context: Context): ScheduleAlarm {
                val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    ?: throw IllegalStateException("AlarmManager is null")
                return ScheduleAlarm(am, context)
            }
        }
    }
}