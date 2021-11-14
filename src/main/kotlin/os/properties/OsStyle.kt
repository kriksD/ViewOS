package os.properties

import androidx.compose.ui.graphics.Color
import java.io.File

class OsStyle {
    private val settingsFile = File("ViewOS/Properties/Style/settings.txt")
    private val settingsText by lazy { if (settingsFile.exists()) settingsFile.readText() else "" }
    private val backgroundPathCheck by lazy {
        val background = Regex("backgroundPath:\".*\"").find(settingsText)?.value ?: "ViewOS/Properties/Style/fon.png"

        if (File(background).exists()) {
            background
        } else {
            "ViewOS/Properties/Style/fon.png"
        }
    }
    var backgroundPath = backgroundPathCheck
    var bottomBarTransparency =
        Regex("bottomBarTransparency:\\d\\.\\d").find(settingsText)?.value?.substringAfter(":")?.toFloat()
            ?: 0.5F
    private val colorFloats by lazy {
        val floats =
            Regex("bottomBarColor:\\d.?\\d{0,3},\\d.?\\d{0,3},\\d.?\\d{0,3}")
                .find(settingsText)?.value?.substringAfter(":") ?: "0.0,0.0,0.0"
        val regexResult = Regex("\\d.?\\d{0,3}").findAll(floats)

        listOf(
            regexResult.elementAt(0).value.toFloat(),
            regexResult.elementAt(1).value.toFloat(),
            regexResult.elementAt(2).value.toFloat()
        )
    }
    var bottomBarColor = Color(colorFloats[0], colorFloats[1], colorFloats[2])

    fun cancelRunning() {
        settingsFile.writeText(
            settingsText
                .replace(Regex("backgroundPath:\".*\""), "backgroundPath:\"$backgroundPath\"")
                .replace(
                    Regex("bottomBarTransparency:\\d\\.\\d"),
                    "bottomBarTransparency:${String.format("%.1f", bottomBarTransparency).replace(",", ".")}"
                )
                .replace(
                    Regex("bottomBarColor:\\d.?\\d{0,3},\\d.?\\d{0,3},\\d.?\\d{0,3}"),
                    "bottomBarColor:${
                        String.format("%.3f", bottomBarColor.red).replace(",", ".")
                    },${
                        String.format("%.3f", bottomBarColor.green).replace(",", ".")
                    },${
                        String.format("%.3f", bottomBarColor.blue).replace(",", ".")
                    }"
                )
        )
    }
}

private fun colorFromString(rgb: String): Color {
    val newRgb = rgb.replace(Regex("[()]"), "").replace(" ", "")
    return if (newRgb.contains(Regex("\\d{1,3},\\d{1,3},\\d{1,3}"))) {
        val rgbNumbers = Regex("\\d{1,3}").findAll(newRgb)
        val r = rgbNumbers.elementAt(0).value.toInt()
        val g = rgbNumbers.elementAt(1).value.toInt()
        val b = rgbNumbers.elementAt(2).value.toInt()

        Color(r, g, b)
    } else {
        Color.Black
    }
}