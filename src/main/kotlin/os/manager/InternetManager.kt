package os.manager

import java.io.File

object InternetManager {
    private var state = true

    fun check(): Boolean {
        return state
    }

    fun disable() {
        state = false
    }

    fun enable() {
        state = true
    }

    fun internetFolder(): File? {
        return if (state) {
            File("Hidden files/Internet")
        } else {
            null
        }
    }

    fun internetFolderPath(): String? {
        return if (state) {
            "Hidden files/Internet"
        } else {
            null
        }
    }
}