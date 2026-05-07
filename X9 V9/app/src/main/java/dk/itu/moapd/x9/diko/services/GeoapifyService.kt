package dk.itu.moapd.x9.diko.services

import dk.itu.moapd.x9.diko.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder

object GeoapifyService {

    private val client = OkHttpClient()

    suspend fun autocomplete(query: String): List<Triple<String, Double, Double>> = withContext(Dispatchers.IO) {
        val geoapifyApiKey = BuildConfig.GEOAPIFY_API_KEY
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val url = "https://api.geoapify.com/v1/geocode/autocomplete" +
                "?text=$encodedQuery&limit=5&apiKey=$geoapifyApiKey"

        val request = Request.Builder().url(url).build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext emptyList()
                val body = response.body.string()

                val results = mutableListOf<Triple<String, Double, Double>>()

                val json = JSONObject(body)
                val features = json.optJSONArray("features") ?: return@withContext emptyList()

                for (i in 0 until features.length()) {
                    val props = features.getJSONObject(i).getJSONObject("properties")

                    val name = props.optString("formatted", "")
                    val lat = props.optDouble("lat", 0.0)
                    val lon = props.optDouble("lon", 0.0)

                    if (name.isNotEmpty()) {
                        results.add(Triple(name, lat, lon))
                    }
                }

                results
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Reverse geocodes the given coordinates to a street address.
     *
     * @param lat Latitude of the location.
     * @param lon Longitude of the location.
     * @return A Triple containing the formatted address, latitude, and longitude, or null if not found.
     */
    suspend fun reverseGeocode(lat: Double, lon: Double): Triple<String, Double, Double>? = withContext(Dispatchers.IO) {
        val geoapifyApiKey = BuildConfig.GEOAPIFY_API_KEY
        val url = "https://api.geoapify.com/v1/geocode/reverse" +
                "?lat=$lat&lon=$lon&apiKey=$geoapifyApiKey"

        val request = Request.Builder().url(url).build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                val body = response.body.string()

                val json = JSONObject(body)
                val features = json.optJSONArray("features") ?: return@withContext null

                if (features.length() > 0) {
                    val props = features.getJSONObject(0).getJSONObject("properties")
                    val address = props.optString("formatted", "")
                    val resLat = props.optDouble("lat", lat)
                    val resLon = props.optDouble("lon", lon)

                    if (address.isNotEmpty()) {
                        Triple(address, resLat, resLon)
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }
}
