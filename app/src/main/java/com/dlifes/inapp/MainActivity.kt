package com.dlifes.inapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.dlifes.inapp.repository.InAppReviewManager
import com.dlifes.inapp.repository.InAppUpdateManager
import com.dlifes.inapp.ui.theme.InAppTheme
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        InAppUpdateManager.initialize(this)
        InAppReviewManager.initialize(getSharedPreferences("app_prefs", MODE_PRIVATE))
        setContent {
            InAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    lifecycleScope.launch {
                        InAppUpdateManager.checkForUpdates()

                    }

                    Text(text = "Hello App")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        InAppUpdateManager?.checkInstallStatus()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) { // The request code used for in-app updates
            if (resultCode == RESULT_OK) {
                // Update was successful
                Toast.makeText(this, "App updated successfully!", Toast.LENGTH_SHORT).show()
            } else {
                // Handle update failure
                Toast.makeText(this, "App updated Failed. Please Use Playstore to update your app", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
    }

}
