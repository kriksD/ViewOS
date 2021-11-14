package os.manager

import os.properties.OsProperties

object NotificationManager {
    private val notifications = mutableListOf<Notification>()
    private val onSent = mutableListOf<(notification: Notification) -> Unit>()

    fun add(message: String, onClick: (notification: Notification) -> Unit = {}) {
        val newNotification = Notification(message, OsProperties.currentTime(), OsProperties.currentDate(), onClick)
        notifications.add(newNotification)

        onSent.forEach {
            it(newNotification)
        }
    }

    fun add(notification: Notification) {
        notifications.add(notification)

        onSent.forEach {
            it(notification)
        }
    }

    fun remove(notification: Notification) {
        notifications.remove(notification)
    }

    fun addAll(notifications: Collection<Notification>) {
        this.notifications.addAll(notifications)

        notifications.forEach { fNotification ->
            onSent.forEach {
                it(fNotification)
            }
        }
    }

    fun removeAll(notifications: Collection<Notification>) {
        this.notifications.removeAll(notifications)
    }

    fun clear() {
        notifications.clear()
    }

    fun count(): Int {
        return notifications.size
    }

    fun forEach(lambda: (notification: Notification) -> Unit) {
        notifications.forEach {
            lambda(it)
        }
    }

    fun getAll(): List<Notification> {
        return notifications.toList()
    }

    fun addOnSent(onSent: (notification: Notification) -> Unit) {
        this.onSent.add(onSent)
    }

    fun removeOnSent(onSent: (notification: Notification) -> Unit) {
        this.onSent.remove(onSent)
    }
}