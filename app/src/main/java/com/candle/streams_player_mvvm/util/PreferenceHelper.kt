package com.candle.streams_player_mvvm.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object PreferenceHelper {

    val USER = "USER"
    val IS_ADMIN = "is_admin"

    val PREFF_NAME = "default_pref"

    fun defaultPreference(context: Context): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun customPreference(context: Context): SharedPreferences = context.getSharedPreferences(PREFF_NAME, Context.MODE_PRIVATE)

    inline fun SharedPreferences.editMe(operation: (SharedPreferences.Editor) -> Unit) {
        val editMe = edit()
        operation(editMe)
        editMe.apply()
    }

    var SharedPreferences.user
        get() = getString(USER, "")
        set(value) {
            editMe {
                it.putString(USER, value)
            }
        }

    var SharedPreferences.isAdmin
        get() = getBoolean(IS_ADMIN, false)
        set(value) {
            editMe {
                it.putBoolean(IS_ADMIN, value)
            }
        }

    var SharedPreferences.clearValues
        get() = { }
        set(value) {
            editMe {
                it.clear()
            }
        }
}

