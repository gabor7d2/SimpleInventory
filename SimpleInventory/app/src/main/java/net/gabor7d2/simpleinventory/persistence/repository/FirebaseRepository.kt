package net.gabor7d2.simpleinventory.persistence.repository

import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import net.gabor7d2.simpleinventory.model.Category
import net.gabor7d2.simpleinventory.model.Item

class FirebaseRepository : Repository() {

    companion object {
        val CATEGORIES_PATH = "categories"
        val ITEMS_PATH = "items"
        val ITEM_COUNTER_PATH = "itemCounter"
    }

    private val database = Firebase.database("https://simpleinventory-27229-default-rtdb.europe-west1.firebasedatabase.app/")

    override fun init() {
        database.setPersistenceEnabled(true)

        database.getReference(CATEGORIES_PATH).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val category = snapshot.getValue<Category>()
                if (category != null) {
                    notifyAddCategory(category)
                    if (!categories.containsKey(category.id!!)) {
                        categories[category.id] = category
                    }
                }
                Log.d("FirebaseRepository", "Category added: $category")
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val category = snapshot.getValue<Category>()
                if (category != null) {
                    notifyUpdateCategory(category)
                    categories[category.id!!] = category
                }
                Log.d("FirebaseRepository", "Category changed: $category")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val category = snapshot.getValue<Category>()
                if (category != null) {
                    notifyRemoveCategory(category)
                    categories.remove(category.id!!)
                }
                Log.d("FirebaseRepository", "Category removed: $category")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {
                Log.w("FirebaseRepository", "CategoryListener Cancelled", error.toException())
            }
        })

        database.getReference(ITEMS_PATH).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val item = snapshot.getValue<Item>()
                if (item != null) {
                    notifyAddItem(item)
                    if (!items.containsKey(item.id!!)) {
                        items[item.id] = item
                    }
                }
                Log.d("FirebaseRepository", "Item added: $item")
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val item = snapshot.getValue<Item>()
                if (item != null) {
                    notifyUpdateItem(item)
                    items[item.id!!] = item
                }
                Log.d("FirebaseRepository", "Item changed: $item")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val item = snapshot.getValue<Item>()
                if (item != null) {
                    notifyRemoveItem(item)
                    items.remove(item.id!!)
                }
                Log.d("FirebaseRepository", "Item removed: $item")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {
                Log.w("FirebaseRepository", "ItemListener Cancelled", error.toException())
            }
        })
    }

    override fun doAddCategory(category: Category): Category {
        val newCategory = category.copy(id = database.getReference(CATEGORIES_PATH).push().key!!)
        database.getReference(CATEGORIES_PATH).child(newCategory.id!!).setValue(newCategory)
        categories[newCategory.id] = newCategory
        return newCategory
    }

    override fun doUpdateCategory(category: Category) {
        database.getReference(CATEGORIES_PATH).child(category.id!!).setValue(category)
    }

    override fun doRemoveCategory(id: String) {
        database.getReference(CATEGORIES_PATH).child(id).removeValue()
    }


    override fun doAddItem(item: Item): Item {
        val newItem = item.copy(id = database.getReference(ITEMS_PATH).push().key!!)
        database.getReference(ITEMS_PATH).child(newItem.id!!).setValue(newItem)
        items[newItem.id] = newItem
        setItemBarcode(newItem)
        return newItem
    }

    override fun doUpdateItem(item: Item) {
        database.getReference(ITEMS_PATH).child(item.id!!).setValue(item)
    }

    override fun doRemoveItem(id: String) {
        database.getReference(ITEMS_PATH).child(id).removeValue()
    }

    private fun setItemBarcode(item: Item) {
        database.getReference(ITEM_COUNTER_PATH).runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val counter = mutableData.getValue<Int>() ?: 1000

                mutableData.value = counter + 1
                database.getReference(ITEMS_PATH).child(item.id!!).child("barcode").setValue(counter)
                return Transaction.success(mutableData)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?,
            ) {
                // Transaction completed
                Log.d("FirebaseRepository", "postTransaction:onComplete:$databaseError")
            }
        })
    }
}