package net.gabor7d2.simpleinventory.ui.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.gabor7d2.simpleinventory.databinding.ListItemPickerBinding

class PickerRecyclerViewAdapter<T>(
    private val noItem: T,
    private val itemNameSupplier: (T) -> String,
    private val searchResultSupplier: (String) -> List<T>,
    private val onPick: (T) -> Unit
) :
    RecyclerView.Adapter<PickerRecyclerViewAdapter<T>.PickerViewHolder>() {

    private val values: MutableList<T> = mutableListOf()

    init {
        doSearch("")
    }

    override fun getItemCount() = values.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickerViewHolder {
        return PickerViewHolder(
            ListItemPickerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PickerViewHolder, position: Int) {
        holder.bind(values[position], onPick)
    }

    fun doSearch(name: String) {
        values.clear()
        if (name.isEmpty()) values.add(noItem)
        values.addAll(searchResultSupplier(name))
        notifyDataSetChanged()
    }

    inner class PickerViewHolder(private val binding: ListItemPickerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: T, onPick: (T) -> Unit) {
            binding.textView.text = itemNameSupplier(item)
            binding.root.setOnClickListener {
                onPick(item)
            }
        }
    }
}