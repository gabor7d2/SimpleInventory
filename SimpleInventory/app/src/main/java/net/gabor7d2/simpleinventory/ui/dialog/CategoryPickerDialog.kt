package net.gabor7d2.simpleinventory.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import net.gabor7d2.simpleinventory.databinding.DialogPickerBinding

class CategoryPickerDialog : DialogFragment() {

    private lateinit var binding: DialogPickerBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return context?.let {
            binding = DialogPickerBinding.inflate(layoutInflater)

            val adapter = CategoryPickerRecyclerViewAdapter {
                parentFragmentManager.setFragmentResult("categoryPickerResult", Bundle().apply {
                    putString("categoryId", it.id)
                })
                dismiss()
            }

            binding.list.adapter = adapter
            binding.editTextSearch.hint = "Search categories..."
            binding.editTextSearch.requestFocus()
            binding.editTextSearch.doOnTextChanged { text, _, _, _ ->
                adapter.doSearch(text.toString())
            }

            val dialog = MaterialAlertDialogBuilder(it)
                .setTitle("Pick a category")
                .setView(binding.root)
                .create()

            dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            dialog
        } ?: throw IllegalStateException("Context cannot be null")
    }
}