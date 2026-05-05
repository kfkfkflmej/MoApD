package dk.itu.moapd.x9.diko.ui.main.reportLog

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getString
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigationrail.NavigationRailView
import dk.itu.moapd.x9.diko.R
import dk.itu.moapd.x9.diko.data.ReportRepository
import dk.itu.moapd.x9.diko.databinding.FragmentMyListBinding
import dk.itu.moapd.x9.diko.ui.common.showSnackBar
import dk.itu.moapd.x9.diko.ui.list.CustomAdapter
import dk.itu.moapd.x9.diko.ui.list.SwipeToDeleteCallback
import dk.itu.moapd.x9.diko.ui.main.UserViewModel
import dk.itu.moapd.x9.diko.ui.main.report.ReportCardViewModel
import dk.itu.moapd.x9.diko.ui.main.report.ReportUpdateViewModel
import kotlin.getValue


private const val TAG = "MyReportList"

class MyListFragment : Fragment(R.layout.fragment_my_list) {
    // Defines the Report List Fragment.
    // Displays a list of all submitted reports.
    // Allows the user to add new reports by accessing the Report Activity.
    private lateinit var binding: FragmentMyListBinding
    private var adapter: CustomAdapter? = null
    private val repository by lazy { ReportRepository() }

    //private val reportViewModel: ReportCardViewModel by activityViewModels()
    private val reportViewModel: ReportUpdateViewModel by activityViewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ReportUpdateViewModel(repository) as T
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated() called")
        val userId = repository.currentUserId() ?: return

        val options = repository
            .reportOptionsByUser(userId, this)

        binding = FragmentMyListBinding.bind(view)
        adapter = CustomAdapter(
            emptyView = binding.emptyView,
            options = options
        )

        setupRecyclerView(requireNotNull(adapter))

        binding.fabAddReport.setOnClickListener {
            reportViewModel.clearLiveModel()
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
                .selectedItemId = R.id.fragment_report
        }

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
        adapter = null
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach() called")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView() called")
    }


    private fun setupRecyclerView(adapter: CustomAdapter) =
        // Sets up the RecyclerView for displaying the list of reports.
        // Based on the Fabricio's repo example
        binding.reportListView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = null
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            this.adapter = adapter

            val swipeHandler = object : SwipeToDeleteCallback() {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val pos = viewHolder.bindingAdapterPosition
                    val key = adapter.getRef(pos).key ?: return


                    if (direction == ItemTouchHelper.LEFT) {
                        repository.deleteReport(key = key)
                        viewHolder.itemView.showSnackBar(
                            getString(viewHolder.itemView.context, R.string.item_deleted)
                        )
                    } else if (direction == ItemTouchHelper.RIGHT) {
                        // Populate ViewModel for update

                        reportViewModel.loadReport(key)
                        // Navigate to ReportCardFragment
                        navigateTo(R.id.fragment_report)

                        // Restore the item in the list since it wasn't deleted
                        adapter.notifyItemChanged(pos)
                    }
                }
            }

            ItemTouchHelper(swipeHandler).attachToRecyclerView(this)

            ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
                val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
                view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = navBarHeight
                }
                insets
            }
        }

    private fun navigateTo(destinationId: Int) {
        val activity = requireActivity()
        // Try to find bottom nav (Portrait)
        activity.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.selectedItemId = destinationId
        // Try to find nav rail (Landscape)
        activity.findViewById<NavigationRailView>(R.id.navigation_rail)?.selectedItemId = destinationId
    }
}
