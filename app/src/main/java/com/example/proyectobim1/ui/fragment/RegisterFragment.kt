package com.example.proyectobim1.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.proyectobim1.R
import com.example.proyectobim1.data.local.PreferencesManager
import com.example.proyectobim1.data.model.Car
import com.example.proyectobim1.data.model.Resource
import com.example.proyectobim1.data.remote.RetrofitClient
import com.example.proyectobim1.data.repository.CarRepository
import com.example.proyectobim1.databinding.FragmentRegisterBinding
import com.example.proyectobim1.ui.adapter.CarIconAdapter
import com.example.proyectobim1.ui.viewmodel.CarViewModel

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CarViewModel
    private lateinit var iconAdapter: CarIconAdapter

    // Lista de los 5 íconos vectoriales locales
    private val carIcons = listOf(
        R.drawable.ic_car_sedan,
        R.drawable.ic_car_suv,
        R.drawable.ic_car_sports,
        R.drawable.ic_car_truck,
        R.drawable.ic_car_electric
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = PreferencesManager(requireContext())
        val api = RetrofitClient.getInstance(prefs)
        val repo = CarRepository(api, prefs)
        viewModel = CarViewModel(repo)

        setupIconSelector()
        setupValidations()
        setupSaveButton()
    }

    private fun setupIconSelector() {
        iconAdapter = CarIconAdapter(carIcons) { /* Lógica de selección dentro del adaptador */ }
        binding.rvCarIcons.adapter = iconAdapter
    }

    private fun setupValidations() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateForm()
            }
        }

        binding.etMake.addTextChangedListener(watcher)
        binding.etModel.addTextChangedListener(watcher)
        binding.etYear.addTextChangedListener(watcher)
        binding.etSpeed.addTextChangedListener(watcher)
        binding.etFuel.addTextChangedListener(watcher)
    }

    private fun validateForm() {
        val make = binding.etMake.text.toString().trim()
        val model = binding.etModel.text.toString().trim()
        val yearStr = binding.etYear.text.toString().trim()
        val speedStr = binding.etSpeed.text.toString().trim()
        val fuelStr = binding.etFuel.text.toString().trim()

        val year = yearStr.toIntOrNull()
        val speed = speedStr.toIntOrNull()
        val fuel = fuelStr.toIntOrNull()

        // REGLAS ANTI-IA DE VALIDACIÓN
        val isValid = make.isNotEmpty() &&
                model.isNotEmpty() &&
                year != null && year in 1900..2026 && // Regla 1
                speed != null && speed > 0 &&          // Regla 2
                fuel != null && fuel > 0               // Regla 2

        binding.btnSaveCar.isEnabled = isValid
        binding.btnSaveCar.alpha = if (isValid) 1.0f else 0.5f
    }

    private fun setupSaveButton() {
        binding.btnSaveCar.setOnClickListener {
            val car = Car(
                make = binding.etMake.text.toString().trim(),
                model = binding.etModel.text.toString().trim(),
                year = binding.etYear.text.toString().trim().toInt(),
                speed = binding.etSpeed.text.toString().trim().toInt(),
                fuel = binding.etFuel.text.toString().trim().toInt()
            )

            val selectedImageResId = iconAdapter.getSelectedIcon()

            viewModel.saveCar(car, selectedImageResId)
        }

        viewModel.createCarResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> binding.btnSaveCar.isEnabled = false
                is Resource.Success -> {
                    Toast.makeText(context, "Vehículo guardado con éxito", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack() // Volver a la lista
                }
                is Resource.Error -> {
                    Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                    validateForm() // Re-habilitar botón si hay error
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}