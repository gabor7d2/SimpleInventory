package net.gabor7d2.simpleinventory.ui.item

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.gabor7d2.simpleinventory.databinding.FragmentItemsBinding
import net.gabor7d2.simpleinventory.persistence.repository.RepositoryManager
import net.gabor7d2.simpleinventory.model.Item
import net.gabor7d2.simpleinventory.ui.ListItemRecyclerViewAdapter

class ItemsFragment : Fragment() {

    private var _binding: FragmentItemsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemsBinding.inflate(inflater, container, false)

        val adapter = ListItemRecyclerViewAdapter<Item>()
        RepositoryManager.instance.addItemChildrenListener(null, adapter)

        with(binding.list) {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}