package net.gabor7d2.simpleinventory.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import net.gabor7d2.simpleinventory.databinding.DialogPickerBinding

abstract class PickerDialogBase<T> : DialogFragment() {

    private lateinit var binding: DialogPickerBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return context?.let {
            binding = DialogPickerBinding.inflate(layoutInflater)

            val adapter = PickerRecyclerViewAdapter<T>(noItem, ::listItemNameSupplier, ::searchResultSupplier) {
                onPick(it)
                dismiss()
            }

            binding.list.adapter = adapter
            binding.editTextSearch.hint = searchHint
            binding.editTextSearch.requestFocus()
            binding.editTextSearch.doOnTextChanged { text, _, _, _ ->
                adapter.doSearch(text.toString())
            }

            val dialog = MaterialAlertDialogBuilder(it)
                .setTitle(dialogTitle)
                .setView(binding.root)
                .create()

            dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            dialog
        } ?: throw IllegalStateException("Context cannot be null")
    }

    abstract val dialogTitle: String

    abstract val searchHint: String

    abstract val noItem: T

    abstract fun listItemNameSupplier(item: T): String

    abstract fun searchResultSupplier(name: String): List<T>

    abstract fun onPick(item: T)
}