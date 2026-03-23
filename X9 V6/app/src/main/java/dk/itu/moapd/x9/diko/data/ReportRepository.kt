package dk.itu.moapd.x9.diko.data

import dk.itu.moapd.x9.diko.model.Report

object ReportRepository {
    // Defines the ReportRepository class.
    // It is used as a simulation for a storage of reports.

    private val reports = mutableListOf<Report>()

    fun addReport(report: Report) {
        reports.add(report)
    }

    fun getReports(): List<Report> {
        return reports
    }
}
