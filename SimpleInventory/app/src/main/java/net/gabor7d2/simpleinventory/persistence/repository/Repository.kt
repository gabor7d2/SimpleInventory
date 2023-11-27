package net.gabor7d2.simpleinventory.persistence.repository

import net.gabor7d2.simpleinventory.persistence.CollectionListener
import net.gabor7d2.simpleinventory.persistence.EntityListener
import net.gabor7d2.simpleinventory.model.Category
import net.gabor7d2.simpleinventory.model.Item
import net.gabor7d2.simpleinventory.model.ListItem
import java.util.concurrent.locks.ReentrantReadWriteLock

abstract class Repository {

    companion object {
        val BARCODE_LENGTH = 8
    }

    protected val lock = ReentrantReadWriteLock()

    protected val categories: MutableMap<String, Category> = mutableMapOf()
    protected val items: MutableMap<String, Item> = mutableMapOf()

    protected val categoryListeners: MutableMap<String, MutableList<EntityListener<Category>>> = mutableMapOf()
    protected val itemListeners: MutableMap<String, MutableList<EntityListener<Item>>> = mutableMapOf()

    protected val categoryChildrenListeners: MutableMap<String?, MutableList<CollectionListener<Category>>> = mutableMapOf()
    protected val itemChildrenListeners: MutableMap<String?, MutableList<CollectionListener<Item>>> = mutableMapOf()
    protected val itemsOfCategoryListeners: MutableMap<String, MutableList<CollectionListener<Item>>> = mutableMapOf()

    protected val favouritesListeners: MutableList<CollectionListener<ListItem>> = mutableListOf()
    protected val globalListeners: MutableList<CollectionListener<ListItem>> = mutableListOf()

    fun addCategoryListener(categoryId: String, listener: EntityListener<Category>) {
        if (categoryListeners[categoryId] == null) {
            categoryListeners[categoryId] = mutableListOf()
        }
        categoryListeners[categoryId]!!.add(listener)

        try {
            lock.readLock().lock()
            listener.onChanged(getCategory(categoryId))
        } finally {
            lock.readLock().unlock()
        }
    }

    fun removeCategoryListener(listener: EntityListener<Category>) {
        categoryListeners.forEach { it.value.remove(listener) }
    }

    fun addItemListener(itemId: String, listener: EntityListener<Item>) {
        if (itemListeners[itemId] == null) {
            itemListeners[itemId] = mutableListOf()
        }
        itemListeners[itemId]!!.add(listener)

        try {
            lock.readLock().lock()
            listener.onChanged(getItem(itemId))
        } finally {
            lock.readLock().unlock()
        }
    }

    fun removeItemListener(listener: EntityListener<Item>) {
        itemListeners.forEach { it.value.remove(listener) }
    }

    fun addCategoryChildrenListener(categoryId: String?, listener: CollectionListener<Category>) {
        if (categoryChildrenListeners[categoryId] == null) {
            categoryChildrenListeners[categoryId] = mutableListOf()
        }
        categoryChildrenListeners[categoryId]!!.add(listener)

        try {
            lock.readLock().lock()
            getChildrenOfCategory(categoryId).forEach { listener.onAdded(it) }
        } finally {
            lock.readLock().unlock()
        }
    }

    fun removeCategoryChildrenListener(listener: CollectionListener<Category>) {
        categoryChildrenListeners.forEach { it.value.remove(listener) }
    }

    fun addItemChildrenListener(itemId: String?, listener: CollectionListener<Item>) {
        if (itemChildrenListeners[itemId] == null) {
            itemChildrenListeners[itemId] = mutableListOf()
        }
        itemChildrenListeners[itemId]!!.add(listener)

        try {
            lock.readLock().lock()
            getChildrenOfItem(itemId).forEach { listener.onAdded(it) }
        } finally {
            lock.readLock().unlock()
        }
    }

    fun removeItemChildrenListener(listener: CollectionListener<Item>) {
        itemChildrenListeners.forEach { it.value.remove(listener) }
    }

    fun addItemsOfCategoryListener(categoryId: String, listener: CollectionListener<Item>) {
        if (itemsOfCategoryListeners[categoryId] == null) {
            itemsOfCategoryListeners[categoryId] = mutableListOf()
        }
        itemsOfCategoryListeners[categoryId]!!.add(listener)

        try {
            lock.readLock().lock()
            getItemsOfCategory(categoryId).forEach { listener.onAdded(it) }
        } finally {
            lock.readLock().unlock()
        }
    }

    fun removeItemsOfCategoryListener(listener: CollectionListener<Item>) {
        itemsOfCategoryListeners.forEach { it.value.remove(listener) }
    }

    fun addFavouritesListener(listener: CollectionListener<ListItem>) {
        favouritesListeners.add(listener)

        try {
            lock.readLock().lock()
            getFavourites().forEach { listener.onAdded(it) }
        } finally {
            lock.readLock().unlock()
        }
    }

    fun removeFavouritesListener(listener: CollectionListener<ListItem>) {
        favouritesListeners.remove(listener)
    }

    fun addGlobalListener(listener: CollectionListener<ListItem>) {
        globalListeners.add(listener)

        try {
            lock.readLock().lock()
            getAllCategories().forEach { listener.onAdded(it) }
            getAllItems().forEach { listener.onAdded(it) }
        } finally {
            lock.readLock().unlock()
        }
    }

    fun removeGlobalListener(listener: CollectionListener<ListItem>) {
        globalListeners.remove(listener)
    }


    abstract fun init()

    private fun <T> withReadLock(block: () -> T): T {
        try {
            lock.readLock().lock()
            return block()
        } finally {
            lock.readLock().unlock()
        }
    }

    private fun <T> withWriteLock(block: () -> T): T {
        try {
            lock.writeLock().lock()
            return block()
        } finally {
            lock.writeLock().unlock()
        }
    }


    fun getCategory(id: String): Category {
        return withReadLock { categories[id] ?: throw IllegalArgumentException("Category with id $id not found") }
    }

    fun getAllCategories(): List<Category> {
        return withReadLock { categories.values.toList() }
    }

    fun searchCategories(name: String): List<Category> {
        return withReadLock { categories.values.filter { it.name.contains(name, ignoreCase = true) }.sortedBy { it.name } }
    }

    // returns root categories if id is null
    fun getChildrenOfCategory(id: String?): List<Category> {
        return withReadLock { categories.values.filter { it.parentId == id } }
    }

    fun getItemsOfCategory(id: String): List<Item> {
        return withReadLock { items.values.filter { it.categoryId == id } }
    }


    fun addOrUpdateCategory(category: Category): Category {
        return withWriteLock {
            if (category.id != null && categories.containsKey(category.id)) {
                if (categories[category.id] == category) return@withWriteLock category
                doUpdateCategory(category)
                category
            } else {
                doAddCategory(category)
            }
        }
    }

    protected abstract fun doAddCategory(category: Category): Category

    protected abstract fun doUpdateCategory(category: Category)

    protected fun notifyAddCategory(newCategory: Category) {
        categoryChildrenListeners[newCategory.parentId]?.forEach { it.onAdded(newCategory) }
        globalListeners.forEach { it.onAdded(newCategory) }

        if (newCategory.favourite) {
            favouritesListeners.forEach { it.onAdded(newCategory) }
        }
    }

    protected fun notifyUpdateCategory(newCategory: Category) {
        val oldCategory = categories[newCategory.id]!!

        if (oldCategory.parentId != newCategory.parentId) {
            categoryChildrenListeners[oldCategory.parentId]?.forEach { it.onRemoved(oldCategory) }
            categoryChildrenListeners[newCategory.parentId]?.forEach { it.onAdded(newCategory) }
        } else {
            categoryChildrenListeners[newCategory.parentId]?.forEach { it.onChanged(newCategory) }
        }

        if (oldCategory.favourite != newCategory.favourite) {
            if (newCategory.favourite) {
                favouritesListeners.forEach { it.onAdded(newCategory) }
            } else {
                favouritesListeners.forEach { it.onRemoved(newCategory) }
            }
        }

        categoryListeners[newCategory.id]?.forEach { it.onChanged(newCategory) }
        globalListeners.forEach { it.onChanged(newCategory) }
    }


    fun removeCategory(id: String) {
        withWriteLock {
            if (categories.containsKey(id)) {
                doRemoveCategory(id)

                for (child in getChildrenOfCategory(id)) {
                    val newCategory = child.copy(parentId = null)
                    addOrUpdateCategory(newCategory)
                }

                for (item in getItemsOfCategory(id)) {
                    val newItem = item.copy(categoryId = null)
                    addOrUpdateItem(newItem)
                }
            }
        }
    }

    protected abstract fun doRemoveCategory(id: String)

    protected fun notifyRemoveCategory(category: Category) {
        categoryChildrenListeners[category.parentId]?.forEach { it.onRemoved(category) }
        categoryListeners[category.id]?.forEach { it.onRemoved(category) }

        globalListeners.forEach { it.onRemoved(category) }
        if (category.favourite) {
            favouritesListeners.forEach { it.onRemoved(category) }
        }
    }



    fun getItem(id: String): Item {
        return withReadLock { items[id] ?: throw IllegalArgumentException("Item with id $id not found") }
    }

    fun getItemByBarcode(barcode: Int): Item? {
        return withReadLock { items.values.find { it.barcode == barcode } }
    }

    fun getAllItems(): List<Item> {
        return withReadLock { items.values.toList() }
    }

    fun searchItems(name: String): List<Item> {
        return withReadLock { items.values.filter { it.name.contains(name, ignoreCase = true) }.sortedBy { it.name } }
    }

    // returns root items if id is null
    fun getChildrenOfItem(id: String?): List<Item> {
        return withReadLock { items.values.filter { it.parentId == id } }
    }


    fun addOrUpdateItem(item: Item): Item {
        return withWriteLock {
            if (item.id != null && items.containsKey(item.id)) {
                if (items[item.id] == item) return@withWriteLock item
                doUpdateItem(item)
                item
            } else {
                doAddItem(item)
            }
        }
    }

    protected abstract fun doAddItem(item: Item): Item

    protected abstract fun doUpdateItem(item: Item)

    protected fun notifyAddItem(newItem: Item) {
        itemChildrenListeners[newItem.parentId]?.forEach { it.onAdded(newItem) }
        itemsOfCategoryListeners[newItem.categoryId]?.forEach { it.onAdded(newItem) }
        globalListeners.forEach { it.onAdded(newItem) }

        if (newItem.favourite) {
            favouritesListeners.forEach { it.onAdded(newItem) }
        }
    }

    protected fun notifyUpdateItem(newItem: Item) {
        val oldItem = items[newItem.id]!!

        if (oldItem.parentId != newItem.parentId) {
            itemChildrenListeners[oldItem.parentId]?.forEach { it.onRemoved(oldItem) }
            itemChildrenListeners[newItem.parentId]?.forEach { it.onAdded(newItem) }
        } else {
            itemChildrenListeners[newItem.parentId]?.forEach { it.onChanged(newItem) }
        }

        if (oldItem.categoryId != newItem.categoryId) {
            itemsOfCategoryListeners[oldItem.categoryId]?.forEach { it.onRemoved(oldItem) }
            itemsOfCategoryListeners[newItem.categoryId]?.forEach { it.onAdded(newItem) }
        } else {
            itemsOfCategoryListeners[newItem.categoryId]?.forEach { it.onChanged(newItem) }
        }

        if (oldItem.favourite != newItem.favourite) {
            if (newItem.favourite) {
                favouritesListeners.forEach { it.onAdded(newItem) }
            } else {
                favouritesListeners.forEach { it.onRemoved(newItem) }
            }
        }

        itemListeners[newItem.id]?.forEach { it.onChanged(newItem) }
        globalListeners.forEach { it.onChanged(newItem) }
    }


    fun removeItem(id: String) {
        withWriteLock {
            if (items.containsKey(id)) {
                doRemoveItem(id)

                for (child in getChildrenOfItem(id)) {
                    val newItem = child.copy(parentId = null)
                    addOrUpdateItem(newItem)
                }
            }
        }
    }

    protected abstract fun doRemoveItem(id: String)

    protected fun notifyRemoveItem(item: Item) {
        itemChildrenListeners[item.parentId]?.forEach { it.onRemoved(item) }
        itemsOfCategoryListeners[item.categoryId]?.forEach { it.onRemoved(item) }
        itemListeners[item.id]?.forEach { it.onRemoved(item) }

        globalListeners.forEach { it.onRemoved(item) }
        if (item.favourite) {
            favouritesListeners.forEach { it.onRemoved(item) }
        }
    }


    fun getFavourites(): List<ListItem> {
        return withReadLock {
            categories.values.filter { it.favourite } + items.values.filter { it.favourite }
        }
    }



    fun favouriteCategory(category: Category, favourite: Boolean) {
        addOrUpdateCategory(category.copy(favourite = favourite))
    }

    fun favouriteCategory(categoryId: String, favourite: Boolean) {
        withWriteLock { favouriteCategory(getCategory(categoryId), favourite) }
    }

    fun renameCategory(category: Category, newName: String) {
        addOrUpdateCategory(category.copy(name = newName))
    }

    fun changeCategoryParent(category: Category, newParentId: String?) {
        addOrUpdateCategory(category.copy(parentId = newParentId))
    }

    fun favouriteItem(item: Item, favourite: Boolean) {
        addOrUpdateItem(item.copy(favourite = favourite))
    }

    fun favouriteItem(itemId: String, favourite: Boolean) {
        withWriteLock { favouriteItem(getItem(itemId), favourite) }
    }

    fun renameItem(item: Item, newName: String) {
        addOrUpdateItem(item.copy(name = newName))
    }

    fun changeItemParent(item: Item, newParentId: String?) {
        addOrUpdateItem(item.copy(parentId = newParentId))
    }

    fun changeItemCategory(item: Item, newCategoryId: String?) {
        addOrUpdateItem(item.copy(categoryId = newCategoryId))
    }
}