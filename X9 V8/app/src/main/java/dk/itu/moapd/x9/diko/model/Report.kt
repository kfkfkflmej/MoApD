package dk.itu.moapd.x9.diko.model

data class Report(
     val userId: String? = null,
     val title: String = "",
     val location: String = "",
     val longitude: Double = 0.0,
     val latitude: Double = 0.0,
     val locationKey: String? = null,
     val date: String = "",
     val type: String = "",
     val description: String = "",
     val severity: String = "",
     val imageRef: String? = null,
     val createdAt: Long? = null,
     val updatedAt: Long? = null
)
