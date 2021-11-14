package os.desktop

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import os.manager.NotificationManager
import os.manager.ProgramsCreator
import os.manager.ProgramsManager
import os.properties.OsProperties
import java.awt.im.InputContext
import java.io.File
import kotlin.concurrent.timer

class BottomBar {
    companion object {
        val height = 45.dp
    }
}

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    isWindowAskingToClose: MutableState<Boolean>,
    programsReload: MutableState<Boolean>,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(BottomBar.height)
            .background(
                brush = Brush.horizontalGradient(
                    listOf(
                        OsProperties.osStyle.bottomBarColor,
                        OsProperties.osStyle.bottomBarColor
                    )
                ),
                alpha = OsProperties.osStyle.bottomBarTransparency
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (OsProperties.bottomBarSettings.iconsInCenter) {
            MenuButton(
                isWindowAskingToClose = isWindowAskingToClose,
            )
            IconBar(
                programsReload = programsReload,
            )
        } else if (!OsProperties.bottomBarSettings.iconsInCenter) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MenuButton(
                    isWindowAskingToClose = isWindowAskingToClose,
                )
                IconBar(
                    programsReload = programsReload,
                )
            }
        }

        InfoViewer()
    }
}

@Composable
private fun IconBar(
    programsReload: MutableState<Boolean>,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (programsReload.value) {
            ProgramsManager.getAll().forEach { program ->
                ProgramIcon(
                    icon = painterResource(program.icon),
                    title = program.title,
                    data = program,
                )
            }
        } else {
            programsReload.value = true
        }
    }
}

@Composable
private fun MenuButton(
    isWindowAskingToClose: MutableState<Boolean>,
) {
    val expanded = remember { mutableStateOf(false) }

    Button(
        onClick = {
            expanded.value = true
        },
        shape = RoundedCornerShape(0.dp),
        modifier = Modifier.fillMaxHeight(),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
    ) {
        DropdownMainMenu(
            expanded = expanded,
            isWindowAskingToClose = isWindowAskingToClose,
        )

        Image(
            painter = painterResource("osIcon.png"),
            contentDescription = "os icon"
        )
    }
}

@Composable
private fun DropdownMainMenu(
    expanded: MutableState<Boolean>,
    isWindowAskingToClose: MutableState<Boolean>,
) {
    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false },
        modifier = Modifier
            .background(Color.DarkGray)
    ) {
        val listOfProgramFolders = File("ViewOS/ProgramData").listFiles()
        listOfProgramFolders?.forEach { file ->
            val mPropertiesFile = File("${file.path}/mProperties.txt")
            val inMenu = if (mPropertiesFile.exists()) mPropertiesFile.readText().contains("inMenu:true") else true

            if (inMenu) {
                CustomDropdownItemWithIcon(
                    title = file.nameWithoutExtension,
                    icon = painterResource(ProgramsCreator.getInstance(file.nameWithoutExtension).icon)
                ) {
                    ProgramsManager.open(
                        ProgramsCreator.getInstance(file.nameWithoutExtension)
                    )
                }
            }
        }

        Divider()

        CustomDropdownItem(title = "settings") {
            ProgramsManager.open(ProgramsCreator.getInstance("Settings"))
        }

        CustomDropdownItem(title = "shut down") {
            isWindowAskingToClose.value = true
        }
    }
}

@Composable
private fun InfoViewer() {
    val timeStr = remember { mutableStateOf(OsProperties.currentTimeAsString()) }

    val timerExists = remember { mutableStateOf(false) }
    if (!timerExists.value) {
        timer("gettingTimeAndDate", true, 833, 833) {
            timeStr.value = OsProperties.currentTimeAsString()
            timerExists.value = true
        }
    }

    Row(

    ) {
        Text(
            text = InputContext.getInstance().locale.toString(),
            color = Color.LightGray,
            fontSize = 24.sp,
            modifier = Modifier.padding(4.dp),
            textAlign = TextAlign.Center,
        )

        Text(
            text = timeStr.value,
            color = Color.LightGray,
            fontSize = 24.sp,
            modifier = Modifier.padding(4.dp),
            textAlign = TextAlign.Center,
        )

        val notificationsExpanded = remember { mutableStateOf(false) }

        IconButton(
            onClick = {
                notificationsExpanded.value = true
            }
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "notifications",
                tint = Color.LightGray
            )
            if (NotificationManager.count() > 0) {
                Text(
                    text = NotificationManager.count().toString(),
                    modifier = Modifier
                        .size(16.dp)
                        .offset(10.dp, 6.dp)
                        .background(
                            color = Color.Red,
                            shape = CircleShape
                        ),
                    color = Color.LightGray,
                    textAlign = TextAlign.Center
                )
            }
            DropdownNotificationsMenu(
                expanded = notificationsExpanded,
            )
        }
    }
}

@Composable
private fun DropdownNotificationsMenu(
    expanded: MutableState<Boolean>,
) {
    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false },
        modifier = Modifier
            .background(Color.DarkGray)
    ) {
        NotificationManager.getAll().forEach {
            CustomDropdownItemWithClose(
                title = it.toString(),
                onClick = {
                    it.onClick(it)
                    expanded.value = false
                },
                onClose = {
                    NotificationManager.remove(it)
                }
            )
        }

        Divider()

        CustomDropdownItem(
            title = "close all",
            onClick = {
                NotificationManager.clear()
            }
        )
    }
}

@Composable
private fun CustomDropdownItemWithClose(title: String = "item", onClick: () -> Unit, onClose: () -> Unit) {
    DropdownMenuItem(
        onClick = {
            onClick()
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = Color.White,
                modifier = Modifier
                    .weight(9F),
            )
            IconButton(
                onClick = {
                    onClose()
                },
                modifier = Modifier
                    .weight(1F)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "close notification",
                    tint = Color.LightGray,
                )
            }
        }
    }
}

@Composable
private fun ProgramIcon(
    icon: Painter = painterResource("9.png"),
    title: String = "program",
    data: SubWindowData,
) {
    val expanded = remember { mutableStateOf(false) }

    Button(
        onClick = {
            expanded.value = true

            ProgramsManager.close(data)
            ProgramsManager.open(data)
        },
        modifier = Modifier
            .padding(0.dp)
            .size(40.dp),
        shape = RoundedCornerShape(0.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray),
        contentPadding = PaddingValues(2.dp)
    ) {
        DropdownProgramIconMenu(
            expanded = expanded,
            title = title,
        )

        Column {
            Image(
                painter = icon,
                contentDescription = "program icon",
                modifier = Modifier.size(34.dp),
            )
            Spacer(
                modifier = Modifier
                    .height(4.dp)
                    .fillMaxWidth()
                    .background(Color.Blue)
            )
        }
    }
}

@Composable
private fun DropdownProgramIconMenu(
    expanded: MutableState<Boolean>,
    title: String
) {
    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false },
        modifier = Modifier
            .background(Color.DarkGray),
    ) {
        CustomDropdownItem(title = title) {}
    }
}

@Composable
private fun CustomDropdownItemWithIcon(
    title: String = "item",
    icon: Painter? = null,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        onClick = {
            onClick()
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            icon?.let {
                Image(
                    painter = it,
                    contentDescription = "app icon",
                    modifier = Modifier
                        .size(28.dp)
                )
            }
            Text(
                text = title,
                color = Color.White,
            )
        }
    }
}

@Composable
private fun CustomDropdownItem(title: String = "item", onClick: () -> Unit) {
    DropdownMenuItem(
        onClick = {
            onClick()
        }
    ) {
        Text(
            text = title,
            color = Color.White,
        )
    }
}