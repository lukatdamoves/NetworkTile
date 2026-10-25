package com.james.networktile

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.telephony.SubscriptionManager

/**
 * Opens the phone's Mobile network settings page for the default DATA
 * subscription (the "selected" SIM). Without the subscription extra the
 * page just opens SIM 1. Only launches intents the system can actually
 * resolve, and a failed launch moves on to the next candidate.
 */
object NetworkSettingsOpener {

    /** Default data subscription id, or INVALID_SUBSCRIPTION_ID if unknown. */
    fun dataSubId(): Int {
        return try {
            SubscriptionManager.getDefaultDataSubscriptionId()
        } catch (_: Exception) {
            SubscriptionManager.INVALID_SUBSCRIPTION_ID
        }
    }

    fun open(context: Context, launch: (Intent) -> Unit) {
        val subId = dataSubId()

        val networkIntent = Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS)
        if (subId != SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
            networkIntent.putExtra(Settings.EXTRA_SUB_ID, subId)
            // Legacy key some Settings builds read; harmless if ignored.
            networkIntent.putExtra("subscription_id", subId)
        }

        val candidates = listOf(
            networkIntent,
            Intent(Settings.ACTION_WIRELESS_SETTINGS),
            Intent(Settings.ACTION_SETTINGS)
        )
        for (intent in candidates) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val resolved = try {
                intent.resolveActivity(context.packageManager)
            } catch (_: Exception) {
                null
            }
            if (resolved == null) continue
            try {
                launch(intent)
                return
            } catch (_: Exception) {
                // Try the next candidate.
            }
        }
    }
}
