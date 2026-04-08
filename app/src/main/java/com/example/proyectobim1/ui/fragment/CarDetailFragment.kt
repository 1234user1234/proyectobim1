package com.example.proyectobim1.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.proyectobim1.data.local.PreferencesManager
import com.example.proyectobim1.data.model.Resource
import com.example.proyectobim1.data.remote.RetrofitClient
import com.example.proyectobim1.data.repository.CarRepository
import com.example.proyectobim1.databinding.FragmentCarDetailBinding
import com.example.proyectobim1.ui.viewmodel.CarViewModel

class CarDetailFragment : Fragment() {
    private var _binding: FragmentCarDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CarViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCarDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = PreferencesManager(requireContext())
        val api = RetrofitClient.getInstance(prefs)
        val repo = CarRepository(api, prefs)
        viewModel = CarViewModel(repo)

        // Obtener el ID pasado por SafeArgs desde el RecyclerView
        val carId = requireArguments().getInt("carId", -1)

        viewModel.getCarDetail(carId)

        viewModel.carDetail.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> { /* Podrías mostrar un progressBar si quisieras */ }
                is Resource.Success -> {
                    val carUi = resource.data
                    val car = carUi.car

                    // Asignar imagen local
                    binding.ivDetailIcon.setImageResource(carUi.imageResId)

                    binding.tvDetailMakeModel.text = "${car.make} ${car.model}"
                    binding.tvDetailYear.text = "Año: ${car.year}"

                    // --- LÓGICA ANTI-IA 1: CONVERSIÓN A MPH ---
                    // En la lista se mostró en km/h, aquí se convierte a mph (km/h * 0.62)
                    val speedMph = (car.speed * 0.62).toInt()
                    binding.tvDetailSpeed.text = "Velocidad Máxima: $speedMph mph"

                    binding.tvDetailFuel.text = "Combustible: ${car.fuel} L"
                }
                is Resource.Error -> { /* Mostrar error */ }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}