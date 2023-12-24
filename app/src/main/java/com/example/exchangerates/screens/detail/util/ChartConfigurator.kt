package com.example.exchangerates.screens.detail.util

import android.graphics.Color
import android.util.Log
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.color.MaterialColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class ChartConfigurator(private val chart: LineChart) {

    fun configureChart() {
        chart.apply {
            // Настройка внешнего вида графика
            description.isEnabled = false
            setTouchEnabled(true)
            setDrawGridBackground(true)
            isDragEnabled = true
            setScaleEnabled(true)

            val textColor = MaterialColors.getColor(
                this.context, com.google.android.material.R.attr.colorOnSecondary, Color.BLACK
            )

            // Настройка оси X
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.valueFormatter = DateAxisValueFormatter()
            xAxis.labelRotationAngle = -45f
            xAxis.setDrawGridLines(true)
            xAxis.textColor = textColor

            val oneDayInMillis = 24 * 60 * 60 * 1000
            xAxis.granularity = oneDayInMillis.toFloat()

            // Настройка оси Y
            axisRight.isEnabled = false
            axisLeft.setDrawGridLines(true)
            axisLeft.textColor = textColor

            legend.isEnabled = false

            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    if (e != null) {
                        Log.d("Chart", "e != null")
                        val inputDate = e.data as LocalDate
                        val outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.US)

                        try {
                            val outputDate = inputDate.format(outputFormatter)

                            //включить описание
                            description.isEnabled = true
                            description.textSize = 12f
                            description.text = "Date: $outputDate\nValue: ${e.y}"
                        } catch (e: Exception) {
                            Log.d("Chart", "Exception")
                            e.printStackTrace()
                        }
                    }
                }

                override fun onNothingSelected() {}
            })
        }
    }
}