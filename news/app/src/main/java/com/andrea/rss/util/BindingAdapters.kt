package com.andrea.rss.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.andrea.rss.R
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView

@BindingAdapter("imageUrl")
fun setImageUrl(imageView: ImageView, url: String?) {
    Glide.with(imageView.context).load(url).placeholder(R.drawable.ic_launcher).into(imageView)
}
