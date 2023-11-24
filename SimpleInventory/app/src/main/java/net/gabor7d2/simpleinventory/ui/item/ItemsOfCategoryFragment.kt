package net.gabor7d2.simpleinventory.ui.item

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.MutableSelection
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import net.gabor7d2.simpleinventory.MobileNavigationDirections
import net.gabor7d2.simpleinventory.R
import net.gabor7d2.simpleinventory.databinding.FragmentListItemsBinding
import net.gabor7d2.simpleinventory.persistence.repository.RepositoryManager
import net.gabor7d2.simpleinventory.model.Item
import net.gabor7d2.simpleinventory.ui.ListItemRecyclerViewAdapter
import java.util.UUID

class ItemsOfCategoryFragment(private val categoryId: String) : Fragment(), MenuProvider {

    private var _binding: FragmentListItemsBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: ListItemRecyclerViewAdapter<Item>

    private var tracker: SelectionTracker<String>? = null

    private var menu: Menu? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListItemsBinding.inflate(inflater, container, false)

        adapter = ListItemRecyclerViewAdapter(findNavController())
        RepositoryManager.instance.addItemsOfCategoryListener(categoryId, adapter)
        binding.list.adapter = adapter

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
                    switchToSelectionMenuItems(tracker.hasSelection())
                }
            })

        binding.fab.setOnClickListener {
            val newItem = RepositoryManager.instance.addOrUpdateItem(Item(null, getString(R.string.new_item), categoryId, null))
            findNavController().navigate(
                MobileNavigationDirections.actionGotoItemDetailsFragment(newItem.name, newItem.id!!)
            )
        }

        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        tracker?.onSaveInstanceState(outState)
    }

    private fun switchToSelectionMenuItems(selection: Boolean) {
        menu?.let {
            it.findItem(R.id.action_search)?.isVisible = !selection
            it.findItem(R.id.action_select_all)?.isVisible = selection
            it.findItem(R.id.action_export_barcode)?.isVisible = selection
            it.findItem(R.id.action_favourite)?.isVisible = selection
            it.findItem(R.id.action_unfavourite)?.isVisible = selection
            it.findItem(R.id.action_delete)?.isVisible = selection
        }
    }

    override fun onDestroyView() {
        RepositoryManager.instance.removeItemsOfCategoryListener(adapter)
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.options_menu, menu)
        this.menu = menu

        val searchView = menu.findItem(R.id.action_search)?.actionView as SearchView
        searchView.queryHint = getString(R.string.search_items)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = true

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText ?: "")
                return true
            }
        })

        menu.findItem(R.id.action_select_all).setOnMenuItemClickListener { _ ->
            tracker?.let {
                it.setItemsSelected(adapter.getVisibleListItems().map { it.id }, true)
            }
            true
        }

        menu.findItem(R.id.action_favourite).setOnMenuItemClickListener {_ ->
            tracker?.let {
                val selectionCopy = MutableSelection<String>()
                it.copySelection(selectionCopy)
                selectionCopy.forEach {
                    RepositoryManager.instance.addOrUpdateItem(
                        RepositoryManager.instance.getItem(it).copy(favourite = true)
                    )
                }
            }
            true
        }

        menu.findItem(R.id.action_unfavourite).setOnMenuItemClickListener {_ ->
            tracker?.let {
                val selectionCopy = MutableSelection<String>()
                it.copySelection(selectionCopy)
                selectionCopy.forEach {
                    RepositoryManager.instance.addOrUpdateItem(
                        RepositoryManager.instance.getItem(it).copy(favourite = false)
                    )
                }
            }
            true
        }

        menu.findItem(R.id.action_delete).setOnMenuItemClickListener {_ ->
            tracker?.let { tracker ->
                val selectionCopy = MutableSelection<String>()
                tracker.copySelection(selectionCopy)
                selectionCopy.forEach {
                    RepositoryManager.instance.removeItem(it)
                }
            }
            true
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return menuItem.itemId == R.id.action_search
    }
}