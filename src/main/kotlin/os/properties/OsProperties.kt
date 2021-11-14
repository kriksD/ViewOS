package os.properties

import os.time.Date
import os.time.Time

object OsProperties {
    private val osTime by lazy { OsTime() }
    val osStyle by lazy { OsStyle() }
    val bottomBarSettings by lazy { BottomBarSettings() }

    fun currentTime(): Time {
        return osTime.currentTime()
    }

    fun setCurrentTime(time: Time) {
        osTime.setCurrentTime(time)
    }

    fun currentDate(): Date {
        return osTime.currentDate()
    }

    fun setCurrentDate(date: Date) {
        osTime.setCurrentDate(date)
    }

    fun currentTimeAsString(): String {
        return "${osTime.currentTime()} ${osTime.currentDate()}"
    }

    fun cancelRunning() {
        osTime.cancelRunning()
        bottomBarSettings.cancelRunning()
        osStyle.cancelRunning()
    }
}