package dk.itu.moapd.x9.diko.data

import dk.itu.moapd.x9.diko.model.Report

object ReportRepository {

    private val reports = mutableListOf<Report>()

    fun addReport(report: Report) {
        reports.add(report)
    }

    fun getReports(): List<Report> {
        return reports
    }
}
