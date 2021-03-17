package com.agppratham.demo.activity

import android.content.Context
import android.content.IntentFilter
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.agppratham.demo.R
import com.agppratham.demo.alarm.data.Alarm
import com.agppratham.demo.alarm.data.activity.EditAlarmActivity
import com.agppratham.demo.alarm.data.activity.EditAlarmActivity.Companion.buildAddEditAlarmActivityIntent
import com.agppratham.demo.alarm.data.adapter.AlarmAdapter
import com.agppratham.demo.alarm.data.dialog.AlarmEditDialog
import com.agppratham.demo.alarm.data.service.LoadAlarmsReceiver
import com.agppratham.demo.alarm.data.service.LoadAlarmsService
import com.agppratham.demo.alarm.data.util.AlarmUtils
import com.agppratham.demo.dialog.EditAlarmDialog
import kotlinx.android.synthetic.main.activity_alarm.*
import java.util.*

class AlarmActivity : BaseActivity(), LoadAlarmsReceiver.OnAlarmsLoadedListener {
    private var alarms = ArrayList<Alarm>()
    private var currentEditAlarmDialog: EditAlarmDialog? = null
    lateinit var context: Context
    private var mReceiver: LoadAlarmsReceiver? = null
    private var mAdapter: AlarmAdapter? = null

    companion object {
        const val EDIT_ALARM = 1
        const val ADD_ALARM = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)
        context = this
        mReceiver = LoadAlarmsReceiver(this)
        mAdapter = AlarmAdapter()
        alarms_list.layoutManager = LinearLayoutManager(this)
        alarms_list.itemAnimator = DefaultItemAnimator()
        alarms_list.adapter = mAdapter
        alarm_fab.setOnClickListener {

            val intent = buildAddEditAlarmActivityIntent(this, ADD_ALARM,Alarm())
            startActivity(intent)
            //   val i: Intent = buildAddEditAlarmActivityIntent(getContext(), ADD_ALARM)
            //   startActivity(i)
        }
    }
/*    override fun alarmToggled(id: Int, isEnabled: Boolean) {
        if (context.dbHelper.updateAlarmEnabledState(id, isEnabled)) {
            val alarm = alarms.firstOrNull { it.id == id } ?: return
            alarm.isEnabled = isEnabled
            checkAlarmState(alarm)
        } else {
        }
    }
    private fun setupAlarms() {
        alarms = context?.dbHelper?.getAlarms() ?: return
        if (context?.getNextAlarm()?.isEmpty() == true) {
            alarms.forEach {
                if (it.days == TODAY_BIT && it.isEnabled && it.timeInMinutes <= getCurrentDayMinutes()) {
                    it.isEnabled = false
                    ensureBackgroundThread {
                        context?.dbHelper?.updateAlarmEnabledState(it.id, false)
                    }
                }
            }
        }

        val currAdapter = alarms_list.adapter
        if (currAdapter == null) {
            AlarmsAdapter(this, alarms, this, alarms_list) {
                openEditAlarm(it as Alarm)
            }.apply {
                alarms_list.adapter = this
            }
        } else {
            (currAdapter as AlarmsAdapter).updateItems(alarms)
        }
    }
    private fun checkAlarmState(alarm: Alarm) {
        if (alarm.isEnabled) {
            context?.scheduleNextAlarm(alarm, true)
        } else {
            context?.cancelAlarmClock(alarm)
        }
    }
    private fun openEditAlarm(alarm: Alarm) {
        currentEditAlarmDialog = EditAlarmDialog(this, alarm) {
            alarm.id = it
            currentEditAlarmDialog = null
            setupAlarms()
            checkAlarmState(alarm)
        }
    }*/

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(LoadAlarmsService.ACTION_COMPLETE)
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver!!, filter)
        LoadAlarmsService.launchLoadAlarmsService(this)
    }

    override fun onStop() {
        super.onStop()
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver!!)
    }

    override fun onAlarmsLoaded(alarms: ArrayList<Alarm>?) {
        mAdapter!!.setAlarms(alarms!!)
    }
    /*  private fun setupViews() {
         apply {
              alarm_fab.setOnClickListener {
                  val newAlarm = context.createNewAlarm(DEFAULT_ALARM_MINUTES, 0)
                  newAlarm.isEnabled = true
                  newAlarm.days = getTomorrowBit()
                  openEditAlarm(newAlarm)
              }
          }

          setupAlarms()
      }

      fun updateAlarmSound(alarmSound: AlarmSound) {
          currentEditAlarmDialog?.updateSelectedAlarmSound(alarmSound)
      }*/
}