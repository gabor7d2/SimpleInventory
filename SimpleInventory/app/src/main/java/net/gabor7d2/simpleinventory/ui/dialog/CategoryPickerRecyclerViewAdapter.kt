package net.gabor7d2.simpleinventory.ui.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.gabor7d2.simpleinventory.databinding.ListItemPickerBinding
import net.gabor7d2.simpleinventory.model.Category
import net.gabor7d2.simpleinventory.persistence.repository.RepositoryManager

class CategoryPickerRecyclerViewAdapter(private val onCategorySelected: (Category) -> Unit) :
    RecyclerView.Adapter<CategoryPickerRecyclerViewAdapter.CategoryViewHolder>() {

    private val values: MutableList<Category> = mutableListOf()

    init {
        doSearch("")
    }

    override fun getItemCount() = values.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            ListItemPickerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(values[position], onCategorySelected)
    }

    fun doSearch(name: String) {
        values.clear()
        if (name.isEmpty()) values.add(Category(null, "(No category)", null))
        values.addAll(RepositoryManager.instance.searchCategories(name))
        notifyDataSetChanged()
    }

    inner class CategoryViewHolder(private val binding: ListItemPickerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category, onCategorySelected: (Category) -> Unit) {
            binding.textView.text = category.name
            binding.root.setOnClickListener {
                onCategorySelected(category)
            }
        }
    }
}