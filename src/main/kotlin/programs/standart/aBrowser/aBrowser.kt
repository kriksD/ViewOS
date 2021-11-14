package programs.standart.aBrowser

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import os.desktop.SubWindowData
import java.io.File

@Composable
fun ABrowser(
    data: SubWindowData,
) {

    val url = remember {
        mutableStateOf(
            if (data.args["url"] != null) data.args["url"].toString() else "http-aglela"
        )
    }
    val pageContent = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        val pageFile = File("D:/myProgs/vsProgs/AgOS/x64/Release/AgOS/programFiles/Browser/sites/${url.value}.html")

        if (pageFile.exists()) {
            pageContent.value = pageFile.readText()
        }

        TextField(
            value = url.value,
            onValueChange = {
                url.value = it
                data.args["url"] = url.value
            },
            modifier = Modifier.fillMaxWidth()
        )

        val scrollState = remember { mutableStateOf(0) }

        Box(
            modifier = Modifier.fillMaxSize().verticalScroll(ScrollState(scrollState.value))
        ) {
            Text(pageContent.value.replace(Regex("<[^<>]*>"), ""))
        }
    }
}