package dk.itu.moapd.x9.diko.ui.report



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