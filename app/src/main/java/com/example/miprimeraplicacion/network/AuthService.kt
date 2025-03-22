package com.example.miprimeraplicacion.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val boleta: String, val password: String)
data class LoginResponse(val message: String, val token: String)

interface AuthService {
    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}