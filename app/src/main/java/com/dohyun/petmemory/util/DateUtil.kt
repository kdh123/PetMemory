package com.dohyun.petmemory.util

import android.annotation.SuppressLint
import com.dohyun.petmemory.ui.profile.ProfileActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.abs

object DateUtil {

    fun todayDate(pattern: String = "yyyy-MM-dd HH:mm"): String {
        val date = Date()
        return SimpleDateFormat(pattern, Locale.getDefault()).format(date)
    }

    fun convertMillisToDate(millis: Long): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        return formatter.format(Date(millis))
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateYearMonthDay(
        strDate: String,
        pattern: String = "yyyy-MM-dd"
    ): ProfileActivity.DateDto {
        val year: Int
        val month: Int
        val day: Int
        return try {
            val sdf = SimpleDateFormat(pattern)
            val date: Date = sdf.parse(strDate) as Date
            val cal: Calendar = Calendar.getInstance()
            cal.time = date
            year = cal.get(Calendar.YEAR)
            month = (cal.get(Calendar.MONTH) + 1) % 12
            day = cal.get(Calendar.DATE)
            ProfileActivity.DateDto(year, month, day)
        } catch (e: Exception) {
            ProfileActivity.DateDto()
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateGap(oldDate: String, newDate: String): Long {
        return try {
            val prevDate = convertStringToDate(oldDate)
            val afterDate = convertStringToDate(newDate)

            val gap = abs(afterDate!!.time - prevDate!!.time)

            TimeUnit.DAYS.convert(gap, TimeUnit.MILLISECONDS)
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    fun convertStringToDate(strDate: String, pattern: String = "yyyy-MM-dd"): Date? {
        val format = SimpleDateFormat(pattern, Locale.getDefault())
        return try {
            format.parse(strDate)
        } catch (e: Exception) {
            null
        }
    }
}