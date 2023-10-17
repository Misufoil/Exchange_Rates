package com.example.exchangerates.model

import androidx.room.Entity
import java.io.Serializable

@Entity(
    tableName = "articles"
)
data class CurrencyItem(
    val ID: String,
    val NumCode: String,
    val CharCode: String,
    val Nominal: Int,
    val Name: String,
    val Value: Double,
    val Previous: Double
): Serializable
