package net.gabor7d2.simpleinventory.ui.category

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import net.gabor7d2.simpleinventory.databinding.FragmentCategoriesBinding
import net.gabor7d2.simpleinventory.persistence.repository.RepositoryManager
import net.gabor7d2.simpleinventory.model.Category
import net.gabor7d2.simpleinventory.ui.ListItemRecyclerViewAdapter

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)

        val adapter = ListItemRecyclerViewAdapter<Category>(findNavController())
        RepositoryManager.instance.addCategoryChildrenListener(null, adapter)

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