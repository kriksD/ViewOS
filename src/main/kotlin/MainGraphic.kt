import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.launch
import os.ViewOS
import os.desktop.BottomBar
import os.desktop.WindowArea
import os.manager.NotificationManager
import os.manager.ProgramsCreator
import os.manager.ProgramsManager
import os.properties.BottomBarPosition
import os.properties.OsProperties
import java.awt.im.InputContext
import kotlin.random.Random

fun main() = application {

    val isOpen = remember { mutableStateOf(true) }
    val isAskingToClose = remember { mutableStateOf(false) }
    val state = rememberWindowState(placement = WindowPlacement.Fullscreen)

    if (isOpen.value) {
        Window(
            title = "ViewOS",
            onCloseRequest = { isAskingToClose.value = true },
            state = state,
        ) {

            if (isAskingToClose.value) {
                ViewOS.cancelRunning()
                isOpen.value = false
            }

            WindowManageArea(
                isAskingToClose,
            )
        }
    }
}

@Composable
fun WindowManageArea(
    isAskingToClose: MutableState<Boolean>,
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { mutableStateOf(SnackbarHostState()) }
    NotificationManager.addOnSent {
        scope.launch {
            snackbarHostState.value.showSnackbar(it.toString())
        }
    }
    NotificationManager.add(
        message = "Welcome to ViewOS ${ViewOS.currentVersion}!",
        onClick = {
            ProgramsManager.open(ProgramsCreator.getInstance("Welcome"))
        }
    )

    val programsReload = remember { mutableStateOf(true) }
    ProgramsManager.addOnOpen {
        programsReload.value = false
    }
    ProgramsManager.addOnClose {
        programsReload.value = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        WindowArea(
            modifier = Modifier.align(Alignment.TopCenter),
            programsReload = programsReload,
        )

        BottomBar(
            modifier = Modifier.align(
                when (OsProperties.bottomBarSettings.bottomBarPosition) {
                    BottomBarPosition.Bottom -> Alignment.BottomCenter
                    BottomBarPosition.Top -> Alignment.TopCenter
                }
            ),
            isWindowAskingToClose = isAskingToClose,
            programsReload = programsReload,
        )
    }
    SnackbarHost(snackbarHostState.value)

    if (Random(OsProperties.currentTime().toLong() + OsProperties.currentDate().day).nextInt(100) == 0)
        bsod(isAskingToClose)
}

@Composable
private fun bsod(
    isAskingToClose: MutableState<Boolean>,
) {
    Column(
        modifier = Modifier
            .background(Color.Blue)
            .fillMaxSize()
    ) {
        Text(
            text = "Critical error!",
            color = Color.LightGray
        )
        Text(
            text = "Os version: ${ViewOS.currentVersion}",
            color = Color.LightGray
        )
        Text(
            text = "Programs opened:",
            color = Color.LightGray
        )

        ProgramsManager.getAll().forEach {
            Text(
                text = "${it.title} ${it.args} ${it.icon} ${it.id} ${it.content}",
                color = Color.LightGray
            )
        }

        Text(
            text = "Language: ${InputContext.getInstance().locale}",
            color = Color.LightGray
        )
        Text(
            text = "OsProperties:",
            color = Color.LightGray
        )
        Text(
            text = OsProperties.currentTimeAsString(),
            color = Color.LightGray
        )
        Text(
            text = OsProperties.osStyle.backgroundPath,
            color = Color.LightGray
        )
        Text(
            text = OsProperties.osStyle.bottomBarColor.toString(),
            color = Color.LightGray
        )
        Text(
            text = OsProperties.osStyle.bottomBarTransparency.toString(),
            color = Color.LightGray
        )
        Text(
            text = OsProperties.bottomBarSettings.bottomBarPosition.name,
            color = Color.LightGray
        )
        Text(
            text = OsProperties.bottomBarSettings.iconsInCenter.toString(),
            color = Color.LightGray
        )
        Text(
            text = "Notifications:",
            color = Color.LightGray
        )

        NotificationManager.getAll().forEach {
            Text(
                text = "${it.date} ${it.time} ${it.message} ${it.onClick}",
                color = Color.LightGray
            )
        }

        Button(
            onClick = {
                isAskingToClose.value = true
            }
        ) {
            Text("Shut down")
        }
    }
}