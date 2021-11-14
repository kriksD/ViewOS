package os.desktop

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import os.manager.Notification
import java.awt.Dimension
import java.awt.Toolkit
import kotlin.math.roundToInt

data class SubWindowData(
    val title: String = "program",
    val icon: String = "9.png",
    var x: Float = 10f,
    var y: Float = 10f,
    var width: Dp = 500.dp,
    var height: Dp = 500.dp,
    val content: @Composable (
        data: SubWindowData,
        programsThatOpen: MutableList<SubWindowData>,
        programsReload: MutableState<Boolean>,
    ) -> Unit = { data, pio, reload -> },
    val args: MutableMap<String, Any> = mutableMapOf(),
    val id: Int = 0
) {
    fun getContentHeight(): Dp {
        return height - SubWindow.statusBarHeight - SubWindow.controlBarHeight
    }
}

class SubWindow {
    companion object {
        val controlBarHeight = 35.dp
        val statusBarHeight = 20.dp

        val minimumWidth = 300.dp
        val minimumHeight = 100.dp
    }
}

@Composable
fun SubWindow(
    title: String,
    icon: Painter = painterResource("9.png"),
    x: Float = 10f,
    y: Float = 10f,
    width: Dp = 500.dp,
    height: Dp = 500.dp,
    programsThatOpen: MutableList<SubWindowData>?,
    data: SubWindowData?,
    programsReload: MutableState<Boolean>?,
    content: @Composable () -> Unit
) {
    val offsetX = remember { mutableStateOf(x) }
    val offsetY = remember { mutableStateOf(y) }
    val windowWidth = remember { mutableStateOf(width) }
    val windowHeight = remember { mutableStateOf(height) }

    val saveData = remember { mutableStateOf(true) }

    if (saveData.value) {
        data?.width = windowWidth.value
        data?.height = windowHeight.value
        data?.x = offsetX.value
        data?.y = offsetY.value

        saveData.value = false
    }

    val windowCornerShape = RoundedCornerShape(16.dp)

    Column(
        Modifier
            .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
            .width(windowWidth.value)
            .height(windowHeight.value)
            .border(1.dp, Color.Black, windowCornerShape)
            .clip(windowCornerShape)
            .background(Color.Gray),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        ControlBar(
            icon = icon,
            title = title,
            offsetX = offsetX,
            offsetY = offsetY,
            windowHeight = windowHeight,
            windowWidth = windowWidth,
            programsThatOpen = programsThatOpen,
            data = data,
            programsReload = programsReload,
            saveData = saveData,
        )
        ContentArea(
            windowHeight,
            content
        )
        StatusBar(
            windowHeight,
            windowWidth,
            saveData,
        )
    }
}

@Composable
private fun ControlBar(
    title: String,
    icon: Painter,
    offsetX: MutableState<Float>,
    offsetY: MutableState<Float>,
    windowWidth: MutableState<Dp>,
    windowHeight: MutableState<Dp>,
    programsThatOpen: MutableList<SubWindowData>?,
    data: SubWindowData?,
    programsReload: MutableState<Boolean>?,
    saveData: MutableState<Boolean>,
) {
    Row(
        modifier = Modifier
            .background(Color.Blue)
            .fillMaxWidth()
            .height(SubWindow.controlBarHeight)
            .border(1.dp, Color.Black)
            .clickable {
                programsThatOpen?.remove(data)
                data?.let { programsThatOpen?.add(it) }
                programsReload?.value = false

            }.pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consumeAllChanges()

                    val size: Dimension = Toolkit.getDefaultToolkit().screenSize

                    offsetX.value += dragAmount.x
                    if (offsetY.value < size.height - BottomBar.height.value - SubWindow.controlBarHeight.value) {
                        offsetY.value += dragAmount.y
                    } else {
                        offsetY.value = size.height - BottomBar.height.value - SubWindow.controlBarHeight.value - 1
                    }

                    saveData.value = true
                }
            },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ControlBarInfo(title, icon)
        ControlBarButtons(
            offsetX = offsetX,
            offsetY = offsetY,
            windowWidth = windowWidth,
            windowHeight = windowHeight,
            programsThatOpen = programsThatOpen,
            data = data,
            programsReload = programsReload,
            saveData = saveData,
        )
    }
}

@Composable
private fun ControlBarInfo(
    title: String,
    icon: Painter
) {
    Row {
        Image(
            painter = icon,
            contentDescription = "program icon",
            modifier = Modifier.padding(8.dp).size(20.dp),
        )
        Spacer(Modifier.height(16.dp))

        Text(title, modifier = Modifier.padding(8.dp), color = Color.White)
    }
}

@Composable
private fun ControlBarButtons(
    offsetX: MutableState<Float>,
    offsetY: MutableState<Float>,
    windowWidth: MutableState<Dp>,
    windowHeight: MutableState<Dp>,
    programsThatOpen: MutableList<SubWindowData>?,
    data: SubWindowData?,
    programsReload: MutableState<Boolean>?,
    saveData: MutableState<Boolean>,
) {
    Row {
        Button(
            onClick = {
                windowWidth.value = SubWindow.minimumWidth
                windowHeight.value = SubWindow.minimumHeight

                saveData.value = true
            },
            shape = RoundedCornerShape(0.dp)
        ) {
            Text("-")
        }

        Button(
            onClick = {
                val size: Dimension = Toolkit.getDefaultToolkit().screenSize

                windowWidth.value = size.width.dp
                windowHeight.value = size.height.dp - BottomBar.height
                offsetX.value = 0f
                offsetY.value = 0f

                saveData.value = true
            },
            shape = RoundedCornerShape(0.dp)
        ) {
            Text("□")
        }

        Button(
            onClick = {
                programsThatOpen?.remove(data)
                programsReload?.value = false
            },
            shape = RoundedCornerShape(0.dp)
        ) {
            Text("x")
        }
    }
}

@Composable
private fun ContentArea(
    windowHeight: MutableState<Dp>,
    content: @Composable () -> Unit
) {
    Box(
        Modifier
            .height(windowHeight.value - SubWindow.controlBarHeight - SubWindow.statusBarHeight)
            .fillMaxWidth()
    ) {
        content()
    }
}

@Composable
private fun StatusBar(
    windowHeight: MutableState<Dp>,
    windowWidth: MutableState<Dp>,
    saveData: MutableState<Boolean>,
) {
    Row(
        modifier = Modifier
            .background(Color.Blue)
            .height(20.dp)
            .fillMaxWidth()
            .border(1.dp, Color.Black),
        horizontalArrangement = Arrangement.End
    ) {
        Text(text = "=",
            color = Color.LightGray,
            modifier = Modifier
                .background(Color.Blue)
                .width(SubWindow.statusBarHeight)
                .fillMaxHeight()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consumeAllChanges()

                        if (windowWidth.value > SubWindow.minimumWidth) {
                            windowWidth.value += dragAmount.x.dp
                        } else {
                            windowWidth.value = SubWindow.minimumWidth + 1.dp
                        }

                        if (windowHeight.value > SubWindow.minimumHeight) {
                            windowHeight.value += dragAmount.y.dp
                        } else {
                            windowHeight.value = SubWindow.minimumHeight + 1.dp
                        }

                        saveData.value = true
                    }
                }
        )
    }
}