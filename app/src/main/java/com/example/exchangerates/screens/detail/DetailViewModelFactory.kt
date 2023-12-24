package com.example.exchangerates.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.exchangerates.data.jsoup.GraphRepository

class DetailViewModelFactory: ViewModelProvider.Factory {
    private val graphRepository by lazy { GraphRepository() }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailViewModel(
            graphRepository = graphRepository
        ) as T
    }
}