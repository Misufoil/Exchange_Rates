package com.example.exchangerates.screens.currency

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.exchangerates.REALIZATION
import com.example.exchangerates.data.retrofit.Repository
import com.example.exchangerates.data.room.CurrencyDatabase
import com.example.exchangerates.data.room.repository.CurrencyRepositoryRealization
import com.example.exchangerates.model.Currency
import com.example.exchangerates.model.CurrencyItem
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response

class CurrencyViewModel(application: Application): AndroidViewModel(application) {
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

//    fun searchDatabase(searchQuery: String): LiveData<List<CurrencyItem>> {
//        return REALIZATION.searchCurrency(searchQuery)
//    }

    fun searchCurrencies(query: String): LiveData<Response<Currency>> {
        val response = myCurrencyList.value
        return if (query.isEmpty()) {
            myCurrencyList
        } else {

            getCurrenciesRetrofit()
        }
    }
}