package com.james.networktile

import android.app.Activity
import android.app.StatusBarManager
import android.content.ComponentName
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

/**
 * Setup helper. The tile does the work; this screen explains it and offers
 * a one-tap "add tile" prompt (Android 13+).
 */
class MainActivity : Activity() {

    private lateinit var targetView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(48, 64, 48, 48)
            setBackgroundColor(Color.WHITE)
        }

        val title = TextView(this).apply {
            text = "Network Tile"
            textSize = 26f
            setTextColor(Color.BLACK)
        }
        val body = TextView(this).apply {
            text = "This app adds one tile to your quick settings. " +
                "Tap the tile and it opens your phone's Mobile network " +
                "settings, where you pick 4G or 5G for your SIM.\n\n" +
                "It changes nothing by itself and needs no special " +
                "permissions, so your banking apps are unaffected."
            textSize = 16f
            setTextColor(Color.DKGRAY)
            setPadding(0, 32, 0, 32)
        }
        val addButton = Button(this).apply {
            text = "Add tile to quick settings"
            setOnClickListener { requestTile() }
        }
        val manual = TextView(this).apply {
            text = "Or add it manually: pull down quick settings twice, " +
                "tap Edit (pencil), then drag the \"Network\" tile up."
            textSize = 14f
            setTextColor(Color.GRAY)
            setPadding(0, 32, 0, 32)
        }
        targetView = TextView(this).apply {
            textSize = 14f
            setTextColor(Color.DKGRAY)
            setPadding(0, 8, 0, 8)
        }

        layout.addView(title)
        layout.addView(body)
        layout.addView(addButton)
        layout.addView(manual)
        layout.addView(targetView)

        val scroll = ScrollView(this).apply { addView(layout) }
        setContentView(scroll)
    }

    override fun onResume() {
        super.onResume()
        refreshTarget()
    }

    private fun refreshTarget() {
        val subId = NetworkSettingsOpener.dataSubId()
        targetView.text = if (subId != android.telephony.SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
            "Tile will open settings for your data SIM (subscription id $subId)."
        } else {
            "Could not detect your data SIM — tile opens the default page."
        }
    }

    private fun requestTile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val component = ComponentName(this, NetworkTileService::class.java)
            val statusBarManager = getSystemService(StatusBarManager::class.java)
            if (statusBarManager == null) {
                Toast.makeText(
                    this,
                    "Pull down quick settings, tap Edit, and drag the \"Network\" tile up.",
                    Toast.LENGTH_LONG
                ).show()
                return
            }
            statusBarManager.requestAddTileService(
                component,
                getString(R.string.tile_label),
                Icon.createWithResource(this, R.drawable.ic_network_tile),
                mainExecutor
            ) { result ->
                if (result != StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ADDED) {
                    Toast.makeText(
                        this,
                        "Tile not added. You can still add it manually from Edit.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } else {
            Toast.makeText(
                this,
                "Pull down quick settings, tap Edit, and drag the \"Network\" tile up.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
