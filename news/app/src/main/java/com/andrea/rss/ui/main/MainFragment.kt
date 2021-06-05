package com.andrea.rss.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.andrea.rss.R
import com.andrea.rss.databinding.FragmentRssFeedListBinding
import com.andrea.rss.util.MainViewModelFactory

class MainFragment : Fragment() {
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(requireActivity())
    }

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRssFeedListBinding.inflate(inflater, container, false)
        context ?: return binding.root

        val adapter = RssFeedAdapter()
        binding.feedList.adapter = adapter
        binding.lifecycleOwner = this

        viewModel.allFeeds.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item -> true
                R.id.about -> true
                else -> false
            }
        }

        return binding.root
    }
}