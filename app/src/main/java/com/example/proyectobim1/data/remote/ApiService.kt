package com.example.proyectobim1.data.remote

import com.example.proyectobim1.data.model.Car
import com.example.proyectobim1.data.model.LoginRequest
import com.example.proyectobim1.data.model.LoginResponse
import retrofit2.http.*

interface ApiService {
    //@POST("auth/login")
    //suspend fun login(@Body request: LoginRequest): LoginResponse
    @POST("auth/api/token/")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("cars/")
    suspend fun getCars(): List<Car>

    @POST("cars/")
    suspend fun createCar(@Body car: Car): Car

    @GET("cars/{pk}/")
    suspend fun getCarById(@Path("pk") id: Int): Car
}