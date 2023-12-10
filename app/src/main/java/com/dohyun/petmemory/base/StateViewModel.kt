package com.dohyun.petmemory.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ST = State
 * */
abstract class StateViewModel<ST>(private val initialState : ST) : ViewModel() {

    private val _loadingState : MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loadingState : StateFlow<Boolean>
        get() = _loadingState

    fun showLoading() {
        _loadingState.value = true
    }

    fun hideLoading() {
        _loadingState.value = false
    }

    protected var _state : MutableStateFlow<ST> = MutableStateFlow(initialState)
    val state : StateFlow<ST>
        get() = _state
}