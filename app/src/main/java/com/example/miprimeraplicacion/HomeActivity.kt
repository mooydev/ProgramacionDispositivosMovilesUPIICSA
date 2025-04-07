package com.example.miprimeraplicacion

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var btnCerrarSesion: Button
    private lateinit var btnOrdenar: Button
    private lateinit var tvBienvenida: TextView

    // Nuevas variables para información de usuario
    private lateinit var txtNombreUsuario: TextView
    private lateinit var txtNumeroBoleta: TextView
    private lateinit var imgUsuario: CircleImageView

    // Variables para información de sesión
    private lateinit var txtHoraInicio: TextView
    private lateinit var txtTiempoTranscurrido: TextView

    // Variables para el cronómetro
    private val timerHandler = Handler(Looper.getMainLooper())
    private var startTime = 0L
    private var timeWhenStopped = 0L
    private var isChronometerRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        inicializarVistas()
        verificarSesion()
        cargarDatosUsuario()
        iniciarCronometroSesion()

        btnOrdenar.setOnClickListener {
            Toast.makeText(this, "Funcionalidad de ordenar en desarrollo", Toast.LENGTH_SHORT).show()
            // Aquí iría la navegación a la actividad de ordenar
        }

        // Configurar botón de cerrar sesión
        btnCerrarSesion.setOnClickListener {
            mostrarDialogoConfirmacion()
        }
    }

    private fun inicializarVistas() {
        // Vistas ya existentes
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)
        btnOrdenar = findViewById(R.id.btnOrdenar)
        tvBienvenida = findViewById(R.id.textView)

        // Nuevas vistas para información de usuario
        txtNombreUsuario = findViewById(R.id.txtNombreUsuario)
        txtNumeroBoleta = findViewById(R.id.txtNumeroBoleta)
        imgUsuario = findViewById(R.id.imgUsuario)

        // Vistas para información de sesión
        txtHoraInicio = findViewById(R.id.txtHoraInicio)
        txtTiempoTranscurrido = findViewById(R.id.txtTiempoTranscurrido)
    }

    private fun verificarSesion() {
        val sharedPreferences = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
        val token = sharedPreferences.getString("TOKEN", null)

        if (token.isNullOrEmpty()) {
            // Si no hay token, redirigir al login
            irAlLogin()
        }
    }

    private fun cargarDatosUsuario() {
        val sharedPreferences = getSharedPreferences("APP_PREFS", MODE_PRIVATE)

        // Recuperar datos del usuario desde SharedPreferences
        val nombre = sharedPreferences.getString("NOMBRE_USUARIO", "Usuario") ?: "Usuario"
        val boleta = sharedPreferences.getString("NUMERO_BOLETA", "Sin boleta") ?: "Sin boleta"
        val fotoUrl = sharedPreferences.getString("FOTO_URL", "") ?: ""

        // Mostrar datos en la interfaz
        txtNombreUsuario.text = nombre
        txtNumeroBoleta.text = "No. Boleta: $boleta"

        // Cargar imagen de perfil
        if (fotoUrl.isNotEmpty()) {
            Glide.with(this)
                .load(fotoUrl)
                .placeholder(R.drawable.default_user)
                .error(R.drawable.default_user)
                .into(imgUsuario)
        } else {
            imgUsuario.setImageResource(R.drawable.default_user)
        }
    }

    private fun iniciarCronometroSesion() {
        val sharedPreferences = getSharedPreferences("APP_PREFS", MODE_PRIVATE)

        // Verificar si ya hay una sesión iniciada
        val sesionIniciada = sharedPreferences.getBoolean("SESION_INICIADA", false)
        val horaInicio = sharedPreferences.getLong("HORA_INICIO_SESION", 0L)
        val tiempoAcumulado = sharedPreferences.getLong("TIEMPO_ACUMULADO", 0L)

        if (sesionIniciada && horaInicio > 0) {
            // Continuar la sesión existente
            val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val horaInicioStr = sdf.format(Date(horaInicio))
            txtHoraInicio.text = "Inicio: $horaInicioStr"

            // Calcular tiempo transcurrido desde el inicio
            timeWhenStopped = tiempoAcumulado
            startTime = SystemClock.elapsedRealtime() - timeWhenStopped
        } else {
            // Iniciar nueva sesión
            val currentTime = System.currentTimeMillis()
            val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val horaInicioStr = sdf.format(Date(currentTime))
            txtHoraInicio.text = "Inicio: $horaInicioStr"

            // Guardar hora de inicio
            val editor = sharedPreferences.edit()
            editor.putBoolean("SESION_INICIADA", true)
            editor.putLong("HORA_INICIO_SESION", currentTime)
            editor.putLong("TIEMPO_ACUMULADO", 0L)
            editor.apply()

            startTime = SystemClock.elapsedRealtime()
            timeWhenStopped = 0L
        }

        // Iniciar el cronómetro
        isChronometerRunning = true
        timerHandler.postDelayed(actualizarTiempo, 0)
    }

    private val actualizarTiempo = object : Runnable {
        override fun run() {
            if (isChronometerRunning) {
                val timeElapsed = SystemClock.elapsedRealtime() - startTime

                val seconds = (timeElapsed / 1000).toInt()
                val minutes = seconds / 60
                val hours = minutes / 60

                val secondsDisplay = seconds % 60
                val minutesDisplay = minutes % 60

                txtTiempoTranscurrido.text = String.format(
                    Locale.getDefault(),
                    "Tiempo: %02d:%02d:%02d",
                    hours,
                    minutesDisplay,
                    secondsDisplay
                )

                // Guardar tiempo acumulado periódicamente (cada segundo)
                val sharedPreferences = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putLong("TIEMPO_ACUMULADO", timeElapsed)
                editor.apply()

                // Programar la próxima actualización
                timerHandler.postDelayed(this, 1000)
            }
        }
    }

    private fun mostrarDialogoConfirmacion() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de que deseas cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                cerrarSesion()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun cerrarSesion() {
        // Detener el cronómetro
        isChronometerRunning = false
        timerHandler.removeCallbacks(actualizarTiempo)

        // 1. Eliminar token y datos de sesión de SharedPreferences
        val sharedPreferences = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("TOKEN")
        editor.remove("SESION_INICIADA")
        editor.remove("HORA_INICIO_SESION")
        editor.remove("TIEMPO_ACUMULADO")
        // Si tienes más datos de sesión, elimínalos también
        editor.apply()

        // 2. Mostrar mensaje de confirmación
        Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()

        // 3. Redireccionar al login
        irAlLogin()
    }

    private fun irAlLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        // Limpiar la pila de actividades para que el usuario no pueda volver atrás
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onPause() {
        super.onPause()
        // Guardar el estado del cronómetro
        if (isChronometerRunning) {
            val sharedPreferences = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putLong("TIEMPO_ACUMULADO", SystemClock.elapsedRealtime() - startTime)
            editor.apply()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Detener el cronómetro para evitar fugas de memoria
        timerHandler.removeCallbacks(actualizarTiempo)
    }
}