package com.dohyun.petmemory.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject

class LocationUtil @Inject constructor(@ApplicationContext private val context: Context) {

    fun getAddress(lat: Double?, lng: Double?): String {
        if (lat == null || lng == null) {
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