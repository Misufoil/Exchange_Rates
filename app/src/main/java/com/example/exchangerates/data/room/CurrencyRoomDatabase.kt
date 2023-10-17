//package com.example.exchangerates.data.room
//
//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//import com.example.exchangerates.data.room.dao.CurrencyDao
//import com.example.exchangerates.model.CurrencyItem
//
//@Database(
//    entities = [CurrencyItem::class],
//    version = 1
//)
//abstract class CurrencyRoomDatabase: RoomDatabase() {
//
//    abstract fun getCurrencyDao(): CurrencyDao
//
//    companion object {
//        @Volatile
//        private var instance: CurrencyRoomDatabase? = null
//        private val LOCK = Any()
//
//        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
//            instance ?: createDatabase(context).also { instance = it}
//        }
//
//        private fun createDatabase(context: Context) =
//            Room.databaseBuilder(
//                context.applicationContext,
//                CurrencyRoomDatabase::class.java,
//                "currency_db.db"
//            ).build()
//
//
//    }
//
//}