package com.dohyun.petmemory.extension

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.dohyun.petmemory.util.LocationUtil

@BindingAdapter("image")
fun setImage(view: ImageView, imageUrl: String) {
    Glide.with(view)
        .load(imageUrl)
        .into(view)
}

@BindingAdapter("fullImage")
fun setFullImage(view: ImageView, imageUrl: String?) {
    if (imageUrl == null) {
        return
    }

    Glide.with(view)
        .load(imageUrl)
        .centerCrop()
        .into(view)
}

@BindingAdapter("petName")
fun setPetName(view: TextView, petName: String?) {
    view.text = petName ?: ""
}

@BindingAdapter("lat", "lng")
fun setLocation(view: TextView, lat: Double, lng: Double) {
    view.context.let {
        view.text = LocationUtil(it).getAddress(lat = lat, lng = lng)
    }
}

@BindingAdapter("intToString")
fun intToString(view: TextView, int: Int) {
    view.text = "$int"
}

@BindingAdapter("intToSex")
fun intToSex(view: TextView, sex: Int) {
    view.text = if (sex == 0) {
        "남"
    } else {
        "여"
    }
}

@BindingAdapter("isProfileCheck")
fun isProfileCheck(view: ImageView, isChecked: Boolean) {
    if (isChecked) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
}