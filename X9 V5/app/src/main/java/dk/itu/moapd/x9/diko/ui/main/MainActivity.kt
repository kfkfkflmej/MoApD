package dk.itu.moapd.x9.diko.ui.main


import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import dk.itu.moapd.x9.diko.R

import dk.itu.moapd.x9.diko.databinding.ActivityMainBinding


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    // Defines the Launcher Activity and the main entry point of the app.
    // The activity sets the main parts of the UI and hosts the main fragments of the app.

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Log.d(TAG, "onCreate(Bundle?) called")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ViewCompat.setOnApplyWindowInsetsListener(binding.fragmentContainerView) { view, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

                view.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
                )

                insets
            }
        }

        setupNavigation()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    private fun setupNavigation() {
        // Set up UI interactive elements.
        // Portrait: bottom navigation.
        // Landscape: navigation rail.
        // Based on the Fabricio's repo example


        setSupportActionBar(binding.topAppBar)

        binding.topAppBar?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val navController = findNavController()
        appBarConfiguration = AppBarConfiguration(navController.graph)

        setupActionBarIfPortrait(navController)
        setupNavigation(navController)
    }

    /**
     * Returns the NavController from the NavHostFragment.
     */
    private fun findNavController(): NavController {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container_view) as NavHostFragment

        return navHostFragment.navController
    }

    /**
     * Connects BottomNavigationView (if present).
     */
    private fun setupNavigation(navController: NavController) {
        // Portrait: bottom navigation. Landscape: navigation rail.
        binding.bottomNavigation?.setupWithNavController(navController)
        binding.navigationRail?.setupWithNavController(navController)
    }

    private fun setupActionBarIfPortrait(navController: NavController) {
        if (resources.configuration.orientation != Configuration.ORIENTATION_PORTRAIT) return


        setSupportActionBar(binding.topAppBar)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController =
            (
                    supportFragmentManager.findFragmentById(R.id.fragment_container_view)
                            as NavHostFragment
                    ).navController
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}


