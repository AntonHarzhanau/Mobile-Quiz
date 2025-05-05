package com.example.musicalquizz.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface DeezerService {
//    @GET("search")
//    suspend fun searchTracks(@Query("q") query: String): TrackResponse

    @GET("search")
    suspend fun searchTracks(
        @Query("q") query: String,
        @Query("index") index: Int = 0,   // смещение
        @Query("limit") limit: Int = 25   // сколько треков
    ): TrackResponse
}


object DeezerApi {
    private const val BASE_URL = "https://api.deezer.com/"

    val retrofitService: DeezerService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DeezerService::class.java)
    }
}