package net.gabor7d2.simpleinventory.ui.itemdetails

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
import net.gabor7d2.simpleinventory.R
import net.gabor7d2.simpleinventory.databinding.FragmentItemDetailsBinding
import net.gabor7d2.simpleinventory.model.Item
import net.gabor7d2.simpleinventory.persistence.EntityListener
import net.gabor7d2.simpleinventory.persistence.repository.RepositoryManager
import net.gabor7d2.simpleinventory.ui.dialog.CategoryPickerDialog
import net.gabor7d2.simpleinventory.ui.dialog.EditTextDialog
import net.gabor7d2.simpleinventory.ui.dialog.ItemPickerDialog

class ItemDetailsFragment(private val itemId: String) : Fragment(), EntityListener<Item> {

    private var _binding: FragmentItemDetailsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemDetailsBinding.inflate(inflater, container, false)
        RepositoryManager.instance.addItemListener(itemId, this)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        RepositoryManager.instance.removeItemListener(this)
        _binding = null
    }

    override fun onChanged(entity: Item) {
        binding.textViewName.text = entity.name

        binding.editNameButton.setOnClickListener {
            val dialog = EditTextDialog(getString(R.string.edit_name), prefill = entity.name)
            clearFragmentResultListener("editTextResult")
            setFragmentResultListener("editTextResult") { _, result ->
                val text = result.getString("text")!!
                RepositoryManager.instance.addOrUpdateItem(entity.copy(name = text))
                clearFragmentResultListener("editTextResult")
            }
            dialog.show(parentFragmentManager, "EditTextDialog")
        }


        val parent =
            if (entity.parentId == null) getString(R.string.no_parent)
            else RepositoryManager.instance.getItem(entity.parentId).name
        binding.textViewParent.text = parent

        binding.openParentDetailsButton.visibility = if (entity.parentId == null) View.GONE else View.VISIBLE

        if (entity.parentId != null) {
            binding.openParentDetailsButton.setOnClickListener {
                findNavController().navigate(
                    MobileNavigationDirections.actionGotoItemDetailsFragment(parent, entity.parentId)
                )
            }
        }

        binding.editParentButton.setOnClickListener {
            val dialog = ItemPickerDialog()
            clearFragmentResultListener("itemPickerResult")
            setFragmentResultListener("itemPickerResult") { _, result ->
                val itemId = result.getString("itemId")
                RepositoryManager.instance.addOrUpdateItem(entity.copy(parentId = itemId))
                clearFragmentResultListener("itemPickerResult")
            }
            dialog.show(parentFragmentManager, "ItemPickerDialog")
        }


        val category =
            if (entity.categoryId == null) getString(R.string.no_category)
            else RepositoryManager.instance.getCategory(entity.categoryId).name
        binding.textViewCategory.text = category

        binding.openCategoryDetailsButton.visibility = if (entity.categoryId == null) View.GONE else View.VISIBLE

        if (entity.categoryId != null) {
            binding.openCategoryDetailsButton.setOnClickListener {
                findNavController().navigate(
                    MobileNavigationDirections.actionGotoCategoryDetailsFragment(category, entity.categoryId)
                )
            }
        }

        binding.editCategoryButton.setOnClickListener {
            val dialog = CategoryPickerDialog()
            clearFragmentResultListener("categoryPickerResult")
            setFragmentResultListener("categoryPickerResult") { _, result ->
                val categoryId = result.getString("categoryId")
                RepositoryManager.instance.addOrUpdateItem(entity.copy(categoryId = categoryId))
                clearFragmentResultListener("categoryPickerResult")
            }
            dialog.show(parentFragmentManager, "CategoryPickerDialog")
        }
    }

    override fun onRemoved(entity: Item) {
        // TODO test
        Toast.makeText(context, getString(R.string.item_has_been_deleted), Toast.LENGTH_LONG).show()
        findNavController().popBackStack()
    }
}