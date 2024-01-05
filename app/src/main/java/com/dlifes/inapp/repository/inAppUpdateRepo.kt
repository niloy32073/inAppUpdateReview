package com.dlifes.inapp.repository

import androidx.activity.ComponentActivity
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

object InAppUpdateManager {
    private var appUpdateManager: com.google.android.play.core.appupdate.AppUpdateManager? = null
    private var activity: ComponentActivity? = null

    fun initialize(activity: ComponentActivity) {
        appUpdateManager = AppUpdateManagerFactory.create(activity)
        this.activity = activity
    }

    suspend fun checkForUpdates() {
        val appUpdateInfoTask = appUpdateManager!!.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            when {
                // Flexible update available
                appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.updatePriority() < 4
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> {
                    appUpdateManager!!.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.FLEXIBLE,
                        activity!!,
                        123
                    )
                }
                // Immediate update available
                appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.updatePriority() >= 4
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) -> {
                    appUpdateManager!!.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        activity!!,
                        123
                    )
                }
            }
        }
    }

    // Additional functions with null assertions where needed

    fun completeUpdate() {
        appUpdateManager!!.completeUpdate()
    }

    fun checkInstallStatus() {
        val installStatusTask = appUpdateManager!!.appUpdateInfo
        installStatusTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                // Prompt the user to complete the update
                appUpdateManager!!.completeUpdate()
            }
        }
    }
}
