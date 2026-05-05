package dk.itu.moapd.x9.diko.ui.tabs

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import dk.itu.moapd.x9.diko.ui.main.reportLog.CalendarFragment
import dk.itu.moapd.x9.diko.ui.main.reportLog.MyListFragment
import dk.itu.moapd.x9.diko.ui.main.reportLog.ReportListFragment

class ReportTabsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ReportListFragment()
            1 -> MyListFragment()
            2 -> CalendarFragment()
            else -> ReportListFragment()
        }
    }
}