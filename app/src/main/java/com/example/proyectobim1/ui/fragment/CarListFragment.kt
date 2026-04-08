package com.example.proyectobim1.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectobim1.R
import com.example.proyectobim1.data.local.PreferencesManager
import com.example.proyectobim1.data.model.Resource
import com.example.proyectobim1.data.remote.RetrofitClient
import com.example.proyectobim1.data.repository.CarRepository
import com.example.proyectobim1.databinding.FragmentCarListBinding
import com.example.proyectobim1.ui.adapter.CarAdapter
import com.example.proyectobim1.ui.viewmodel.CarViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CarListFragment : Fragment() {
    private var _binding: FragmentCarListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CarViewModel
    private lateinit var adapter: CarAdapter
    private var searchJob: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCarListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = PreferencesManager(requireContext())
        val api = RetrofitClient.getInstance(prefs)
        val repo = CarRepository(api, prefs)
        viewModel = CarViewModel(repo)

        setupRecyclerView()
        setupSearchView()
        setupObservers()

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_carList_to_register)
        }
    }

    private fun setupRecyclerView() {
        adapter = CarAdapter { carId ->
            // CREAR UN BUNDLE ESTÁNDAR
            val bundle = Bundle()
            bundle.putInt("carId", carId)

            // NAVEGAR PASANDO EL BUNDLE
            findNavController().navigate(R.id.action_carList_to_detail, bundle)
        }
        binding.rvCars.layoutManager = LinearLayoutManager(context)
        binding.rvCars.adapter = adapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(300) // Debounce de 300ms para no saturar mientras escribe
                    viewModel.filterCars(newText ?: "")
                }
                return true
            }
        })
    }

    private fun setupObservers() {
        viewModel.cars.observe(viewLifecycleOwner) { resource ->
            binding.progressBar.isVisible = resource is Resource.Loading
            binding.tvEmpty.isVisible = resource is Resource.Success && resource.data.isEmpty()
            binding.rvCars.isVisible = resource is Resource.Success && resource.data.isNotEmpty()

            if (resource is Resource.Success) {
                adapter.submitList(resource.data) // DiffUtil se encarga de las actualizaciones
            }
        }

        // Cargar datos al entrar
        viewModel.fetchCars()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}