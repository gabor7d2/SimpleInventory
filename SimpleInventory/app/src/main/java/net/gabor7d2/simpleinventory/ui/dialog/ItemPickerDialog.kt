package net.gabor7d2.simpleinventory.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import net.gabor7d2.simpleinventory.databinding.DialogItemPickerBinding

class ItemPickerDialog : DialogFragment() {

    private lateinit var binding: DialogItemPickerBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return context?.let {
            binding = DialogItemPickerBinding.inflate(layoutInflater)

            val adapter = ItemPickerRecyclerViewAdapter {
                parentFragmentManager.setFragmentResult("itemPickerResult", Bundle().apply {
                    putString("itemId", it.id)
                })
                dismiss()
            }

            binding.list.adapter = adapter
            binding.editTextSearch.doOnTextChanged { text, start, before, count ->
                adapter.doSearch(text.toString())
            }

            MaterialAlertDialogBuilder(it)
                .setTitle("Pick an item")
                .setView(binding.root)
                .create()
        } ?: throw IllegalStateException("Context cannot be null")
    }
}