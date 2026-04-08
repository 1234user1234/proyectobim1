package com.example.proyectobim1.data.remote

import com.example.proyectobim1.App
import com.example.proyectobim1.data.local.PreferencesManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val prefs: PreferencesManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        //Request
        val originalRequest = chain.request()
        val newRequest = originalRequest.newBuilder().apply {
            prefs.getToken()?.let { token ->
                addHeader("Authorization", "Bearer $token")
            }
        }.build()

        // procede Request
        val response = chain.proceed(newRequest)

        //401 (Sesión expirada)
        if (response.code == 401) {
            prefs.clearSession()
            App.sessionExpiredEvent.postValue(true)
        }

        return response
    }
}