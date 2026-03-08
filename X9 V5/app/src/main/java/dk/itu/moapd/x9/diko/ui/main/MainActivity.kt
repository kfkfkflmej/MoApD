package dk.itu.moapd.x9.diko.ui.main

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.ui.AppBarConfiguration
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import dk.itu.moapd.x9.diko.R
import com.google.android.material.snackbar.Snackbar

import dk.itu.moapd.x9.diko.data.ReportRepository
import dk.itu.moapd.x9.diko.databinding.ActivityMainBinding


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration


    /*
    private val reportLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    )
    { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Bundle? = result.data?.getBundleExtra(REPORT_SUCCESSFUL)

            var report_data: Report = Report("", "", "", "", "", "")
            val convertBundleToReport = report_data.convertBundleToReport(data!!)
            convertBundleToReport.let {
                ReportRepository.addReport(it)
            }
            setupRecyclerView()


            Snackbar.make(
                binding.root,
                "Report submitted",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
     */


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
        //setupRecyclerView()
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
    private fun setupNavigation(navController: androidx.navigation.NavController) {
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


