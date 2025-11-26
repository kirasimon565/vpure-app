package com.vpure.app.api

import android.content.Context
import com.vpure.app.data.Preferences
import com.vpure.app.data.UserManager
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = Preferences.getToken(context)

        // Attach Authorization header ONLY if token exists
        val requestBuilder = originalRequest.newBuilder()
        if (!token.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        val response = chain.proceed(requestBuilder.build())

        // If the server returns 401 → token expired or invalid
        if (response.code == 401) {
            // Clear token + user
            Preferences.clearToken(context)
            UserManager.clearUser(context)

            // Do NOT start an Activity here — app will redirect safely when it sees no token
        }

        return response
    }
}
