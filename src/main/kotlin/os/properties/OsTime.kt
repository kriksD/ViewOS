package os.properties

import os.time.Date
import os.time.Time
import java.io.File
import kotlin.concurrent.timer

class OsTime(withTimer: Boolean = true) {

    private var currentTime = Time(timeFromFile())
    private var currentDate = Date(dateFromFile())

    private val timer by lazy {
        if (withTimer) {
            timer("timeAndDate", true, 833, 833) {
                currentTime.add(1)

                if (currentTime == Time(0, 0)){
                    currentDate.day += 1
                }
            }
        } else {
            null
        }
    }

    init {
        timer
    }

    private fun timeFromFile(): Long {
        val timeFile = File("ViewOS/Properties/Time/time.data")

        return if (timeFile.exists()) {
            timeFile.readText().toLong()
        } else {
            0
        }
    }

    private fun dateFromFile(): Long {
        val dateFile = File("ViewOS/Properties/Time/date.data")

        return if (dateFile.exists()) {
            dateFile.readText().toLong()
        } else {
            0
        }
    }

    fun currentTime(): Time {
        return currentTime
    }

    fun setCurrentTime(time: Time) {
        currentTime = time
    }

    fun currentDate(): Date {
        return currentDate
    }

    fun setCurrentDate(date: Date) {
        currentDate = date
    }

    fun cancelRunning() {
        timer?.cancel()

        val timeFile = File("ViewOS/Properties/Time/time.data")
        timeFile.writeText(currentTime.toLong().toString())

        val dateFile = File("ViewOS/Properties/Time/date.data")
        dateFile.writeText(currentDate.day.toString())
    }
}