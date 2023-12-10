package com.dohyun.petmemory.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import dagger.hilt.android.qualifiers.ActivityContext
import java.util.Locale
import javax.inject.Inject

class LocationUtil @Inject constructor(@ActivityContext private val context: Context) {

    fun getAddress(lat: Double, lng: Double): String {
        if (lat == 0.0 || lng == 0.0) {
            return "알 수 없음"
        }

        val geocoder = Geocoder(context, Locale.getDefault())

        var getAddress: Address? = null

        getAddress = try {
            geocoder.getFromLocation(lat, lng, 1)?.get(0)
        } catch (_: Exception) {
            null
        }

        return getAddress?.run {
            val adminArea = adminArea
            val subLocal = subLocality
            val thoroughfare = thoroughfare

            "$adminArea $subLocal $thoroughfare"
        } ?: kotlin.run {
            "알 수 없음"
        }
    }
}