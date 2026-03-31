package parth.appdev.edgeaiassistant

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.*
import dagger.hilt.android.AndroidEntryPoint
import parth.appdev.edgeaiassistant.ui.screens.analytics.AnalyticsScreen
import parth.appdev.edgeaiassistant.ui.screens.home.HomeScreen
import parth.appdev.edgeaiassistant.ui.theme.EdgeAITheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }

        setContent {
            EdgeAITheme {

                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {

                    composable("home") {
                        HomeScreen(navController = navController)
                    }

                    composable("analytics") {
                        AnalyticsScreen()
                    }
                }
            }
        }
    }
}