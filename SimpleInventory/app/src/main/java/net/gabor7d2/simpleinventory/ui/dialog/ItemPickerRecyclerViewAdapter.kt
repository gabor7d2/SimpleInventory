package net.gabor7d2.simpleinventory.ui.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.gabor7d2.simpleinventory.databinding.ListItemItemPickerBinding
import net.gabor7d2.simpleinventory.model.Item
import net.gabor7d2.simpleinventory.persistence.repository.RepositoryManager

class ItemPickerRecyclerViewAdapter(private val onItemSelected: (Item) -> Unit) :
    RecyclerView.Adapter<ItemPickerRecyclerViewAdapter.ItemPickerViewHolder>() {

    private val values: MutableList<Item> = mutableListOf()

    init {
        doSearch("")
    }

    override fun getItemCount() = values.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemPickerViewHolder {
        return ItemPickerViewHolder(
            ListItemItemPickerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemPickerViewHolder, position: Int) {
        holder.bind(values[position], onItemSelected)
    }

    fun doSearch(name: String) {
        values.clear()
        if (name.isEmpty()) values.add(Item(null, "(No item)", null))
        values.addAll(RepositoryManager.instance.searchItems(name))
        notifyDataSetChanged()
    }

    inner class ItemPickerViewHolder(private val binding: ListItemItemPickerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item, onItemSelected: (Item) -> Unit) {
            binding.textViewName.text = item.name
            binding.root.setOnClickListener {
                onItemSelected(item)
            }
        }
    }
}