package com.example.proyectobim1.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectobim1.databinding.ItemIconBinding

class CarIconAdapter(private val icons: List<Int>, private val onIconSelected: (Int) -> Unit) :
    RecyclerView.Adapter<CarIconAdapter.IconViewHolder>() {

    private var selectedPosition = 0
    private var selectedIconRes = icons[0]

    inner class IconViewHolder(val binding: ItemIconBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val binding = ItemIconBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IconViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        val iconRes = icons[position]
        with(holder.binding) {
            ivIconItem.setImageResource(iconRes)

            // Círculo de selección visual (Makoto Shinkai naranja)
            if (position == selectedPosition) {
                ivIconItem.setBackgroundColor(android.graphics.Color.parseColor("#33FF6B35")) // Naranja semi-transparente
            } else {
                ivIconItem.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            }

            root.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = holder.adapterPosition
                selectedIconRes = iconRes
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
                onIconSelected(selectedIconRes)
            }
        }
    }

    override fun getItemCount() = icons.size

    fun getSelectedIcon() = selectedIconRes
}