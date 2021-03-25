package com.agppratham.demo.alarm.data.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.IntDef
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.agppratham.demo.MusicListActivity
import com.agppratham.demo.R
import com.agppratham.demo.alarm.data.Alarm
import com.agppratham.demo.alarm.data.AlarmSong
import com.agppratham.demo.alarm.data.DatabaseHelper
import com.agppratham.demo.alarm.data.dialog.AlarmEditDialog
import com.agppratham.demo.alarm.data.service.AlarmReceiver
import com.agppratham.demo.alarm.data.service.LoadAlarmsService
import com.agppratham.demo.alarm.data.util.ViewUtil
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_edit_alarm.*
import kotlinx.android.synthetic.main.dialog_edit_alarm.*
import kotlinx.android.synthetic.main.dialog_edit_alarm.btnok
import kotlinx.android.synthetic.main.dialog_edit_alarm.dv_friday
import kotlinx.android.synthetic.main.dialog_edit_alarm.dv_monday
import kotlinx.android.synthetic.main.dialog_edit_alarm.dv_saturday
import kotlinx.android.synthetic.main.dialog_edit_alarm.dv_sunday
import kotlinx.android.synthetic.main.dialog_edit_alarm.dv_thursday
import kotlinx.android.synthetic.main.dialog_edit_alarm.dv_tuesday
import kotlinx.android.synthetic.main.dialog_edit_alarm.dv_wednesday
import kotlinx.android.synthetic.main.dialog_edit_alarm.edit_alarm_label
import kotlinx.android.synthetic.main.dialog_edit_alarm.edit_alarm_sound
import kotlinx.android.synthetic.main.dialog_edit_alarm.edit_alarm_time_picker
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.*

class EditAlarmActivity : AppCompatActivity() {
    final @Mode
    var modes = UNKNOWN
    var alarmSong: AlarmSong = AlarmSong()
    var MON_SONG: String? = ""
    var TUE_SONG: String? = ""
    var WED_SONG: String? = ""
    var THRUS_SONG: String? = ""
    var FRI_SONG: String? = ""
    var SAT_SONG: String? = ""
    var SUN_SONG: String? = ""
    var alarmData: Alarm? = null

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Alarm.SUN && resultCode == RESULT_OK && data != null) {
            alarmSong = data?.getSerializableExtra("alarmSong") as AlarmSong
            edit_alarm_sound.setText(alarmSong.title)
            SUN_SONG = alarmSong.uri
            // alarmSongList.add(alarmSong)
            //uri = data?.getStringExtra("uri")
        } else if (requestCode == Alarm.MON && resultCode == RESULT_OK && data != null) {
            alarmSong = data?.getSerializableExtra("alarmSong") as AlarmSong
            edit_monday_alarm_sound.setText(alarmSong.title)
            MON_SONG = alarmSong.uri
            //alarmSongList.add(alarmSong)
            //uri = data?.getStringExtra("uri")
        } else if (requestCode == Alarm.TUES && resultCode == RESULT_OK && data != null) {
            alarmSong = data?.getSerializableExtra("alarmSong") as AlarmSong
            edit_tuesday_alarm_sound.setText(alarmSong.title)
            TUE_SONG = alarmSong.uri
            //alarmSongList.add(alarmSong)
            //uri = data?.getStringExtra("uri")
        } else if (requestCode == Alarm.WED && resultCode == RESULT_OK && data != null) {
            alarmSong = data?.getSerializableExtra("alarmSong") as AlarmSong
            edit_wednesday_alarm_sound.setText(alarmSong.title)
            WED_SONG = alarmSong.uri
            // alarmSongList.add(alarmSong)
            //uri = data?.getStringExtra("uri")
        } else if (requestCode == Alarm.THURS && resultCode == RESULT_OK && data != null) {
            alarmSong = data?.getSerializableExtra("alarmSong") as AlarmSong
            edit_thrusday_alarm_sound.setText(alarmSong.title)
            THRUS_SONG = alarmSong.uri
            // alarmSongList.add(alarmSong)
            //uri = data?.getStringExtra("uri")
        } else if (requestCode == Alarm.FRI && resultCode == RESULT_OK && data != null) {
            alarmSong = data?.getSerializableExtra("alarmSong") as AlarmSong
            edit_friday_alarm_sound.setText(alarmSong.title)
            FRI_SONG = alarmSong.uri
            // alarmSongList.add(alarmSong)
            //uri = data?.getStringExtra("uri")
        } else if (requestCode == Alarm.SAT && resultCode == RESULT_OK && data != null) {
            alarmSong = data?.getSerializableExtra("alarmSong") as AlarmSong
            edit_saturday_alarm_sound.setText(alarmSong.title)
            SAT_SONG = alarmSong.uri
            // alarmSongList.add(alarmSong)
            //uri = data?.getStringExtra("uri")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_alarm)
        modes = intent.getIntExtra(MODE_EXTRA, UNKNOWN)
        val alarm = getAlarm()
        ViewUtil.setTimePickerTime(edit_alarm_time_picker, alarm!!.time)
        edit_alarm_label.setText(alarm.label)
        if (alarm.suN_SONG != null) {
            if (!alarm.suN_SONG.equals("") && !alarm.suN_SONG.isNullOrEmpty()) {
                edit_alarm_sound.setText(alarm.suN_SONG)
            }
        }
        if (alarm.moN_SONG != null) {
            if (!alarm.moN_SONG.equals("") && !alarm.moN_SONG.isNullOrEmpty()) {
                edit_monday_alarm_sound.setText(alarm.moN_SONG)
            }
        }
        if (alarm.tueS_SONG != null) {
            if (!alarm.tueS_SONG.equals("") && !alarm.tueS_SONG.isNullOrEmpty()) {
                edit_tuesday_alarm_sound.setText(alarm.tueS_SONG)
            }
        }
        if (alarm.weD_SONG != null) {
            if (!alarm.weD_SONG.equals("") && !alarm.weD_SONG.isNullOrEmpty()) {
                edit_wednesday_alarm_sound.setText(alarm.weD_SONG)
            }
        }
        if (alarm.thruS_SONG != null) {
            if (!alarm.thruS_SONG.equals("") && !alarm.thruS_SONG.isNullOrEmpty()) {
                edit_thrusday_alarm_sound.setText(alarm.thruS_SONG)
            }
        }
        if (alarm.frI_SONG != null) {
            if (!alarm.frI_SONG.equals("") && !alarm.frI_SONG.isNullOrEmpty()) {
                edit_friday_alarm_sound.setText(alarm.frI_SONG)
            }
        }
        if (alarm.saT_SONG != null) {
            if (!alarm.saT_SONG.equals("") && !alarm.saT_SONG.isNullOrEmpty()) {
                edit_saturday_alarm_sound.setText(alarm.saT_SONG)
            }
        }
        setDayCheckboxes(alarm!!)
        btnok.setOnClickListener {
            save()
        }
        edit_alarm_sound.setOnClickListener {
            var intent = Intent(this, MusicListActivity::class.java)
            intent.putExtra("day", Alarm.SUN)
            startActivityForResult(intent, Alarm.SUN)
        }
        edit_monday_alarm_sound.setOnClickListener {
            var intent = Intent(this, MusicListActivity::class.java)
            intent.putExtra("day", Alarm.MON)
            startActivityForResult(intent, Alarm.MON)
        }
        edit_tuesday_alarm_sound.setOnClickListener {
            var intent = Intent(this, MusicListActivity::class.java)
            intent.putExtra("day", Alarm.TUES)
            startActivityForResult(intent, Alarm.TUES)
        }
        edit_wednesday_alarm_sound.setOnClickListener {
            var intent = Intent(this, MusicListActivity::class.java)
            intent.putExtra("day", Alarm.WED)
            startActivityForResult(intent, Alarm.WED)
        }
        edit_thrusday_alarm_sound.setOnClickListener {
            var intent = Intent(this, MusicListActivity::class.java)
            intent.putExtra("day", Alarm.THURS)
            startActivityForResult(intent, Alarm.THURS)
        }
        edit_friday_alarm_sound.setOnClickListener {
            var intent = Intent(this, MusicListActivity::class.java)
            intent.putExtra("day", Alarm.FRI)
            startActivityForResult(intent, Alarm.FRI)
        }
        edit_saturday_alarm_sound.setOnClickListener {
            var intent = Intent(this, MusicListActivity::class.java)
            intent.putExtra("day", Alarm.SAT)
            startActivityForResult(intent, Alarm.SAT)
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
        var alarm: Alarm?
        if (alarmData != null) {
            alarm = alarmData
        } else {
            alarm = getAlarm()
        }
        val time = Calendar.getInstance()
        time[Calendar.MINUTE] = ViewUtil.getTimePickerMinute(edit_alarm_time_picker)
        time[Calendar.HOUR_OF_DAY] = ViewUtil.getTimePickerHour(edit_alarm_time_picker)
        time[Calendar.SECOND] = 0
        println("Time And TimeMills" + time.time + "*********" + time.timeInMillis)
        alarm!!.time = time.timeInMillis
        alarm.label = edit_alarm_label.getText().toString()
        alarm.setDay(Alarm.MON, dv_monday.isChecked())
        alarm.setDay(Alarm.TUES, dv_tuesday.isChecked())
        alarm.setDay(Alarm.WED, dv_wednesday.isChecked())
        alarm.setDay(Alarm.THURS, dv_thursday.isChecked())
        alarm.setDay(Alarm.FRI, dv_friday.isChecked())
        alarm.setDay(Alarm.SAT, dv_saturday.isChecked())
        alarm.setDay(Alarm.SUN, dv_sunday.isChecked())
        alarm.setMON_SONG(MON_SONG)
        alarm.setTUES_SONG(TUE_SONG)
        alarm.setWED_SONG(WED_SONG)
        alarm.setTHRUS_SONG(THRUS_SONG)
        alarm.setFRI_SONG(FRI_SONG)
        alarm.setSAT_SONG(SAT_SONG)
        alarm.setSUN_SONG(SUN_SONG)
        val rowsUpdated = DatabaseHelper.getInstance(this).updateAlarm(alarm)
        val messageId: Int =
            if (rowsUpdated == 1) R.string.update_complete else R.string.update_failed
        Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show()
        AlarmReceiver.setReminderAlarm(this, alarm)
        finish()
    }

    private fun getAlarm(): Alarm? {
        when (modes) {
            AlarmEditDialog.EDIT_ALARM -> {
                alarmData = getIntent().getParcelableExtra(ALARM_EXTRA)!!
                return getIntent().getParcelableExtra(ALARM_EXTRA)
            }

            AlarmEditDialog.ADD_ALARM -> {
                val id: Long = DatabaseHelper.getInstance(this).addAlarm()
                LoadAlarmsService.launchLoadAlarmsService(this)
                alarmData = Alarm(id)
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