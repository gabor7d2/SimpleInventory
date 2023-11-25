package net.gabor7d2.simpleinventory.ui.item

import android.view.Menu
import androidx.navigation.fragment.findNavController
import net.gabor7d2.simpleinventory.MobileNavigationDirections
import net.gabor7d2.simpleinventory.R
import net.gabor7d2.simpleinventory.model.Item
import net.gabor7d2.simpleinventory.persistence.repository.RepositoryManager
import net.gabor7d2.simpleinventory.ui.ListItemRecyclerViewAdapter
import net.gabor7d2.simpleinventory.ui.SelectableListItemFragmentBase

class ItemsOfCategoryFragment(private val categoryId: String) : SelectableListItemFragmentBase<Item>() {

    override val searchHint by lazy { getString(R.string.search_items) }

    override fun registerAdapterAsListener(adapter: ListItemRecyclerViewAdapter<Item>) {
        RepositoryManager.instance.addItemsOfCategoryListener(categoryId, adapter)
    }

    override fun unregisterAdapterAsListener(adapter: ListItemRecyclerViewAdapter<Item>) {
        RepositoryManager.instance.removeItemsOfCategoryListener(adapter)
    }

    override fun onFabClicked() {
        val newItem = RepositoryManager.instance.addOrUpdateItem(Item(null, getString(R.string.new_item), categoryId, null))
        findNavController().navigate(
            MobileNavigationDirections.actionGotoItemDetailsFragment(newItem.name, newItem.id!!)
        )
    }

    override fun onSearchQueryChanged(newQuery: String?) {
        adapter.filter(newQuery ?: "")
    }

    override fun instantiateAdapter(): ListItemRecyclerViewAdapter<Item> {
        return ListItemRecyclerViewAdapter(findNavController(), true)
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