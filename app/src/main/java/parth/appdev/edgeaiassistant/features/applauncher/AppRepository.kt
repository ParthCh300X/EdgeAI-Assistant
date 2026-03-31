package parth.appdev.edgeaiassistant.features.applauncher

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import parth.appdev.edgeaiassistant.domain.model.AppInfo

class AppRepository(private val context: Context) {

    fun getInstalledApps(): List<AppInfo> {

        val pm = context.packageManager
        val apps = mutableListOf<AppInfo>()

        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        val resolveInfoList = pm.queryIntentActivities(intent, 0)

        for (resolveInfo in resolveInfoList) {
            val appName = resolveInfo.loadLabel(pm).toString()
            val packageName = resolveInfo.activityInfo.packageName

            apps.add(AppInfo(appName, packageName))
        }

        return apps
    }
}