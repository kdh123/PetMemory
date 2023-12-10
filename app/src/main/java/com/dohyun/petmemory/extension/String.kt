package com.dohyun.petmemory.extension

import android.content.Context
import android.widget.Toast

fun String.showToast(context : Context?) {
    context?.let {
        Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
    }
}