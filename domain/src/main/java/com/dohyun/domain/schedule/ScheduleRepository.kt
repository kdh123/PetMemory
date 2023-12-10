package com.dohyun.domain.schedule

interface ScheduleRepository {
    suspend fun getAllSchedule(): List<ScheduleDto>
    suspend fun getScheduleInfo(scheduleId: Int): ScheduleDto?
    suspend fun saveSchedule(scheduleDto: ScheduleDto)
    suspend fun deleteSchedule(scheduleId: Int)
    suspend fun updateSchedule(scheduleDto: ScheduleDto)
}