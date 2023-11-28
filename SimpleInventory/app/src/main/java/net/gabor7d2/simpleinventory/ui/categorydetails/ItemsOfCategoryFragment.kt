package net.gabor7d2.simpleinventory.ui.categorydetails

import android.view.Menu
import android.view.MenuItem
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.MutableSelection
import net.gabor7d2.simpleinventory.BarcodeUtils
import net.gabor7d2.simpleinventory.MobileNavigationDirections
import net.gabor7d2.simpleinventory.R
import net.gabor7d2.simpleinventory.model.Item
import net.gabor7d2.simpleinventory.persistence.repository.RepositoryManager
import net.gabor7d2.simpleinventory.ui.ListItemRecyclerViewAdapter
import net.gabor7d2.simpleinventory.ui.SelectableListItemFragmentBase

class ItemsOfCategoryFragment : SelectableListItemFragmentBase<Item>() {

    override val searchHint by lazy { getString(R.string.search_items) }

    private val categoryId: String by lazy { ItemsOfCategoryFragmentArgs.fromBundle(requireArguments()).categoryId }

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

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.action_export_barcode) {
            tracker?.let {
                val selectionCopy = MutableSelection<String>()
                it.copySelection(selectionCopy)
                val items = selectionCopy.toList()
                    .mapNotNull { RepositoryManager.instance.getItem(it) }
                    .sortedBy { it.barcode }
                BarcodeUtils.exportBarcodes(requireContext(), items)
            }
        }
        return super.onMenuItemSelected(menuItem)
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