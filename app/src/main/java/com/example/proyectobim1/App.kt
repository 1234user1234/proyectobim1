package com.example.proyectobim1

import android.app.Application
import androidx.lifecycle.MutableLiveData

class App : Application() {
    companion object {
        val sessionExpiredEvent = MutableLiveData<Boolean>()
    }
    override fun onCreate() {
        super.onCreate()
    }
}