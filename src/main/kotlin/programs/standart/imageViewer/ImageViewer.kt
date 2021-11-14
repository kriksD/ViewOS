package programs.standart.imageViewer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import os.desktop.SubWindowData
import os.properties.OsProperties
import java.io.File


@Composable
fun ImageViewer(
    programsThatOpen: MutableList<SubWindowData>,
    data: SubWindowData,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Text(data.args["path"].toString())

        Divider()

        /*val zoom = remember { mutableStateOf(1F) }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Slider(
                value = zoom.value,
                onValueChange = {
                    zoom.value = it
                },
                valueRange = 0F..3F,
                steps = 40,
                modifier = Modifier
                    .width(300.dp)
            )
            Text(
                text = String.format("%.1f", zoom.value).replace(",","."),
                color = Color.DarkGray,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }*/

        val imgFile = File(data.args["path"].toString())
        val image = org.jetbrains.skia.Image.makeFromEncoded(imgFile.readBytes()).toComposeImageBitmap()

        Image(
            bitmap = image,
            contentDescription = "image",
            modifier = Modifier,
                //.width((image.width * zoom.value).dp)
                //.height((image.height * zoom.value).dp),
            contentScale = ContentScale.Fit
        )
    }
}