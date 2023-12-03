package com.example.exchangerates.screens.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exchangerates.model.CurrencyItem
import com.example.exchangerates.util.REALIZATION
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriteViewModel : ViewModel() {
    fun getFavoriteCurrency(): LiveData<List<CurrencyItem>> =
        REALIZATION.allCurrency

    fun addFavoriteCurrency(currencyItem: CurrencyItem, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            REALIZATION.insertCurrency(currencyItem) {
                onSuccess()
            }
        }
    }

    fun deleteFavoriteCurrency(currencyItem: CurrencyItem, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            REALIZATION.deleteCurrency(currencyItem) {
                onSuccess()
            }
        }
    }
}