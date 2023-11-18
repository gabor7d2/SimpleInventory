package net.gabor7d2.simpleinventory.ui

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import net.gabor7d2.simpleinventory.databinding.ListItemCategoryBinding
import net.gabor7d2.simpleinventory.databinding.ListItemItemBinding
import net.gabor7d2.simpleinventory.persistence.CollectionListener
import net.gabor7d2.simpleinventory.model.Category
import net.gabor7d2.simpleinventory.model.Item
import net.gabor7d2.simpleinventory.model.ListItem
import net.gabor7d2.simpleinventory.ui.category.CategoryViewHolder
import net.gabor7d2.simpleinventory.ui.item.ItemViewHolder

class ListItemRecyclerViewAdapter<T : ListItem>
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    CollectionListener<T> {

    private val values: MutableList<T> = mutableListOf()

    override fun getItemCount(): Int = values.size

    override fun getItemViewType(position: Int): Int {
        return values[position].listItemType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            ListItem.TYPE_CATEGORY -> return CategoryViewHolder(
                    ListItemCategoryBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            ListItem.TYPE_ITEM -> return ItemViewHolder(
                ListItemItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw IllegalArgumentException("Unknown viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = values[position]
        when (holder) {
            is CategoryViewHolder -> holder.bind(item as Category)
            is ItemViewHolder -> holder.bind(item as Item)
            else -> throw IllegalArgumentException("Unknown ViewHolder: ${holder.javaClass.simpleName}")
        }
    }

    override fun onAdded(entity: T) {
        values.add(entity)
        notifyItemInserted(values.size - 1)
    }

    override fun onChanged(entity: T) {
        val index = values.indexOfFirst { it.id == entity.id }
        values[index] = entity
        notifyItemChanged(index)
    }

    override fun onRemoved(entity: T) {
        val index = values.indexOfFirst { it.id == entity.id }
        values.removeAt(index)
        notifyItemRemoved(index)
    }
}