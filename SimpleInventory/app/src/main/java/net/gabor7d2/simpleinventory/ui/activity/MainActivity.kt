package net.gabor7d2.simpleinventory.ui.activity

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavAction
import androidx.navigation.NavActionBuilder
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import net.gabor7d2.simpleinventory.R
import net.gabor7d2.simpleinventory.databinding.ActivityMainBinding
import net.gabor7d2.simpleinventory.model.Category
import net.gabor7d2.simpleinventory.ui.ListItemInteractListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    private val TAG = "MainActivity"

    private val database = Firebase.database("https://simpleinventory-27229-default-rtdb.europe-west1.firebasedatabase.app/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.categoriesFragment, R.id.homeFragment, R.id.itemsFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        navView.selectedItemId = R.id.homeFragment

        navView.setOnItemSelectedListener { item ->
            navController.popBackStack(R.id.homeFragment, inclusive = false)
            if (item.itemId != R.id.homeFragment) {
                navController.navigate(item.itemId)
            }
            true
        }

        //seedDatabase()
        listenToChanges()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    private fun seedDatabase() {
        val rootRef = database.reference

        rootRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.value
                Log.d(TAG, "Value is: $value")
                if (value != null) return

                rootRef.child("categories").setValue(
                    listOf(
                        mapOf(
                            "id" to "1",
                            "name" to "Food"
                        ),
                        mapOf(
                            "id" to "2",
                            "name" to "Electronic"
                        ),
                        mapOf(
                            "id" to "3",
                            "name" to "Book"
                        )
                    )
                )
                rootRef.child("items").setValue(
                    listOf(
                        mapOf(
                            "id" to "1",
                            "name" to "Bread",
                            "category" to mapOf(
                                "id" to "1",
                                "name" to "Food"
                            )
                        ),
                        mapOf(
                            "id" to "2",
                            "name" to "HDMI cable",
                            "category" to mapOf(
                                "id" to "2",
                                "name" to "Electronic"
                            )
                        ),
                        mapOf(
                            "id" to "3",
                            "name" to "Isaac Asimov: Foundation Empire",
                            "category" to mapOf(
                                "id" to "3",
                                "name" to "Book"
                            )
                        )
                    )
                )
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    private fun listenToChanges() {
        database.getReference("categories").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val category = snapshot.getValue<Category>()
                Log.d(TAG, "Category added: $category")
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val category = snapshot.getValue<Category>()
                Log.d(TAG, "Category changed: $category")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val category = snapshot.getValue<Category>()
                Log.d(TAG, "Category removed: $category")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                val category = snapshot.getValue<Category>()
                Log.d(TAG, "Category moved: $category")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Cancelled", error.toException())
            }

        })
    }
}