package net.gabor7d2.simpleinventory.persistence.repository

import net.gabor7d2.simpleinventory.model.Category
import net.gabor7d2.simpleinventory.model.Item
import net.gabor7d2.simpleinventory.model.ListItem
import java.util.UUID

class MemoryRepository : Repository() {

    private val categories: MutableMap<String, Category> = mutableMapOf()
    private val items: MutableMap<String, Item> = mutableMapOf()

    override fun init() {
        categories["1002"] = Category("1002", "Food")
        categories["1003"] = Category("1003", "Electronic", favourite = true)
        categories["1004"] = Category("1004", "Book")
        categories["1005"] = Category("1005", "Boxes", favourite = true)

        items["1"] = Item("1", "Spicy Bread", "1002", "4")
        items["2"] = Item("2", "DisplayPort cable", "1003")
        items["3"] = Item("3", "Isaac Asimov: Foundation Empire", "1004", "4", true)
        items["4"] = Item("4", "Box #1", "1005")
    }

    override fun getCategory(id: String): Category {
        try {
            lock.readLock().lock()
            return categories[id] ?: throw IllegalArgumentException("Category with id $id not found")
        } finally {
            lock.readLock().unlock()
        }
    }

    override fun getAllCategories(): List<Category> {
        return categories.values.toList()
    }

    override fun searchCategories(name: String): List<Category> {
        return categories.values.filter { it.name.contains(name, ignoreCase = true) }
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

    override fun addOrUpdateCategory(category: Category): Category {
        try {
            lock.writeLock().lock()

            if (category.id != null && categories.containsKey(category.id)) {
                val oldCategory = categories[category.id]!!

                if (oldCategory.parentId != category.parentId) {
                    categoryChildrenListeners[oldCategory.parentId]?.forEach { it.onRemoved(category) }
                    categoryChildrenListeners[category.parentId]?.forEach { it.onAdded(category) }
                } else {
                    categoryChildrenListeners[category.parentId]?.forEach { it.onChanged(category) }
                }

                if (oldCategory.favourite != category.favourite) {
                    if (category.favourite) {
                        favouritesListeners.forEach { it.onAdded(category) }
                    } else {
                        favouritesListeners.forEach { it.onRemoved(category) }
                    }
                }

                categoryListeners[category.id]?.forEach { it.onChanged(category) }
                globalListeners.forEach { it.onChanged(category) }
                categories[category.id] = category
                return category
            } else {
                val newCategory =
                    if (category.id != null) category
                    else category.copy(id = UUID.randomUUID().toString())
                categoryChildrenListeners[newCategory.parentId]?.forEach { it.onAdded(newCategory) }
                globalListeners.forEach { it.onAdded(newCategory) }
                categories[newCategory.id!!] = newCategory
                return newCategory
            }
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

                if (category.favourite) {
                    favouritesListeners.forEach { it.onRemoved(category) }
                }
                globalListeners.forEach { it.onRemoved(category) }

                categories.remove(id)

                for (item in getItemsOfCategory(id)) {
                    val newItem = item.copy(categoryId = null)
                    addOrUpdateItem(newItem)
                }

                for (child in getChildrenOfCategory(id)) {
                    val newCategory = child.copy(parentId = null)
                    addOrUpdateCategory(newCategory)
                }
            }
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

    override fun getAllItems(): List<Item> {
        return items.values.toList()
    }

    override fun searchItems(name: String): List<Item> {
        return items.values.filter { it.name.contains(name, ignoreCase = true) }
    }

    override fun getChildrenOfItem(id: String?): List<Item> {
        try {
            lock.readLock().lock()
            return items.values.filter { it.parentId == id }
        } finally {
            lock.readLock().unlock()
        }
    }

    override fun addOrUpdateItem(item: Item): Item {
        try {
            lock.writeLock().lock()

            if (item.id != null && items.containsKey(item.id)) {
                val oldItem = items[item.id]!!

                if (oldItem.parentId != item.parentId) {
                    itemChildrenListeners[oldItem.parentId]?.forEach { it.onRemoved(item) }
                    itemChildrenListeners[item.parentId]?.forEach { it.onAdded(item) }
                } else {
                    itemChildrenListeners[item.parentId]?.forEach { it.onChanged(item) }
                }

                if (oldItem.categoryId != item.categoryId) {
                    itemsOfCategoryListeners[oldItem.categoryId]?.forEach { it.onRemoved(item) }
                    itemsOfCategoryListeners[item.categoryId]?.forEach { it.onAdded(item) }
                } else {
                    itemsOfCategoryListeners[item.categoryId]?.forEach { it.onChanged(item) }
                }

                if (oldItem.favourite != item.favourite) {
                    if (item.favourite) {
                        favouritesListeners.forEach { it.onAdded(item) }
                    } else {
                        favouritesListeners.forEach { it.onRemoved(item) }
                    }
                }

                itemListeners[item.id]?.forEach { it.onChanged(item) }
                globalListeners.forEach { it.onChanged(item) }
                items[item.id] = item
                return item
            } else {
                val newItem =
                    if (item.id != null) item
                    else item.copy(id = UUID.randomUUID().toString())
                itemChildrenListeners[newItem.parentId]?.forEach { it.onAdded(newItem) }
                itemsOfCategoryListeners[newItem.categoryId]?.forEach { it.onAdded(newItem) }
                globalListeners.forEach { it.onAdded(newItem) }
                items[newItem.id!!] = newItem
                return newItem
            }
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

                if (item.favourite) {
                    favouritesListeners.forEach { it.onRemoved(item) }
                }
                globalListeners.forEach { it.onRemoved(item) }
                items.remove(id)

                for (child in getChildrenOfItem(id)) {
                    val newItem = child.copy(parentId = null)
                    addOrUpdateItem(newItem)
                }
            }
        } finally {
            lock.writeLock().unlock()
        }
    }

    override fun getFavourites(): List<ListItem> {
        try {
            lock.readLock().lock()
            return categories.values.filter { it.favourite } + items.values.filter { it.favourite }
        } finally {
            lock.readLock().unlock()
        }
    }
}