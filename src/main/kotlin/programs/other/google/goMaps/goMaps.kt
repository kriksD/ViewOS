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
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import os.desktop.SubWindowData
import viewOsAppends.fpsCount
import viewOsAppends.getImageBitmap
import java.io.File
import kotlin.math.abs

@Composable
fun GoMaps(
    data: SubWindowData,
) {
    val fps = remember { mutableStateOf(0)}

    val x = remember { mutableStateOf(
        if (data.args["x"] != null) data.args["x"] as Dp else 0.dp
    ) }
    val y = remember { mutableStateOf(
        if (data.args["y"] != null) data.args["y"] as Dp else 0.dp
    ) }
    val roads = listOf(1)
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
            getVisibleFragments(
                areaWidth = data.width.value.toInt(),
                areaHeight = data.getContentHeight().value.toInt(),
                position = Offset(x.value.value, y.value.value),
                minX = minX,
                minY = minY,
                fragmentSize = x1FragmentSize
            ).forEach { fragment ->
                fragment.image?.let { imageBitmap ->
                    drawImage(
                        image = imageBitmap,
                        topLeft = Offset(fragment.positionX, fragment.positionY)
                    )
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

    fps.value = fpsCount()
}

private fun getVisibleFragments(areaWidth: Int, areaHeight: Int, position: Offset, minX: Int, minY: Int, fragmentSize: Int): List<MapFragment> {
    val currentMinX = (abs(position.x).toInt() / fragmentSize) + minX
    val currentMaxX = ((abs(position.x).toInt() + areaWidth) / fragmentSize) - 1
    val currentMinY = (abs(position.y).toInt() / fragmentSize) + minY
    val currentMaxY = ((abs(position.y).toInt() + areaHeight) / fragmentSize) - 2

    val fragments = mutableListOf<MapFragment>()
    val fragmentFiles = File("ViewOS/ProgramData/GoMaps/maps").listFiles()

    for (ix in currentMinX..currentMaxX) {
        for (iy in currentMinY..currentMaxY) {
            val imageFile = fragmentFiles?.find { file ->
                file.name.equals("${ix}_${iy}.png")
            }
            val image = imageFile?.let { file ->
                getImageBitmap(file)
            }

            val px = (ix + abs(minX)) * fragmentSize
            val py = (iy + abs(minY)) * fragmentSize

            fragments.add(
                MapFragment(
                    image = image,
                    positionX = px.toFloat(),
                    positionY = py.toFloat()
                )
            )
        }
    }

    return fragments
}