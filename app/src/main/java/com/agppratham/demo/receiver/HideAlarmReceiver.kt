package com.agppratham.demo.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.agppratham.demo.Constants.ALARM_ID
import com.agppratham.demo.helper.dbHelper
import com.agppratham.demo.helper.ensureBackgroundThread
import com.agppratham.demo.helper.hideNotification

class HideAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra(ALARM_ID, -1)
        context.hideNotification(id)

        ensureBackgroundThread {
            val alarm = context.dbHelper.getAlarmWithId(id)
            if (alarm != null && alarm.days < 0) {
                context.dbHelper.updateAlarmEnabledState(alarm.id, false)
              //  context.updateWidgets()
            }
        }
    }
}
