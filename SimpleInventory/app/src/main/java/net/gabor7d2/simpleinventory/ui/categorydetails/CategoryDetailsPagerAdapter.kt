package net.gabor7d2.simpleinventory.ui.categorydetails

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import net.gabor7d2.simpleinventory.ui.category.CategoriesFragment

class CategoryDetailsPagerAdapter(fragment: Fragment, private val categoryId: String) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    fun getPageTitle(position: Int): String = when(position){
        0 -> "Details"
        1 -> "Sub-categories"
        else -> throw IllegalArgumentException("Invalid position: $position")
    }

    override fun createFragment(position: Int): Fragment = when(position){
        0 -> CategoryDetailsFragment(categoryId)
        1 -> CategoriesFragment(categoryId)
        else -> throw IllegalArgumentException("Invalid position: $position")
    }
}