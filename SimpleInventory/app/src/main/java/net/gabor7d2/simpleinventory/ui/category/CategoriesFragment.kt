package net.gabor7d2.simpleinventory.ui.category

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
import net.gabor7d2.simpleinventory.model.Category
import net.gabor7d2.simpleinventory.ui.ListItemRecyclerViewAdapter
import java.util.UUID

class CategoriesFragment(private val categoryId: String? = null) : Fragment(), MenuProvider {

    private var _binding: FragmentListItemsBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: ListItemRecyclerViewAdapter<Category>

    private var tracker: SelectionTracker<String>? = null

    private var menu: Menu? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListItemsBinding.inflate(inflater, container, false)

        adapter = ListItemRecyclerViewAdapter(findNavController())
        RepositoryManager.instance.addCategoryChildrenListener(categoryId, adapter)
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
            val newCategory = RepositoryManager.instance.addOrUpdateCategory(Category(null, getString(R.string.new_category), categoryId))
            findNavController().navigate(
                MobileNavigationDirections.actionGotoCategoryDetailsFragment(newCategory.name, newCategory.id!!)
            )
        }

        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }

    private fun switchToSelectionMenuItems(selection: Boolean) {
        menu?.let {
            it.findItem(R.id.action_search)?.isVisible = !selection
            it.findItem(R.id.action_select_all)?.isVisible = selection
            it.findItem(R.id.action_favourite)?.isVisible = selection
            it.findItem(R.id.action_unfavourite)?.isVisible = selection
            it.findItem(R.id.action_delete)?.isVisible = selection
        }
    }

    override fun onDestroyView() {
        RepositoryManager.instance.removeCategoryChildrenListener(adapter)
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.options_menu, menu)
        this.menu = menu

        val searchView = menu.findItem(R.id.action_search)?.actionView as SearchView
        searchView.queryHint = getString(R.string.search_categories)

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
                    RepositoryManager.instance.addOrUpdateCategory(
                        RepositoryManager.instance.getCategory(it).copy(favourite = true)
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
                    RepositoryManager.instance.addOrUpdateCategory(
                        RepositoryManager.instance.getCategory(it).copy(favourite = false)
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
                    RepositoryManager.instance.removeCategory(it)
                }
            }
            true
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return menuItem.itemId == R.id.action_search
    }
}