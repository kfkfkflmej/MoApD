package dk.itu.moapd.x9.diko.model

import android.os.Bundle

data class Report(
     val title: String,
     val location: String,
     val date: String,
     val type: String,
     val description: String,
     val severity: String
){  // Defines the Report data class.
    // It is used to store the report information and pass it between activities.

    /*fun isNotEmpty(): Boolean {
        return title.isNotEmpty() &&
                location.isNotEmpty() &&
                date.isNotEmpty() &&
                type.isNotEmpty() &&
                severity.isNotEmpty()
    }*/
    fun convertBundleToReport(bundle: Bundle): Report {
        return Report(
            title = bundle.getString("title", ""),
            location = bundle.getString("location", ""),
            date = bundle.getString("date", ""),
            type = bundle.getString("type", ""),
            description = bundle.getString("description", ""),
            severity = bundle.getString("severity", "")
        )
    }
    fun toBundle(): Bundle {
        val bundle = Bundle()
        bundle.putString("title", title)
        bundle.putString("location", location)
        bundle.putString("date", date)
        bundle.putString("type", type)
        bundle.putString("description", description)
        bundle.putString("severity", severity)
        return bundle
    }
}
