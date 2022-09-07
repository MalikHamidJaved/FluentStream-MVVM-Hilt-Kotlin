package com.candle.streams_player_mvvm.network

import retrofit2.http.GET

interface StreamApi {

    @GET("/history")
    suspend fun get(): List<StreamItemResponse>
}