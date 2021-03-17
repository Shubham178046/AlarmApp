package com.agppratham.demo.alarm.data.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.IntDef
import androidx.appcompat.app.AlertDialog
import com.agppratham.demo.R
import com.agppratham.demo.activity.AlarmActivity
import com.agppratham.demo.alarm.data.Alarm
import com.agppratham.demo.alarm.data.DatabaseHelper
import com.agppratham.demo.alarm.data.service.AlarmReceiver
import com.agppratham.demo.alarm.data.service.LoadAlarmsService
import com.agppratham.demo.alarm.data.util.ViewUtil
import kotlinx.android.synthetic.main.dialog_edit_alarm.*
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.*

class AlarmEditDialog(var context: AlarmActivity, var modes: Int, var alarms: Alarm , var onClick: OnClick) : Dialog(
    context
) {
    private val scalIn: Animation?
    private val scalOut: Animation
    private val view: View?
    val ALARM_EXTRA = "alarm_extra"
    val MODE_EXTRA = "mode_extra"

    companion object {
        @Retention(RetentionPolicy.SOURCE)
        @IntDef(EDIT_ALARM, ADD_ALARM, UNKNOWN)
        internal annotation class Mode

        const val EDIT_ALARM: Int = 1
        const val ADD_ALARM: Int = 2
        const val UNKNOWN: Int = 0
    }

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_alarm, null)
        setContentView(view!!)
        val lp = WindowManager.LayoutParams()
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        lp.copyFrom(window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        lp.gravity = Gravity.CENTER
        window!!.attributes = lp
        window!!.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        )
        window!!.setFlags(
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
        )

        this.setCanceledOnTouchOutside(true)

        scalIn = AnimationUtils.loadAnimation(context, R.anim.scale_in_dialog)
        scalOut = AnimationUtils.loadAnimation(context, R.anim.scale_out_dialog)
        show()
        setOnCancelListener { view.startAnimation(scalOut) }
        scalOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
            }

            override fun onAnimationEnd(animation: Animation) {
                dismiss()
            }

            override fun onAnimationRepeat(animation: Animation) {
            }
        })
        setDayCheckboxes(alarms)
        btnok.setOnClickListener {
            save()
            onClick.onClick()
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
        val rowsUpdated = DatabaseHelper.getInstance(getContext()).updateAlarm(alarm)
        val messageId: Int =
            if (rowsUpdated == 1) R.string.update_complete else R.string.update_failed
        Toast.makeText(getContext(), messageId, Toast.LENGTH_SHORT).show()
        AlarmReceiver.setReminderAlarm(getContext(), alarm)
        dismiss()
       // getActivity().finish()
    }
    private fun getAlarm(): Alarm? {
        when (modes) {
            EDIT_ALARM -> return context.getIntent().getParcelableExtra(
                ALARM_EXTRA
            )
            ADD_ALARM -> {
                val id: Long = DatabaseHelper.getInstance(context).addAlarm()
                LoadAlarmsService.launchLoadAlarmsService(context)
                return Alarm(id)
            }
            UNKNOWN -> throw IllegalStateException(
                "Mode supplied as intent extra for " +
                        "TAG" + " must match value in " +
                        Mode::class.java.getSimpleName()
            )
            else -> throw IllegalStateException(
                ("Mode supplied as intent extra for " +
                        "TAG" + " must match value in " +
                        Mode::class.java.getSimpleName())
            )
        }
    }
    private fun delete() {
        val alarm = getAlarm()
        val builder: AlertDialog.Builder = AlertDialog.Builder(getContext(), R.style.DeleteAlarmDialogTheme)
        builder.setTitle(R.string.delete_dialog_title)
        builder.setMessage(R.string.delete_dialog_content)
        builder.setPositiveButton(R.string.yes,
            DialogInterface.OnClickListener { dialogInterface, i -> //Cancel any pending notifications for this alarm
                AlarmReceiver.cancelReminderAlarm(getContext(), alarm!!)
                val rowsDeleted = DatabaseHelper.getInstance(getContext()).deleteAlarm(alarm)
                val messageId: Int
                if (rowsDeleted == 1) {
                    messageId = R.string.delete_complete
                    Toast.makeText(getContext(), messageId, Toast.LENGTH_SHORT).show()
                    LoadAlarmsService.launchLoadAlarmsService(getContext())
                 //   getActivity().finish()
                } else {
                    messageId = R.string.delete_failed
                    Toast.makeText(getContext(), messageId, Toast.LENGTH_SHORT).show()
                }
            })
        builder.setNegativeButton(R.string.no, null)
        builder.show()
    }
    @Mode
    private fun getMode(): Int {
        return context.getIntent().getIntExtra(
            MODE_EXTRA,
            UNKNOWN
        )
    }
    interface OnClick{
        fun onClick()
    }
}