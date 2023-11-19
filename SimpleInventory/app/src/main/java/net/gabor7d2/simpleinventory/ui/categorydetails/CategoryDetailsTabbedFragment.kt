package net.gabor7d2.simpleinventory.ui.categorydetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import net.gabor7d2.simpleinventory.databinding.FragmentTabbedBinding

class CategoryDetailsTabbedFragment : Fragment() {

    private var _binding: FragmentTabbedBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTabbedBinding.inflate(inflater, container, false)

        val args: CategoryDetailsTabbedFragmentArgs by navArgs()
        val adapter = CategoryDetailsPagerAdapter(this, args.categoryId)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}