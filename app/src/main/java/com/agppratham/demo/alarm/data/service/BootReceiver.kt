package com.agppratham.demo.alarm.data.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.agppratham.demo.alarm.data.Alarm
import com.agppratham.demo.alarm.data.DatabaseHelper
import com.agppratham.demo.alarm.data.service.AlarmReceiver.Companion.setReminderAlarms
import java.util.concurrent.Executors

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (Intent.ACTION_BOOT_COMPLETED == intent!!.getAction()) {
            Executors.newSingleThreadExecutor().execute {
                val alarms: List<Alarm> = DatabaseHelper.getInstance(context).getAlarms()
                setReminderAlarms(context, alarms)
            }
        }
    }
}