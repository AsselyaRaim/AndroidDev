package com.example.cryptotracker.data.models.currency


import com.google.gson.annotations.SerializedName

data class Currency(
    val id: Int,
    @SerializedName("circulating_supply")
    val circulatingSupply: Double,
    @SerializedName("cmc_rank")
    val cmcRank: Int,
    @SerializedName("date_added")
    val dateAdded: String,
    @SerializedName("last_updated")
    val lastUpdated: String,
    @SerializedName("max_supply")
    val maxSupply: Double,
    val name: String,
    @SerializedName("num_market_pairs")
    val numMarketPairs: Int,
    val quote: Quote,
    val symbol: String,
    @SerializedName("total_supply")
    val totalSupply: Double
)