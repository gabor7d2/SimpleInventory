package net.gabor7d2.simpleinventory.ui.category

import net.gabor7d2.simpleinventory.databinding.ListItemCategoryBinding
import net.gabor7d2.simpleinventory.model.Category
import net.gabor7d2.simpleinventory.ui.ViewHolder

class CategoryViewHolder(binding: ListItemCategoryBinding) : ViewHolder(binding.root) {
    val idView = binding.itemNumber
    val contentView = binding.content

    override fun toString(): String {
        return super.toString() + " '" + contentView.text + "'"
    }

    fun bind(category: Category) {
        idView.text = category.id
        contentView.text = category.name
    }
}