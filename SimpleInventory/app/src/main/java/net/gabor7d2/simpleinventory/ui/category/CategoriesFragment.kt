package net.gabor7d2.simpleinventory.ui.category

import androidx.navigation.fragment.findNavController
import net.gabor7d2.simpleinventory.MobileNavigationDirections
import net.gabor7d2.simpleinventory.R
import net.gabor7d2.simpleinventory.model.Category
import net.gabor7d2.simpleinventory.persistence.repository.RepositoryManager
import net.gabor7d2.simpleinventory.ui.ListItemRecyclerViewAdapter
import net.gabor7d2.simpleinventory.ui.SelectableListItemFragmentBase

class CategoriesFragment(private val categoryId: String? = null) : SelectableListItemFragmentBase<Category>()  {

    override val searchHint: String
        get() = getString(R.string.search_categories)

    override fun registerAdapterAsListener(adapter: ListItemRecyclerViewAdapter<Category>) {
        RepositoryManager.instance.addCategoryChildrenListener(categoryId, adapter)
    }

    override fun unregisterAdapterAsListener(adapter: ListItemRecyclerViewAdapter<Category>) {
        RepositoryManager.instance.removeCategoryChildrenListener(adapter)
    }

    override fun onFabClicked() {
        val newCategory = RepositoryManager.instance.addOrUpdateCategory(Category(null, getString(R.string.new_category), categoryId))
        findNavController().navigate(
            MobileNavigationDirections.actionGotoCategoryDetailsFragment(newCategory.name, newCategory.id!!)
        )
    }

    override fun onSearchQueryChanged(newQuery: String?) {
        adapter.filter(newQuery ?: "")
    }

    override fun onActionFavourite(itemIds: List<String>) {
        itemIds.forEach {
            RepositoryManager.instance.addOrUpdateCategory(
                RepositoryManager.instance.getCategory(it).copy(favourite = true)
            )
        }
    }

    override fun onActionUnfavourite(itemIds: List<String>) {
        itemIds.forEach {
            RepositoryManager.instance.addOrUpdateCategory(
                RepositoryManager.instance.getCategory(it).copy(favourite = false)
            )
        }
    }

    override fun onActionDelete(itemIds: List<String>) {
        itemIds.forEach {
            RepositoryManager.instance.removeCategory(it)
        }
    }
}