package com.dohyun.domain.pet

import java.io.Serializable

data class Pet(
    val id: Int = 0,
    val bigType: Int = 0,
    val type: String = "",
    val name: String = "",
    val age: Int = 0,
    val birthDay: String = "",
    val sinceDay: String = "",
    val weight: Double = 0.0,
    val sex: Int = 0,
    val imageUrl: String = ""
) : Serializable
