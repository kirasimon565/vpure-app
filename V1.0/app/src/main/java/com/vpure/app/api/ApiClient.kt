package com.vpure.app.api

import android.content.Context
import com.vpure.app.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private fun createOkHttpClient(context: Context): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(TokenInterceptor(context))
            .build()
    }

    fun getClient(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient(context))
            .build()
    }

    fun getWebSocketClient(context: Context): OkHttpClient {
        // We can reuse the same client, but for clarity, we'll create a new one.
        // In a real app, you might want a single client instance.
        return createOkHttpClient(context)
    }
}
