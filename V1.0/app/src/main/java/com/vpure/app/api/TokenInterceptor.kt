package com.vpure.app.api

import android.content.Context
import com.vpure.app.data.Preferences
import com.vpure.app.data.UserManager
import android.content.Intent
import com.vpure.app.LoginActivity
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody

class TokenInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        val token = Preferences.getToken(context)

        token?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        val response = chain.proceed(requestBuilder.build())

        if (response.code == 401) {
            // Token is invalid or expired, clear data and redirect to login
            Preferences.clearToken(context)
            UserManager.clearUser(context)
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)

            // Return a dummy response to prevent the original failed response from propagating
            return response.newBuilder()
                .code(200) // Pretend the request was successful to prevent onFailure callbacks
                .body(ResponseBody.create(null, "")) // Empty body
                .build()
        }

        return response
    }
}
