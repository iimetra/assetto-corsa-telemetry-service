package dev.iimetra.ac.assettocorsaeventforwarder

const val G_CONSTANT = 9.80665f

fun Float.toPercent(): Float = this * 100

fun Float.toGPower(): Float = this / G_CONSTANT