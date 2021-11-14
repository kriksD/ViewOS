package os.manager

import androidx.compose.ui.unit.dp
import os.desktop.SubWindow
import os.desktop.SubWindowData
import os.properties.OsProperties
import programs.other.analyticsCenter.countryAnalytics.CountryAnalytics
import programs.other.google.goChat.GoChat
import programs.other.google.goMaps.GoMaps
import programs.standart.aBrowser.ABrowser
import programs.standart.calculator.Calculator
import programs.standart.explorer.Explorer
import programs.standart.imageViewer.ImageViewer
import programs.standart.market.AppMarket
import programs.standart.sapper.Sapper
import programs.standart.settings.Settings
import programs.standart.testProg.ui.TestProg
import programs.standart.testProg.ui.TestProg2
import programs.standart.textEditor.TextEditor
import programs.standart.vBrowser.VBrowser
import programs.standart.welcomeProgram.WelcomeProgram
import kotlin.random.Random

object ProgramsCreator {
    private val programsData = mutableListOf(
        SubWindowData(
            title = "Welcome",
            width = SubWindow.minimumWidth,
            height = SubWindow.minimumHeight,
            x = 800F,
            y = 400F,
            content = { data, pto, reload -> WelcomeProgram() },
            icon = "osIcon.png"
        ),
        SubWindowData(
            title = "TestProg",
            content = { data, pto, reload -> TestProg() },
            icon = "osIconSecond.png"
        ),
        SubWindowData(
            title = "TestProg2",
            content = { data, pto, reload -> TestProg2() },
            icon = "osIconSecond.png"
        ),
        SubWindowData(
            title = "Explorer",
            width = 900.dp,
            content = { data, pto, reload -> Explorer(pto, data, reload) },
            icon = "explorerIcon.png"
        ),
        SubWindowData(
            title = "Text Editor",
            icon = "textEditorIcon.png",
            content = { data, pto, reload -> TextEditor(pto, data) },
            args = mutableMapOf(Pair("path", "none"))
        ),
        SubWindowData(
            title = "Image Viewer",
            icon = "imageViewerIcon.png",
            content = { data, pto, reload -> ImageViewer(pto, data) },
            args = mutableMapOf(Pair("path", "none"))
        ),
        SubWindowData(
            title = "ABrowser",
            content = { data, pto, reload -> ABrowser(pto, data) },
            icon = "osIconSecond.png"
        ),
        SubWindowData(
            title = "Sapper",
            height = 434.dp,
            width = 500.dp,
            content = { data, pto, reload -> Sapper(pto, data) },
            icon = "osIconSecond.png"
        ),
        SubWindowData(
            title = "VBrowser",
            content = { data, pto, reload -> VBrowser(pto, data) },
            icon = "vBrowserIcon.png"
        ),
        SubWindowData(
            title = "AppMarket",
            content = { data, pto, reload -> AppMarket(pto, data) },
            icon = "appMarketIcon.png"
        ),
        SubWindowData(
            title = "Calculator",
            height = 150.dp,
            content = { data, pto, reload -> Calculator(pto, data) },
            icon = "calculatorIcon.png"
        ),
        SubWindowData(
            title = "GoMaps",
            content = { data, pto, reload -> GoMaps(pto, data) },
            icon = "mapsIcon.png"
        ),
        SubWindowData(
            title = "GoChat",
            content = { data, pto, reload -> GoChat(pto, data) },
            icon = "chatIcon.png"
        ),
        SubWindowData(
            title = "Settings",
            content = { data, pto, reload -> Settings(pto, data) },
            icon = "settingsIcon.png"
        ),
        SubWindowData(
            title = "Country Analytics",
            content = { data, pto, reload -> CountryAnalytics(pto, data) },
            icon = "acIcon.png"
        ),
    )

    fun getInstance(name: String): SubWindowData {
        return copySubWindowData(programsData.find { it.title == name } ?: getEmptyInstance())
    }

    fun getEmptyInstance(): SubWindowData {
        return SubWindowData(
            "program",
            "9.png"
        )
    }

    private fun copySubWindowData(data: SubWindowData) : SubWindowData {
        return SubWindowData(
            title = data.title,
            icon = data.icon,
            x = data.x,
            y = data.y,
            width = data.width,
            height = data.height,
            content = data.content,
            args = data.args.toMutableMap(),
            id = Random(OsProperties.currentTime().toLong()).nextInt()
        )
    }
}