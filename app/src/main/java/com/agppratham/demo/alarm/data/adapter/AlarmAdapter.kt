package com.agppratham.demo.alarm.data.adapter

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.agppratham.demo.R
import com.agppratham.demo.alarm.data.Alarm
import com.agppratham.demo.alarm.data.activity.EditAlarmActivity
import com.agppratham.demo.alarm.data.activity.EditAlarmActivity.Companion.ALARM_EXTRA
import com.agppratham.demo.alarm.data.activity.EditAlarmActivity.Companion.EDIT_ALARM
import com.agppratham.demo.alarm.data.activity.EditAlarmActivity.Companion.buildAddEditAlarmActivityIntent
import com.agppratham.demo.alarm.data.util.AlarmUtils
import kotlinx.android.synthetic.main.item_alarm.view.*

class AlarmAdapter() :
    RecyclerView.Adapter<AlarmAdapter.ViewHolder>() {
    private var mAlarms: List<Alarm>? = null

    private var mDays: Array<String>? = null
    private var mAccentColor = -1

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_alarm, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (mAccentColor == -1) {
            mAccentColor = ContextCompat.getColor(holder.itemView.context, R.color.accent)
        }
        if (mDays == null) {
            mDays = holder.itemView.getResources().getStringArray(R.array.days_abbreviated)
        }
        val alarm: Alarm = mAlarms!![position]
        holder.itemView.alarm_time.setText(AlarmUtils.getReadableTime(alarm.time))
        holder.itemView.alarm_days.setText(buildSelectedDays(alarm))
        holder.itemView.alarm_label.setText(alarm.label)
        holder.itemView.setOnClickListener {
            val intent = buildAddEditAlarmActivityIntent(holder.itemView.context,EDIT_ALARM,alarm)
            holder.itemView.context.startActivity(intent)
        }
    }

    fun setAlarms(alarms: List<Alarm>) {
        mAlarms = alarms
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (mAlarms == null) 0 else mAlarms!!.size
    }

    private fun buildSelectedDays(alarm: Alarm): Spannable? {
        val numDays = 7
        val days = alarm.days
        val builder = SpannableStringBuilder()
        var span: ForegroundColorSpan
        var startIndex: Int
        var endIndex: Int
        for (i in 0 until numDays) {
            startIndex = builder.length
            val dayText = mDays!![i]
            builder.append(dayText)
            builder.append(" ")
            endIndex = startIndex + dayText.length
            val isSelected = days.valueAt(i)
            if (isSelected) {
                span = ForegroundColorSpan(mAccentColor)
                builder.setSpan(span, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        return builder
    }
}