package com.dohyun.petmemory.base

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import com.dohyun.petmemory.common.LoadingDialog
import com.dohyun.petmemory.extension.repeatOnStart

abstract class StateFragment<VB : ViewDataBinding, ST> : BaseFragment<VB>() {

    abstract val fragmentActivity : FragmentActivity?
    abstract val stateViewModel : StateViewModel<ST>

    private var loadingDialog: LoadingDialog? = null


    abstract fun render(state : ST)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog()
        observeData()
    }

    open fun observeData() {
        viewLifecycleOwner.repeatOnStart {
            stateViewModel.loadingState.collect { isShow ->
                if (isShow) {
                    loadingDialog?.run {
                        if (!isAdded) {
                            fragmentActivity?.supportFragmentManager
                                ?.beginTransaction()
                                ?.add(this, LoadingDialog::class.java.name)
                                ?.commitAllowingStateLoss()
                        }
                    }
                } else {
                    Handler(Looper.getMainLooper()).postDelayed({
                        loadingDialog?.run {
                            if (isAdded) {
                                dismissAllowingStateLoss()
                            }
                        }
                    }, 100)
                }
            }
        }

        viewLifecycleOwner.repeatOnStart {
            stateViewModel.state.collect{
                render(state = it)
            }
        }
    }

    override fun onDestroyView() {
        loadingDialog?.run {
            if (isAdded) {
                dismissAllowingStateLoss()
            }
        }
        loadingDialog = null

        super.onDestroyView()
    }
}