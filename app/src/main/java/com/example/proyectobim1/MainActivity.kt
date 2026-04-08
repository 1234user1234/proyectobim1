package com.example.proyectobim1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.proyectobim1.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Observar expiración de sesión (Punto 2)
        App.sessionExpiredEvent.observe(this) { isExpired ->
            if (isExpired) {
                Snackbar.make(binding.root, "Sesión expirada", Snackbar.LENGTH_INDEFINITE).show()
                navController.navigate(R.id.loginFragment)
                App.sessionExpiredEvent.value = false // Resetear
            }
        }
    }
}
