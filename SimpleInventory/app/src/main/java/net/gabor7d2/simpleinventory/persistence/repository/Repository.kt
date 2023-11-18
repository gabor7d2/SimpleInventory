package net.gabor7d2.simpleinventory.persistence.repository

import net.gabor7d2.simpleinventory.persistence.CollectionListener
import net.gabor7d2.simpleinventory.persistence.EntityListener
import net.gabor7d2.simpleinventory.model.Category
import net.gabor7d2.simpleinventory.model.Item
import java.util.concurrent.locks.ReentrantReadWriteLock

abstract class Repository {

    protected val lock = ReentrantReadWriteLock()

    protected val categoryListeners: MutableMap<String, MutableList<EntityListener<Category>>> = mutableMapOf()
    protected val itemListeners: MutableMap<String, MutableList<EntityListener<Item>>> = mutableMapOf()

    protected val categoryChildrenListeners: MutableMap<String?, MutableList<CollectionListener<Category>>> = mutableMapOf()
    protected val itemChildrenListeners: MutableMap<String?, MutableList<CollectionListener<Item>>> = mutableMapOf()
    protected val itemsOfCategoryListeners: MutableMap<String, MutableList<CollectionListener<Item>>> = mutableMapOf()

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


    abstract fun init()


    abstract fun getCategory(id: String): Category

    // returns root categories if id is null
    abstract fun getChildrenOfCategory(id: String?): List<Category>

    abstract fun getItemsOfCategory(id: String): List<Item>

    abstract fun addOrUpdateCategory(category: Category): Category

    abstract fun removeCategory(id: String)


    abstract fun getItem(id: String): Item

    // returns root items if id is null
    abstract fun getChildrenOfItem(id: String?): List<Item>

    abstract fun addOrUpdateItem(item: Item): Item

    abstract fun removeItem(id: String)
}