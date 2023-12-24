package com.example.exchangerates.data.jsoup

import com.example.exchangerates.model.GraphPoint
import com.example.exchangerates.util.PARSING_URL
import org.jsoup.Jsoup
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class GraphRepository {
    fun getGraphPoints(id: String, fromDate: String, toDate: String): List<GraphPoint> {
        val myGraphPoints = mutableListOf<GraphPoint>()

        try {
            val url =
                "$PARSING_URL&UniDbQuery.VAL_NM_RQ=$id&UniDbQuery.From=$fromDate&UniDbQuery.To=$toDate"
            val document = Jsoup.connect(url).get()
            val trElements = document.select("table[class=data]").select("tr")

            myGraphPoints.clear()
            if (trElements != null) {
                for (i in 2 until trElements.size) {
                    val row = trElements[i]
                    val tdElements = row.select("td")
                    val date = tdElements[0].text()
                    val nominal = tdElements[1].text().toDouble()
                    val value = String.format(
                        Locale.US,
                        "%.4f",
                        tdElements[2].text().replace(",", ".").toDouble() / nominal
                    ).toDouble()
                    val dateFormat = LocalDate.parse(
                        date,
                        DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault())
                    )

                    myGraphPoints.add(0, (GraphPoint(dateFormat, value)))
                }
            } else {
                return emptyList()
            }
        } catch (e: IOException) {
            return emptyList()
        }
        return myGraphPoints
    }
}