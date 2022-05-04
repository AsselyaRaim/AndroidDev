package com.example.cryptotracker.data.models.currencyMetadata


import com.google.gson.annotations.SerializedName

data class Urls(
    val reddit: List<String>,
    @SerializedName("source_code")
    val sourceCode: List<String>,
    @SerializedName("technical_doc")
    val technicalDoc: List<String>,
    val twitter: List<Any>,
    val website: List<String>
)