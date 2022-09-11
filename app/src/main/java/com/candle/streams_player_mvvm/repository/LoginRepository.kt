package com.candle.streams_player_mvvm.repository

import com.candle.streams_player_mvvm.model.LoggedInUser
import com.candle.streams_player_mvvm.network.LoginDataSource
import com.candle.streams_player_mvvm.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource) {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {

        user = null
    }

    fun logout() {
        user = null
        dataSource.logout()
    }

    suspend fun login(username: String): Flow<DataState<LoggedInUser>> = flow {
        emit(DataState.Loading)
        val result = dataSource.login(username)

        if(result != null){
            setLoggedInUser(result)
            emit(DataState.Success(result))
        }else{
            emit(DataState.Error(Exception("No user found")))
        }

    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}