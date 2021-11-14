package viewOsUis

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import java.io.File

val emptyImageBitmap: ImageBitmap = ImageBitmap(0, 0)

fun getImageBitmap(imagePath: String): ImageBitmap? {
    return if (File(imagePath).exists())
        org.jetbrains.skia.Image.makeFromEncoded(File(imagePath).readBytes()).toComposeImageBitmap()
    else
        null
}

fun getImageBitmap(imageFile: File): ImageBitmap? {
    return if (imageFile.exists())
        org.jetbrains.skia.Image.makeFromEncoded(imageFile.readBytes()).toComposeImageBitmap()
    else
        null
}