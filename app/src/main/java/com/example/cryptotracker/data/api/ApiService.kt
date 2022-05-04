package com.example.cryptotracker.data.api

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

const val API_KEY = "28dccb26-5e86-46f3-965a-63e420858ca4"

interface ApiService {
    @GET("v1/cryptocurrency/listings/latest")
    @Headers(
        "X-CMC_PRO_API_KEY: 28dccb26-5e86-46f3-965a-63e420858ca4"
    )
    fun getCurrencyList(): Call<ResponseBody>

    @GET("/v2/cryptocurrency/quotes/latest")
    @Headers(
        "X-CMC_PRO_API_KEY: 28dccb26-5e86-46f3-965a-63e420858ca4"
    )
    fun getCurrencyDetails(@Query("id") id: String): Call<ResponseBody>

    @GET("v2/cryptocurrency/info")
    @Headers(
        "X-CMC_PRO_API_KEY: 28dccb26-5e86-46f3-965a-63e420858ca4"
    )
    fun getCurrencyMetadata(@Query("id") id: String): Call<ResponseBody>

    companion object {
        operator fun invoke(): ApiService {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://pro-api.coinmarketcap.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}