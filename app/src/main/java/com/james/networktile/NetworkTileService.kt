package com.james.networktile

import android.app.PendingIntent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

/**
 * Quick Settings tile that jumps to the phone's Mobile network settings
 * page, where the user picks the preferred network type (4G/5G).
 * It changes nothing itself, so it needs no special permissions.
 *
 * NOTE: on Android 14+, startActivityAndCollapse(Intent) throws
 * UnsupportedOperationException — tiles must launch via a PendingIntent.
 */
class NetworkTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        qsTile?.let {
            it.state = Tile.STATE_INACTIVE
            it.updateTile()
        }
    }

    override fun onClick() {
        super.onClick()
        NetworkSettingsOpener.open(this) { intent ->
            val pending = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            startActivityAndCollapse(pending)
        }
    }
}
