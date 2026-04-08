package com.example.proyectobim1.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectobim1.R
import com.example.proyectobim1.data.model.CarUi
import com.example.proyectobim1.databinding.ItemCarBinding

class CarAdapter(private val onItemClickListener: (Int) -> Unit) : ListAdapter<CarUi, CarAdapter.CarViewHolder>(CarDiffCallback()) {

    inner class CarViewHolder(val binding: ItemCarBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val binding = ItemCarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val carUi = currentList[position]
        with(holder.binding) {
            ivCarIcon.setImageResource(carUi.imageResId)
            tvMakeModel.text = "${carUi.car.make} ${carUi.car.model}"
            tvYear.text = carUi.car.year.toString()

            // Anti-IA 1: Velocidad en km/h en la lista
            tvSpeed.text = "${carUi.car.speed} km/h"

            // Anti-IA 2: Alerta de combustible < 15
            if (carUi.car.fuel < 15) {
                tvFuel.setTextColor(root.context.getColor(R.color.warning_red))
                ivWarningFuel.visibility = android.view.View.VISIBLE
            } else {
                tvFuel.setTextColor(root.context.getColor(R.color.starlight_white))
                ivWarningFuel.visibility = android.view.View.GONE
            }
            tvFuel.text = "${carUi.car.fuel} L"

            root.setOnClickListener { onItemClickListener(carUi.car.id) }
        }
    }

    // Anti-IA 3: DifUtil implementado correctamente
    class CarDiffCallback : DiffUtil.ItemCallback<CarUi>() {
        override fun areItemsTheSame(oldItem: CarUi, newItem: CarUi): Boolean = oldItem.car.id == newItem.car.id
        override fun areContentsTheSame(oldItem: CarUi, newItem: CarUi): Boolean = oldItem == newItem
    }
}