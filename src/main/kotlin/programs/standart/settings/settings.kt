package programs.standart.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import os.desktop.SubWindowData
import os.properties.BottomBarPosition
import os.properties.OsProperties
import os.time.Date
import os.time.Time
import viewOsUis.TextCheckbox
import java.io.File

@Composable
fun Settings(
    data: SubWindowData,
) {
    val iconsInCenter = remember { mutableStateOf(OsProperties.bottomBarSettings.iconsInCenter) }
    val bottomBarPosition = remember { mutableStateOf(OsProperties.bottomBarSettings.bottomBarPosition) }
    val timeText = remember { mutableStateOf(OsProperties.currentTime().toString()) }
    val dateDay = remember { mutableStateOf(OsProperties.currentDate().day.toString()) }
    val backgroundPath = remember { mutableStateOf(OsProperties.osStyle.backgroundPath) }
    val bottomBarTransparency = remember { mutableStateOf(OsProperties.osStyle.bottomBarTransparency) }
    val bottomBarColor = remember { mutableStateOf(OsProperties.osStyle.bottomBarColor) }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState(0))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Restart OS for confirm changes!",
            color = Color.Red,
            fontSize = 18.sp
        )

        Divider()

        Text(
            text = "Bottom bar",
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            fontSize = 22.sp
        )
        TextCheckbox(
            text = "Icons in center",
            check = iconsInCenter.value,
            textColor = Color.DarkGray,
            onCheckedChange = {
                iconsInCenter.value = it
                OsProperties.bottomBarSettings.iconsInCenter = it
            }
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Position",
                color = Color.DarkGray,
                fontSize = 16.sp,
            )
            val expandedBBPDropdownMenu = remember { mutableStateOf(false) }
            Button(
                onClick = {
                    expandedBBPDropdownMenu.value = true
                }
            ) {
                Text(BottomBarPosition.toString(bottomBarPosition.value))

                DropdownMenu(
                    expanded = expandedBBPDropdownMenu.value,
                    onDismissRequest = {
                        expandedBBPDropdownMenu.value = false
                    }
                ) {
                    BottomBarPosition.values().forEach {
                        DropdownMenuItem(
                            onClick = {
                                bottomBarPosition.value = it
                                OsProperties.bottomBarSettings.bottomBarPosition = it
                            },
                        ) {
                            Text(BottomBarPosition.toString(it))
                        }
                    }
                }
            }
        }

        Divider()

        Text(
            text = "Date and time",
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            fontSize = 22.sp
        )
        TextField(
            value = timeText.value,
            onValueChange = {
                timeText.value = it

                OsProperties.setCurrentTime(Time.fromString(it) ?: OsProperties.currentTime())
            },
            label = { Text("time") }
        )
        TextField(
            value = dateDay.value,
            onValueChange = {
                dateDay.value = it

                if (it.contains(Regex("\\s*\\d*\\s*")))
                    OsProperties.setCurrentDate(Date(it.replace(" ", "").toLong()))
            },
            label = { Text("date") }
        )

        Divider()

        Text(
            text = "Style",
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            fontSize = 22.sp
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = backgroundPath.value,
            onValueChange = {
                backgroundPath.value = it

                if (File(it).exists())
                    OsProperties.osStyle.backgroundPath = it
            },
            label = { Text("background image") }
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Bottom bar transparency",
                color = Color.DarkGray,
                fontSize = 16.sp,
            )
            Slider(
                value = bottomBarTransparency.value,
                onValueChange = {
                    bottomBarTransparency.value = it

                    OsProperties.osStyle.bottomBarTransparency = it
                },
                steps = 9,
                modifier = Modifier
                    .width(250.dp)
            )
            Text(
                text = String.format("%.1f", bottomBarTransparency.value),
                color = Color.DarkGray,
                fontSize = 16.sp,
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Bottom bar color",
                color = Color.DarkGray,
                fontSize = 16.sp,
            )
            val expandedCPDropdownMenu = remember { mutableStateOf(false) }
            val colors = listOf(
                Color.Black, Color.DarkGray, Color.Gray,
                Color.LightGray, Color.White, Color.Blue,
                Color.Red, Color.Cyan, Color.Magenta,
                Color.Green, Color.Yellow
            )
            Button(
                onClick = {
                    expandedCPDropdownMenu.value = true
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = bottomBarColor.value)
            ) {
                DropdownMenu(
                    expanded = expandedCPDropdownMenu.value,
                    onDismissRequest = {
                        expandedCPDropdownMenu.value = false
                    }
                ) {
                    colors.forEach {
                        DropdownMenuItem(
                            onClick = {
                                bottomBarColor.value = it
                                OsProperties.osStyle.bottomBarColor = it
                            },
                            modifier = Modifier
                                .background(it)
                        ) {

                        }
                    }
                }
            }
        }

        Divider()
    }
}