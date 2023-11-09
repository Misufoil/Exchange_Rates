package com.example.exchangerates.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exchangerates.model.CurrencyItem
import com.example.exchangerates.util.REALIZATION
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {
    fun addFavoriteCurrency(currencyItem: CurrencyItem, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            REALIZATION.insertCurrency(currencyItem) {
                onSuccess()
            }
        }
    }
}