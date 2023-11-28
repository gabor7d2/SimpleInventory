package net.gabor7d2.simpleinventory.ui.dialog

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import net.gabor7d2.simpleinventory.R
import net.gabor7d2.simpleinventory.model.Category
import net.gabor7d2.simpleinventory.persistence.repository.RepositoryManager

class CategoryPickerDialog : PickerDialogBase<Category>() {

    companion object {
        private val RESULT_KEY = "categoryPickerResult"
        private val CATEGORY_ID_KEY = "categoryId"
        private val DIALOG_TAG = "CategoryPickerDialog"
    }

    override val dialogTitle by lazy { getString(R.string.pick_a_category) }

    override val searchHint by lazy { getString(R.string.search_categories) }

    override val noItem by lazy { Category(null, getString(R.string.no_category)) }

    override fun listItemNameSupplier(item: Category) = item.name

    override fun searchResultSupplier(name: String) = RepositoryManager.instance.searchCategories(name)

    override fun onPick(item: Category) {
        parentFragmentManager.setFragmentResult("categoryPickerResult", Bundle().apply {
            putString("categoryId", item.id)
        })
    }

    fun show(parentFragment: Fragment, onPick: (categoryId: String?) -> Unit) {
        parentFragment.clearFragmentResultListener(RESULT_KEY)
        parentFragment.setFragmentResultListener(RESULT_KEY) { _, result ->
            onPick(result.getString(CATEGORY_ID_KEY))
            parentFragment.clearFragmentResultListener(RESULT_KEY)
        }
        show(parentFragment.parentFragmentManager, DIALOG_TAG)
    }
}