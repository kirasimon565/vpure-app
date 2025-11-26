package com.vpure.app.data

import android.content.Context
import com.vpure.app.utils.Constants

object Preferences {

    fun getToken(context: Context): String? {
        val prefs = context.getSharedPreferences(Constants.PREFS_TOKEN_FILE, Context.MODE_PRIVATE)
        return prefs.getString(Constants.USER_TOKEN, null)
    }

    fun setToken(context: Context, token: String?) {
        val prefs = context.getSharedPreferences(Constants.PREFS_TOKEN_FILE, Context.MODE_PRIVATE)
        prefs.edit().putString(Constants.USER_TOKEN, token).apply()
    }

    fun clearToken(context: Context) {
        val prefs = context.getSharedPreferences(Constants.PREFS_TOKEN_FILE, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}
