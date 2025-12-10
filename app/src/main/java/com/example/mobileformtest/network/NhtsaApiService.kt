package com.example.mobileformtest.network

import com.example.mobileformtest.model.VinDecodeResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "https://vpic.nhtsa.dot.gov/api/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface NhtsaApiService {
    @GET("vehicles/DecodeVinValues/{vin}")
    suspend fun decodeVin(
        @Path("vin") vin: String,
        @Query("format") format: String = "json",
        @Query("modelyear") modelYear: String? = null
    ): VinDecodeResponse
}

object NhtsaApi {
    val retrofitService: NhtsaApiService by lazy {
        retrofit.create(NhtsaApiService::class.java)
    }
}