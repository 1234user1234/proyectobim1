package com.example.proyectobim1.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.proyectobim1.R
import com.example.proyectobim1.data.local.PreferencesManager
import com.example.proyectobim1.data.model.Resource
import com.example.proyectobim1.data.remote.RetrofitClient
import com.example.proyectobim1.databinding.FragmentLoginBinding
import com.example.proyectobim1.ui.viewmodel.AuthViewModel

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AuthViewModel
    private lateinit var prefs: PreferencesManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = PreferencesManager(requireContext())
        val api = RetrofitClient.getInstance(prefs)
        viewModel = AuthViewModel(api)

        // Si ya hay token, ir directo a la lista
        if (prefs.getToken() != null) {
            findNavController().navigate(R.id.action_login_to_carList)
            return
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()
            if (email.isNotEmpty() && pass.isNotEmpty()) {
                viewModel.login(email, pass)
            } else {
                Toast.makeText(context, "Complete los campos", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.loginResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> binding.btnLogin.isEnabled = false
                is Resource.Success -> {
                    //prefs.saveToken(resource.data.token)
                    prefs.saveToken(resource.data.access)
                    findNavController().navigate(R.id.action_login_to_carList)
                }
                is Resource.Error -> {
                    Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                    binding.btnLogin.isEnabled = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}