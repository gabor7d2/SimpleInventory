package net.gabor7d2.simpleinventory.model

interface ListItem {
    val listItemType: Int

    val id: String?

    val name: String

    val favourite: Boolean

    companion object {
        const val TYPE_CATEGORY = 1
        const val TYPE_ITEM = 2
    }
}