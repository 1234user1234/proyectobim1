package com.example.proyectobim1.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectobim1.data.model.Car
import com.example.proyectobim1.data.model.CarUi
import com.example.proyectobim1.data.model.Resource
import com.example.proyectobim1.data.repository.CarRepository
import kotlinx.coroutines.launch

class CarViewModel(private val repo: CarRepository) : ViewModel() {

    private val _cars = MutableLiveData<Resource<List<CarUi>>>()
    val cars: LiveData<Resource<List<CarUi>>> = _cars

    private val _carDetail = MutableLiveData<Resource<CarUi>>()
    val carDetail: LiveData<Resource<CarUi>> = _carDetail

    private val _createCarResult = MutableLiveData<Resource<Car>>()
    val createCarResult: LiveData<Resource<Car>> = _createCarResult

    private var originalCars: List<CarUi> = emptyList()

    fun fetchCars() {
        viewModelScope.launch {
            _cars.value = Resource.Loading
            try {
                originalCars = repo.getCars()
                _cars.value = Resource.Success(originalCars)
            } catch (e: Exception) {
                _cars.value = Resource.Error("Error al cargar vehículos")
            }
        }
    }

    fun filterCars(query: String) {
        if (query.isEmpty()) {
            _cars.value = Resource.Success(originalCars)
            return
        }
        val filtered = originalCars.filter {
            it.car.make.contains(query, ignoreCase = true) ||
                    it.car.model.contains(query, ignoreCase = true)
        }
        _cars.value = Resource.Success(filtered)
    }

    fun getCarDetail(id: Int) {
        viewModelScope.launch {
            _carDetail.value = Resource.Loading
            try {
                _carDetail.value = Resource.Success(repo.getCarDetail(id))
            } catch (e: Exception) {
                _carDetail.value = Resource.Error(e.message ?: "Error")
            }
        }
    }

    fun saveCar(car: Car, imageResId: Int) {
        viewModelScope.launch {
            _createCarResult.value = Resource.Loading
            try {
                _createCarResult.value = Resource.Success(repo.createCar(car, imageResId))
            } catch (e: Exception) {
                _createCarResult.value = Resource.Error(e.message ?: "Error al guardar")
            }
        }
    }
}