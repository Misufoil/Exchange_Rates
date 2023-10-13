package com.example.exchangerates.data.repository


import com.example.exchangerates.data.api.RetrofitInstance
import com.example.exchangerates.model.Currency
import retrofit2.Response

class Repository {
    suspend fun getCurrencyInfo(): Response<Currency> {
        return RetrofitInstance.api.getCurrencyInfo()
    }
}