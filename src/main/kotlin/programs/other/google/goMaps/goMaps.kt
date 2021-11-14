package programs.other.google.goMaps

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.skia.Image
import os.desktop.SubWindowData
import java.io.File
import kotlin.math.abs

@Composable
fun GoMaps(
    data: SubWindowData,
) {
    val lastUpdate = remember { mutableStateOf(0L) }
    val time = remember { mutableStateOf(0L) }

    val fpsCount = remember { mutableStateOf(0) }
    val fps = remember { mutableStateOf(0)}

    val x = remember { mutableStateOf(
        if (data.args["x"] != null) data.args["x"] as Dp else 0.dp
    ) }
    val y = remember { mutableStateOf(
        if (data.args["y"] != null) data.args["y"] as Dp else 0.dp
    ) }
    val roads = listOf(1)
    val fragments = remember { File("ViewOS/ProgramData/GoMaps/maps").listFiles() }
    val maxY = 1
    val minY = -2
    val maxX = 4
    val minX = -1
    val x1FragmentSize = 774

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
            .background(Color.Black)
    ) {
        Canvas(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consumeAllChanges()

                        x.value += dragAmount.x.dp
                        y.value += dragAmount.y.dp

                        data.args["x"] = x.value
                        data.args["y"] = y.value
                    }
                }
                .offset(x.value, y.value)
                .fillMaxSize(),
        ) {
            val currentMinX = (abs(x.value.value.toInt()) / x1FragmentSize) + minX
            val currentMaxX = ((abs(x.value.value.toInt()) + data.width.value.toInt()) / x1FragmentSize) - 1
            val currentMinY = (abs(y.value.value.toInt()) / x1FragmentSize) + minY
            val currentMaxY = ((abs(y.value.value.toInt()) + data.getContentHeight().value.toInt()) / x1FragmentSize) - 2

            for (ix in currentMinX..currentMaxX) {
                for (iy in currentMinY..currentMaxY) {
                    val imageFile = fragments?.find { file ->
                        file.name.equals("${ix}_${iy}.png")
                    }
                    val image = imageFile?.let { file ->
                        Image.makeFromEncoded(file.readBytes()).toComposeImageBitmap()
                    }

                    val px = (ix + abs(minX)) * x1FragmentSize
                    val py = (iy + abs(minY)) * x1FragmentSize

                    image?.let { imageBitmap ->
                        drawImage(
                            image = imageBitmap,
                            topLeft = Offset(px.toFloat(), py.toFloat())
                        )
                    }
                }
            }
        }
        Text(
            text = "${fps.value} fps",
            fontSize = 24.sp,
            color = Color.LightGray,
            modifier = Modifier
                .padding(4.dp)
                .background(Color.DarkGray, shape = RoundedCornerShape(4.dp))
                .padding(4.dp)
                .align(Alignment.TopEnd)
        )
    }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis { ms ->
                time.value = ms
                fpsCount.value++
                if (fpsCount.value == 10) {
                    fps.value = (10000 / (ms - lastUpdate.value)).toInt()
                    lastUpdate.value = ms
                    fpsCount.value = 0
                }
            }
        }
    }
}