package com.example.exchangerates.screens.currency

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.exchangerates.util.REALIZATION
import com.example.exchangerates.data.retrofit.Repository
import com.example.exchangerates.data.room.CurrencyDatabase
import com.example.exchangerates.data.room.repository.CurrencyRepositoryRealization
import com.example.exchangerates.model.Currency
import com.example.exchangerates.model.CurrencyItem
import kotlinx.coroutines.launch
import retrofit2.Response

class CurrencyViewModel(application: Application) : AndroidViewModel(application) {
    var repo = Repository()
    val myCurrencyList: MutableLiveData<Response<Currency>> = MutableLiveData()
    private val context = application

    fun getCurrenciesRetrofit() {
        viewModelScope.launch {
            try {
                myCurrencyList.value = repo.getCurrencyInfo()
            } catch (e: Exception) {
                Log.e("ERROR", e.message.toString())
            }
        }
    }

    fun initDatabase() {
        val daoCurrency = CurrencyDatabase.invoke(context).getCurrencyDao()
        REALIZATION = CurrencyRepositoryRealization(daoCurrency)
    }

    fun filterAndSortCurrencies(query: String): List<CurrencyItem> {
        val lowercaseQuery = query.lowercase()

        return myCurrencyList.value?.body()!!.Valute.values.filter { currency ->
            // Проверка наличия запроса в полях Name или NumCode
            currency.Name.lowercase().contains(lowercaseQuery) ||
                    currency.CharCode.lowercase().contains(lowercaseQuery)
        }.sortedWith(compareBy({ it.Name }, { it.CharCode }))

    }
}