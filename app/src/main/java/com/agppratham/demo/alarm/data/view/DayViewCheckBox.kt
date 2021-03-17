package com.agppratham.demo.alarm.data.view

import android.R
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.ContextCompat


class DayViewCheckBox : AppCompatCheckBox {
    var contexts: Context

    constructor(context: Context) : super(context) {
        contexts = context
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        contexts = context
    }

    override fun setChecked(t: Boolean) {
        if (t) {
            this.setTextColor(Color.WHITE)
        } else {
            this.setTextColor(ContextCompat.getColor(getContext(), R.color.holo_red_dark))
        }
        super.setChecked(t)
    }
}