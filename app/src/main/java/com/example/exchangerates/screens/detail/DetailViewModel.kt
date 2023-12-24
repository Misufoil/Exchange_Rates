package com.example.exchangerates.screens.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exchangerates.data.jsoup.GraphRepository
import com.example.exchangerates.model.CurrencyItem
import com.example.exchangerates.model.GraphPoint
import com.example.exchangerates.util.REALIZATION
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailViewModel(private val graphRepository: GraphRepository) : ViewModel() {
    private val _graphPoints = MutableLiveData<List<GraphPoint>>()
    val graphPoints: LiveData<List<GraphPoint>> = _graphPoints

    private val _convertToAnotherResult = MutableLiveData<String>()
    val convertToAnotherResult: LiveData<String> = _convertToAnotherResult

    private val _convertToAnotherRub = MutableLiveData<String>()
    val convertToAnotherRub: LiveData<String> = _convertToAnotherRub

    fun loadGraphPoints(id: String, fromDate: String, toDate: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val points = graphRepository.getGraphPoints(id, fromDate, toDate)
            withContext(Dispatchers.Main) {
                _graphPoints.value = points
            }
        }
    }

    fun addFavoriteCurrency(currencyItem: CurrencyItem, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            REALIZATION.insertCurrency(currencyItem) {
                onSuccess()
            }
        }
    }

    fun convertToAnother(value: Double, nominal: Int, rubleAmount: String) {
        val amount = rubleAmount.toDoubleOrNull()
        if (amount != null) {
            val anotherAmount = amount / (value / nominal)
            _convertToAnotherResult.value = String.format("%.4f", anotherAmount)
        } else {
            _convertToAnotherResult.value = ""
        }
    }

    fun convertToRub(value: Double, nominal: Int, anotherAmount: String) {
        val amount = anotherAmount.toDoubleOrNull()
        if (amount != null) {
            val rubleAmount = amount * (value / nominal)
            _convertToAnotherRub.value = String.format("%.4f", rubleAmount)
        } else {
            _convertToAnotherRub.value = ""
        }
    }
}