package net.gabor7d2.simpleinventory.ui.itemdetails

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import net.gabor7d2.simpleinventory.ui.item.ItemsFragment

class ItemDetailsPagerAdapter(fragment: Fragment, private val itemId: String) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    fun getPageTitle(position: Int): String = when(position){
        0 -> "Details"
        1 -> "Sub-items"
        else -> throw IllegalArgumentException("Invalid position: $position")
    }

    override fun createFragment(position: Int): Fragment = when(position){
        0 -> ItemDetailsMainFragment(itemId)
        1 -> ItemsFragment(itemId)
        else -> throw IllegalArgumentException("Invalid position: $position")
    }
}