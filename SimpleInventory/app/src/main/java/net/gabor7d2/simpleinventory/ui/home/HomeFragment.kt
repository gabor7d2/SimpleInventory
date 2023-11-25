package net.gabor7d2.simpleinventory.ui.home

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.navigation.fragment.findNavController
import net.gabor7d2.simpleinventory.MobileNavigationDirections
import net.gabor7d2.simpleinventory.R
import net.gabor7d2.simpleinventory.persistence.repository.RepositoryManager
import net.gabor7d2.simpleinventory.model.Item
import net.gabor7d2.simpleinventory.model.ListItem
import net.gabor7d2.simpleinventory.persistence.Preferences
import net.gabor7d2.simpleinventory.ui.ListItemFragmentBase
import net.gabor7d2.simpleinventory.ui.ListItemRecyclerViewAdapter

class HomeFragment : ListItemFragmentBase<ListItem>() {

    override val searchHint by lazy {
        getString(R.string.search_globally)
    }

    override fun registerAdapterAsListener(adapter: ListItemRecyclerViewAdapter<ListItem>) {
        RepositoryManager.instance.addFavouritesListener(adapter)
    }

    override fun unregisterAdapterAsListener(adapter: ListItemRecyclerViewAdapter<ListItem>) {
        RepositoryManager.instance.removeFavouritesListener(adapter)
    }

    override fun onFabClicked() {
        val newItem = RepositoryManager.instance.addOrUpdateItem(Item(null, getString(R.string.new_item), null, null))
        findNavController().navigate(
            MobileNavigationDirections.actionGotoItemDetailsFragment(newItem.name, newItem.id!!)
        )
    }

    override fun onSearchQueryChanged(newQuery: String?) {
        val query = newQuery ?: ""
        if (query.isEmpty() && adapter.searchQuery.isNotEmpty()) {
            adapter.filter(query)
            RepositoryManager.instance.removeGlobalListener(adapter)
            adapter.clear()
            RepositoryManager.instance.addFavouritesListener(adapter)
        } else if (query.isNotEmpty() && adapter.searchQuery.isEmpty()) {
            adapter.filter(query)
            RepositoryManager.instance.removeFavouritesListener(adapter)
            adapter.clear()
            RepositoryManager.instance.addGlobalListener(adapter)
        } else {
            adapter.filter(query)
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateMenu(menu, menuInflater)
        //menu.findItem(R.id.action_log_out).isVisible = true
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.action_log_out) {
            Preferences(requireActivity()).clearLoginCredentials()
            //startActivity(Intent(requireContext(), LoginActivity::class.java))
            return true
        }
        return super.onMenuItemSelected(menuItem)
    }
}