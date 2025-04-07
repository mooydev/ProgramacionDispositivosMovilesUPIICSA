package com.example.miprimeraplicacion.network

import com.example.miprimeraplicacion.models.PerfilUsuarioResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

data class LoginRequest(val boleta: String, val password: String)
data class LoginResponse(val message: String, val token: String)

interface AuthService {
    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("usuarios/{boleta}")
    fun obtenerPerfilUsuario(
        @Header("Authorization") token: String,
        @Path("boleta") boleta: String
    ): Call<PerfilUsuarioResponse>
}