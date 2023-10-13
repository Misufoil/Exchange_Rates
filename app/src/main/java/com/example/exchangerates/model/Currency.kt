package com.example.exchangerates.model

data class Currency(
    val Date: String,
    val PreviousDate: String,
    val PreviousURL: String,
    val Timestamp: String,
    val Valute: Map<String, CurrencyItem>
)
