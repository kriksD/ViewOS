package programs.other.google.goMaps

import androidx.compose.ui.graphics.ImageBitmap

data class MapFragment(
    val image: ImageBitmap?,
    val positionX: Float,
    val positionY: Float
)
