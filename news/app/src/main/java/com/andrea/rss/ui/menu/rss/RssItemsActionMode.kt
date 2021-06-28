package com.andrea.rss.ui.menu.rss

import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import com.andrea.rss.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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
                    if (removeAll { it == id }) {
                        adapter.markItemSelection(id, false)
                    }
                }
            }

            actionMode = actionMode ?: activity.startSupportActionMode(this@RssItemsActionMode)
            when (isEmpty()) {
                true -> actionMode?.finish()
                else -> actionMode?.title = count().let { if (it > 0) it.toString() else "" }
            }
        }
        adapter.actionModeEnabled = true
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
                MaterialAlertDialogBuilder(activity)
                    .setTitle(activity.resources.getQuantityString(R.plurals.delete_feed_source, selectedItemIds.size))
                    .setMessage(activity.resources.getString(R.string.delete_feed_source_message))
                    .setNegativeButton(activity.resources.getString(R.string.cancel)) { _, _ ->
                        // Do nothing
                    }
                    .setPositiveButton(activity.resources.getString(R.string.confirm)) { _, _ ->
                        viewModel.deleteItems(selectedItemIds)
                        mode.finish()
                    }
                    .show()
                true
            }
            else -> false
        }
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        actionMode = null
        selectedItemIds.clear()
        adapter.actionModeEnabled = false
        viewModel.endActionMode()
        adapter.clearSelection()
    }
}