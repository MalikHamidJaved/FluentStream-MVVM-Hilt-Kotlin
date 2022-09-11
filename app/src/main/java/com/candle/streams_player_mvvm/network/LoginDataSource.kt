package com.candle.streams_player_mvvm.network

import com.candle.streams_player_mvvm.model.LoggedInUser
import com.candle.streams_player_mvvm.util.DataState
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    val dummyAdminMap = HashMap<String,String>()
    fun login(username: String): LoggedInUser? {
        makeDummyAdmins()
        try {

            val fakeUser = LoggedInUser(username, dummyAdminMap.contains(username))
            return fakeUser
        } catch (e: Throwable) {
            return  null
        }
    }

    private fun makeDummyAdmins() {
        dummyAdminMap.put("farbrother0","");
        dummyAdminMap.put("bushill3f","");
        dummyAdminMap.put("mklagges3g","");
        dummyAdminMap.put("rmcellen3h","");
        dummyAdminMap.put("baguley3i","");
        dummyAdminMap.put("mirams4","");
        dummyAdminMap.put("oduggan7","");
        dummyAdminMap.put("gbert9","");
        dummyAdminMap.put("gamblesa","");
    }

    fun logout() {
    }
}