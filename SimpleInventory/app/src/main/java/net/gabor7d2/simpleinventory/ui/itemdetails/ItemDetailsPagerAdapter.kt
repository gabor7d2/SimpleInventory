package net.gabor7d2.simpleinventory.ui.itemdetails

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import net.gabor7d2.simpleinventory.ui.item.ItemsFragment
import net.gabor7d2.simpleinventory.ui.item.ItemsFragmentArgs

class ItemDetailsPagerAdapter(fragment: Fragment, private val itemId: String) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    fun getPageTitle(position: Int): String = when(position){
        0 -> "Details"
        1 -> "Sub-items"
        else -> throw IllegalArgumentException("Invalid position: $position")
    }

    override fun createFragment(position: Int): Fragment = when(position){
        0 -> {
            ItemDetailsFragment().apply {
                arguments = ItemDetailsFragmentArgs(itemId).toBundle()
            }
        }
        1 -> {
            ItemsFragment().apply {
                arguments = ItemsFragmentArgs(itemId).toBundle()
            }
        }
        else -> throw IllegalArgumentException("Invalid position: $position")
    }
}