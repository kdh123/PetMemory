package com.dohyun.data.schedule.source

import com.dohyun.data.room.AppDatabase
import com.dohyun.domain.schedule.ScheduleDto

class ScheduleLocalDataSource(db: AppDatabase) : ScheduleDataSource {
    private val service = db.scheduleDao()

    override suspend fun getAllSchedule(): List<ScheduleDto> {
        return service.getAllSchedule()?.map { it.toDto() } ?: emptyList()
    }

    override suspend fun getScheduleInfo(scheduleId: Int): ScheduleDto? {
        return service.getScheduleInfo(scheduleId)?.toDto()
    }

    override suspend fun saveSchedule(scheduleDto: ScheduleDto) {
        val entity = scheduleDto.run {
            ScheduleEntity(
                scheduleId = scheduleId,
                date = date,
                title = title,
                content = content,
                petName = petName,
                petImageUrl = petImageUrl
            )
        }
        service.saveSchedule(scheduleEntity = entity)
    }

    override suspend fun deleteSchedule(scheduleId: Int) {
        service.deleteSchedule(scheduleId = scheduleId)
    }

    override suspend fun updateSchedule(scheduleDto: ScheduleDto) {
        service.updateSchedule(scheduleDto = scheduleDto)
    }
}