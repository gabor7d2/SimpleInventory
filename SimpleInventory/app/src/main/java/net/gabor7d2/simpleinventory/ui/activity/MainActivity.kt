package net.gabor7d2.simpleinventory.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import net.gabor7d2.simpleinventory.R
import net.gabor7d2.simpleinventory.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    // TODO export barcodes

    // TODO fix details fragment argument

    // TODO app icon
    // TODO navbar icons
    // TODO splash screen
    // TODO logout
    // TODO show dialog if user registered
    // TODO delete confirmation dialog
    // TODO generify stuff
    // TODO cleanup
    // TODO animations
    // TODO remember search query on back navigation
    // TODO material 3

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
                navController.navigate(item.itemId, null, navOptions {
                    anim {
                        enter = R.anim.fade_in
                        exit = R.anim.fade_out
                        popEnter = R.anim.fade_in
                        popExit = R.anim.fade_out
                    }
                })
            }
            true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}