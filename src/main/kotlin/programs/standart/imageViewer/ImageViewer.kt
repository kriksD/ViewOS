package programs.standart.imageViewer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import os.desktop.SubWindowData
import java.io.File


@Composable
fun ImageViewer(
    data: SubWindowData,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Text(data.args["path"].toString())

        Divider()

        val imgFile = File(data.args["path"].toString())
        val image = org.jetbrains.skia.Image.makeFromEncoded(imgFile.readBytes()).toComposeImageBitmap()

        Image(
            bitmap = image,
            contentDescription = "image",
            contentScale = ContentScale.Fit
        )
    }
}