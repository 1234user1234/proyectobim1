package com.example.proyectobim1.data.local
import android.content.Context
import android.content.SharedPreferences
import com.example.proyectobim1.R

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("AutoDrivePrefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) = prefs.edit().putString("TOKEN", token).apply()
    fun getToken(): String? = prefs.getString("TOKEN", null)
    fun clearSession() = prefs.edit().clear().apply()

    // Persistencia de imágenes locales
    fun saveCarImage(carId: Int, imageResId: Int) = prefs.edit().putInt("IMG_$carId", imageResId).apply()
    fun getCarImage(carId: Int): Int = prefs.getInt("IMG_$carId", R.drawable.ic_car_sedan)
}