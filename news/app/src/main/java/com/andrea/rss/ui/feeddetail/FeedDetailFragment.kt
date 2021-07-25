package com.andrea.rss.ui.feeddetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.andrea.rss.R
import com.andrea.rss.databinding.FragmentFeedDetailBinding
import com.andrea.rss.util.AppViewModelFactory
import com.andrea.rss.util.toast
import com.google.android.material.appbar.AppBarLayout

class FeedDetailFragment : Fragment() {
    companion object {
        const val FEED_ID = "feedId"

        fun instance(feedId: Int) = FeedDetailFragment().apply { arguments = Bundle().apply { putInt(FEED_ID, feedId) } }
    }

    private val viewModel: FeedDetailViewModel by viewModels {
        AppViewModelFactory(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedDetailBinding.inflate(inflater, container, false)
        context ?: return binding.root

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.getFeedById(arguments?.getInt(FEED_ID)!!).observe(viewLifecycleOwner) { rssFeed ->
            when (rssFeed) {
                null -> {
                    requireContext().toast(R.string.feed_open_error)
                    activity?.finish()
                }
                else -> {
                    binding.rssFeed = rssFeed
                    viewModel.fetchMedia(requireContext(), rssFeed)
                }
            }
        }

        viewModel.feed.observe(viewLifecycleOwner) {
            binding.rssFeed = it
        }

        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }

        binding.appBar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isExpanded = false
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                scrollRange = if (scrollRange == -1) appBarLayout.totalScrollRange else scrollRange
                if (scrollRange + verticalOffset == 0) {
                    isExpanded = true
                } else if (isExpanded) {
                    isExpanded = false
                }
            }
        })

        return binding.root
    }
}