package com.dohyun.petmemory.ui.profile

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import com.dohyun.petmemory.R
import com.dohyun.petmemory.databinding.LayoutSpinnerBinding

class ProfileSpinnerAdapter(
    context: Context,
    @LayoutRes private val resId: Int,
    private val values: MutableList<String>
) : ArrayAdapter<String>(context, resId, values) {

    override fun getCount() = values.size

    override fun getItem(position: Int) = values[position]

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = DataBindingUtil.inflate<LayoutSpinnerBinding>(
            LayoutInflater.from(parent.context),
            R.layout.layout_spinner,
            parent,
            false
        )

        val text = values[position]
        try {
            binding.tvValue.text = text
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = DataBindingUtil.inflate<LayoutSpinnerBinding>(
            LayoutInflater.from(parent.context),
            R.layout.layout_spinner,
            parent,
            false
        )
        val text = values[position]
        try {
            binding.tvValue.text = text

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }
}