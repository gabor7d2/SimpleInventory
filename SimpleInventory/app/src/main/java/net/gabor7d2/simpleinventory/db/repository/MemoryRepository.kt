package net.gabor7d2.simpleinventory.db.repository

import net.gabor7d2.simpleinventory.model.Category
import net.gabor7d2.simpleinventory.model.Item

class MemoryRepository : Repository() {

    private val categories: MutableMap<String, Category> = mutableMapOf()
    private val items: MutableMap<String, Item> = mutableMapOf()

    // TODO create DbItem and DbCategory classes, generate ids

    override fun init() {
        categories["1"] = Category("1", "Food")
        categories["2"] = Category("2", "Electronic")
        categories["3"] = Category("3", "Book")

        items["1"] = Item("1", "Spicy Bread", "1")
        items["2"] = Item("2", "HDMI cable", "2")
        items["3"] = Item("3", "Isaac Asimov: Foundation Empire", "3")
    }

    override fun getCategory(id: String): Category {
        try {
            lock.readLock().lock()
            return categories[id] ?: throw IllegalArgumentException("Category with id $id not found")
        } finally {
            lock.readLock().unlock()
        }
    }

    override fun getChildrenOfCategory(id: String?): List<Category> {
        try {
            lock.readLock().lock()
            return categories.values.filter { it.parentId == id }
        } finally {
            lock.readLock().unlock()
        }
    }

    override fun getItemsOfCategory(id: String): List<Item> {
        try {
            lock.readLock().lock()
            return items.values.filter { it.categoryId == id }
        } finally {
            lock.readLock().unlock()
        }
    }

    override fun addOrUpdateCategory(category: Category) {
        try {
            lock.writeLock().lock()

            if (categories.containsKey(category.id)) {
                categoryChildrenListeners[category.parentId]?.forEach { it.onChanged(category) }
                categoryListeners[category.id]?.forEach { it.onChanged(category) }
            } else {
                categoryChildrenListeners[category.parentId]?.forEach { it.onAdded(category) }
            }

            categories[category.id] = category
        } finally {
            lock.writeLock().unlock()
        }
    }

    override fun removeCategory(id: String) {
        try {
            lock.writeLock().lock()

            if (categories.containsKey(id)) {
                val category = categories[id]!!
                categoryChildrenListeners[category.parentId]?.forEach { it.onRemoved(category) }
                categoryListeners[id]?.forEach { it.onRemoved(category) }
                categories.remove(id)
            }

            categories.remove(id)
        } finally {
            lock.writeLock().unlock()
        }
    }


    override fun getItem(id: String): Item {
        try {
            lock.readLock().lock()
            return items[id] ?: throw IllegalArgumentException("Item with id $id not found")
        } finally {
            lock.readLock().unlock()
        }
    }

    override fun getChildrenOfItem(id: String?): List<Item> {
        try {
            lock.readLock().lock()
            return items.values.filter { it.parentId == id }
        } finally {
            lock.readLock().unlock()
        }
    }

    override fun addOrUpdateItem(item: Item) {
        try {
            lock.writeLock().lock()

            if (items.containsKey(item.id)) {
                itemChildrenListeners[item.parentId]?.forEach { it.onAdded(item) }
                itemsOfCategoryListeners[item.categoryId]?.forEach { it.onAdded(item) }
                itemListeners[item.id]?.forEach { it.onChanged(item) }
            } else {
                itemChildrenListeners[item.parentId]?.forEach { it.onChanged(item) }
                itemsOfCategoryListeners[item.categoryId]?.forEach { it.onChanged(item) }
            }

            items[item.id] = item
        } finally {
            lock.writeLock().unlock()
        }
    }

    override fun removeItem(id: String) {
        try {
            lock.writeLock().lock()

            if (items.containsKey(id)) {
                val item = items[id]!!
                itemChildrenListeners[item.parentId]?.forEach { it.onRemoved(item) }
                itemsOfCategoryListeners[item.categoryId]?.forEach { it.onRemoved(item) }
                itemListeners[id]?.forEach { it.onRemoved(item) }
                items.remove(id)
            }
        } finally {
            lock.writeLock().unlock()
        }
    }
}