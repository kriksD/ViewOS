package programs.standart.explorer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import os.desktop.SubWindowData
import os.manager.ProgramsCreator
import viewOsUis.Table
import viewOsUis.TableType
import java.io.File

@Composable
fun Explorer(
    programsThatOpen: MutableList<SubWindowData>,
    data: SubWindowData,
    programsReload: MutableState<Boolean>,
) {
    val path = remember { mutableStateOf(
        if (data.args["path"] != null) data.args["path"].toString() else "ViewOS/User"
    ) }
    val tableIsOpen = remember { mutableStateOf(true) }

    val tableData = mapOf(
        Pair("name (${path.value.substringAfterLast("/")})", getFileNamesWithBackRow(path.value)),
        Pair("path (${path.value})", getFilePathsWithBackRow(path.value))
    )

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        ControlButtons(
            path = path.value,
            tableIsOpen = tableIsOpen,
        )

        if (tableIsOpen.value) {
            val clipboard = LocalClipboardManager.current
            val scroll = rememberScrollState()

            Table(
                modifier = Modifier
                    .verticalScroll(scroll),
                data = tableData,
                type = TableType.WITH_COLUMN_TITLE,
                onItemClicked = { text: MutableState<String>, column: Int, row: Int ->
                    tableIsOpen.value = false

                    if (checkForPng(text.value)) {
                        val imageViewer = ProgramsCreator.getInstance("Image Viewer")
                        imageViewer.args["path"] = text.value
                        programsThatOpen.add(imageViewer)
                        programsReload.value = false

                    } else if (checkForTxt(text.value)) {
                        val textEditor = ProgramsCreator.getInstance("Text Editor")
                        textEditor.args["path"] = text.value
                        programsThatOpen.add(textEditor)
                        programsReload.value = false

                    } else if (checkForPath(text.value)) {
                        path.value = text.value
                    } else {
                        clipboard.setText(AnnotatedString(text.value))
                    }

                    data.args["path"] = path.value
                }
            )
        } else {
            tableIsOpen.value = true
        }
    }

}

@Composable
private fun ControlButtons(
    path: String,
    tableIsOpen: MutableState<Boolean>,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val nameOfCreateFile = remember { mutableStateOf("") }

        TextField(
            value = nameOfCreateFile.value,
            onValueChange = {
                nameOfCreateFile.value = it
            },
        )

        Button(
            onClick = {
                val file = File("${path}/${nameOfCreateFile.value}")
                file.createNewFile()
                tableIsOpen.value = false
            },
        ) {
            Text(text = "create")
        }

        Button(
            onClick = {
                val file = File("${path}/${nameOfCreateFile.value}")
                file.mkdir()
                tableIsOpen.value = false
            },
        ) {
            Text("create folder")
        }

        Button(
            onClick = {
                val file = File("${path}/${nameOfCreateFile.value}")

                if (file.exists()) {
                    file.deleteRecursively()
                    tableIsOpen.value = false
                }
            },
        ) {
            Text("delete")
        }
    }
}

private fun getFileNamesWithBackRow(path: String): List<String> {
    val file = File(path)
    val files = file.listFiles()?.toList() ?: emptyList()
    val names = mutableListOf<String>()

    if (checkForBackRow(path)) {
        names.add("back")
    }

    files.forEach {
        names.add(it.name)
    }

    return names
}

private fun getFilePathsWithBackRow(path: String): List<String> {
    val file = File(path)
    val files = file.listFiles()?.toList() ?: emptyList()
    val paths = mutableListOf<String>()

    if (checkForBackRow(path)) {
        paths.add(path.removeRange(path.lastIndexOf("/"), path.length))
    }

    files.forEach {
        paths.add(it.path.replace("\\", "/"))
    }

    return paths
}

private fun checkForBackRow(path: String): Boolean {
    return Regex("ViewOS/.*").containsMatchIn(path)
}

private fun checkForPath(path: String): Boolean {
    return Regex("ViewOS/?.*").containsMatchIn(path)
}

private fun checkForTxt(path: String): Boolean {
    return Regex("ViewOS/.*\\.(txt|vgml)").containsMatchIn(path)
}

private fun checkForPng(path: String): Boolean {
    return Regex("ViewOS/.*\\.(png|jpeg)").containsMatchIn(path)
}