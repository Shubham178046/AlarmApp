package com.agppratham.demo.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.agppratham.demo.helper.rescheduleEnabledAlarms

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        context.rescheduleEnabledAlarms()
    }
}
