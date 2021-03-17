package com.agppratham.demo.helper

import java.util.ArrayList

fun <T> ArrayList<T>.moveLastItemToFront() {
    val last = removeAt(size - 1)
    add(0, last)
}