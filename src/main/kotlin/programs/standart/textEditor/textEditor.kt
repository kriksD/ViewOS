package programs.standart.textEditor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import os.desktop.SubWindowData
import java.io.File

@Composable
fun TextEditor(
    data: SubWindowData,
) {
    var textFromFile = ""
    if (data.args["path"] != null) {
        textFromFile = File(data.args["path"].toString()).readText()
    }
    val text = remember { mutableStateOf(TextFieldValue(textFromFile)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(data.args["path"].toString())

        Divider()

        BasicTextField(
            value = text.value,
            onValueChange = {
                text.value = it
                File(data.args["path"].toString()).writeText(it.text)
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
