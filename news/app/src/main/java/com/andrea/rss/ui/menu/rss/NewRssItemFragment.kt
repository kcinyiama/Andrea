package com.andrea.rss.ui.menu.rss

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.andrea.rss.R
import com.andrea.rss.databinding.FragmentRssItemNewBinding
import com.andrea.rss.util.*
import com.google.android.material.snackbar.Snackbar

class NewRssItemFragment : Fragment() {
    private val viewModel: NewRssItemViewModel by viewModels {
        AppViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRssItemNewBinding.inflate(inflater, container, false)
        context ?: return binding.root

        binding.lifecycleOwner = this
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigate(
                NewRssItemFragmentDirections.actionNewRssItemFragmentToRssItemsFragment()
            )
        }

        viewModel.spinner.observe(viewLifecycleOwner, {
            when (it) {
                true -> {
                    binding.btnAddItem.isEnabled = false
                    binding.textRssLink.isEnabled = false
                    binding.progressBar.visible()
                    binding.textWait.visible()
                }
                else -> {
                    binding.btnAddItem.isEnabled = true
                    binding.textRssLink.isEnabled = true
                    binding.progressBar.gone()
                    binding.textWait.gone()
                }
            }
        })

        viewModel.urlValidator.observe(viewLifecycleOwner, {
            when (it) {
                true -> {
                    requireContext().toast(R.string.feed_save_success)
                    findNavController().navigateUp()
                }
                else -> {
                    requireContext().toast(R.string.feed_save_error)
                }
            }
        })

        binding.btnAddItem.setOnClickListener {
            val link = binding.textRssLink.editText?.text.toString()
            if (link.isNotEmpty()) {
                viewModel.saveRssUrl(link)
            }
        }
        return binding.root
    }
}