package dk.itu.moapd.x9.diko.ui.main.reportLog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dk.itu.moapd.x9.diko.R
import dk.itu.moapd.x9.diko.ui.tabs.ReportTabsAdapter

class ReportsTabsFragment : Fragment(R.layout.fragment_report_tabs) {

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)
            val tabLayout = view.findViewById<TabLayout>(R.id.log_tabs)

            val adapter = ReportTabsAdapter(this)
            viewPager.adapter = adapter
            viewPager.setCurrentItem(0, false)

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> "List"
                    1 -> "My reports"
                    2 -> "Calendar"
                    else -> ""
                }
            }.attach()
        }
}
