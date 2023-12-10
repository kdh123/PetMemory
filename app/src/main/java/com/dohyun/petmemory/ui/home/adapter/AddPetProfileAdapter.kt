package com.dohyun.petmemory.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dohyun.petmemory.R
import com.dohyun.petmemory.databinding.ItemAddPetProfileBinding

class AddPetProfileAdapter(private val onProfileClick: () -> Unit) : RecyclerView.Adapter<AddPetProfileAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_add_pet_profile, parent, false))
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.sivPetProfile.setOnClickListener {
            onProfileClick()
        }
    }

    inner class ViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = ItemAddPetProfileBinding.bind(itemView)
    }
}