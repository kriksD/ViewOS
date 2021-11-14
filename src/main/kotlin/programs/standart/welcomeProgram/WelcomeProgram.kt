package programs.standart.welcomeProgram

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import os.ViewOS

@Composable
fun WelcomeProgram() {
    val backgroundColor = if (ViewOS.currentVersion.contains(Regex("alpha|beta"))) {
        Color.Red
    } else {
        Color.Gray
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource("osIcon.png"),
            contentDescription = "os icon"
        )
        Column {
            Text("Welcome to ViewOS!", fontSize = 22.sp, color = Color.DarkGray, textAlign = TextAlign.Center)
            Text("version: ${ViewOS.currentVersion}", fontSize = 12.sp, color = Color.DarkGray, textAlign = TextAlign.Center)
        }
    }
}