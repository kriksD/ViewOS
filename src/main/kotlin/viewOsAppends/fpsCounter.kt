package viewOsAppends

import androidx.compose.runtime.*

@Composable
fun fpsCount(): Int {
    var lastUpdate = 0L
    var fpsCount = 0
    val fps = remember { mutableStateOf(0)}

    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis { ms ->
                fpsCount++
                if (fpsCount == 10) {
                    fps.value = (10000 / (ms - lastUpdate)).toInt()
                    lastUpdate = ms
                    fpsCount = 0
                }
            }
        }
    }

    return fps.value
}