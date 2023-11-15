package net.gabor7d2.simpleinventory.ui.item

import net.gabor7d2.simpleinventory.databinding.ListItemItemBinding
import net.gabor7d2.simpleinventory.model.Item
import net.gabor7d2.simpleinventory.ui.ViewHolder

class ItemViewHolder(binding: ListItemItemBinding) : ViewHolder(binding.root) {
    val idView = binding.itemNumber
    val contentView = binding.content

    override fun toString(): String {
        return super.toString() + " '" + contentView.text + "'"
    }

    fun bind(item: Item) {
        idView.text = item.id
        contentView.text = item.name
    }
}