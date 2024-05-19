package com.dohyun.petmemory.ui.profile

import java.io.Serializable

data class DateResult(
    val year: Int = 2023,
    val month: Int = 7,
    val day: Int = 1
) : Serializable