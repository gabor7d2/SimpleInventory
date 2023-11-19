package net.gabor7d2.simpleinventory.ui.categorydetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import net.gabor7d2.simpleinventory.MobileNavigationDirections
import net.gabor7d2.simpleinventory.databinding.FragmentCategoryDetailsBinding
import net.gabor7d2.simpleinventory.model.Category
import net.gabor7d2.simpleinventory.persistence.EntityListener
import net.gabor7d2.simpleinventory.persistence.repository.RepositoryManager
import net.gabor7d2.simpleinventory.ui.dialog.CategoryPickerDialog
import net.gabor7d2.simpleinventory.ui.dialog.EditTextDialog

class CategoryDetailsFragment(private val categoryId: String) : Fragment(), EntityListener<Category> {

    private var _binding: FragmentCategoryDetailsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryDetailsBinding.inflate(inflater, container, false)
        RepositoryManager.instance.addCategoryListener(categoryId, this)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        RepositoryManager.instance.removeCategoryListener(this)
        _binding = null
    }

    override fun onChanged(entity: Category) {
        binding.textViewName.text = entity.name

        binding.editNameButton.setOnClickListener {
            val dialog = EditTextDialog("Edit name", prefill = entity.name)
            clearFragmentResultListener("editTextResult")
            setFragmentResultListener("editTextResult") { _, result ->
                val text = result.getString("text")!!
                RepositoryManager.instance.addOrUpdateCategory(entity.copy(name = text))
                clearFragmentResultListener("editTextResult")
            }
            dialog.show(parentFragmentManager, "EditTextDialog")
        }


        val parent =
            if (entity.parentId == null) "(No parent)"
            else RepositoryManager.instance.getCategory(entity.parentId).name
        binding.textViewParent.text = parent

        binding.openParentDetailsButton.visibility = if (entity.parentId == null) View.GONE else View.VISIBLE

        if (entity.parentId != null) {
            binding.openParentDetailsButton.setOnClickListener {
                findNavController().navigate(
                    MobileNavigationDirections.actionGotoCategoryDetailsFragment(parent, entity.parentId)
                )
            }
        }

        binding.editParentButton.setOnClickListener {
            val dialog = CategoryPickerDialog()
            clearFragmentResultListener("categoryPickerResult")
            setFragmentResultListener("categoryPickerResult") { _, result ->
                val categoryId = result.getString("categoryId")
                RepositoryManager.instance.addOrUpdateCategory(entity.copy(parentId = categoryId))
                clearFragmentResultListener("categoryPickerResult")
            }
            dialog.show(parentFragmentManager, "CategoryPickerDialog")
        }
    }

    override fun onRemoved(entity: Category) {
        // TODO test
        Toast.makeText(context, "Category has been deleted", Toast.LENGTH_LONG).show()
        findNavController().popBackStack()
    }
}