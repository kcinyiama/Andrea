package com.andrea.rss.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.andrea.rss.databinding.MainFragmentBinding
import com.andrea.rss.domain.RssFeed
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
        val binding = MainFragmentBinding.inflate(inflater, container, false)
        context ?: return binding.root

        binding.fetch.setOnClickListener {
            viewModel.insertRssItem()
        }

        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1)
        binding.newsList.adapter = adapter

        viewModel.allFeeds.observe(viewLifecycleOwner) {
            (binding.newsList.adapter as ArrayAdapter<String>).addAll(it.toStringList())
        }

        return binding.root
    }

    private fun List<RssFeed>.toStringList(): List<String> {
        return map {
            "${it.id}\n\n${it.fullStoryLink}\n\n${it.shortTitle}\n\n${it.publicationDate}\n\n${HtmlCompat.fromHtml(it.description, FROM_HTML_MODE_COMPACT)}"
        }
    }
}