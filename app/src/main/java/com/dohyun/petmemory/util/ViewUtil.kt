package com.dohyun.petmemory.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.math.roundToInt

class ViewUtil @Inject constructor(@ApplicationContext private val context: Context) {
    fun convertDPtoPX(dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp.toFloat() * density).roundToInt()
    }
}