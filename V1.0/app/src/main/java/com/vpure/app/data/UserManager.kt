package com.vpure.app.data

import android.content.Context
import com.google.gson.Gson
import com.vpure.app.models.User

object UserManager {
    private const val PREFS_USER_FILE = "prefs_user_file"
    private const val USER_DATA = "user_data"
    private val gson = Gson()

    fun saveUser(context: Context, user: User) {
        val prefs = context.getSharedPreferences(PREFS_USER_FILE, Context.MODE_PRIVATE)
        val userJson = gson.toJson(user)
        prefs.edit().putString(USER_DATA, userJson).apply()
    }

    fun getUser(context: Context): User? {
        val prefs = context.getSharedPreferences(PREFS_USER_FILE, Context.MODE_PRIVATE)
        val userJson = prefs.getString(USER_DATA, null)
        return if (userJson != null) {
            gson.fromJson(userJson, User::class.java)
        } else {
            null
        }
    }

    fun clearUser(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_USER_FILE, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}
