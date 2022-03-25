package programs.other.google.goChat

import os.time.Date
import os.time.Time

data class Message(
    val message: String,
    val nickname: String,
    val time: Time,
    val date: Date
) {
    companion object {
        fun getInstanceOfProperties(messageProperties: String): Message {
            val message = Regex("Message:\".*\"").find(messageProperties)?.value?.substringAfter(":")?.replace("\"", "") ?: "Message isn`t exist :("
            val nickname = Regex("By:\".*\"").find(messageProperties)?.value?.substringAfter(":")?.replace("\"", "") ?: "Nickname isn`t exist :("
            val time = Time(Regex("Time:\\d+").find(messageProperties)?.value?.substringAfter(":")?.toLong() ?: 0)
            val date = Date(Regex("Date:\\d+").find(messageProperties)?.value?.substringAfter(":")?.toLong() ?: 0)

            return Message(
                message,
                nickname,
                time,
                date
            )
        }
    }
}