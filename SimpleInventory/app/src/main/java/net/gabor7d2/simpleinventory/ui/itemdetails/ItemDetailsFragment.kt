package net.gabor7d2.simpleinventory.ui.itemdetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import net.gabor7d2.simpleinventory.databinding.FragmentTabbedBinding

class ItemDetailsFragment : Fragment() {

    private var _binding: FragmentTabbedBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTabbedBinding.inflate(inflater, container, false)

        val itemId = arguments?.getString("itemId") ?: throw IllegalArgumentException("Missing itemId argument")
        val adapter = ItemDetailsPagerAdapter(this, itemId)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()

        return binding.root
    }
}