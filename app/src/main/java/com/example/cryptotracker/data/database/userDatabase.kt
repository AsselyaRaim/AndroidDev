package com.example.cryptotracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cryptotracker.data.converters.CurrencyConverter
import com.example.cryptotracker.data.dao.userDao
import com.example.cryptotracker.data.models.User

@Database(entities = [User::class], version = 1)
@TypeConverters(CurrencyConverter::class)
abstract class userDatabase: RoomDatabase() {
    abstract fun userDao(): userDao
}