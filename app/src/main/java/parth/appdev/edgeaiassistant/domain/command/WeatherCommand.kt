package parth.appdev.edgeaiassistant.domain.command

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class WeatherCommand(
    private val context: Context
) : Command {

    private val client = OkHttpClient()

    override suspend fun execute(): String {
        // Check location permission
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) return "Enable location permission for weather."

        // Get last known location
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        @Suppress("MissingPermission")
        val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: return "Couldn't get your location. Make sure location is enabled."

        val lat = location.latitude
        val lon = location.longitude

        return try {
            // Open-Meteo — free, no API key
            val url = "https://api.open-meteo.com/v1/forecast" +
                    "?latitude=$lat&longitude=$lon" +
                    "&current=temperature_2m,weathercode,windspeed_10m" +
                    "&temperature_unit=celsius"

            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return "No weather data received."

            val json    = JSONObject(body)
            val current = json.getJSONObject("current")
            val temp    = current.getDouble("temperature_2m")
            val code    = current.getInt("weathercode")
            val wind    = current.getDouble("windspeed_10m")

            val condition = weatherCodeToText(code)
            "Weather: $condition, ${temp}°C, Wind ${wind} km/h"

        } catch (e: Exception) {
            "Couldn't fetch weather. Check your internet connection."
        }
    }

    private fun weatherCodeToText(code: Int): String = when (code) {
        0            -> "Clear sky"
        1, 2, 3      -> "Partly cloudy"
        45, 48       -> "Foggy"
        51, 53, 55   -> "Drizzle"
        61, 63, 65   -> "Rainy"
        71, 73, 75   -> "Snowy"
        80, 81, 82   -> "Rain showers"
        95           -> "Thunderstorm"
        96, 99       -> "Thunderstorm with hail"
        else         -> "Unknown conditions"
    }
}