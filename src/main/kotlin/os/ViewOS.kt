package os

import os.properties.OsProperties

object ViewOS {

    const val currentVersion: String = "1.1.0"

    fun cancelRunning() {
        OsProperties.cancelRunning()
    }
}