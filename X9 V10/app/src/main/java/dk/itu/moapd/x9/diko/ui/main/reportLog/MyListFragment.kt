package dk.itu.moapd.x9.diko.ui.main.reportLog

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.ContextCompat.getString
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigationrail.NavigationRailView
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.x9.diko.R
import dk.itu.moapd.x9.diko.data.ReportRepository
import dk.itu.moapd.x9.diko.data.UserRepository
import dk.itu.moapd.x9.diko.databinding.FragmentMyListBinding
import dk.itu.moapd.x9.diko.model.Report
import dk.itu.moapd.x9.diko.ui.common.showSnackBar
import dk.itu.moapd.x9.diko.ui.list.CustomAdapter
import dk.itu.moapd.x9.diko.ui.list.ReportItemLongClickListener
import dk.itu.moapd.x9.diko.ui.list.SwipeActionHandler
import dk.itu.moapd.x9.diko.ui.main.UserViewModel
import dk.itu.moapd.x9.diko.ui.main.report.ReportUpdateViewModel
import kotlin.getValue


private const val TAG = "MyReportList"

class MyListFragment : Fragment(R.layout.fragment_my_list), ReportItemLongClickListener {
    /**
     * Defines the My Reports Tab.
     * Displays all reports submitted by the current user and allows the user to delete or update reports.
     * Written mostly by me with some help from AI agent for the swipeHandler.
     */
    private lateinit var binding: FragmentMyListBinding
    private var adapter: CustomAdapter? = null
    private val userRepository by lazy { UserRepository() }
    private val repository by lazy { ReportRepository() }

    private val reportViewModel: ReportUpdateViewModel by activityViewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ReportUpdateViewModel(repository) as T
            }
        }
    }

    private val userViewModel: UserViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return UserViewModel(userRepository) as T
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated() called")
        binding = FragmentMyListBinding.bind(view)

        val userId = userRepository.currentUserId() ?: return
        userViewModel.loadUser(userId)

        val options = repository
            .reportOptionsByUser(userId, this)

        adapter = CustomAdapter(
            emptyView = binding.emptyView,
            options = options,
            itemLongClickListener = this
        )

        setupRecyclerView(requireNotNull(adapter))

        userViewModel.username.observe(viewLifecycleOwner) {
            updateUserUI()
        }
        userViewModel.email.observe(viewLifecycleOwner) {
            updateUserUI()
        }

        binding.fabAddReport.setOnClickListener {
            reportViewModel.clearLiveModel()
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
                .selectedItemId = R.id.report_flow_graph
        }

    }

    override fun onItemLongClick(report: Report, position: Int) {
        // Creates a dialog to display the report details
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(report.title)
            .setMessage(report.description)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    private fun updateUserUI() {
        val username = userViewModel.username.value
        val email = userViewModel.email.value

        when (username) {
            "Guest" if email == "Guest" -> {
                binding.textGuests.visibility = View.VISIBLE
                binding.anonymousMessage.visibility = View.GONE
                binding.reportListView.visibility = View.GONE
                binding.fabAddReport.visibility = View.GONE
            }
            "Guest" -> {
                binding.textGuests.visibility = View.GONE
                binding.anonymousMessage.visibility = View.VISIBLE
                binding.reportListView.visibility = View.GONE
                binding.fabAddReport.visibility = View.GONE
            }
            else -> {
                binding.textGuests.visibility = View.GONE
                binding.anonymousMessage.visibility = View.GONE
                binding.reportListView.visibility = View.VISIBLE
                binding.fabAddReport.visibility = View.VISIBLE
                // emptyView visibility is handled by the adapter
            }
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
        binding.reportListView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            // Use DefaultItemAnimator to handle swipe return animations
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            this.adapter = adapter

            val swipeHandler = object : SwipeActionHandler() {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val pos = viewHolder.bindingAdapterPosition
                    if (pos == RecyclerView.NO_POSITION) return
                    
                    // Capture the report data IMMEDIATELY to avoid issues when the snackbar dismisses
                    val report = adapter.getItem(pos)
                    val key = adapter.getRef(pos).key ?: return
                    val imageRef = report.imageRef

                    if (direction == ItemTouchHelper.LEFT) {
                        viewHolder.itemView.showSnackBar(
                            message = getString(viewHolder.itemView.context, R.string.item_deleted),
                            duration = Snackbar.LENGTH_LONG,
                            actionText = getString(viewHolder.itemView.context, R.string.undo),
                            action = {
                                // Restore the item in the list by notifying the adapter
                                adapter.notifyItemChanged(pos)
                            },
                            onDismissed = {
                                // Delete from Firebase using captured values
                                repository.deleteReport(key = key, imageRef = imageRef)
                            }
                        )
                    } else if (direction == ItemTouchHelper.RIGHT) {
                        // Populate ViewModel for update
                        reportViewModel.loadReport(key) {
                            // Navigate to ReportCardFragment only AFTER data is loaded
                            navigateTo(R.id.report_flow_graph)
                        }

                        // Restore the item in the list since it wasn't deleted
                        adapter.notifyItemChanged(pos)
                    }
                }
            }

            ItemTouchHelper(swipeHandler).attachToRecyclerView(this)

        }

    private fun navigateTo(destinationId: Int) {
        val activity = requireActivity()
        // Try to find bottom nav (Portrait)
        activity.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.selectedItemId = destinationId
        // Try to find nav rail (Landscape)
        activity.findViewById<NavigationRailView>(R.id.navigation_rail)?.selectedItemId = destinationId
    }
}
