package com.dohyun.petmemory.base

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * ST = State
 * */

abstract class BaseActivity<VB : ViewDataBinding> : AppCompatActivity() {

    private var _binding: VB? = null
    val binding: VB
        get() = _binding!!

    abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = DataBindingUtil.setContentView(this, layoutId)


        //window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        initData(intent)
        initView()
        initBinding()
    }

    abstract fun initView()
    abstract fun initBinding()
    abstract fun initData(intent: Intent?)
}