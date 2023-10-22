package com.example.exchangerates.util

import com.example.exchangerates.data.room.repository.CurrencyRepositoryRealization

const val BASE_URL = "https://www.cbr-xml-daily.ru/"
lateinit var REALIZATION: CurrencyRepositoryRealization
const val SEARCH_NEWS_TAME_DELAY = 500L