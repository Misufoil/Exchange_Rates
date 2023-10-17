//package com.example.exchangerates.data.room.dao
//
//import androidx.lifecycle.LiveData
//import androidx.room.*
//import com.example.exchangerates.model.CurrencyItem
//
//@Dao
//interface CurrencyDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(currencyItem: CurrencyItem)
//
//    @Delete
//    suspend fun delete(currencyItem: CurrencyItem)
//
//    @Query("SELECT * FROM articles")
//    fun getFavoriteMovies(): LiveData<List<CurrencyItem>>
//
//}