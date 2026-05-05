package dk.itu.moapd.x9.diko.data

import dk.itu.moapd.x9.diko.model.Report

/**
 * A utility object to provide test data for the application.
 */
object ReportData {
    val sampleReports = listOf(
        Report(
            title = "Pothole on Main St",
            location = "Main St & 5th Ave",
            date = "23/04/2026",
            type = "Pothole",
            description = "Large pothole in the middle of the intersection, dangerous for cyclists.",
            severity = "High"
        ),
        Report(
            title = "Broken streetlight",
            location = "Oak Lane",
            date = "24/04/2026",
            type = "Maintenance",
            description = "Streetlight is flickering and mostly dark at night.",
            severity = "Medium"
        ),
        Report(
            title = "Graffiti in Central Park",
            location = "Central Park West",
            date = "24/04/2026",
            type = "Incident",
            description = "New graffiti on the wooden benches near the fountain.",
            severity = "Low"
        ),
        Report(
            title = "Graffiti in Central Park",
            location = "Central Park West",
            date = "24/04/2026",
            type = "Incident",
            description = "New graffiti on the wooden benches near the fountain.",
            severity = "Low"
        ),
        Report(
            title = "Graffiti in Central Park",
            location = "Central Park West",
            date = "24/04/2026",
            type = "Incident",
            description = "New graffiti on the wooden benches near the fountain.",
            severity = "Low"
        ),
        Report(
            title = "Graffiti in Central Park",
            location = "Central Park West",
            date = "24/04/2026",
            type = "Incident",
            description = "New graffiti on the wooden benches near the fountain.",
            severity = "Low"
        ),
        Report(
            title = "Graffiti in Central Park",
            location = "Central Park West",
            date = "24/04/2026",
            type = "Incident",
            description = "New graffiti on the wooden benches near the fountain.",
            severity = "Low"
        ),
        Report(
            title = "Graffiti in Central Park",
            location = "Central Park West",
            date = "24/04/2026",
            type = "Incident",
            description = "New graffiti on the wooden benches near the fountain.",
            severity = "Low"
        ),
        Report(
            title = "Graffiti in Central Park",
            location = "Central Park West",
            date = "24/04/2026",
            type = "Incident",
            description = "New graffiti on the wooden benches near the fountain.",
            severity = "Low"
        ),
        Report(
            title = "Graffiti in Central Park",
            location = "Central Park West",
            date = "24/04/2026",
            type = "Incident",
            description = "New graffiti on the wooden benches near the fountain.",
            severity = "Low"
        ),
        Report(
            title = "Illegal Parking",
            location = "University Ave",
            date = "20/04/2026",
            type = "Heavy Traffic",
            description = "Car blocking the fire hydrant for several hours.",
            severity = "Medium"
        )
    )

    /**
     * Populates the ReportRepository with the sample data.
     */
    fun populateRepository() {
        sampleReports.forEach { report ->
            ReportRepository.addReport(report)
        }
    }
}
