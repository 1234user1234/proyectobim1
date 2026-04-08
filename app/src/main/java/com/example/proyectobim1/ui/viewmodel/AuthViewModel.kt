package com.example.proyectobim1.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectobim1.data.model.LoginRequest
import com.example.proyectobim1.data.model.LoginResponse
import com.example.proyectobim1.data.model.Resource
import com.example.proyectobim1.data.remote.ApiService
import kotlinx.coroutines.launch

class AuthViewModel(private val api: ApiService) : ViewModel() {

    private val _loginResult = MutableLiveData<Resource<LoginResponse>>()
    val loginResult: LiveData<Resource<LoginResponse>> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = Resource.Loading
            try {
                val response = api.login(LoginRequest(email, password))
                _loginResult.value = Resource.Success(response)
            } catch (e: Exception) {
                _loginResult.value = Resource.Error("Credenciales inválidas o error de conexión")
            }
        }
    }
}