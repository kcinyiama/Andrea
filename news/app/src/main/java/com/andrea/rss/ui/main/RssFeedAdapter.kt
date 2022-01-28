package com.andrea.rss.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.andrea.rss.databinding.ListItemRssFeedBinding
import com.andrea.rss.domain.RssFeed

class RssFeedAdapter(private val viewModel: MainViewModel) :
    ListAdapter<RssFeed, RssFeedAdapter.ViewHolder>(RssFeedDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, viewModel)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder private constructor(
        private val binding: ListItemRssFeedBinding,
        private val viewModel: MainViewModel
    ) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup, viewModel: MainViewModel): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemRssFeedBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, viewModel)
            }
        }

        fun bind(item: RssFeed) {
            binding.rssFeed = item
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }

    class RssFeedDiffCallback : DiffUtil.ItemCallback<RssFeed>() {
        override fun areItemsTheSame(oldItem: RssFeed, newItem: RssFeed): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RssFeed, newItem: RssFeed): Boolean {
            return oldItem == newItem
        }
    }
}