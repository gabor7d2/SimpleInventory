package net.gabor7d2.simpleinventory.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import net.gabor7d2.simpleinventory.R
import net.gabor7d2.simpleinventory.databinding.FragmentListItemsBinding
import net.gabor7d2.simpleinventory.model.ListItem

abstract class ListItemFragmentBase<T : ListItem> : Fragment(), MenuProvider {

    private var _binding: FragmentListItemsBinding? = null
    protected val binding get() = _binding!!

    protected lateinit var adapter: ListItemRecyclerViewAdapter<T>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListItemsBinding.inflate(inflater, container, false)

        adapter = ListItemRecyclerViewAdapter(findNavController())
        registerAdapterAsListener(adapter)
        binding.list.adapter = adapter

        binding.fab.setOnClickListener {
            onFabClicked()
        }

        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }

    override fun onDestroyView() {
        unregisterAdapterAsListener(adapter)
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.list_menu, menu)

        val searchView = menu.findItem(R.id.action_search)?.actionView as SearchView
        searchView.queryHint = searchHint

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = true

            override fun onQueryTextChange(newText: String?): Boolean {
                onSearchQueryChanged(newText)
                return true
            }
        })
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return menuItem.itemId == R.id.action_search
    }

    abstract val searchHint: String

    abstract fun registerAdapterAsListener(adapter: ListItemRecyclerViewAdapter<T>)

    abstract fun unregisterAdapterAsListener(adapter: ListItemRecyclerViewAdapter<T>)

    abstract fun onFabClicked()

    abstract fun onSearchQueryChanged(newQuery: String?)
}