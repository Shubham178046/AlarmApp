package com.agppratham.demo.alarm.data.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.IntDef
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.agppratham.demo.R
import com.agppratham.demo.alarm.data.Alarm
import com.agppratham.demo.alarm.data.DatabaseHelper
import com.agppratham.demo.alarm.data.dialog.AlarmEditDialog
import com.agppratham.demo.alarm.data.service.AlarmReceiver
import com.agppratham.demo.alarm.data.service.LoadAlarmsService
import com.agppratham.demo.alarm.data.util.ViewUtil
import kotlinx.android.synthetic.main.dialog_edit_alarm.*
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.*

class EditAlarmActivity : AppCompatActivity() {
    var modes = -1

    companion object {
        const val ALARM_EXTRA = "alarm_extra"
        const val MODE_EXTRA = "mode_extra"

        @Retention(RetentionPolicy.SOURCE)
        @IntDef(EDIT_ALARM, ADD_ALARM, UNKNOWN)
        internal annotation class Mode

        const val EDIT_ALARM: Int = 1
        const val ADD_ALARM: Int = 2
        const val UNKNOWN: Int = 0
        fun buildAddEditAlarmActivityIntent(
            context: Context?,
            @Mode mode: Int,
            alarm: Alarm
        ): Intent? {
            val i =
                Intent(context, EditAlarmActivity::class.java)
            i.putExtra(ALARM_EXTRA, alarm)
            i.putExtra(MODE_EXTRA, mode)
            return i
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_alarm)
        modes = intent.getIntExtra(MODE_EXTRA, -1)
        val alarm = getAlarm()
        ViewUtil.setTimePickerTime(edit_alarm_time_picker, alarm!!.time)
        edit_alarm_label.setText(alarm.label)
        setDayCheckboxes(alarm!!)
        btnok.setOnClickListener {
            save()
        }
    }

    private fun setDayCheckboxes(alarm: Alarm) {
        dv_monday.setChecked(alarm.getDay(Alarm.MON))
        dv_tuesday.setChecked(alarm.getDay(Alarm.TUES))
        dv_wednesday.setChecked(alarm.getDay(Alarm.WED))
        dv_thursday.setChecked(alarm.getDay(Alarm.THURS))
        dv_friday.setChecked(alarm.getDay(Alarm.FRI))
        dv_saturday.setChecked(alarm.getDay(Alarm.SAT))
        dv_sunday.setChecked(alarm.getDay(Alarm.SUN))
    }

    private fun save() {
        val alarm = getAlarm()
        val time = Calendar.getInstance()
        time[Calendar.MINUTE] = ViewUtil.getTimePickerMinute(edit_alarm_time_picker)
        time[Calendar.HOUR_OF_DAY] = ViewUtil.getTimePickerHour(edit_alarm_time_picker)
        alarm!!.time = time.timeInMillis
        alarm.label = edit_alarm_label.getText().toString()
        alarm.setDay(Alarm.MON, dv_monday.isChecked())
        alarm.setDay(Alarm.TUES, dv_tuesday.isChecked())
        alarm.setDay(Alarm.WED, dv_wednesday.isChecked())
        alarm.setDay(Alarm.THURS, dv_thursday.isChecked())
        alarm.setDay(Alarm.FRI, dv_friday.isChecked())
        alarm.setDay(Alarm.SAT, dv_saturday.isChecked())
        alarm.setDay(Alarm.SUN, dv_sunday.isChecked())
        val rowsUpdated = DatabaseHelper.getInstance(this).updateAlarm(alarm)
        val messageId: Int =
            if (rowsUpdated == 1) R.string.update_complete else R.string.update_failed
        Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show()
        AlarmReceiver.setReminderAlarm(this, alarm)
        finish()
    }

    private fun getAlarm(): Alarm? {
        when (modes) {
            AlarmEditDialog.EDIT_ALARM ->
                return getIntent().getParcelableExtra(ALARM_EXTRA)
            AlarmEditDialog.ADD_ALARM -> {
                val id: Long = DatabaseHelper.getInstance(this).addAlarm()
                LoadAlarmsService.launchLoadAlarmsService(this)
                return Alarm(id)
            }
            AlarmEditDialog.UNKNOWN -> throw IllegalStateException(
                "Mode supplied as intent extra for " +
                        "TAG" + " must match value in " +
                        AlarmEditDialog.Companion.Mode::class.java.getSimpleName()
            )
            else -> throw IllegalStateException(
                ("Mode supplied as intent extra for " +
                        "TAG" + " must match value in " +
                        AlarmEditDialog.Companion.Mode::class.java.getSimpleName())
            )
        }
    }

    private fun delete() {
        val alarm = getAlarm()
        val builder: AlertDialog.Builder = AlertDialog.Builder(
            this,
            R.style.DeleteAlarmDialogTheme
        )
        builder.setTitle(R.string.delete_dialog_title)
        builder.setMessage(R.string.delete_dialog_content)
        builder.setPositiveButton(R.string.yes,
            DialogInterface.OnClickListener { dialogInterface, i -> //Cancel any pending notifications for this alarm
                AlarmReceiver.cancelReminderAlarm(this, alarm!!)
                val rowsDeleted = DatabaseHelper.getInstance(this).deleteAlarm(alarm)
                val messageId: Int
                if (rowsDeleted == 1) {
                    messageId = R.string.delete_complete
                    Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show()
                    LoadAlarmsService.launchLoadAlarmsService(this)
                    //   getActivity().finish()
                } else {
                    messageId = R.string.delete_failed
                    Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show()
                }
            })
        builder.setNegativeButton(R.string.no, null)
        builder.show()
    }

    @AlarmEditDialog.Companion.Mode
    private fun getMode(): Int {
        return getIntent().getIntExtra(
            MODE_EXTRA,
            AlarmEditDialog.UNKNOWN
        )
    }
}