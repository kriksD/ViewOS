package os.desktop

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import os.manager.Notification
import os.manager.ProgramsManager
import os.properties.OsProperties
import java.io.File

@Composable
fun WindowArea(
    modifier: Modifier,
    programsReload: MutableState<Boolean>,
) {

    Box(
        modifier = modifier.fillMaxSize()
    ) {

        Image(
            bitmap = org.jetbrains.skia.Image.makeFromEncoded(File(OsProperties.osStyle.backgroundPath).readBytes())
                .toComposeImageBitmap(),
            contentDescription = "fon image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
        )

        if (programsReload.value) {
            ProgramsManager.getAll().forEach { data ->
                SubWindow(
                    x = data.x,
                    y = data.y,
                    width = data.width,
                    height = data.height,
                    title = data.title,
                    icon = painterResource(data.icon),
                    data = data,
                ) {
                    data.content(data)
                }
            }
        } else {
            programsReload.value = true
        }
    }
}