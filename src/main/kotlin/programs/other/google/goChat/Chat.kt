package programs.other.google.goChat

import java.io.File

data class Chat(
    val name: String,
    val lastMessage: String,
    private var isFavorite: Boolean
) {
    fun setIsFavorite(value: Boolean) {
        isFavorite = value

        val file = File("ViewOS/ProgramData/GoChat/Chats/$name/properties.txt")

        if (file.exists()) {
            file.writeText(
                file.readText().replace(
                    Regex("favorite:(true|false)"), "favorite:$isFavorite"
                )
            )
        }
    }

    fun isFavorite(): Boolean {
        return isFavorite
    }
}
