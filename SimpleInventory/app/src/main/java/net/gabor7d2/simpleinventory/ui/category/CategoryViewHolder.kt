package net.gabor7d2.simpleinventory.ui.category

import android.graphics.drawable.ColorDrawable
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavController
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import net.gabor7d2.simpleinventory.MobileNavigationDirections
import net.gabor7d2.simpleinventory.R
import net.gabor7d2.simpleinventory.databinding.ListItemCategoryBinding
import net.gabor7d2.simpleinventory.model.Category
import net.gabor7d2.simpleinventory.persistence.repository.RepositoryManager

class CategoryViewHolder(
    private val binding: ListItemCategoryBinding,
    private val navController: NavController
) : RecyclerView.ViewHolder(binding.root) {

    private var category: Category? = null

    fun bind(category: Category, selected: Boolean) {
        if (category.id == null) {
            throw IllegalArgumentException("Category id cannot be null")
        }
        this.category = category

        binding.textViewName.text = category.name

        if (category.favourite) {
            binding.buttonFavourite.setImageResource(R.drawable.ic_star_filled)
        } else {
            binding.buttonFavourite.setImageResource(R.drawable.ic_star_outline)
        }

        binding.buttonFavourite.setOnClickListener {
            RepositoryManager.instance.addOrUpdateCategory(category.copy(favourite = !category.favourite))
        }

        binding.root.setOnClickListener {
            navController.navigate(
                MobileNavigationDirections.actionGotoCategoryDetailsFragment(category.name, category.id)
            )
        }
    }

    fun getItemDetails(): ItemDetailsLookup.ItemDetails<String> =
        object : ItemDetailsLookup.ItemDetails<String>() {
            override fun getPosition(): Int = bindingAdapterPosition
            override fun getSelectionKey(): String = category?.id ?: throw IllegalStateException()
        }
}