package os.manager

import os.properties.OsProperties
import os.time.Date
import os.time.Time

data class Notification(
    val message: String,
    val time: Time,
    val date: Date,
    val onClick: (notification: Notification) -> Unit = {}
) {
    companion object {
        fun getCurrentTimeInstance(message: String, onClick: (notification: Notification) -> Unit = {}): Notification {
            return Notification(message, OsProperties.currentTime().copy(), OsProperties.currentDate().copy(), onClick)
        }
    }

    var isShow = false

    override fun toString(): String {
        return "$time $date | $message"
    }
}