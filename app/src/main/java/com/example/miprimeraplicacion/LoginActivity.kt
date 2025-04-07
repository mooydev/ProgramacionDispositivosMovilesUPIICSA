package com.example.miprimeraplicacion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.miprimeraplicacion.models.PerfilUsuarioResponse
import com.example.miprimeraplicacion.network.LoginRequest
import com.example.miprimeraplicacion.network.LoginResponse
import com.example.miprimeraplicacion.network.RetrofitClient
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import android.util.Log
import android.view.Gravity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.util.*
import kotlin.math.log

class LoginActivity : AppCompatActivity() {

    private lateinit var boleta: TextInputLayout
    private lateinit var password: TextInputLayout

    private lateinit var etBoleta: EditText
    private lateinit var etPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val tvIrRegistro = findViewById<TextView>(R.id.tvIrRegistro)

        boleta = findViewById(R.id.boleta)
        password = findViewById(R.id.password)

        etBoleta = findViewById(R.id.etBoleta)
        etPassword = findViewById(R.id.etPassword)

        val loginButton = findViewById<Button>(R.id.btnLogin)

        tvIrRegistro.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val boletaText = etBoleta.text.toString()
            val passwordText = etPassword.text.toString()

            val request = LoginRequest(boletaText, passwordText)

            RetrofitClient.authInstance.login(request).enqueue(object : Callback<LoginResponse> {override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val toast = Toast.makeText(this@LoginActivity, "Login exitoso", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.TOP, 0, 200) // Cambia la posición
                    toast.show()
                    val token = response.body()?.token
                    if (token != null) {
                        // Guardar token temporalmente
                        val sharedPreferences = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
                        sharedPreferences.edit().putString("TOKEN", token).apply()
                        // Obtener datos del usuario
                        obtenerDatosUsuario(token, boletaText)
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java);
                        startActivity(intent);
                        finish()
                    } else {
                        val toast = Toast.makeText(this@LoginActivity, "Error en los datos recibidos", Toast.LENGTH_SHORT)
                        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 200) // Cambia la posición
                        toast.show()
                    }
                } else {
                    // Leer el cuerpo del error
                    val errorBody = response.errorBody()?.string()
                    Log.e("LoginError", "Código: ${response.code()}, Mensaje: $errorBody")

                    // Intentar convertir el mensaje en JSON
                    val gson = Gson()
                    val type = object : TypeToken<Map<String, String>>() {}.type
                    val errorMap: Map<String, String>? = try {
                        gson.fromJson<Map<String, String>>(errorBody, type)
                    } catch (e: Exception) {
                        null
                    }

                    val mensajeError = errorMap?.get("error") ?: "Error desconocido"
                    val toast = Toast.makeText(this@LoginActivity, mensajeError, Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 200)
                    toast.show()
                }
            }
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
        }
    }
    private fun obtenerDatosUsuario(token: String, boleta: String) {
        RetrofitClient.authInstance.obtenerPerfilUsuario("Bearer $token", boleta)
            .enqueue(object : Callback<PerfilUsuarioResponse> {
                override fun onResponse(
                    call: Call<PerfilUsuarioResponse>,
                    response: Response<PerfilUsuarioResponse>
                ) {
                    if (response.isSuccessful) {
                        val perfil = response.body()
                        if (perfil != null) {
                            guardarDatosUsuario(
                                token,
                                perfil.nombre,
                                perfil.boleta,
                            )
                        }
                    } else {
                        val toast = Toast.makeText(this@LoginActivity, "Error al obtener datos del usuario", Toast.LENGTH_SHORT)
                        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 200) // Cambia la posición
                        toast.show()
                    }
                }

                override fun onFailure(call: Call<PerfilUsuarioResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun guardarDatosUsuario(token: String, nombreUsuario: String, numeroBoleta: String) {
        val sharedPreferences = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Guardar token de autenticación
        editor.putString("TOKEN", token)

        // Guardar datos del usuario
        editor.putString("NOMBRE_USUARIO", nombreUsuario)
        editor.putString("NUMERO_BOLETA", numeroBoleta)

        // Guardar información de la sesión
        val currentTime = System.currentTimeMillis()
        editor.putBoolean("SESION_INICIADA", true)
        editor.putLong("HORA_INICIO_SESION", currentTime)
        editor.putLong("TIEMPO_ACUMULADO", 0L)

        // Aplicar cambios
        editor.apply()
    }
}