package net.gabor7d2.simpleinventory.model

data class Category(
    override val id: String?,
    val name: String,
    val parentId: String? = null,
    val favourite: Boolean = false,
) : ListItem {
    override val listItemType: Int = ListItem.TYPE_CATEGORY
}
