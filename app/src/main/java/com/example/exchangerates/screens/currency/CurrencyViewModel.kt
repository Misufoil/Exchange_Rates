package com.example.exchangerates.screens.currency

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exchangerates.data.retrofit.Repository
import com.example.exchangerates.model.CurrencyItem
import kotlinx.coroutines.launch

class CurrencyViewModel(val repo: Repository) : ViewModel() {
    private val _myCurrencyList = MutableLiveData<List<CurrencyItem>>()
    val myCurrencyList: LiveData<List<CurrencyItem>> = _myCurrencyList

    private val _mySearchCurrencyList = MutableLiveData<List<CurrencyItem>>()
    val mySearchCurrencyList: LiveData<List<CurrencyItem>> = _mySearchCurrencyList

    fun getCurrenciesRetrofit() {
        viewModelScope.launch {
            try {
                val response = repo.getCurrencyInfo()
                _myCurrencyList.value =
                    response.body()!!.Valute.values.sortedBy { it.CharCode.lowercase() }
            } catch (e: Exception) {
                Log.e("ERROR", e.message.toString())
            }
        }
    }

    fun filterAndSortCurrencies(query: String) {
        val lowercaseQuery = query.lowercase()

        _mySearchCurrencyList.value = myCurrencyList.value?.filter { currency ->
            currency.Name.lowercase().contains(lowercaseQuery) ||
                    currency.CharCode.lowercase().contains(lowercaseQuery)
        }!!.sortedWith(compareBy({ it.Name }, { it.CharCode }))
    }
}