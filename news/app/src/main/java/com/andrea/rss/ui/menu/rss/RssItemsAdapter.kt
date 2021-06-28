package com.andrea.rss.ui.menu.rss

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.andrea.rss.databinding.ListItemRssItemContentBinding
import com.andrea.rss.databinding.ListItemRssItemGroupBinding
import com.andrea.rss.domain.RssItem

class RssItemsAdapter(private val rssItemViewModel: RssItemsViewModel) :
    ListAdapter<RssItemsAdapter.WrappedRssItem, RssItemsAdapter.ViewHolder>(RssItemDiffCallback()) {

    var actionModeEnabled = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, viewType, rssItemViewModel)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun submit(list: List<RssItem>) {
        val items = list.toWrappedRssItems()
        if (actionModeEnabled) {
            for (item in items) {
                currentList.firstOrNull { !it.isHeader && it.rssItem.id == item.rssItem.id }?.let {
                    item.selected = it.selected
                }
            }
        }
        submitList(items)
    }

    override fun getItemViewType(position: Int) =
        if (getItem(position).isHeader) ViewHolder.HEADER else -1

    fun markItemSelection(id: Int, selected: Boolean) {
        with(mutableListOf<WrappedRssItem>()) {
            addAll(currentList)
            for (item in this) {
                if (!item.isHeader && item.rssItem.id == id) {
                    item.selected = selected
                    break
                }
            }
            submitList(this)
        }
    }

    fun clearSelection() {
        with(mutableListOf<WrappedRssItem>()) {
            addAll(currentList)
            for (item in this) {
                item.selected = false
            }
            submitList(this)
        }
    }

    class ViewHolder private constructor(
        private val binding: ViewDataBinding,
        private val viewType: Int,
        private val rssItemViewModel: RssItemsViewModel
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            const val HEADER = 0

            fun from(
                parent: ViewGroup,
                viewType: Int,
                rssItemViewModel: RssItemsViewModel
            ): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = when (viewType) {
                    HEADER -> ListItemRssItemGroupBinding.inflate(layoutInflater, parent, false)
                    else -> ListItemRssItemContentBinding.inflate(layoutInflater, parent, false)
                }
                return ViewHolder(binding, viewType, rssItemViewModel)
            }
        }

        fun bind(item: WrappedRssItem) {
            when (viewType) {
                HEADER -> (binding as ListItemRssItemGroupBinding).rssItem = item.rssItem
                else -> {
                    val contentBinding = (binding as ListItemRssItemContentBinding)
                    contentBinding.wrapper = item
                    contentBinding.viewModel = rssItemViewModel
                }
            }
            binding.executePendingBindings()
        }
    }

    class RssItemDiffCallback : DiffUtil.ItemCallback<WrappedRssItem>() {
        override fun areItemsTheSame(oldItem: WrappedRssItem, newItem: WrappedRssItem): Boolean {
            return oldItem.isHeader == newItem.isHeader && oldItem.rssItem.id == newItem.rssItem.id
        }

        override fun areContentsTheSame(oldItem: WrappedRssItem, newItem: WrappedRssItem): Boolean {
            // TODO Fix later
            return false
        }
    }

    data class WrappedRssItem(val isHeader: Boolean, val rssItem: RssItem, var selected: Boolean = false)

    private fun List<RssItem>.toWrappedRssItems(): List<WrappedRssItem> {
        var group: String? = null
        val items = mutableListOf<WrappedRssItem>()

        for (item in this) {
            when (group) {
                null -> items += listOf(WrappedRssItem(true, item), WrappedRssItem(false, item))
                else -> {
                    if (group == item.group) {
                        items += WrappedRssItem(false, item)
                    } else {
                        items += listOf(WrappedRssItem(true, item), WrappedRssItem(false, item))
                    }
                }
            }
            group = item.group
        }
        return items
    }
}