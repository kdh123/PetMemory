package com.dohyun.data

import androidx.datastore.preferences.core.booleanPreferencesKey

object Util {
    const val WEATHER_API_KEY = "UNIJQ7Xe7FsqLn87Ah3aGJAzhbzFeyKOrDjJKV75DWZq6F2iwTM6UDguB2jWsvK3c1ZMzFtB%2B55Wi5bpvboiwQ%3D%3D"

    val PREF_KEY_IS_LOGIN = booleanPreferencesKey("pref_key_is_login")
    val PREF_KEY_IS_MAP_GUIDE_CHECK = booleanPreferencesKey("pref_key_is_map_guide_check")
}