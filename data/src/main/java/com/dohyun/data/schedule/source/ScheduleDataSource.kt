package com.dohyun.data.schedule.source

import com.dohyun.domain.schedule.ScheduleDto

interface ScheduleDataSource {
    suspend fun getAllSchedule(): List<ScheduleDto>
    suspend fun getScheduleInfo(scheduleId: Int): ScheduleDto?
    suspend fun saveSchedule(scheduleDto: ScheduleDto)
    suspend fun deleteSchedule(scheduleId: Int)
    suspend fun updateSchedule(scheduleDto: ScheduleDto)
}