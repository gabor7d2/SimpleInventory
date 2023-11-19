package net.gabor7d2.simpleinventory.persistence.repository

import net.gabor7d2.simpleinventory.model.Category
import net.gabor7d2.simpleinventory.model.Item
import java.util.UUID

open class MemoryRepository : Repository() {

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


    override fun doAddCategory(category: Category): Category {
        val newCategory = category.copy(id = UUID.randomUUID().toString())
        notifyAddCategory(newCategory)
        categories[newCategory.id!!] = newCategory
        return newCategory
    }

    override fun doUpdateCategory(category: Category) {
        notifyUpdateCategory(category)
        categories[category.id!!] = category
    }

    override fun doRemoveCategory(id: String) {
        notifyRemoveCategory(categories[id]!!)
        categories.remove(id)
    }


    override fun doAddItem(item: Item): Item {
        val newItem = item.copy(id = UUID.randomUUID().toString())
        notifyAddItem(newItem)
        items[newItem.id!!] = newItem
        return newItem
    }

    override fun doUpdateItem(item: Item) {
        notifyUpdateItem(item)
        items[item.id!!] = item
    }

    override fun doRemoveItem(id: String) {
        notifyRemoveItem(items[id]!!)
        items.remove(id)
    }
}