package com.dohyun.petmemory.extension

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

fun LifecycleOwner.repeatOnStart(block: suspend CoroutineScope.() -> Unit) {
    lifecycleScope.launch {
        block()
    }
}

fun CoroutineScope.handle(
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    block: suspend CoroutineScope.() -> Unit,
    error: ((Throwable) -> Unit)? = null
) {
    val scope = this + CoroutineExceptionHandler { _, throwable ->
        error?.invoke(throwable)
    }

    scope.launch(context = dispatcher, block = block)
}