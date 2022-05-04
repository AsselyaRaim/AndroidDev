package com.example.cryptotracker.data.converters

import androidx.room.TypeConverter
import com.example.cryptotracker.data.models.currency.Currency
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CurrencyConverter {
    @TypeConverter
    fun fromListOfInts(value: MutableList<Int>?): String? {
        if (value == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<MutableList<Int>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toListOfInts(value: String?): MutableList<Int>? {
        if (value == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<MutableList<Int>>() {}.type
        return gson.fromJson(value, type)
    }
}