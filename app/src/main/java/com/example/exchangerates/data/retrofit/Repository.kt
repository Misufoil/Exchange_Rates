package com.example.exchangerates.data.retrofit


import androidx.lifecycle.LiveData
import com.example.exchangerates.data.retrofit.api.RetrofitInstance
import com.example.exchangerates.model.Currency
import com.example.exchangerates.model.CurrencyItem
import retrofit2.Response

class Repository {
    suspend fun getCurrencyInfo(): Response<Currency> {
        return RetrofitInstance.api.getCurrencyInfo()
    }
}