package com.example.exchangerates.data.api

import com.example.exchangerates.model.Currency
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("daily_json.js")
    suspend fun getCurrencyInfo(): Response<Currency>
}