package com.example.exchangerates.model

import java.io.Serializable

data class CurrencyItem(
    val ID: String,
    val NumCode: String,
    val CharCode: String,
    val Nominal: Int,
    val Name: String,
    val Value: Double,
    val Previous: Double
): Serializable
