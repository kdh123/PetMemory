package com.dohyun.domain.common

import okhttp3.ResponseBody
import org.json.JSONObject

sealed class CommonResult<T, F>(val data: T? = null, val failEvent: F? = null, val error: ErrorData? = null) {
    class Success<T, F>(data: T?) : CommonResult<T, F>(data = data)
    class Fail<T, F>(failEvent: F? = null, error: ErrorData? = null) : CommonResult<T, F>(failEvent = failEvent, error = error)
}

data class ErrorResponse(
    val errorBody: ResponseBody?
) {
    private val jsonObject = JSONObject(errorBody?.string() ?: "")
    private val strJsonError = if (jsonObject.has("error")) {
        jsonObject.getString("error")
    } else {
        ""
    }

    private val jsonError = JSONObject(strJsonError)

    val code: String = if (jsonError.has("code")) {
        jsonError.getString("code")
    } else {
        "-1"
    }

    val message: String = if (jsonError.has("message")) {
        jsonError.getString("message")
    } else {
        ""
    }

    val type: String = if (jsonError.has("type")) {
        jsonError.getString("type")
    } else {
        "Unknown"
    }

    fun toError(): ErrorData {
        return ErrorData(code = code, message = message, type = type)
    }
}

data class ErrorData(
    val code: String,
    val message: String,
    val type: String
)