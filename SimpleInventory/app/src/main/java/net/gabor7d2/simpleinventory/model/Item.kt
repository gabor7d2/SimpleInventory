package net.gabor7d2.simpleinventory.model

import com.google.firebase.database.Exclude

data class Item(
    override val id: String? = null,
    override val name: String = "",
    val categoryId: String? = null,
    val parentId: String? = null,
    override val favourite: Boolean = false,
    val barcode: Int = 0
) : ListItem {
    @Exclude @get:Exclude
    override val listItemType: Int = ListItem.TYPE_ITEM
}