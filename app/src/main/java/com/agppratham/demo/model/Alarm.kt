package com.agppratham.demo.model

open class Alarm (
    var id: Int= 0,
    var timeInMinutes: Int= 0,
    var days: Int= 0,
    var isEnabled: Boolean = false,
    var vibrate: Boolean = false,
    var soundTitle: String = "",
    var soundUri: String= "",
    var label: String= "",
)
