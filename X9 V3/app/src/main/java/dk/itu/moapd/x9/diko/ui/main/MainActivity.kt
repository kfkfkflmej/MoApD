package dk.itu.moapd.x9.diko.ui.main

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import dk.itu.moapd.x9.diko.R
import com.google.android.material.snackbar.Snackbar

import dk.itu.moapd.x9.diko.data.ReportRepository
import dk.itu.moapd.x9.diko.databinding.ActivityFragmentMainBinding
import dk.itu.moapd.x9.diko.databinding.ActivityMainBinding
import dk.itu.moapd.x9.diko.model.Report
import dk.itu.moapd.x9.diko.ui.list.CustomAdapter
import dk.itu.moapd.x9.diko.ui.report.REPORT_SUCCESSFUL
import dk.itu.moapd.x9.diko.ui.report.ReportCard


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityFragmentMainBinding
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

        binding = ActivityFragmentMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val navController = findNavController()
        appBarConfiguration = AppBarConfiguration(navController.graph)

        setupActionBarWithNavController(navController, appBarConfiguration)
        setupBottomNavigation(navController)
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
    private fun setupBottomNavigation(navController: NavController) {
        binding.bottomNavigation?.setupWithNavController(navController)
    }

}


