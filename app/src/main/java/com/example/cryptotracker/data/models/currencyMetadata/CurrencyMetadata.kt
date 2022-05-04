package com.example.cryptotracker.data.models.currencyMetadata


import com.google.gson.annotations.SerializedName

data class CurrencyMetadata(
    val id: Int,
    @SerializedName("date_added")
    val dateAdded: String,
    val description: String,
    val logo: String,
    val name: String,
    val slug: String,
    val symbol: String,
    val urls: Urls
)