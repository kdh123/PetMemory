package com.dohyun.data.schedule.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dohyun.domain.schedule.ScheduleDto

@Dao
interface ScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveSchedule(scheduleEntity: ScheduleEntity)

    @Query("DELETE FROM schedule WHERE scheduleId = :scheduleId")
    fun deleteSchedule(scheduleId: Int)

    @Query("SELECT * FROM schedule")
    fun getAllSchedule(): List<ScheduleEntity>?

    @Query("SELECT * FROM schedule WHERE scheduleId = :scheduleId")
    fun getScheduleInfo(scheduleId: Int): ScheduleEntity?

    @Update(entity = ScheduleEntity::class)
    fun updateSchedule(scheduleDto: ScheduleDto)
}