package programs.standart.testProg.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun TestProg() {
    val points = remember { mutableStateOf(setOf<Offset>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consumeAllChanges()

                        val newPoints = points.value.toMutableSet()
                        newPoints.add(change.position)
                        points.value = newPoints
                    }
                },
        ) {
            drawPoints(
                points = points.value.toList(),
                pointMode = PointMode.Polygon,
                color = Color.Red,
                strokeWidth = 2F
            )
        }
    }
}

@Composable
fun TestProg2() {
    val text = remember { mutableStateOf("text") }

    Button(
        onClick = {
            text.value += " text"
        }
    ) {
        Text(text.value)
    }

}