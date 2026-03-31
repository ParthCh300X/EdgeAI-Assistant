package parth.appdev.edgeaiassistant.domain.command

import android.content.Context
import android.content.Intent
import parth.appdev.edgeaiassistant.features.applauncher.AppMatcher
import parth.appdev.edgeaiassistant.features.applauncher.AppRepository

class OpenAppCommand(
    private val context: Context,
    private val input: String
) : Command {

    private val repo = AppRepository(context)
    private val matcher = AppMatcher()

    override fun execute(): String {

        val apps = repo.getInstalledApps()
        val query = extractAppName(input)

        val matches = matcher.findBestMatch(query, apps)

        if (matches.isEmpty()) {
            return "No app found"
        }

        // 🔥 STEP 1 — EXACT MATCH
        val exact = matches.find {
            it.name.equals(query, ignoreCase = true)
        }

        if (exact != null) {
            launchApp(exact.packageName)
            return "Opening ${exact.name}"
        }

        // 🔥 STEP 2 — BEST MATCH (first result)
        val best = matches.first()

        // 🔥 If strong match → open directly
        if (best.name.lowercase().contains(query)) {
            launchApp(best.packageName)
            return "Opening ${best.name}"
        }

        // 🔥 STEP 3 — fallback (ambiguity)
        val names = matches.take(3).joinToString { it.name }
        return "Multiple apps found: $names"
    }

    private fun extractAppName(input: String): String {
        return input
            .lowercase()
            .replace("open", "")
            .replace("launch", "")
            .replace("start", "")
            .replace("please", "")
            .replace("the", "")
            .trim()
    }

    private fun launchApp(packageName: String) {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}