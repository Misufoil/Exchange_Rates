package com.example.exchangerates.util

import com.example.exchangerates.data.room.repository.CurrencyRepositoryRealization

const val BASE_URL = "https://www.cbr-xml-daily.ru/"
const val PARSING_URL = "https://www.cbr.ru/currency_base/dynamics/?UniDbQuery.Posted=True&UniDbQuery.so=1&UniDbQuery.mode=1&UniDbQuery.date_req1=&UniDbQuery.date_req2="
lateinit var REALIZATION: CurrencyRepositoryRealization
const val SEARCH_NEWS_TAME_DELAY = 500L