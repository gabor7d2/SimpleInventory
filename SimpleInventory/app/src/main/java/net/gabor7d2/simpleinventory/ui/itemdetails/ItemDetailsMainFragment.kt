package net.gabor7d2.simpleinventory.ui.itemdetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import net.gabor7d2.simpleinventory.R
import net.gabor7d2.simpleinventory.databinding.FragmentItemDetailsMainBinding
import net.gabor7d2.simpleinventory.model.Item
import net.gabor7d2.simpleinventory.persistence.EntityListener
import net.gabor7d2.simpleinventory.persistence.repository.RepositoryManager

class ItemDetailsMainFragment(private val itemId: String) : Fragment(), EntityListener<Item> {

    private var _binding: FragmentItemDetailsMainBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemDetailsMainBinding.inflate(inflater, container, false)
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

        binding.textViewCategory.text =
            RepositoryManager.instance.getCategory(entity.categoryId).name

        binding.openParentDetailsButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("itemId", entity.parentId)
            findNavController().navigate(R.id.itemDetailsFragment, bundle)
        }
    }

    override fun onRemoved(entity: Item) {
        // TODO test
        findNavController().popBackStack()
    }
}