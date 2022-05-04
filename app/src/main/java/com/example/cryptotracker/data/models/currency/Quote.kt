package com.example.cryptotracker.data.models.currency


import com.google.gson.annotations.SerializedName

data class Quote(
    @SerializedName("USD")
    val dataUSD: USD
)