package com.andrea.rss.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.andrea.rss.R
import com.andrea.rss.databinding.FragmentRssFeedListBinding
import com.andrea.rss.ui.feeddetail.FeedDetailActivity
import com.andrea.rss.util.AppViewModelFactory


class MainFragment : Fragment() {
    private val viewModel: MainViewModel by viewModels {
        AppViewModelFactory(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRssFeedListBinding.inflate(inflater, container, false)
        context ?: return binding.root

        val adapter = RssFeedAdapter(viewModel)

        binding.feedList.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.allFeeds.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.navigationToDetail.observe(viewLifecycleOwner) {
            it?.let { feedId ->
                Intent(activity, FeedDetailActivity::class.java).apply {
                    putExtra("feedId", feedId)
                    startActivity(this)
                }
                viewModel.doneNavigating()
            }
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item -> {
                    findNavController().navigate(MainFragmentDirections.actionMainFragmentToRssItemsFragment())
                    true
                }
                else -> false
            }
        }

        return binding.root
    }
}