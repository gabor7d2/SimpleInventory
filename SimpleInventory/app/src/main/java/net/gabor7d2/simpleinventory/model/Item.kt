package net.gabor7d2.simpleinventory.model

data class Item(
    override val id: String?,
    override val name: String,
    val categoryId: String?,
    val parentId: String? = null,
    override val favourite: Boolean = false,
) : ListItem {
    override val listItemType: Int = ListItem.TYPE_ITEM
}