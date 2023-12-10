package com.dohyun.domain.schedule

import java.io.Serializable

data class ScheduleDto(
    val scheduleId: Int = 0,
    val date: String,
    val title: String,
    val content: String,
    val petName: String,
    val petImageUrl: String
) : Serializable
