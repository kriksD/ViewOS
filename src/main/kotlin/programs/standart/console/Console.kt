package programs.standart.console

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.dp
import os.ViewOS
import os.desktop.SubWindowData
import os.manager.NotificationManager
import os.manager.ProgramsCreator
import os.manager.ProgramsManager
import os.properties.BottomBarPosition
import os.properties.OsProperties
import os.time.Date
import os.time.Time
import java.io.File

private data class Command(
    val name: String,
    val description: String,
    val onInvoke: (args: List<String>) -> Unit
)

@Composable
fun Console(
    data: SubWindowData
) {
    val font = FontFamily(Font(resource = "font/consola.ttf"))

    val command = remember {
        mutableStateOf(
            if (data.args["command"] != null) data.args["command"].toString() else ""
        )
    }
    val text = remember {
        mutableStateOf(
            if (data.args["text"] != null) data.args["text"].toString()
            else "Console for ViewOS ${ViewOS.currentVersion}. Enter command ~help for view available commands."
        )
    }
    val commands = remember {
        mutableListOf(
            Command(
                name = "~open <program>",
                description = "Open program.",
                onInvoke = {
                    if (it.isNotEmpty()) {
                        ProgramsManager.open(ProgramsCreator.getInstance(it[0]))
                        text.value += "\nProgram ${it[0]} is opened."
                    } else {
                        text.value += "\nError! Type a program name."
                    }
                }
            ),
            Command(
                name = "~programsList",
                description = "Show list of programs.",
                onInvoke = {
                    val files = File("ViewOS/ProgramData").listFiles()
                    files?.forEach {
                        text.value += "\n${it.nameWithoutExtension}"
                    }
                }
            ),
            Command(
                name = "~time",
                description = "Show current time.",
                onInvoke = {
                    text.value += "\nTime: ${OsProperties.currentTimeAsString()}"
                }
            ),
            Command(
                name = "~setTime <time>",
                description = "Set current time. In format hh:mm",
                onInvoke = {
                    if (it.isNotEmpty()) {
                        val found = Regex("[012 ]\\d:[0123456]\\d").find(it[0])

                        if (found != null) {
                            OsProperties.setCurrentTime(
                                Time(
                                    it[0].substringBefore(":").toInt(),
                                    it[0].substringAfter(":").toInt()
                                )
                            )
                        } else {
                            text.value += "\nError! Format is wrong."
                        }
                    } else {
                        text.value += "\nError! Type a time."
                    }
                    text.value += "\nNow time is ${OsProperties.currentTimeAsString()}"
                }
            ),
            Command(
                name = "~setDate <date>",
                description = "Set current date.",
                onInvoke = {
                    if (it.isNotEmpty()) {
                        val found = Regex("\\d+").find(it[0])

                        if (found != null) {
                            OsProperties.setCurrentDate(
                                Date(
                                    it[0].toLong()
                                )
                            )
                        } else {
                            text.value += "\nError! Format is wrong."
                        }
                    } else {
                        text.value += "\nError! Type a date."
                    }
                    text.value += "\nNow time is ${OsProperties.currentTimeAsString()}"
                }
            ),
            Command(
                name = "~setIconsInCenter <true|false>",
                description = "Change iconsInCenter.",
                onInvoke = {
                    if (it.isNotEmpty()) {
                        val found = Regex("(true|false)").find(it[0])

                        if (found != null) {
                            OsProperties.bottomBarSettings.iconsInCenter = found.value != "false"
                        } else {
                            text.value += "\nError! Format is wrong."
                        }
                    } else {
                        text.value += "\nError! Type a value."
                    }
                    text.value += "\nNow iconsInCenter is ${OsProperties.bottomBarSettings.iconsInCenter}"
                }
            ),
            Command(
                name = "~setBottomBarPosition <top|bottom>",
                description = "Change bottomBarPosition.",
                onInvoke = {
                    if (it.isNotEmpty()) {
                        val found = Regex("(top|bottom)").find(it[0])

                        if (found != null) {
                            OsProperties.bottomBarSettings.bottomBarPosition = BottomBarPosition.fromString(found.value)
                        } else {
                            text.value += "\nError! Format is wrong."
                        }
                    } else {
                        text.value += "\nError! Type a value."
                    }
                    text.value += "\nNow bottomBarPosition is ${BottomBarPosition.toString(OsProperties.bottomBarSettings.bottomBarPosition)}"
                }
            ),
            Command(
                name = "~setBackgroundPath <path>",
                description = "Change backgroundPath. In format ViewOS/<other path>",
                onInvoke = {
                    if (it.isNotEmpty()) {
                        val found = Regex("ViewOS/.+").find(it[0])

                        if (found != null) {
                            OsProperties.osStyle.backgroundPath = found.value
                        } else {
                            text.value += "\nError! Format is wrong."
                        }
                    } else {
                        text.value += "\nError! Type a value."
                    }
                    text.value += "\nNow backgroundPath is ${OsProperties.osStyle.backgroundPath}"
                }
            ),
            Command(
                name = "~setBottomBarTransparency <float>",
                description = "Change bottomBarTransparency. In format num.num",
                onInvoke = {
                    if (it.isNotEmpty()) {
                        val found = Regex("\\d+\\.\\d+").find(it[0])

                        if (found != null) {
                            OsProperties.osStyle.bottomBarTransparency = found.value.toFloat()
                        } else {
                            text.value += "\nError! Format is wrong."
                        }
                    } else {
                        text.value += "\nError! Type a value."
                    }
                    text.value += "\nNow bottomBarTransparency is ${OsProperties.osStyle.bottomBarTransparency}"
                }
            ),
            Command(
                name = "~setBottomBarColor <rgb>",
                description = "Change bottomBarColor. In format float,float,float where float is num.num",
                onInvoke = {
                    if (it.isNotEmpty()) {
                        val found = Regex("\\d\\.?\\d{0,3},\\d\\.?\\d{0,3},\\d\\.?\\d{0,3}").find(it[0])

                        if (found != null) {
                            val regexResult = Regex("\\d\\.?\\d{0,3}").findAll(found.value)

                            try {
                                OsProperties.osStyle.bottomBarColor = Color(
                                    regexResult.elementAt(0).value.toFloat(),
                                    regexResult.elementAt(1).value.toFloat(),
                                    regexResult.elementAt(2).value.toFloat()
                                )
                            } catch (e: Exception) {
                                NotificationManager.add(e.message ?: "Some error :(")
                            }
                        } else {
                            text.value += "\nError! Format is wrong."
                        }
                    } else {
                        text.value += "\nError! Type a value."
                    }

                    val color = OsProperties.osStyle.bottomBarColor
                    text.value += "\nNow bottomBarColor is ${color.red}, ${color.green}, ${color.blue}"
                }
            ),
        )
    }
    val helpAdded = remember { mutableStateOf(false) }
    if (!helpAdded.value) {
        commands.add(
            Command(
                name = "~help",
                description = "Show all commands.",
                onInvoke = {
                    commands.forEach { eCommand ->
                        text.value += "\n${eCommand.name} - ${eCommand.description}"
                    }
                }
            )
        )

        helpAdded.value = true
    }

    Column(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize()
            .padding(4.dp)
    ) {
        val scroll = rememberScrollState(0)
        Text(
            text = text.value,
            color = Color.Green,
            fontFamily = font,
            modifier = Modifier
                .background(Color.Black)
                .fillMaxWidth()
                .weight(9F)
                .verticalScroll(scroll)
        )
        Row(
            modifier = Modifier
                .background(Color.DarkGray)
                .fillMaxWidth()
                .height(50.dp)
        ) {
            OutlinedTextField(
                value = command.value,
                onValueChange = {
                    command.value = it
                    data.args["command"] = command.value
                },
                modifier = Modifier
                    .weight(9F),
                textStyle = TextStyle(fontFamily = font)
            )
            Button(
                onClick = {
                    text.value += "\n" + command.value

                    val splitCommand = command.value.split(" ")
                    val fCommand = commands.find { it.name.contains(splitCommand[0]) }
                    fCommand?.let { it.onInvoke(splitCommand.subList(1, splitCommand.size)) }

                    command.value = ""
                    data.args["command"] = command.value
                    data.args["text"] = text.value
                },
            ) {
                Text("->")
            }
        }
    }
}