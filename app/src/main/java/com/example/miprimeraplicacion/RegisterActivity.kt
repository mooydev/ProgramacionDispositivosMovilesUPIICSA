package com.example.miprimeraplicacion
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.miprimeraplicacion.models.usuario
import com.example.miprimeraplicacion.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etApellidoPaterno = findViewById<EditText>(R.id.etApellidoPaterno)
        val etApellidoMaterno = findViewById<EditText>(R.id.etApellidoMaterno)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etBoleta = findViewById<EditText>(R.id.etBoleta)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)

        btnRegistrar.setOnClickListener {
            val user = usuario (
                etNombre.text.toString(),
                etApellidoPaterno.text.toString(),
                etApellidoMaterno.text.toString(),
                etEmail.text.toString(),
                etBoleta.text.toString(),
                etPassword.text.toString()
            )

            RetrofitClient.instance.registerUser(user)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(applicationContext, "Registro exitoso", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(applicationContext, "Error en el registro", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(applicationContext, "Fallo la conexión: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
        }
    }
}
