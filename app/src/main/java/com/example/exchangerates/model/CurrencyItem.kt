package com.example.exchangerates.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "currency"
)
data class CurrencyItem(
    @PrimaryKey(autoGenerate = false)
    val ID: String,
    val NumCode: String,
    val CharCode: String,
    val Nominal: Int,
    val Name: String,
    val Value: Double,
    val Previous: Double
): Serializable
