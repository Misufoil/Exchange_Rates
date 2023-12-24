package com.example.exchangerates.screens.detail.util

import java.text.SimpleDateFormat
import java.util.*

class DateAxisValueFormatter : com.github.mikephil.charting.formatter.ValueFormatter() {
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)

    override fun getFormattedValue(value: Float): String {
        return dateFormat.format(value.toLong())
    }
}