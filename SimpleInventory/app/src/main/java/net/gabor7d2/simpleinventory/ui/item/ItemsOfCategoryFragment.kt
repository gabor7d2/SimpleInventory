package net.gabor7d2.simpleinventory.ui.item

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import net.gabor7d2.simpleinventory.MobileNavigationDirections
import net.gabor7d2.simpleinventory.databinding.FragmentListItemsBinding
import net.gabor7d2.simpleinventory.persistence.repository.RepositoryManager
import net.gabor7d2.simpleinventory.model.Item
import net.gabor7d2.simpleinventory.ui.ListItemRecyclerViewAdapter

class ItemsOfCategoryFragment(private val categoryId: String) : Fragment() {

    private var _binding: FragmentListItemsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListItemsBinding.inflate(inflater, container, false)

        val adapter = ListItemRecyclerViewAdapter<Item>(findNavController())
        RepositoryManager.instance.addItemsOfCategoryListener(categoryId, adapter)
        binding.list.adapter = adapter

        binding.fab.setOnClickListener {
            val newItem = RepositoryManager.instance.addOrUpdateItem(Item(null, "New Item", categoryId, null))
            findNavController().navigate(
                MobileNavigationDirections.actionGotoItemDetailsFragment(newItem.name, newItem.id!!)
            )
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}