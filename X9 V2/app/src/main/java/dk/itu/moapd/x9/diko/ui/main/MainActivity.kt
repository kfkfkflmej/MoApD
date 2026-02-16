package dk.itu.moapd.x9.diko.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar

import dk.itu.moapd.x9.diko.R
import dk.itu.moapd.x9.diko.data.ReportRepository
import dk.itu.moapd.x9.diko.databinding.ActivityMainBinding
import dk.itu.moapd.x9.diko.model.Report
import dk.itu.moapd.x9.diko.ui.list.CustomAdapter
import java.util.Date
import java.util.Locale
import kotlin.toString


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    private val reportLauncher= registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    { result ->
        if (result.resultCode == RESULT_OK) {
            val data : Bundle? = result.data?.getBundleExtra(REPORT_SUCCESSFUL)

            var report_data : Report = Report("", "", "", "", "", "")
            val convertBundleToReport = report_data.convertBundleToReport(data!!)
            convertBundleToReport.let {
                ReportRepository.addReport(it)
            }
            setupListView()


            Snackbar.make(
                binding.root,
                "Report submitted",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Log.d(TAG, "onCreate(Bundle?) called")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
        setupUI()
        setupListView()
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
    private fun setupUI() =
        with(binding) {

            //Toolbar
            ViewCompat.setOnApplyWindowInsetsListener(appBarLayout) { view, insets ->
                val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())

                view.updatePadding(top = statusBarInsets.top)
                insets
            }
            topAppBar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }




            ViewCompat.setOnApplyWindowInsetsListener(fabAddReport) { view, insets ->
                val bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom

                view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = 16.dpToPx() + bottomInset
                }

                insets
            }

            fabAddReport.setOnClickListener {
                val intent = Intent(this@MainActivity, ReportCard::class.java)
                reportLauncher.launch(intent)
            }

        }

    private fun setupListView() {
        val data = ReportRepository.getReports()
        val adapter = CustomAdapter(
            this,
            R.layout.row_item,
            data
        )
        binding.reportListView.adapter = adapter

    }

    private fun requireContext(): Context {
        TODO("Not yet implemented")
    }
    private fun Int.dpToPx(): Int =
        (this * resources.displayMetrics.density).toInt()
}


