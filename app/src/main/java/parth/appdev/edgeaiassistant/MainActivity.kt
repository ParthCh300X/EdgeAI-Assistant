package parth.appdev.edgeaiassistant

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import parth.appdev.edgeaiassistant.data.preferences.UserPreferences
import parth.appdev.edgeaiassistant.ui.screens.analytics.AnalyticsScreen
import parth.appdev.edgeaiassistant.ui.screens.home.HomeScreen
import parth.appdev.edgeaiassistant.ui.screens.notes.NotesScreen
import parth.appdev.edgeaiassistant.ui.screens.onboarding.OnboardingScreen
import parth.appdev.edgeaiassistant.ui.screens.settings.SettingsScreen
import parth.appdev.edgeaiassistant.ui.theme.EdgeAITheme
import javax.inject.Inject

data class BottomNavItem(
    val route : String,
    val label : String,
    val icon  : ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem("home",      "Chat",      Icons.Default.Chat),
    BottomNavItem("notes",     "Notes",     Icons.Default.Note),
    BottomNavItem("analytics", "Analytics", Icons.Default.Analytics),
    BottomNavItem("settings",  "Settings",  Icons.Default.Settings)
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var prefs: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100
                )
            }
        }

        val onboardingDone = runBlocking { prefs.isOnboardingComplete.first() }
        val startDest      = if (onboardingDone) "home" else "onboarding"

        setContent {
            // Collect theme prefs as state so recomposition happens on change
            val useSystem by prefs.useSystemTheme.collectAsState(initial = true)
            val darkPref  by prefs.isDarkMode.collectAsState(initial = true)
            val systemDark = isSystemInDarkTheme()

            val isDark = if (useSystem) systemDark else darkPref

            EdgeAITheme(darkTheme = isDark) {
                val navController = rememberNavController()
                val navBackStack  by navController.currentBackStackEntryAsState()
                val currentRoute  = navBackStack?.destination?.route
                val noNavRoutes   = setOf("onboarding")
                val showBottomNav = currentRoute !in noNavRoutes

                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        if (showBottomNav) {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 0.dp
                            ) {
                                bottomNavItems.forEach { item ->
                                    NavigationBarItem(
                                        selected  = currentRoute == item.route,
                                        onClick   = {
                                            navController.navigate(item.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState    = true
                                            }
                                        },
                                        icon  = {
                                            Icon(item.icon, contentDescription = item.label)
                                        },
                                        label = { Text(item.label) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor   = Color(0xFF6366F1),
                                            selectedTextColor   = Color(0xFF6366F1),
                                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                            indicatorColor      = Color(0xFF6366F1).copy(alpha = 0.15f)
                                        )
                                    )
                                }
                            }
                        }
                    }
                ) { padding ->
                    NavHost(
                        navController    = navController,
                        startDestination = startDest,
                        modifier         = Modifier.padding(padding)
                    ) {
                        composable("onboarding") {
                            OnboardingScreen(onComplete = {
                                navController.navigate("home") {
                                    popUpTo("onboarding") { inclusive = true }
                                }
                            })
                        }
                        composable("home")      { HomeScreen(navController) }
                        composable("notes")     { NotesScreen() }
                        composable("analytics") { AnalyticsScreen() }
                        composable("settings")  { SettingsScreen() }
                    }
                }
            }
        }
    }
}