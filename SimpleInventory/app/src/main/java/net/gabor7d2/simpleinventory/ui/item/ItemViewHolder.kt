package net.gabor7d2.simpleinventory.ui.item

import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavController
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import net.gabor7d2.simpleinventory.MobileNavigationDirections
import net.gabor7d2.simpleinventory.R
import net.gabor7d2.simpleinventory.databinding.ListItemItemBinding
import net.gabor7d2.simpleinventory.model.Item
import net.gabor7d2.simpleinventory.persistence.repository.RepositoryManager

class ItemViewHolder(
    private val binding: ListItemItemBinding,
    private val navController: NavController
) : RecyclerView.ViewHolder(binding.root) {

    private var item: Item? = null

    init {
        itemId
    }

    fun bind(item: Item, selected: Boolean) {
        if (item.id == null) {
            throw IllegalArgumentException("Item id cannot be null")
        }
        this.item = item

        binding.textViewName.text = item.name

        val category =
            if (item.categoryId == null) ""
            else RepositoryManager.instance.getCategory(item.categoryId).name
        binding.textViewCategory.text = category

        binding.textViewCategory.visibility = if (item.categoryId == null) View.GONE else View.VISIBLE

        if (item.favourite) {
            binding.buttonFavourite.setImageResource(R.drawable.ic_star_filled)
        } else {
            binding.buttonFavourite.setImageResource(R.drawable.ic_star_outline)
        }

        binding.buttonFavourite.setOnClickListener {
            RepositoryManager.instance.addOrUpdateItem(item.copy(favourite = !item.favourite))
        }

        binding.root.setOnClickListener {
            navController.navigate(
                MobileNavigationDirections.actionGotoItemDetailsFragment(item.name, item.id)
            )
        }
    }

    fun getItemDetails(): ItemDetailsLookup.ItemDetails<String> =
        object : ItemDetailsLookup.ItemDetails<String>() {
            override fun getPosition(): Int = bindingAdapterPosition
            override fun getSelectionKey(): String = item?.id ?: throw IllegalStateException()
        }
}