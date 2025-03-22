package com.example.miprimeraplicacion.network

import com.example.miprimeraplicacion.models.usuario
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("registrar")
    fun registerUser(@Body user: usuario): Call<Void>
}