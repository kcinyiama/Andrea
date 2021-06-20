package com.andrea.rss.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.andrea.rss.R

class SimpleDividerItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val divider: Drawable = ContextCompat.getDrawable(context, R.drawable.line_divider)!!

    override fun onDrawOver(c: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val itemCount = parent.adapter?.itemCount ?: 0
        for (i in 0 until itemCount) {
            if (shouldDraw(parent, i + 1, itemCount)) {
                val child: View = parent.getChildAt(i)
                val params = child.layoutParams as RecyclerView.LayoutParams
                val top: Int = child.bottom + params.bottomMargin
                val bottom = top + divider.intrinsicHeight
                divider.setBounds(left, top, right, bottom)
                divider.draw(c)
            }
        }
    }

    private fun shouldDraw(parent: RecyclerView, nextIndex: Int, itemCount: Int): Boolean {
        if (nextIndex < itemCount) {
            val viewType = parent.adapter?.getItemViewType(nextIndex) ?: 0
            return viewType == 0
        }
        return false
    }
}