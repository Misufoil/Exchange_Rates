package com.example.exchangerates.screens.currency

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exchangerates.data.repository.Repository
import com.example.exchangerates.model.Currency
import kotlinx.coroutines.launch
import retrofit2.Response

class CurrencyViewModel: ViewModel() {
    var repo = Repository()
    val myCurrencyList: MutableLiveData<Response<Currency>> = MutableLiveData()

    fun getCurrencies() {
        viewModelScope.launch {
            myCurrencyList.value = repo.getCurrencyInfo()
        }
    }

}