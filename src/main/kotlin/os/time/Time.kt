package os.time

import os.properties.OsProperties

data class Time(var hour: Int, var minute: Int) {

    constructor(value: Long) : this((value / 60.0).toInt(), (value % 60.0).toInt())

    private fun correction() {
        val hours: Int = (minute / 60.0).toInt()
        hour = (hours + hour) % 24
        minute -= hours * 60
    }

    fun add(minutes: Int) {
        minute += minutes
        correction()
    }

    fun remove(minutes: Int) {
        minute -= minutes
        correction()
    }

    fun toLong(): Long {
        return minute.toLong() + hour.toLong() * 60L
    }

    override fun toString(): String {
        return "$hour:${if (minute >= 10) minute else "0$minute"}"
    }

    companion object {
        fun fromString(string: String): Time? {
            return if (string.contains(Regex("\\s*[0-2]?\\d:[0-6]\\d\\s*"))) {
                Time(
                    string.replace(" ","").substringBefore(":").toInt(),
                    string.replace(" ","").substringAfter(":").toInt()
                )
            } else {
                null
            }
        }
    }
}
