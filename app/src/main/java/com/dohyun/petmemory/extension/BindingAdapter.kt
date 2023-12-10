package com.dohyun.petmemory.extension

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.dohyun.petmemory.util.LocationUtil

@BindingAdapter("image")
fun setImage(view: ImageView, imageUrl: String) {
    val startPath =
        Environment.getExternalStorageDirectory().toString()

    val url = if (imageUrl.startsWith(startPath)) {
        convertPathToUri(context = view.context, filePath = imageUrl)
    } else {
        imageUrl
    }

    Glide.with(view)
        .load(url)
        .into(view)
}

@BindingAdapter("fullImage")
fun setFullImage(view: ImageView, imageUrl: String?) {
    if (imageUrl == null) {
        return
    }

    val startPath = Environment.getExternalStorageDirectory().toString()

    val url = if (imageUrl.startsWith(startPath)) {
        convertPathToUri(context = view.context, filePath = imageUrl)
    } else {
        imageUrl
    }

    Glide.with(view)
        .load(url)
        .centerCrop()
        .into(view)
}

@BindingAdapter("petName")
fun setPetName(view: TextView, petName: String?) {
    view.text = petName ?: ""
}

@BindingAdapter("schedulePetSelected")
fun setSchedulePetSelected(view: ImageView, isSelected: Boolean) {
    if (isSelected) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
}

@SuppressLint("Range")
fun convertPathToUri(context: Context, filePath: String): Uri? {
    val cursor = context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
        "_data = '$filePath'", null, null
    )
    cursor!!.moveToNext()
    val cursorIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA)

    var id: Int? = null

    if (cursorIndex >= 0) {
        id = cursor.getInt(cursor.getColumnIndex("_id"))
    }

    cursor.close()

    return id?.let {
        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toLong())
    } ?: run {
        null
    }
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