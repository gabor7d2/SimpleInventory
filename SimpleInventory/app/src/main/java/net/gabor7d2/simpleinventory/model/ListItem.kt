package net.gabor7d2.simpleinventory.model

import com.google.firebase.database.Exclude

interface ListItem {
    @get:Exclude
    val listItemType: Int

    val id: String?

    val name: String

    val favourite: Boolean

    companion object {
        const val TYPE_CATEGORY = 1
        const val TYPE_ITEM = 2
    }
}