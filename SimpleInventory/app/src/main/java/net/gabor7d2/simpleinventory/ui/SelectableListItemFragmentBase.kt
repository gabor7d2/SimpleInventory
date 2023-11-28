package net.gabor7d2.simpleinventory.ui

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.selection.MutableSelection
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import net.gabor7d2.simpleinventory.R
import net.gabor7d2.simpleinventory.model.ListItem
import java.util.UUID

abstract class SelectableListItemFragmentBase<T : ListItem> : ListItemFragmentBase<T>() {

    protected var tracker: SelectionTracker<String>? = null

    private var menu: Menu? = null

    private var actionBar: ActionBar? = null

    private val clearSelectionDrawable: Drawable by lazy {
        val drawable = ResourcesCompat.getDrawable(requireContext().resources, R.drawable.ic_close, requireContext().theme)!!
        drawable.setTint(Color.WHITE)
        drawable
    }

    private var originalTitle = ""
    private var originalDisplayHomeAsUpEnabled = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        val tracker = SelectionTracker.Builder(
            UUID.randomUUID().toString(),
            binding.list,
            adapter.getItemKeyProvider(),
            ListItemRecyclerViewAdapter.MyItemLookup(binding.list),
            StorageStrategy.createStringStorage()
        ).withSelectionPredicate(
            adapter.MySelectionPredicate()
        ).build()

        savedInstanceState?.let {
            tracker.onRestoreInstanceState(it)
        }

        adapter.setTracker(tracker)
        this.tracker = tracker

        tracker.addObserver(
            object : SelectionTracker.SelectionObserver<String>() {
                override fun onSelectionChanged() {
                    switchToSelectionMenuItems(tracker.selection.size())
                }
            })

        view?.isFocusableInTouchMode = true
        view?.requestFocus()
        view?.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (tracker.hasSelection()) {
                    tracker.clearSelection()
                    return true
                }
                return false
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (tracker.hasSelection()) {
                tracker.clearSelection()
            } else {
                isEnabled = false
                requireActivity().onBackPressed()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.let {
            originalTitle = it.title.toString()
            originalDisplayHomeAsUpEnabled = it.displayOptions and ActionBar.DISPLAY_HOME_AS_UP != 0
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateMenu(menu, menuInflater)
        this.menu = menu
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when(menuItem.itemId) {
            android.R.id.home -> {
                if (tracker?.hasSelection() == true) {
                    tracker?.clearSelection()
                    return true
                }
            }
            R.id.action_select_all -> {
                tracker?.let {
                    it.setItemsSelected(adapter.getVisibleListItems().map { it.id }, true)
                }
                return true
            }
            R.id.action_favourite -> {
                tracker?.let {
                    val selectionCopy = MutableSelection<String>()
                    it.copySelection(selectionCopy)
                    onActionFavourite(selectionCopy.toList())
                }
                return true
            }
            R.id.action_unfavourite -> {
                tracker?.let {
                    val selectionCopy = MutableSelection<String>()
                    it.copySelection(selectionCopy)
                    onActionUnfavourite(selectionCopy.toList())
                }
                return true
            }
            R.id.action_delete -> {
                tracker?.let { tracker ->
                    val selectionCopy = MutableSelection<String>()
                    tracker.copySelection(selectionCopy)
                    onActionDelete(selectionCopy.toList())
                }
                return true
            }
        }
        return false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        tracker?.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        tracker?.clearSelection()
    }

    private fun switchToSelectionMenuItems(selectedItems: Int) {
        if (selectedItems > 0) {
            actionBar?.setHomeAsUpIndicator(clearSelectionDrawable)
            actionBar?.setDisplayHomeAsUpEnabled(true)
            actionBar?.title = selectedItems.toString()
        } else {
            actionBar?.setHomeAsUpIndicator(null)
            actionBar?.setDisplayHomeAsUpEnabled(originalDisplayHomeAsUpEnabled)
            actionBar?.title = originalTitle
        }

        menu?.let {
            onSetMenuItemVisibilities(it, selectedItems)
        }
    }

    open fun onSetMenuItemVisibilities(menu: Menu, selectedItems: Int) {
        val selection = selectedItems > 0
        menu.findItem(R.id.action_search)?.isVisible = !selection
        menu.findItem(R.id.action_select_all)?.isVisible = selection
        menu.findItem(R.id.action_favourite)?.isVisible = selection
        menu.findItem(R.id.action_unfavourite)?.isVisible = selection
        menu.findItem(R.id.action_delete)?.isVisible = selection
    }

    abstract fun onActionFavourite(itemIds: List<String>)

    abstract fun onActionUnfavourite(itemIds: List<String>)

    abstract fun onActionDelete(itemIds: List<String>)
}