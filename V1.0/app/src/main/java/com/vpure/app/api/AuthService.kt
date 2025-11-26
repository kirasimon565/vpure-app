package com.vpure.app.api

import com.vpure.app.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class AuthRequest(val email: String, val password: String, val name: String? = null)
data class AuthResponse(val token: String, val user: User)

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: AuthRequest): Response<AuthResponse>
}
