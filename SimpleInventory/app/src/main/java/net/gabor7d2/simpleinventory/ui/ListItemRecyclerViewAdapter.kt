package net.gabor7d2.simpleinventory.ui

import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavController
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import net.gabor7d2.simpleinventory.R
import net.gabor7d2.simpleinventory.databinding.ListItemCategoryBinding
import net.gabor7d2.simpleinventory.databinding.ListItemItemBinding
import net.gabor7d2.simpleinventory.model.Category
import net.gabor7d2.simpleinventory.model.Item
import net.gabor7d2.simpleinventory.model.ListItem
import net.gabor7d2.simpleinventory.persistence.CollectionListener
import net.gabor7d2.simpleinventory.ui.category.CategoryViewHolder
import net.gabor7d2.simpleinventory.ui.item.ItemViewHolder


class ListItemRecyclerViewAdapter<T : ListItem>(
    private val navController: NavController
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    CollectionListener<T> {

    private val comparator = Comparator<ListItem> { o1, o2 -> o1.name.compareTo(o2.name) }

    private val allValues = mutableListOf<ListItem>()
    private val filteredValues = SortedList(ListItem::class.java, ListItemSortedListCallback(comparator))

    var searchQuery = ""
        private set

    private var tracker: SelectionTracker<String>? = null

    private val clickableItemBackgroundId: Int by lazy {
        val outValue = TypedValue()
        navController.context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        outValue.resourceId
    }

    private val selectedItemBackground: ColorDrawable by lazy {
        ColorDrawable(ResourcesCompat.getColor(navController.context.resources, R.color.selection, null))
    }

    fun setTracker(tracker: SelectionTracker<String>?) {
        this.tracker = tracker
    }

    fun getItemKeyProvider(): ItemKeyProvider<String> = MyItemKeyProvider()

    fun getSelectionPredicate(): SelectionTracker.SelectionPredicate<String> = MySelectionPredicate()

    override fun getItemCount() = filteredValues.size()

    override fun getItemViewType(position: Int) = filteredValues[position].listItemType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            ListItem.TYPE_CATEGORY -> return CategoryViewHolder(
                    ListItemCategoryBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), navController
                )
            ListItem.TYPE_ITEM -> return ItemViewHolder(
                ListItemItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), navController
            )
            else -> throw IllegalArgumentException("Unknown viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = filteredValues[position]
        val selected = tracker?.isSelected(item.id) ?: false

        when (holder) {
            is CategoryViewHolder -> holder.bind(item as Category, selected)
            is ItemViewHolder -> holder.bind(item as Item, selected)
            else -> throw IllegalArgumentException("Unknown ViewHolder: ${holder.javaClass.simpleName}")
        }

        if (selected)
            holder.itemView.background = selectedItemBackground
        else
            holder.itemView.setBackgroundResource(clickableItemBackgroundId)
    }

    override fun onAdded(entity: T) {
        Log.d("ListItemRecyclerViewAdapter", "${System.identityHashCode(this)} onAdded: ${entity.name}, ${entity.id}")
        allValues.add(entity)

        if (entity.name.contains(searchQuery, true)) {
            filteredValues.add(entity)
        }
    }

    override fun onChanged(entity: T) {
        Log.d("ListItemRecyclerViewAdapter", "${System.identityHashCode(this)} onChanged: ${entity.name}, ${entity.id}")
        val index = allValues.indexOfFirst { it.id == entity.id }
        allValues[index] = entity

        filteredValues.indexOf(entity).let {
            if (it >= 0) {
                filteredValues.updateItemAt(it, entity)
            }
        }
    }

    override fun onRemoved(entity: T) {
        Log.d("ListItemRecyclerViewAdapter", "${System.identityHashCode(this)} onRemoved: ${entity.name}, ${entity.id}")
        tracker?.deselect(entity.id!!)
        allValues.remove(entity)
        filteredValues.remove(entity)
    }

    private fun replaceWith(items: List<ListItem>) {
        val newItems = items.toMutableList()

        filteredValues.beginBatchedUpdates()
        for (i in filteredValues.size() - 1 downTo 0) {
            val value = filteredValues.get(i)
            if (!newItems.contains(value)) {
                filteredValues.remove(value)
            } else {
                newItems.remove(value)
            }
        }
        filteredValues.addAll(newItems)
        filteredValues.endBatchedUpdates()
    }

    fun filter(query: String) {
        if (query == searchQuery) return
        searchQuery = query

        if (query.isEmpty()) {
            replaceWith(allValues)
            return
        }

        Log.d("ListItemRecyclerViewAdapter", "${System.identityHashCode(this)} filter: $query")
        Log.d("ListItemRecyclerViewAdapter", "${System.identityHashCode(this)} filter: allValues=${allValues.size}, filteredValues=${filteredValues.size()}")
        val results = allValues.filter { it.name.contains(query, true) }.toMutableList()
        Log.d("ListItemRecyclerViewAdapter", "${System.identityHashCode(this)} filter: results=${results.size}")

        replaceWith(results)
    }

    fun clear() {
        Log.d("ListItemRecyclerViewAdapter", "${System.identityHashCode(this)} clear")
        filteredValues.clear()
        allValues.clear()
    }

    fun getVisibleListItems(): List<ListItem> {
        val list = mutableListOf<ListItem>()
        for (i in 0 until filteredValues.size()) {
            list.add(filteredValues.get(i))
        }
        return list
    }

    inner class ListItemSortedListCallback(private val comparator: Comparator<ListItem>) : SortedList.Callback<ListItem>() {
        override fun compare(o1: ListItem, o2: ListItem): Int {
            return comparator.compare(o1, o2)
        }

        override fun onInserted(position: Int, count: Int) {
            notifyItemRangeInserted(position, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            notifyItemRangeRemoved(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            notifyItemMoved(fromPosition, toPosition)
        }

        override fun onChanged(position: Int, count: Int) {
            notifyItemRangeChanged(position, count)
        }

        override fun areItemsTheSame(item1: ListItem, item2: ListItem): Boolean {
            return item1.id == item2.id
        }

        override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem == newItem
        }
    }

    class MyItemLookup(private val rv: RecyclerView) : ItemDetailsLookup<String>() {
        override fun getItemDetails(event: MotionEvent): ItemDetails<String>? {
            val view = rv.findChildViewUnder(event.x, event.y)
            if (view != null) {
                when (val viewHolder = rv.getChildViewHolder(view)) {
                    is CategoryViewHolder -> return viewHolder.getItemDetails()
                    is ItemViewHolder -> return viewHolder.getItemDetails()
                }
            }
            return null
        }
    }

    inner class MyItemKeyProvider: ItemKeyProvider<String>(SCOPE_MAPPED) {
        override fun getKey(position: Int): String = filteredValues[position].id ?: throw IllegalStateException()
        override fun getPosition(key: String): Int = filteredValues.indexOf(allValues.first { it.id == key })
    }

    inner class MySelectionPredicate: SelectionTracker.SelectionPredicate<String>() {
        override fun canSetStateForKey(key: String, nextState: Boolean): Boolean = searchQuery.isEmpty()
        override fun canSetStateAtPosition(position: Int, nextState: Boolean): Boolean = searchQuery.isEmpty()
        override fun canSelectMultiple(): Boolean = true
    }
}