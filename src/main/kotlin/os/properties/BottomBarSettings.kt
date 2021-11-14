package os.properties

import java.io.File

enum class BottomBarPosition {
    Top, Bottom;

    companion object {
        fun fromString(string: String): BottomBarPosition {
            return when (string) {
                "top" -> Top
                "bottom" -> Bottom
                else -> Bottom
            }
        }

        fun toString(position: BottomBarPosition): String {
            return when (position) {
                Top -> "top"
                Bottom -> "bottom"
            }
        }
    }
}

class BottomBarSettings {
    private val settingsFile = File("ViewOS/Properties/settings.txt")
    private val settingsText by lazy { if (settingsFile.exists()) settingsFile.readText() else "" }
    var iconsInCenter = !settingsText.contains(Regex("iconsInCenter:false"))
    var bottomBarPosition = BottomBarPosition.fromString(
        Regex("bottomBarPosition:(top|bottom)").find(settingsText)?.value?.substringAfter(":") ?: "bottom"
    )

    fun cancelRunning() {
        settingsFile.writeText(
            settingsText.replace(Regex("iconsInCenter:(false|true)"), "iconsInCenter:$iconsInCenter")
                .replace(
                    Regex("bottomBarPosition:(top|bottom)"),
                    "bottomBarPosition:${BottomBarPosition.toString(bottomBarPosition)}"
                )
        )
    }
}