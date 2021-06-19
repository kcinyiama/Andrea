package com.andrea.rss.ui.main


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.andrea.rss.databinding.ListItemRssFeedBinding
import com.andrea.rss.domain.RssFeed

class RssFeedAdapter(val clickListener: FeedListener) : ListAdapter<RssFeed, RssFeedAdapter.ViewHolder>(RssFeedDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position),clickListener)
    }

    class ViewHolder private constructor(private val binding: ListItemRssFeedBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: RssFeed, clickListener: FeedListener) {
            binding.rssFeed = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemRssFeedBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
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

    class FeedListener(val clickListener: (RssFeed) -> Unit){
        fun feedDetail(feed: RssFeed) = clickListener(feed.copy())
    }
}