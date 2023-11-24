package net.gabor7d2.simpleinventory.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import net.gabor7d2.simpleinventory.R
import net.gabor7d2.simpleinventory.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    // TODO show barcode
    // TODO export barcodes

    // TODO update actionbar if name is edited
    // TODO splash screen

    // TODO generify stuff
    // TODO cleanup
    // TODO animations
    // TODO remember search query on back navigation

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
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}