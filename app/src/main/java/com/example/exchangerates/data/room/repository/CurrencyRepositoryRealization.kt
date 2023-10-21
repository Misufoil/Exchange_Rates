package com.example.exchangerates.data.room.repository

import androidx.lifecycle.LiveData
import com.example.exchangerates.data.room.dao.CurrencyDao
import com.example.exchangerates.model.CurrencyItem

class CurrencyRepositoryRealization(private val moviesDao: CurrencyDao): CurrencyRepository  {
    override val allCurrency: LiveData<List<CurrencyItem>>
        get() = moviesDao.getFavoriteMovies()

    override suspend fun insertCurrency(currencyItem: CurrencyItem, onSuccess: () -> Unit) {
        moviesDao.insert(currencyItem)
        onSuccess()
    }

    override suspend fun deleteCurrency(currencyItem: CurrencyItem, onSuccess: () -> Unit) {
        moviesDao.delete(currencyItem)
        onSuccess()
    }

    override fun searchCurrency(searchQuery: String): LiveData<List<CurrencyItem>> {
       return moviesDao.searchCurrency("%$searchQuery%")
    }


}