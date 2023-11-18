package net.gabor7d2.simpleinventory.ui.item

import android.os.Bundle
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import net.gabor7d2.simpleinventory.R
import net.gabor7d2.simpleinventory.databinding.ListItemItemBinding
import net.gabor7d2.simpleinventory.model.Item
import net.gabor7d2.simpleinventory.persistence.repository.RepositoryManager

class ItemViewHolder(
    private val binding: ListItemItemBinding,
    private val navController: NavController
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Item) {
        if (item.id == null) {
            throw IllegalArgumentException("Item id cannot be null")
        }
        binding.itemNumber.text = item.id
        binding.content.text = item.name
        binding.deleteButton.setOnClickListener {
            RepositoryManager.instance.removeItem(item.id)
        }
        binding.root.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("itemId", item.id)
            navController.navigate(R.id.itemDetailsFragment, bundle)
        }
    }
}