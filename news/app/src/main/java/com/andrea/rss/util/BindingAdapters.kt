package com.andrea.rss.util

import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.andrea.rss.R
import com.bumptech.glide.Glide

@BindingAdapter("imageUrl")
fun setImageUrl(imageView: ImageView, url: String?) {
    Glide.with(imageView.context).load(url).placeholder(R.drawable.ic_launcher).into(imageView)
}

@BindingAdapter("imageUrl")
fun setImageUrl(imageView: ImageView, url: List<String>?) {
    Glide.with(imageView.context).load(url?.firstOrNull()).placeholder(R.drawable.ic_launcher).into(imageView)
}

@BindingAdapter("applySelection")
fun applySelection(layout: ConstraintLayout, bool: Boolean) {
    layout.isSelected = bool
}
