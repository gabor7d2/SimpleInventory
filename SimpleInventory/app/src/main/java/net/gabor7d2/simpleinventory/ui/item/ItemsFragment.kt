package net.gabor7d2.simpleinventory.ui.item

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import net.gabor7d2.simpleinventory.R
import net.gabor7d2.simpleinventory.databinding.FragmentItemsBinding
import net.gabor7d2.simpleinventory.persistence.repository.RepositoryManager
import net.gabor7d2.simpleinventory.model.Item
import net.gabor7d2.simpleinventory.ui.ListItemRecyclerViewAdapter

class ItemsFragment(private val itemId: String? = null) : Fragment() {

    private var _binding: FragmentItemsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemsBinding.inflate(inflater, container, false)

        val adapter = ListItemRecyclerViewAdapter<Item>(findNavController())
        RepositoryManager.instance.addItemChildrenListener(itemId, adapter)

        with(binding.list) {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }

        binding.fab.setOnClickListener {
            val newItem = RepositoryManager.instance.addOrUpdateItem(Item(null, "New Item", null))
            val bundle = Bundle()
            bundle.putString("itemId", newItem.id)
            findNavController().navigate(R.id.itemDetailsFragment, bundle)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}