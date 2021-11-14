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
            content = { data -> WelcomeProgram() },
            icon = "osIcon.png"
        ),
        SubWindowData(
            title = "TestProg",
            content = { data -> TestProg() },
            icon = "osIconSecond.png"
        ),
        SubWindowData(
            title = "TestProg2",
            content = { data -> TestProg2() },
            icon = "osIconSecond.png"
        ),
        SubWindowData(
            title = "Explorer",
            width = 900.dp,
            content = { data -> Explorer(data) },
            icon = "explorerIcon.png"
        ),
        SubWindowData(
            title = "Text Editor",
            icon = "textEditorIcon.png",
            content = { data -> TextEditor(data) },
            args = mutableMapOf(Pair("path", "none"))
        ),
        SubWindowData(
            title = "Image Viewer",
            icon = "imageViewerIcon.png",
            content = { data -> ImageViewer(data) },
            args = mutableMapOf(Pair("path", "none"))
        ),
        SubWindowData(
            title = "ABrowser",
            content = { data -> ABrowser(data) },
            icon = "osIconSecond.png"
        ),
        SubWindowData(
            title = "Sapper",
            height = 434.dp,
            width = 500.dp,
            content = { data -> Sapper(data) },
            icon = "osIconSecond.png"
        ),
        SubWindowData(
            title = "VBrowser",
            content = { data -> VBrowser(data) },
            icon = "vBrowserIcon.png"
        ),
        SubWindowData(
            title = "AppMarket",
            content = { data -> AppMarket(data) },
            icon = "appMarketIcon.png"
        ),
        SubWindowData(
            title = "Calculator",
            height = 150.dp,
            content = { data -> Calculator(data) },
            icon = "calculatorIcon.png"
        ),
        SubWindowData(
            title = "GoMaps",
            content = { data -> GoMaps(data) },
            icon = "mapsIcon.png"
        ),
        SubWindowData(
            title = "GoChat",
            content = { data -> GoChat(data) },
            icon = "chatIcon.png"
        ),
        SubWindowData(
            title = "Settings",
            content = { data -> Settings(data) },
            icon = "settingsIcon.png"
        ),
        SubWindowData(
            title = "Country Analytics",
            content = { data -> CountryAnalytics(data) },
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