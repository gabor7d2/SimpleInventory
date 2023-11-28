package net.gabor7d2.simpleinventory.ui.categorydetails

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import net.gabor7d2.simpleinventory.ui.category.CategoriesFragment
import net.gabor7d2.simpleinventory.ui.category.CategoriesFragmentArgs

class CategoryDetailsPagerAdapter(fragment: Fragment, private val categoryId: String) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    fun getPageTitle(position: Int): String = when(position){
        0 -> "Details"
        1 -> "Categories"
        2 -> "Items"
        else -> throw IllegalArgumentException("Invalid position: $position")
    }

    override fun createFragment(position: Int): Fragment = when(position){
        0 -> {
            CategoryDetailsFragment().apply {
                arguments = CategoryDetailsFragmentArgs(categoryId).toBundle()
            }
        }
        1 -> {
            CategoriesFragment().apply {
                arguments = CategoriesFragmentArgs(categoryId).toBundle()
            }
        }
        2 -> {
            ItemsOfCategoryFragment().apply {
                arguments = ItemsOfCategoryFragmentArgs(categoryId).toBundle()
            }
        }
        else -> throw IllegalArgumentException("Invalid position: $position")
    }
}