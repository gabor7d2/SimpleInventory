package net.gabor7d2.simpleinventory.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import net.gabor7d2.simpleinventory.databinding.DialogEditTextBinding

class EditTextDialog(
    private val title: String,
    private val hint: String = "",
    private val prefill: String = ""
) : DialogFragment() {

    private lateinit var binding: DialogEditTextBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return context?.let {
            binding = DialogEditTextBinding.inflate(layoutInflater)

            binding.editText.hint = hint
            binding.editText.setText(prefill)
            binding.editText.selectAll()
            binding.editText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    setFragmentResult()
                    dismiss()
                    true
                } else {
                    false
                }
            }

            val dialog = MaterialAlertDialogBuilder(it)
                .setTitle(title)
                .setView(binding.root)
                .setPositiveButton("OK") { _, _ -> setFragmentResult() }
                .setNegativeButton("Cancel", null)
                .create()

            dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            dialog
        } ?: throw IllegalStateException("Context cannot be null")
    }

    private fun setFragmentResult() {
        parentFragmentManager.setFragmentResult("editTextResult", Bundle().apply {
            putString("text", binding.editText.text.toString())
        })
    }
}