package com.example.miprimeraplicacion
// SplashActivity.kt
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        var screenSplash = installSplashScreen();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        screenSplash.setKeepOnScreenCondition{true};
            val intent = (Intent(this, MainActivity::class.java))
            startActivity(intent);
            finish();
    }
}
