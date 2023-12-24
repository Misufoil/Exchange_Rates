package com.example.exchangerates.screens.currency

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.exchangerates.data.retrofit.Repository
import com.example.exchangerates.data.room.CurrencyDatabase
import com.example.exchangerates.data.room.repository.CurrencyRepositoryRealization
import com.example.exchangerates.util.REALIZATION

class CurrencyViewModelFactory(context: Application) : ViewModelProvider.Factory {
    private val repo = Repository()

    init {
        val daoCurrency = CurrencyDatabase.invoke(context).getCurrencyDao()
        REALIZATION = CurrencyRepositoryRealization(daoCurrency)
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CurrencyViewModel(repo = repo) as T
    }
}