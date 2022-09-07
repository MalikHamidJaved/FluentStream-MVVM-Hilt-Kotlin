package com.candle.streams_player_mvvm.network

import com.google.gson.annotations.SerializedName

class StreamSResponse:ArrayList<StreamItemResponse>()

data class StreamItemResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("recording")
    val recording: String?,

    @SerializedName("timestamp")
    val timestamp: String?,

    @SerializedName("username_from")
    val username_from: String?,

    @SerializedName("username_to")
    val username_to: String?
)