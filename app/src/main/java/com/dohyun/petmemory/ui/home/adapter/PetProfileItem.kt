package com.dohyun.petmemory.ui.home.adapter

data class PetProfileItem(
    val petId: Int,
    val petName: String,
    val imageUrl: String,
    val isChecked: Boolean
)