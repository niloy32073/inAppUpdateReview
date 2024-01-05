package com.dlifes.inapp.repository

import android.content.SharedPreferences
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.launch


object InAppReviewManager {
    private const val LAST_REVIEW_REQUEST_KEY = "last_review_request_timestamp"
    private const val MIN_REVIEW_INTERVAL_DAYS = 30

    private var sharedPreferences: SharedPreferences? = null

    fun initialize(sharedPreferences: SharedPreferences) {
        this.sharedPreferences = sharedPreferences
    }

    private fun canRequestReview(): Boolean {
        val lastRequestTimestamp = sharedPreferences!!.getLong(LAST_REVIEW_REQUEST_KEY, 0L)
        val timeSinceLastRequest = System.currentTimeMillis() - lastRequestTimestamp
        val daysSinceLastRequest = timeSinceLastRequest / (1000 * 60 * 60 * 24)
        return daysSinceLastRequest >= MIN_REVIEW_INTERVAL_DAYS
    }

    fun requestReviewIfEligible(activity: ComponentActivity) {
        activity.lifecycleScope.launch { // Launch coroutine within lifecycleScope
            if (canRequestReview()) {
                val reviewManager = ReviewManagerFactory.create(activity)
                val request = reviewManager.requestReviewFlow()

                request.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        reviewManager.launchReviewFlow(activity, task.result)
                        sharedPreferences!!.edit()
                            .putLong(LAST_REVIEW_REQUEST_KEY, System.currentTimeMillis())
                            .apply()
                    }
                }
            }
        }
    }
}

