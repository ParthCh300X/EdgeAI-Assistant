package parth.appdev.edgeaiassistant.features.applauncher

import parth.appdev.edgeaiassistant.domain.model.AppInfo

class AppMatcher {

    fun findBestMatch(query: String, apps: List<AppInfo>): List<AppInfo> {

        val cleanedQuery = clean(query)

        return apps.filter { app ->
            val appName = clean(app.name)

            appName.contains(cleanedQuery) ||
                    cleanedQuery.contains(appName)
        }
    }

    private fun clean(text: String): String {
        return text.lowercase()
            .replace("google", "")
            .replace("app", "")
            .replace("lite", "")
            .replace(Regex("\\s+"), "")
            .trim()
    }
}