package com.candle.streams_player_mvvm.model

data class Stream(
    val id: Int,
    val recording: String?,
    val timestamp: String?,
    val username_from: String?,
    val username_to: String?,
    var isPlaying: Boolean = false
)