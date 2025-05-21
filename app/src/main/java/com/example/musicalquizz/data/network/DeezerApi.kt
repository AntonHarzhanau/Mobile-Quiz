package com.example.musicalquizz.data.network

import com.example.musicalquizz.data.model.Track
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface DeezerService {
    @GET("search")
    suspend fun searchTracks(@Query("q") query: String): TrackResponse

    @GET("search/album")
    suspend fun searchAlbums(@Query("q") query: String): AlbumResponse

    @GET("track/{id}")
    suspend fun getTrackById(@Path("id") id: Long): Track

    @GET("album/{id}")
    suspend fun getAlbumById(@Path("id") id: Long): AlbumWithTracks
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
