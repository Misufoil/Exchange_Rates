package com.example.exchangerates.data.room.repository

import androidx.lifecycle.LiveData
import com.example.exchangerates.model.CurrencyItem

interface CurrencyRepository {
    val allCurrency: LiveData<List<CurrencyItem>>
    suspend fun insertCurrency(currencyItem: CurrencyItem, onSuccess: () -> Unit)
    suspend fun deleteCurrency(currencyItem: CurrencyItem, onSuccess: () -> Unit)
}
