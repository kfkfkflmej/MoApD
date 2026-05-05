package dk.itu.moapd.x9.diko.ui.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dk.itu.moapd.x9.diko.R
import dk.itu.moapd.x9.diko.data.ReportRepository
import dk.itu.moapd.x9.diko.databinding.BottomSheetMapBinding
import dk.itu.moapd.x9.diko.databinding.BottomSheetReportsBinding

private const val TAG = "MapBottomSheet"

class MapBottomSheet : BottomSheetDialogFragment(R.layout.bottom_sheet_map) {

    private val repository by lazy { ReportRepository() }
    private var _binding: BottomSheetMapBinding? = null
    private val binding get() = _binding!!

    private var adapter: CustomAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val date = arguments?.getString("date") ?: ""


        val options = repository.reportOptionsAtDate(date, viewLifecycleOwner)
        adapter = CustomAdapter(options, binding.emptyView)

        binding.mapBottomSheetRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@MapBottomSheet.adapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    override fun onStart() {
        super.onStart()

        val bottomSheet = dialog?.findViewById<View>(
            com.google.android.material.R.id.design_bottom_sheet
        )

        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)

            val layoutParams = it.layoutParams
            layoutParams.height = (resources.displayMetrics.heightPixels * 0.6).toInt()
            it.layoutParams = layoutParams

            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        _binding = null
    }

}
