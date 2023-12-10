package com.dohyun.data.schedule.repository

import com.dohyun.data.schedule.source.ScheduleLocalDataSource
import com.dohyun.domain.schedule.ScheduleDto
import com.dohyun.domain.schedule.ScheduleRepository

class ScheduleRepositoryImpl(private val localDataSource: ScheduleLocalDataSource) :
    ScheduleRepository {
    override suspend fun getAllSchedule(): List<ScheduleDto> {
        return localDataSource.getAllSchedule()
    }

    override suspend fun getScheduleInfo(scheduleId: Int): ScheduleDto? {
        return localDataSource.getScheduleInfo(scheduleId = scheduleId)
    }

    override suspend fun saveSchedule(scheduleDto: ScheduleDto) {
        localDataSource.saveSchedule(scheduleDto = scheduleDto)
    }

    override suspend fun deleteSchedule(scheduleId: Int) {
        localDataSource.deleteSchedule(scheduleId = scheduleId)
    }

    override suspend fun updateSchedule(scheduleDto: ScheduleDto) {
        localDataSource.updateSchedule(scheduleDto = scheduleDto)
    }
}