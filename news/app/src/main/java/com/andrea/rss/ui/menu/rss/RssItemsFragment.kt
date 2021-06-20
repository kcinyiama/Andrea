package com.andrea.rss.ui.menu.rss

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.andrea.rss.databinding.FragmentRssItemsBinding
import com.andrea.rss.ui.widget.SimpleDividerItemDecoration
import com.andrea.rss.util.AppViewModelFactory

class RssItemsFragment : Fragment() {
    private val viewModel: RssItemsViewModel by viewModels {
        AppViewModelFactory(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRssItemsBinding.inflate(inflater, container, false)
        context ?: return binding.root

        val adapter = RssItemsAdapter(viewModel)
        binding.itemList.addItemDecoration(SimpleDividerItemDecoration(requireContext()))
        binding.itemList.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner

        val actionMode = RssItemsActionMode(activity as AppCompatActivity, adapter, viewModel)

        viewModel.allItems.observe(viewLifecycleOwner) {
            adapter.submit(it)
        }

        viewModel.actionMode.observe(viewLifecycleOwner) {
            if (it != null) {
                actionMode.initActionMode(it)
            }
        }

        binding.topAppBar.setNavigationOnClickListener { activity?.onBackPressed() }
        binding.btnAddItem.setOnClickListener {
            findNavController().navigate(
                RssItemsFragmentDirections.actionRssItemsFragmentToNewRssItemFragment()
            )
        }

        return binding.root
    }
}