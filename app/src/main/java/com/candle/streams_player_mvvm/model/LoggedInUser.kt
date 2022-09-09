package com.candle.streams_player_mvvm.model

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
    val userId: String,
    val isAdmin: Boolean
)