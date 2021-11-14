import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import kotlin.concurrent.timer

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
}