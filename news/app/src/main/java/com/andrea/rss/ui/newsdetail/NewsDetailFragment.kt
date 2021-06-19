package com.andrea.rss.ui.newsdetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.andrea.rss.databinding.FragmentNewsDetailBinding
import com.andrea.rss.util.MainViewModelFactory

class NewsDetailFragment : Fragment() {
    val args: NewsDetailFragmentArgs by navArgs()
    private val viewModel: NewsDetailViewModel by viewModels {
        MainViewModelFactory(requireActivity())
    }

    companion object {
        fun newInstance() = NewsDetailFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentNewsDetailBinding.inflate(inflater, container, false)
        context ?: return binding.root
        binding.lifecycleOwner = this
        viewModel.getFeedById(args.feedId).observe(viewLifecycleOwner) {
            it.let { rssFeed ->
                when (rssFeed) {
                    null -> {
                        Toast.makeText(activity, "Data does not exist", Toast.LENGTH_LONG).show()
                        findNavController().navigate(NewsDetailFragmentDirections.actionNewsDetailFragmentToMainFragment())
                    }
                    else -> {
                        binding.rssFeed = rssFeed
                    }
                }
            }
        }
        return binding.root
    }


}