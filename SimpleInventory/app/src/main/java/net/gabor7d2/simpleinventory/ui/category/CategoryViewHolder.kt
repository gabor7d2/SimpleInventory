package net.gabor7d2.simpleinventory.ui.category

import androidx.navigation.NavController
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

    fun bind(category: Category) {
        if (category.id == null) {
            throw IllegalArgumentException("Category id cannot be null")
        }

        binding.textViewName.text = category.name

        binding.buttonDelete.setOnClickListener {
            RepositoryManager.instance.removeCategory(category.id)
        }

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
}