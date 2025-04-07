package com.example.miprimeraplicacion

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.miprimeraplicacion.models.usuario
import com.example.miprimeraplicacion.network.RetrofitClient
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var tilNombre: TextInputLayout
    private lateinit var tilApellidoPaterno: TextInputLayout
    private lateinit var tilApellidoMaterno: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilBoleta: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tilConfirmPassword: TextInputLayout

    private lateinit var etNombre: EditText
    private lateinit var etApellidoPaterno: EditText
    private lateinit var etApellidoMaterno: EditText
    private lateinit var etEmail: EditText
    private lateinit var etBoleta: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegistrar: Button
    private lateinit var tvLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // Inicializar TextInputLayouts
        tilNombre = findViewById(R.id.tilNombre)
        tilApellidoPaterno = findViewById(R.id.tilApellidoPaterno)
        tilApellidoMaterno = findViewById(R.id.tilApellidoMaterno)
        tilEmail = findViewById(R.id.tilEmail)
        tilBoleta = findViewById(R.id.tilBoleta)
        tilPassword = findViewById(R.id.tilPassword)
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword)

        // Inicializar EditTexts
        etNombre = findViewById(R.id.etNombre)
        etApellidoPaterno = findViewById(R.id.etApellidoPaterno)
        etApellidoMaterno = findViewById(R.id.etApellidoMaterno)
        etEmail = findViewById(R.id.etEmail)
        etBoleta = findViewById(R.id.etBoleta)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)

        // Inicializar botones e iconos
        btnRegistrar = findViewById(R.id.btnRegistrar)
        tvLogin = findViewById(R.id.tvLogin)


        // Configurar listeners para validación en tiempo real
        setupTextChangeListeners()

        btnRegistrar.setOnClickListener {
            if (validarFormulario()) {
                registrarUsuario()
            }
        }

        tvLogin.setOnClickListener{
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupTextChangeListeners() {
        etNombre.doAfterTextChanged { validarNombre() }
        etApellidoPaterno.doAfterTextChanged { validarApellidoPaterno() }
        etApellidoMaterno.doAfterTextChanged { validarApellidoMaterno() }
        etEmail.doAfterTextChanged { validarEmail() }
        etBoleta.doAfterTextChanged { validarBoleta() }
        etPassword.doAfterTextChanged {
            validarPassword()
            validarConfirmacionPassword() // Para validar coincidencia cuando cambia la contraseña
        }
        etConfirmPassword.doAfterTextChanged { validarConfirmacionPassword() }
    }

    private fun validarNombre(): Boolean {
        val nombre = etNombre.text.toString().trim()

        return when {
            nombre.isEmpty() -> {
                tilNombre.error = "El nombre es obligatorio"
                false
            }
            nombre.length < 2 -> {
                tilNombre.error = "El nombre debe tener al menos 2 caracteres"
                false
            }
            !nombre.all{ it.isLetter() } -> {
                tilNombre.error = "El nombre debe contener solo letras"
                false
            }
            else -> {
                tilNombre.error = null
                true
            }
        }
    }

    private fun validarApellidoPaterno(): Boolean {
        val apellido = etApellidoPaterno.text.toString().trim()

        return when {
            apellido.isEmpty() -> {
                tilApellidoPaterno.error = "El apellido paterno es obligatorio"
                false
            }
            apellido.length < 2 -> {
                tilApellidoPaterno.error = "El apellido debe tener al menos 2 caracteres"
                false
            }
            !apellido.all{ it.isLetter() } -> {
                tilApellidoPaterno.error = "El apellido debe contener solo letras"
                false
            }
            else -> {
                tilApellidoPaterno.error = null
                true
            }
        }
    }

    private fun validarApellidoMaterno(): Boolean {
        val apellido = etApellidoMaterno.text.toString().trim()

        if (apellido.isEmpty()){
            tilApellidoMaterno.error = null
            return true;
        }else{

        return when {
            apellido.length < 2 -> {
                tilApellidoMaterno.error = "El apellido debe tener al menos 2 caracteres"
                false
            }

            !apellido.all { it.isLetter() } -> {
                tilApellidoMaterno.error = "El apellido debe contener solo letras"
                false
            }

            else -> {
                tilApellidoMaterno.error = null
                true
            }
        }
        }
    }

    private fun validarEmail(): Boolean {
        val email = etEmail.text.toString().trim()
        val emailPattern = android.util.Patterns.EMAIL_ADDRESS

        return when {
            email.isEmpty() -> {
                tilEmail.error = "El correo electrónico es obligatorio"
                false
            }
            !emailPattern.matcher(email).matches() -> {
                tilEmail.error = "Introduce un correo electrónico válido"
                false
            }
            else -> {
                tilEmail.error = null
                true
            }
        }
    }

    private fun validarBoleta(): Boolean {
        val boleta = etBoleta.text.toString().trim()

        return when {
            boleta.isEmpty() -> {
                tilBoleta.error = "La boleta es obligatoria"
                false
            }
            !boleta.all { it.isDigit() } -> {
                tilBoleta.error = "La boleta debe contener solo números"
                false
            }
            else -> {
                tilBoleta.error = null
                true
            }
        }
    }

    private fun validarPassword(): Boolean {
        val password = etPassword.text.toString().trim()

        return when {
            password.isEmpty() -> {
                tilPassword.error = "La contraseña es obligatoria"
                false
            }
            password.length < 6 -> {
                tilPassword.error = "La contraseña debe tener al menos 6 caracteres"
                false
            }
            !password.any { it.isUpperCase() } -> {
                tilPassword.error = "La contraseña debe incluir al menos una letra mayúscula"
                false
            }
            !password.any { it.isDigit() } -> {
                tilPassword.error = "La contraseña debe incluir al menos un número"
                false
            }
            else -> {
                tilPassword.error = null
                true
            }
        }
    }

    private fun validarConfirmacionPassword(): Boolean {
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        return when {
            confirmPassword.isEmpty() -> {
                tilConfirmPassword.error = "La confirmación de contraseña es obligatoria"
                false
            }
            confirmPassword != password -> {
                tilConfirmPassword.error = "Las contraseñas no coinciden"
                false
            }
            else -> {
                tilConfirmPassword.error = null
                true
            }
        }
    }

    private fun validarFormulario(): Boolean {
        // Ejecutar todas las validaciones y comprobar que todas devuelven true
        val nombreValido = validarNombre()
        val apellidoPaternoValido = validarApellidoPaterno()
        val apellidoMaternoValido = validarApellidoMaterno()
        val emailValido = validarEmail()
        val boletaValida = validarBoleta()
        val passwordValido = validarPassword()
        val confirmPasswordValido = validarConfirmacionPassword()

        return nombreValido && apellidoPaternoValido && apellidoMaternoValido &&
                emailValido && boletaValida && passwordValido && confirmPasswordValido
    }

    private fun registrarUsuario() {
        // Mostrar loader
        showLoading(true)

        val nombre = etNombre.text.toString().trim()
        val apellidoPaterno = etApellidoPaterno.text.toString().trim()
        val apellidoMaterno = etApellidoMaterno.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val boleta = etBoleta.text.toString().trim()
        val password = etPassword.text.toString().trim()

        val user = usuario(nombre, apellidoPaterno, apellidoMaterno, email, boleta, password)

        RetrofitClient.instance.registerUser(user)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    showLoading(false)
                    if (response.isSuccessful) {
                        Toast.makeText(applicationContext, "Registro exitoso", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        when (response.code()) {
                            409 -> Toast.makeText(applicationContext, "El usuario ya existe", Toast.LENGTH_LONG).show()
                            422 -> Toast.makeText(applicationContext, "Error en la validación de datos", Toast.LENGTH_LONG).show()
                            else -> Toast.makeText(applicationContext, "Error en el registro: ${response.code()}", Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    showLoading(false)
                    Toast.makeText(applicationContext, "Fallo la conexión: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun showLoading(isLoading: Boolean) {
        // Aquí implementarías la lógica para mostrar/ocultar un indicador de carga
        // Por ejemplo, un ProgressBar
        val progressBar = findViewById<View>(R.id.progressBar)
        if (isLoading) {
            progressBar.visibility = View.VISIBLE
            btnRegistrar.isEnabled = false
        } else {
            progressBar.visibility = View.GONE
            btnRegistrar.isEnabled = true
        }
    }
}