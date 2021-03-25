package com.agppratham.demo.alarm.data.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.agppratham.demo.Constants.ALARM_ID
import com.agppratham.demo.helper.hideNotification
import com.agppratham.demo.helper.stopAlarmSound

class HideAlarmService : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val id = intent!!.getIntExtra(ALARM_ID, -1)
        context!!.hideNotification(id)
        context.stopAlarmSound()
    }
}