package net.gabor7d2.simpleinventory.ui.item

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.gabor7d2.simpleinventory.databinding.FragmentItemsBinding
import net.gabor7d2.simpleinventory.model.Category
import net.gabor7d2.simpleinventory.model.Item

class ItemsFragment : Fragment() {

    private var _binding: FragmentItemsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemsBinding.inflate(inflater, container, false)

        with(binding.list) {
            layoutManager = LinearLayoutManager(context)
            adapter = ItemsRecyclerViewAdapter(
                listOf(
                    Item("1", "Bread"),
                    Item("2", "HDMI cable"),
                    Item("3", "Isaac Asimov: Foundation Empire")
                )
            )
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}