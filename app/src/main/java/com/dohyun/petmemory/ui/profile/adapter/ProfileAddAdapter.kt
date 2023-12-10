package com.dohyun.petmemory.ui.profile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dohyun.petmemory.R
import com.dohyun.petmemory.databinding.ItemAddProfileBinding

class ProfileAddAdapter(private val onProfileClick: () -> Unit) : RecyclerView.Adapter<ProfileAddAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_add_profile, parent, false))
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.cvPetProfile.setOnClickListener {
            onProfileClick()
        }
    }

    inner class ViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = ItemAddProfileBinding.bind(itemView)
    }
}