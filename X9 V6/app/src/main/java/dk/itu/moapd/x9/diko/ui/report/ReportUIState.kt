package dk.itu.moapd.x9.diko.ui.report

import dk.itu.moapd.x9.diko.R

/**
 * Data class representing the state of the main UI components in the application.
 *
 * @property textId The resource ID of the text to be displayed.
 * @property checked A boolean indicating whether a checkbox is checked or not.
 */
data class ReportUIState(
    // Defines the state of the main UI components in the application
    // and helps store temporary values in case of configuration changes.
    var title: String = "",
    var location: String = "",
    var date: String = "",
    var type: String = "",
    var description: String = "",
    var severity: String = "",
)