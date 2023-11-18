package net.gabor7d2.simpleinventory.ui.itemdetails

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import net.gabor7d2.simpleinventory.R
import net.gabor7d2.simpleinventory.databinding.FragmentItemDetailsBinding
import net.gabor7d2.simpleinventory.model.Item
import net.gabor7d2.simpleinventory.persistence.EntityListener
import net.gabor7d2.simpleinventory.persistence.repository.RepositoryManager
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


        val parent =
            if (entity.parentId == null) "(No parent)"
            else RepositoryManager.instance.getItem(entity.parentId).name
        binding.textViewParent.text = parent

        binding.openParentDetailsButton.visibility =
            if (entity.parentId == null) View.GONE else View.VISIBLE

        binding.openParentDetailsButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("itemId", entity.parentId)
            bundle.putString("title", parent)
            findNavController().navigate(R.id.itemDetailsFragment, bundle)
        }

        binding.editParentButton.setOnClickListener {
            val dialog = ItemPickerDialog()
            setFragmentResultListener("itemPickerResult") { _, result ->
                val itemId = result.getString("itemId")
                RepositoryManager.instance.addOrUpdateItem(entity.copy(parentId = itemId))
                clearFragmentResultListener("itemPickerResult")
            }
            dialog.show(parentFragmentManager, "ItemPickerDialog")
        }


        val category =
            if (entity.categoryId == null) "(No category)"
            else RepositoryManager.instance.getCategory(entity.categoryId).name
        binding.textViewCategory.text = category

        binding.openCategoryDetailsButton.visibility =
            if (entity.categoryId == null) View.GONE else View.VISIBLE
    }

    override fun onRemoved(entity: Item) {
        // TODO test
        Toast.makeText(context, "Item has been deleted", Toast.LENGTH_LONG).show()
        findNavController().popBackStack()
    }
}