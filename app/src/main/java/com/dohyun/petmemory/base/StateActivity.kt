package com.dohyun.petmemory.base

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import com.dohyun.petmemory.common.LoadingDialog
import com.dohyun.petmemory.extension.repeatOnStart

abstract class StateActivity<VB : ViewDataBinding, ST> : BaseActivity<VB>() {

    abstract val stateViewModel: StateViewModel<ST>

    private var loadingDialog: LoadingDialog? = null

    abstract fun render(state: ST)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadingDialog = LoadingDialog()
        observeData()
    }

    override fun onDestroy() {
        loadingDialog?.run {
            if (isAdded) {
                dismissAllowingStateLoss()
            }
        }
        loadingDialog = null

        super.onDestroy()
    }

    open fun observeData() {
        repeatOnStart {
            stateViewModel.loadingState.collect { isShow ->
                if (isShow) {
                    loadingDialog?.run {
                        if (!isAdded) {
                            supportFragmentManager
                                .beginTransaction()
                                .add(this, LoadingDialog::class.java.name)
                                .commitAllowingStateLoss()
                        }
                    }

                } else {
                    loadingDialog?.run {
                        if (isAdded) {
                            dismissAllowingStateLoss()
                        }
                    }
                }
            }
        }

        repeatOnStart {
            stateViewModel.state.collect {
                render(state = it)
            }
        }
    }
}