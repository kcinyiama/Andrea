package com.andrea.rss.util

import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.andrea.rss.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable

@BindingAdapter("imageUrl")
fun setImageUrl(imageView: ImageView, url: String?) {
    Glide.with(imageView.context).load(url).placeholder(R.drawable.ic_launcher).into(imageView)
}

@BindingAdapter("imageUrl")
fun setImageUrl(imageView: ImageView, url: List<String>?) {
    Glide.with(imageView.context).load(url?.firstOrNull()).placeholder(R.drawable.ic_launcher)
        .into(imageView)
}

@BindingAdapter("imageUrl")
fun setImageUrl(chip: Chip, url: String?) {
    Glide.with(chip.context).asDrawable().load(url).placeholder(R.drawable.ic_launcher).into(object : CustomTarget<Drawable>() {
        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            chip.chipIcon = resource
        }

        override fun onLoadCleared(placeholder: Drawable?) {
            // Ignore
        }
    })
}

@BindingAdapter("applySelection")
fun applySelection(layout: ConstraintLayout, bool: Boolean) {
    layout.isSelected = bool
}
