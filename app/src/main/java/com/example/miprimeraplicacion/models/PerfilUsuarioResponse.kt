package com.example.miprimeraplicacion.models

data class PerfilUsuarioResponse(
    val nombre: String,
    val apellidoPaterno: String,
    val apellidoMaterno: String,
    val email: String,
    val boleta: String,
    val password: String
)