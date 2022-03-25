package viewOsAppends

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Line(
    modifier: Modifier = Modifier,
    width: Dp = 4.dp,
    color: Color = Color.DarkGray,
    orientation: Orientation = Orientation.Vertical,
    padding: Dp = 0.dp
) {
    if (orientation == Orientation.Vertical) {
        Box(
            modifier = modifier
                .padding(padding)
                .background(color)
                .width(width)
                .fillMaxHeight(),
        ) {}
    } else {
        Box(
            modifier = modifier
                .padding(padding)
                .background(color)
                .height(width)
                .fillMaxWidth(),
        ) {}
    }
}