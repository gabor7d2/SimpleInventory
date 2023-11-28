package net.gabor7d2.simpleinventory.model

import com.google.firebase.database.Exclude

data class Category(
    override val id: String? = null,
    override val name: String = "",
    val parentId: String? = null,
    override val favourite: Boolean = false,
) : ListItem {
    @Exclude @get:Exclude
    override val listItemType: Int = ListItem.TYPE_CATEGORY
}
