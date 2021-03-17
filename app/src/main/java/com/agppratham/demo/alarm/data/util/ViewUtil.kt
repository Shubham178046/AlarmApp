package com.agppratham.demo.alarm.data.util

import android.annotation.TargetApi
import android.content.res.Resources
import android.os.Build
import android.widget.TimePicker
import java.util.*

class ViewUtil {
    private fun ViewUtils() {
        throw AssertionError()
    }

    companion object {


        fun dpToPx(dp: Float): Float {
            return dp * Resources.getSystem().displayMetrics.density
        }

        fun setTimePickerTime(picker: TimePicker, time: Long) {
            val c = Calendar.getInstance()
            c.timeInMillis = time
            val minutes = c[Calendar.MINUTE]
            val hours = c[Calendar.HOUR_OF_DAY]
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                picker.minute = minutes
                picker.hour = hours
            } else {
                picker.currentMinute = minutes
                picker.currentHour = hours
            }
        }

        @TargetApi(Build.VERSION_CODES.M)
        fun getTimePickerMinute(picker: TimePicker): Int {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) picker.minute else picker.currentMinute
        }

        @TargetApi(Build.VERSION_CODES.M)
        fun getTimePickerHour(picker: TimePicker): Int {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) picker.hour else picker.currentHour
        }
    }
}