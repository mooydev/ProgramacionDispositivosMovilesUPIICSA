package com.example.miprimeraplicacion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.miprimeraplicacion.network.LoginRequest
import com.example.miprimeraplicacion.network.LoginResponse
import com.example.miprimeraplicacion.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val tvIrRegistro = findViewById<TextView>(R.id.tvIrRegistro)
        val boletaInput = findViewById<EditText>(R.id.boleta)
        val passwordInput = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.btnLogin)

        tvIrRegistro.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val boleta = boletaInput.text.toString()
            val password = passwordInput.text.toString()

            val request = LoginRequest(boleta, password)

            RetrofitClient.authInstance.login(request).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val token = response.body()?.token
                        Toast.makeText(this@LoginActivity, "Login exitoso", Toast.LENGTH_SHORT).show()

                        // Guardar token en SharedPreferences
                        val sharedPreferences = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
                        sharedPreferences.edit().putString("TOKEN", token).apply()

                        // Ir a la pantalla principal
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Error en el login", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Fallo la conexión", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
