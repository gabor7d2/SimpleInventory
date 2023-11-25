package net.gabor7d2.simpleinventory.ui.item

import android.view.Menu
import androidx.navigation.fragment.findNavController
import net.gabor7d2.simpleinventory.MobileNavigationDirections
import net.gabor7d2.simpleinventory.R
import net.gabor7d2.simpleinventory.model.Item
import net.gabor7d2.simpleinventory.persistence.repository.RepositoryManager
import net.gabor7d2.simpleinventory.ui.ListItemRecyclerViewAdapter
import net.gabor7d2.simpleinventory.ui.SelectableListItemFragmentBase

class ItemsFragment(private val itemId: String? = null) : SelectableListItemFragmentBase<Item>() {

    override val searchHint by lazy { getString(R.string.search_items) }

    override fun registerAdapterAsListener(adapter: ListItemRecyclerViewAdapter<Item>) {
        RepositoryManager.instance.addItemChildrenListener(itemId, adapter)
    }

    override fun unregisterAdapterAsListener(adapter: ListItemRecyclerViewAdapter<Item>) {
        RepositoryManager.instance.removeItemChildrenListener(adapter)
    }

    override fun onFabClicked() {
        val newItem = RepositoryManager.instance.addOrUpdateItem(Item(null, getString(R.string.new_item), null, itemId))
        findNavController().navigate(
            MobileNavigationDirections.actionGotoItemDetailsFragment(newItem.name, newItem.id!!)
        )
    }

    override fun onSearchQueryChanged(newQuery: String?) {
        adapter.filter(newQuery ?: "")
    }

    override fun onSetMenuItemVisibilities(menu: Menu, selectedItems: Int) {
        super.onSetMenuItemVisibilities(menu, selectedItems)
        menu.findItem(R.id.action_export_barcode).isVisible = selectedItems > 0
    }

    override fun onActionFavourite(itemIds: List<String>) {
        itemIds.forEach {
            RepositoryManager.instance.favouriteItem(it, true)
        }
    }

    override fun onActionUnfavourite(itemIds: List<String>) {
        itemIds.forEach {
            RepositoryManager.instance.favouriteItem(it, false)
        }
    }

    override fun onActionDelete(itemIds: List<String>) {
        itemIds.forEach {
            RepositoryManager.instance.removeItem(it)
        }
    }
}