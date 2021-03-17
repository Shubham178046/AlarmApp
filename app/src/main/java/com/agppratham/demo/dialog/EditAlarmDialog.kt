package com.agppratham.demo.dialog

import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.agppratham.demo.Constants.ALARM_SOUND_TYPE_ALARM
import com.agppratham.demo.Constants.PICK_AUDIO_FILE_INTENT_ID
import com.agppratham.demo.Constants.SILENT
import com.agppratham.demo.Constants.TODAY_BIT
import com.agppratham.demo.Constants.TOMORROW_BIT
import com.agppratham.demo.R
import com.agppratham.demo.activity.AlarmActivity
import com.agppratham.demo.helper.*
import com.agppratham.demo.model.Alarm
import com.agppratham.demo.model.AlarmSound
import com.simplemobiletools.commons.dialogs.SelectAlarmSoundDialog
import kotlinx.android.synthetic.main.dialog_edit_alarm.view.*
import java.util.*

class EditAlarmDialog(val activity: AlarmActivity, val alarm: Alarm, val callback: (alarmId: Int) -> Unit) {
    private val view = activity.layoutInflater.inflate(R.layout.dialog_edit_alarm, null)
    private var mediaPlayer: MediaPlayer? = null
    init {
        restoreLastAlarm()
      //  updateAlarmTime()

      /*  view.apply {
            edit_alarm_time.setOnClickListener {
                TimePickerDialog(context, timeSetListener, alarm.timeInMinutes / 60, alarm.timeInMinutes % 60, context.baseConfig.use24HourFormat).show()
            }

            edit_alarm_sound.text = alarm.soundTitle
            edit_alarm_sound.setOnClickListener {
                SelectAlarmSoundDialog(activity, alarm.soundUri, AudioManager.STREAM_ALARM, PICK_AUDIO_FILE_INTENT_ID, ALARM_SOUND_TYPE_ALARM, true,
                    onAlarmPicked = {
                        if (it != null) {
                            updateSelectedAlarmSound(it)
                        }
                    }, onAlarmSoundDeleted = {
                        if (alarm.soundUri == it.uri) {
                            val defaultAlarm = context.getDefaultAlarmSound(ALARM_SOUND_TYPE_ALARM)
                            updateSelectedAlarmSound(defaultAlarm)
                        }
                        activity.checkAlarmsWithDeletedSoundUri(it.uri)
                    })
            }

            edit_alarm_vibrate.isChecked = alarm.vibrate
            edit_alarm_vibrate_holder.setOnClickListener {
                edit_alarm_vibrate.toggle()
                alarm.vibrate = edit_alarm_vibrate.isChecked
            }

            edit_alarm_label.setText(alarm.label)

            val dayLetters = activity.resources.getStringArray(R.array.week_day_letters).toList() as ArrayList<String>
            val dayIndexes = arrayListOf(0, 1, 2, 3, 4, 5, 6)
            if (activity.baseConfig.isSundayFirst) {
                dayIndexes.moveLastItemToFront()
            }

            dayIndexes.forEach {
                val pow = Math.pow(2.0, it.toDouble()).toInt()
                val day = activity.layoutInflater.inflate(R.layout.alarm_day, edit_alarm_days_holder, false) as TextView
                day.text = dayLetters[it]

                val isDayChecked = alarm.days > 0 && alarm.days and pow != 0
               // day.background = getProperDayDrawable(isDayChecked)

                day.setTextColor(if (isDayChecked) context.baseConfig.backgroundColor else resources.getColor(R.color.default_text_color))
                day.setOnClickListener {
                    if (alarm.days < 0) {
                        alarm.days = 0
                    }

                    val selectDay = alarm.days and pow == 0
                    if (selectDay) {
                        alarm.days = alarm.days.addBit(pow)
                    } else {
                        alarm.days = alarm.days.removeBit(pow)
                    }
                   // day.background = getProperDayDrawable(selectDay)
                  //  day.setTextColor(if (selectDay) context.config.backgroundColor else textColor)
                    checkDaylessAlarm()
                }

                edit_alarm_days_holder.addView(day)
            }
        }*/

      /*  AlertDialog.Builder(activity)
            .setPositiveButton(R.string.ok, object : DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    if (alarm.days <= 0) {
                        alarm.days = if (alarm.timeInMinutes > getCurrentDayMinutes()) {
                            TODAY_BIT
                        } else {
                            TOMORROW_BIT
                        }
                    }

                    alarm.label = view.edit_alarm_label.text.toString()
                    alarm.isEnabled = true

                    var alarmId = alarm.id
                    if (alarm.id == 0) {
                        alarmId = activity.dbHelper.insertAlarm(alarm)
                        if (alarmId == -1) {
                            // activity.toast(R.string.unknown_error_occurred)
                        }
                    } else {
                        if (!activity.dbHelper.updateAlarm(alarm)) {
                            //  activity.toast(R.string.unknown_error_occurred)
                        }
                    }

                    activity.baseConfig.alarmLastConfig = alarm
                    callback(alarmId)
                    p0!!.dismiss()
                }

            })
            .setNegativeButton(R.string.cancel, null)
            .create()*/


    }
    private fun restoreLastAlarm() {
        if (alarm.id == 0) {
            activity.baseConfig.alarmLastConfig?.let { lastConfig ->
                alarm.label = lastConfig.label
                alarm.days = lastConfig.days
                alarm.soundTitle = lastConfig.soundTitle
                alarm.soundUri = lastConfig.soundUri
                alarm.timeInMinutes = lastConfig.timeInMinutes
                alarm.vibrate = lastConfig.vibrate
            }
        }
    }

 /*   private val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
        alarm.timeInMinutes = hourOfDay * 60 + minute
        updateAlarmTime()
    }

    private fun updateAlarmTime() {
        view.edit_alarm_time.text = activity.getFormattedTime(alarm.timeInMinutes * 60, false, true)
        checkDaylessAlarm()
    }

    private fun checkDaylessAlarm() {
        if (alarm.days <= 0) {
            val textId = if (alarm.timeInMinutes > getCurrentDayMinutes()) {
                R.string.today
            } else {
                R.string.tomorrow
            }

            view.edit_alarm_dayless_label.text = "(${activity.getString(textId)})"
        }
       // view.edit_alarm_dayless_label.beVisibleIf(alarm.days <= 0)
    }
*/
  /*  private fun getProperDayDrawable(selected: Boolean): Drawable {
        val drawableId = if (selected) R.drawable.circle_background_filled else R.drawable.circle_background_stroke
        val drawable = activity.resources.getDrawable(drawableId)
        drawable.applyColorFilter(textColor)
        return drawable
    }*/

    fun updateSelectedAlarmSound(alarmSound: AlarmSound) {
        alarm.soundTitle = alarmSound.title
        alarm.soundUri = alarmSound.uri
        view.edit_alarm_sound.text = alarmSound.title
    }

    fun Int.removeBit(bit: Int) = addBit(bit) - bit

    fun Int.addBit(bit: Int) = this or bit
}
