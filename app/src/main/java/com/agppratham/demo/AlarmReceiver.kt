package com.agppratham.demo

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Handler
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.legacy.content.WakefulBroadcastReceiver
import com.agppratham.demo.Constants.ALARM_ID
import com.agppratham.demo.Constants.ALARM_NOTIF_ID
import com.agppratham.demo.Constants.isOreoPlus
import com.agppratham.demo.helper.*


class AlarmReceiver : WakefulBroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      //  RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, Uri.parse(PrefUtils.getURI(context)))
        val id = intent!!.getIntExtra(ALARM_ID, -1)
        val alarm = context!!.dbHelper.getAlarmWithId(id) ?: return
        if (context.isScreenOn()) {
            context.showAlarmNotification(alarm)
            Handler().postDelayed({
                context.hideNotification(id)
            }, context.baseConfig.alarmMaxReminderSecs * 1000L)
        }
        else {
            if (isOreoPlus()) {
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()

                val notificationManager = context.getSystemService(NotificationManager::class.java)
                if (notificationManager.getNotificationChannel("Alarm") == null) {
                    NotificationChannel("Alarm", "Alarm", NotificationManager.IMPORTANCE_HIGH).apply {
                        setBypassDnd(true)
                        setSound(Uri.parse(alarm.soundUri), audioAttributes)
                        notificationManager.createNotificationChannel(this)
                    }
                }

                val pendingIntent = PendingIntent.getActivity(context, 0, Intent(context, ReminderActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(ALARM_ID, id)
                }, PendingIntent.FLAG_UPDATE_CURRENT)

                val builder = NotificationCompat.Builder(context, "Alarm")
                    .setSmallIcon(R.drawable.ic_alarm_vector)
                    .setContentTitle(context.getString(R.string.alarm))
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setFullScreenIntent(pendingIntent, true)

                try {
                    notificationManager.notify(ALARM_NOTIF_ID, builder.build())
                } catch (e: Exception) {
                    println(e.localizedMessage)
                    //context.showErrorToast(e)
                }
            } else {
                Intent(context, ReminderActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(this)
                }
            }
        }
     /*   if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }*/
        val ringtone = RingtoneManager.getRingtone(context, Uri.parse(PrefUtils.getURI(context)))
        ringtone.play()

        //this will send a notification message

        //this will send a notification message
        val comp = ComponentName(
            context!!.packageName,
            AlarmReceiver::class.java.getName()
        )
        startWakefulService(context, intent!!.setComponent(comp))
        resultCode = Activity.RESULT_OK
    }
}