package com.andrea.rss.ui.menu.rss

import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import com.andrea.rss.R

class RssItemsActionMode(
    private val activity: AppCompatActivity,
    private val adapter: RssItemsAdapter,
    private val viewModel: RssItemsViewModel
) : ActionMode.Callback {

    private var actionMode: ActionMode? = null
    private val selectedItemIds = mutableListOf<Int>()

    fun initActionMode(id: Int) {
        with(selectedItemIds) {
            when (firstOrNull { it == id } == null) {
                true -> {
                    add(id)
                    adapter.markItemSelection(id, true)
                }
                else -> {
                    removeIf { it == id }
                    adapter.markItemSelection(id, false)
                }
            }

            actionMode = actionMode ?: activity.startSupportActionMode(this@RssItemsActionMode)
            when (isEmpty()) {
                true -> actionMode?.finish()
                else -> actionMode?.title = count().let { if (it > 0) it.toString() else "" }
            }
        }
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu?): Boolean {
        val inflater: MenuInflater = mode.menuInflater
        inflater.inflate(R.menu.items_menu, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu?): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_delete -> {
                viewModel.deleteItems(selectedItemIds)
                mode.finish()
                true
            }
            else -> false
        }
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        actionMode = null
        viewModel.endActionMode()
        adapter.clearSelection()
    }
}