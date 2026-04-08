package com.example.proyectobim1.data.repository

import com.example.proyectobim1.data.local.PreferencesManager
import com.example.proyectobim1.data.model.Car
import com.example.proyectobim1.data.model.CarUi
import com.example.proyectobim1.data.remote.ApiService

class CarRepository(private val api: ApiService, private val prefs: PreferencesManager) {
    suspend fun getCars(): List<CarUi> {
        return api.getCars().map { car ->
            CarUi(car, prefs.getCarImage(car.id))
        }
    }

    suspend fun createCar(car: Car, selectedImageResId: Int): Car {
        val newCar = api.createCar(car)
        // Guardar la imagen seleccionada localmente usando el ID que devuelve la API
        prefs.saveCarImage(newCar.id, selectedImageResId)
        return newCar
    }

    suspend fun getCarDetail(id: Int): CarUi {
        val car = api.getCarById(id)
        return CarUi(car, prefs.getCarImage(car.id))
    }
}