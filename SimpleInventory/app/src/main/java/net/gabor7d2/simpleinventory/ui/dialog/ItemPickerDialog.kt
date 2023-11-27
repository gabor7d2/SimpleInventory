package net.gabor7d2.simpleinventory.ui.dialog

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import net.gabor7d2.simpleinventory.R
import net.gabor7d2.simpleinventory.model.Item
import net.gabor7d2.simpleinventory.persistence.repository.RepositoryManager

class ItemPickerDialog : PickerDialogBase<Item>() {

    companion object {
        private val RESULT_KEY = "itemPickerResult"
        private val ITEM_ID_KEY = "itemId"
        private val DIALOG_TAG = "ItemPickerDialog"
    }

    override val dialogTitle by lazy { getString(R.string.pick_an_item) }

    override val searchHint by lazy { getString(R.string.search_items) }

    override val noItem by lazy { Item(null, getString(R.string.no_item)) }

    override fun listItemNameSupplier(item: Item) = item.name

    override fun searchResultSupplier(name: String) = RepositoryManager.instance.searchItems(name)

    override fun onPick(item: Item) {
        parentFragmentManager.setFragmentResult("itemPickerResult", Bundle().apply {
            putString("itemId", item.id)
        })
    }

    fun show(parentFragment: Fragment, onPick: (itemId: String?) -> Unit) {
        parentFragment.clearFragmentResultListener(RESULT_KEY)
        parentFragment.setFragmentResultListener(RESULT_KEY) { _, result ->
            onPick(result.getString(ITEM_ID_KEY))
            parentFragment.clearFragmentResultListener(RESULT_KEY)
        }
        show(parentFragment.parentFragmentManager, DIALOG_TAG)
    }
}