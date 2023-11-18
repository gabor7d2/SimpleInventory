package net.gabor7d2.simpleinventory.ui.itemdetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import net.gabor7d2.simpleinventory.databinding.FragmentItemDetailsBinding
import net.gabor7d2.simpleinventory.model.Item
import net.gabor7d2.simpleinventory.persistence.EntityListener
import net.gabor7d2.simpleinventory.persistence.repository.RepositoryManager

class ItemDetailsFragment : Fragment(), EntityListener<Item> {

    private var _binding: FragmentItemDetailsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemDetailsBinding.inflate(inflater, container, false)

        val itemId = arguments?.getString("itemId") ?: throw IllegalArgumentException("Missing itemId argument")
        RepositoryManager.instance.addItemListener(itemId, this)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
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

        binding.textViewCategory.text =
            RepositoryManager.instance.getCategory(entity.categoryId).name
    }

    override fun onRemoved(entity: Item) {
        // TODO test
        findNavController().popBackStack()
    }
}