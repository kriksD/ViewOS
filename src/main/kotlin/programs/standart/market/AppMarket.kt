package programs.standart.market

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import os.desktop.SubWindowData
import os.manager.Notification
import os.manager.NotificationManager
import os.time.Date
import viewOsUis.emptyImageBitmap
import viewOsUis.getImageBitmap
import java.io.File

private data class AppVersion(
    val version: String,
    val date: Date
)

private data class AppInfo(
    val name: String,
    val description: String,
    val developer: String,
    val filesPath: String,
    val price: Int,
    val isPurchased: Boolean,
    val imagesPath: List<String>,
    val versions: List<AppVersion>
) {
    companion object {
        fun getEmptyInstance(): AppInfo {
            return AppInfo(
                "",
                "",
                "",
                "",
                0,
                false,
                emptyList(),
                emptyList()
            )
        }
    }
}

private enum class ScreenType {
    LIST, APP_INFO, RELOAD
}

@Composable
fun AppMarket(
    programsThatOpen: MutableList<SubWindowData>,
    data: SubWindowData,
) {
    CircularProgressIndicator()

    val textForFind = remember {
        mutableStateOf(
            if (data.args["find"] != null) data.args["find"].toString() else ""
        )
    }

    val appsList = getAppList()
    val apps = remember {
        mutableStateOf(
            if (textForFind.value != "") appsList.filter {
                it.name.contains(textForFind.value) ||
                        it.description.contains(textForFind.value) ||
                        it.developer.contains(textForFind.value) ||
                        it.versions.last().version.contains(textForFind.value) ||
                        "${it.versions.last().date.day}d".contains(textForFind.value) ||
                        it.price.toString().contains(textForFind.value)
            } else appsList
        )
    }

    val screen = remember {
        mutableStateOf(
            if (data.args["screen"] != null) data.args["screen"] as ScreenType else ScreenType.LIST
        )
    }
    val selectedApp = remember {
        mutableStateOf(
            if (data.args["selected"] != null) data.args["selected"] as AppInfo else AppInfo.getEmptyInstance()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TopBar(
            textForFind = textForFind,
            apps = apps,
            data = data
        )

        when (screen.value) {
            ScreenType.LIST -> {
                AppsList(
                    apps = apps.value,
                    selectedApp = selectedApp,
                    screen = screen,
                    data = data
                )

            }
            ScreenType.APP_INFO -> {
                AppInfoPage(
                    selectedApp = selectedApp.value,
                    screen = screen,
                    data = data,
                )

            }
            ScreenType.RELOAD -> {
                screen.value = ScreenType.LIST
            }
        }
    }
}

@Composable
private fun TopBar(
    textForFind: MutableState<String>,
    apps: MutableState<List<AppInfo>>,
    data: SubWindowData,
) {
    Row(
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
            .background(Color.LightGray)
    ) {
        val allApps = getAppList()

        TextField(
            value = textForFind.value,
            onValueChange = { text ->
                textForFind.value = text
                apps.value = allApps.filter {
                    it.name.contains(textForFind.value) ||
                            it.description.contains(textForFind.value) ||
                            it.developer.contains(textForFind.value) ||
                            it.versions.last().version.contains(textForFind.value) ||
                            "${it.versions.last().date.day}d".contains(textForFind.value) ||
                            it.price.toString().contains(textForFind.value)
                }

                data.args["find"] = textForFind.value
            }
        )
    }
}

@Composable
private fun AppsList(
    apps: List<AppInfo>,
    selectedApp: MutableState<AppInfo>,
    screen: MutableState<ScreenType>,
    data: SubWindowData,
) {
    val scroll = remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .verticalScroll(ScrollState(scroll.value))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val rowCount = apps.size / 4 + 1
        var appIndex = 0

        repeat(rowCount) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (i in 1..4) {
                    if (appIndex < apps.size)
                        AppInfoViewer(
                            apps[appIndex],
                            onClick = {
                                selectedApp.value = it
                                screen.value = ScreenType.APP_INFO

                                data.args["selected"] = selectedApp.value
                                data.args["screen"] = screen.value
                            }
                        )
                    appIndex++
                }
            }
        }
    }
}

@Composable
private fun AppInfoViewer(
    appInfo: AppInfo,
    onClick: (appInfo: AppInfo) -> Unit
) {
    Row(
        modifier = Modifier
            .clickable {
                onClick(appInfo)
            }.background(Color.DarkGray)
            .padding(8.dp)
            .width(450.dp)
            .height(160.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val image = if (appInfo.imagesPath.isNotEmpty()) {
            getImageBitmap(appInfo.imagesPath[0])
                ?: emptyImageBitmap
        } else {
            getImageBitmap("ViewOS/Properties/Style/osIconSecond.png")
                ?: emptyImageBitmap
        }

        Image(
            bitmap = image,
            contentDescription = "app image",
            modifier = Modifier
                .width(200.dp)
        )

        Column {
            Text(
                text = appInfo.name,
                color = Color.LightGray,
                fontSize = 26.sp
            )
            Text(
                text = "dev: ${appInfo.developer}",
                color = Color.LightGray,
                fontSize = 14.sp
            )
            Text(
                text = "version: ${appInfo.versions.last().version}",
                color = Color.LightGray,
                fontSize = 14.sp
            )
            val price = appInfo.price
            Text(
                text = "price: ${if (price <= 0) "free" else price}",
                color = Color.LightGray,
                fontSize = 14.sp
            )
            Text(
                text = appInfo.description,
                color = Color.LightGray,
                fontSize = 18.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 3,
            )
        }
    }
}

@Composable
private fun AppInfoPage(
    selectedApp: AppInfo,
    screen: MutableState<ScreenType>,
    data: SubWindowData,
) {
    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
            .background(Color.DarkGray)
            .verticalScroll(scroll),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AppInfoPageButtons(
            selectedApp = selectedApp,
            screen = screen,
            data = data,
        )
        AppInfoPageViewer(selectedApp = selectedApp)
        Text(
            modifier = Modifier
                .padding(8.dp),
            text = selectedApp.description,
            color = Color.LightGray,
            fontSize = 18.sp
        )
        ImageViewer(images = getAllPng(selectedApp.imagesPath))
        VersionsViewer(versions = selectedApp.versions)
    }
}

@Composable
private fun AppInfoPageButtons(
    selectedApp: AppInfo,
    screen: MutableState<ScreenType>,
    data: SubWindowData,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            modifier = Modifier
                .padding(8.dp),
            onClick = {
                screen.value = ScreenType.LIST
                data.args["screen"] = screen.value
            }
        ) {
            Text("Back")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            val mProperties = File("ViewOS/ProgramData/${selectedApp.name}/mProperties.txt")

            var currentVersion = ""
            if (mProperties.exists()) {
                val currentVersionS =
                    Regex("version:[^\n]*\n").find(mProperties.readText())?.value ?: "version: unknown"
                currentVersion = currentVersionS.substringAfter("version:").replace("\n", "")
            }

            val updateVisible =
                remember { mutableStateOf(currentVersion != selectedApp.versions.last().version && mProperties.exists()) }
            val updateCurrentVersion = remember { mutableStateOf(currentVersion) }
            val downloadVisible = remember { mutableStateOf(!mProperties.exists()) }
            val deleteVisible = remember { mutableStateOf(mProperties.exists()) }

            if (selectedApp.price > 0) {
                Text(
                    text = "price: ${selectedApp.price}",
                    color = Color.LightGray,
                    fontSize = 18.sp
                )
            }

            if (updateVisible.value) {
                Text(
                    text = "Current version ${updateCurrentVersion.value}",
                    color = Color.Red,
                    fontSize = 18.sp
                )
                Button(
                    onClick = {
                        val propertiesFile = File("ViewOS/ProgramData/${selectedApp.name}/mProperties.txt")
                        var newProperties = ""
                        propertiesFile.readText().split("\n").forEach {
                            newProperties += if (it.contains(Regex("version:"))) {
                                "version:${selectedApp.versions.last().version}\n"
                            } else {
                                "$it\n"
                            }
                        }
                        propertiesFile.writeText(newProperties)

                        NotificationManager.add(
                            Notification.getCurrentTimeInstance(
                                "App ${selectedApp.name} updated to version ${selectedApp.versions.last().version}!"
                            )
                        )
                        updateVisible.value = false
                    }
                ) {
                    Text("Update")
                }
            }

            if (downloadVisible.value) {
                val purchasedFile = File("Hidden files/Purchased.txt")
                val purchased = if (purchasedFile.exists()) {
                    purchasedFile.readText().split("\n")
                } else {
                    emptyList()
                }

                if (selectedApp.price > 0) {
                    if (purchased.contains(selectedApp.name)) {
                        Button(
                            modifier = Modifier
                                .padding(8.dp),
                            onClick = {
                                File(selectedApp.filesPath).copyRecursively(File("ViewOS/ProgramData/${selectedApp.name}"))
                                File("ViewOS/ProgramData/${selectedApp.name}/mProperties.txt").appendText(
                                    "\nversion:${selectedApp.versions.last().version}"
                                )

                                NotificationManager.add(
                                    Notification.getCurrentTimeInstance(
                                        "App ${selectedApp.name} downloaded!"
                                    )
                                )

                                deleteVisible.value = true
                                downloadVisible.value = false
                            }
                        ) {
                            Text("Download (you already have it app)")
                        }
                    } else {
                        Button(
                            modifier = Modifier
                                .padding(8.dp),
                            onClick = {
                                File(selectedApp.filesPath).copyRecursively(File("ViewOS/ProgramData/${selectedApp.name}"))
                                File("ViewOS/ProgramData/${selectedApp.name}/mProperties.txt").appendText(
                                    "\nversion:${selectedApp.versions.last().version}"
                                )

                                purchasedFile.appendText("${selectedApp.name}\n")

                                NotificationManager.add(
                                    Notification.getCurrentTimeInstance(
                                        "App ${selectedApp.name} purchased!"
                                    )
                                )

                                deleteVisible.value = true
                                downloadVisible.value = false
                            }
                        ) {
                            Text("Buy")
                        }
                    }
                } else {
                    Button(
                        modifier = Modifier
                            .padding(8.dp),
                        onClick = {
                            File(selectedApp.filesPath).copyRecursively(File("ViewOS/ProgramData/${selectedApp.name}"))
                            File("ViewOS/ProgramData/${selectedApp.name}/mProperties.txt").appendText(
                                "\nversion:${selectedApp.versions.last().version}"
                            )

                            NotificationManager.add(
                                Notification.getCurrentTimeInstance(
                                    "App ${selectedApp.name} downloaded!"
                                )
                            )

                            deleteVisible.value = true
                            downloadVisible.value = false
                        }
                    ) {
                        Text("Download")
                    }
                }
            }

            if (deleteVisible.value) {
                Button(
                    modifier = Modifier
                        .padding(8.dp),
                    onClick = {
                        File("ViewOS/ProgramData/${selectedApp.name}").deleteRecursively()

                        NotificationManager.add(
                            Notification.getCurrentTimeInstance(
                                "App ${selectedApp.name} deleted!",
                            )
                        )

                        deleteVisible.value = false
                        downloadVisible.value = true
                        updateVisible.value = false
                    }
                ) {
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
private fun AppInfoPageViewer(
    selectedApp: AppInfo
) {
    Row(
        modifier = Modifier
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val image = if (selectedApp.imagesPath.isNotEmpty()) {
            getImageBitmap(selectedApp.imagesPath[0])
                ?: emptyImageBitmap
        } else {
            getImageBitmap("ViewOS/Properties/Style/osIconSecond.png")
                ?: emptyImageBitmap
        }

        Image(
            bitmap = image,
            contentDescription = "app image",
            modifier = Modifier
                .width(250.dp)
        )

        Column {
            Text(
                text = selectedApp.name,
                color = Color.LightGray,
                fontSize = 32.sp
            )
            Text(
                text = "developer: ${selectedApp.developer}",
                color = Color.LightGray,
                fontSize = 16.sp
            )
            val latestVersion = selectedApp.versions.last()
            Text(
                text = "latest version ${latestVersion.version} released in ${latestVersion.date.day}d",
                color = Color.LightGray,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun ImageViewer(
    images: List<ImageBitmap>
) {
    val imagesScroll = remember { ScrollState(0) }

    Column(
        modifier = Modifier
            .padding(8.dp)
            .height(410.dp)
            .background(Color.Gray),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .horizontalScroll(imagesScroll),
        ) {
            images.forEach {
                Image(
                    bitmap = it,
                    contentDescription = "app image",
                    modifier = Modifier
                        .width(700.dp)
                        .height(390.dp)
                        .padding(8.dp),
                    contentScale = ContentScale.Inside
                )
            }
        }

        HorizontalScrollbar(
            modifier = Modifier
                .padding(4.dp),
            adapter = rememberScrollbarAdapter(imagesScroll),
        )
    }
}

@Composable
private fun VersionsViewer(
    versions: List<AppVersion>
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
    ) {
        Text(
            text = "All versions:",
            color = Color.LightGray,
            fontSize = 16.sp
        )

        versions.sortedWith { first, second ->
            second.date.day.toInt() - first.date.day.toInt()
        }.forEach {
            Text(
                text = "${it.version} released in ${it.date.day}d",
                color = Color.LightGray,
                fontSize = 16.sp
            )
        }
    }
}

private fun getAppList(): List<AppInfo> {
    val appFolders = File("Hidden files/Apps").listFiles()
    val apps = mutableListOf<AppInfo>()

    appFolders?.forEach { appFile ->

        val appInfoFromFile =
            File("${appFile.path.replace("\\", "/")}/Info/Info.txt")
                .readText()
                .replace("\r", "")
                .split("\n")

        var name = "[error]"
        var description = "[error]"
        var developer = "[error]"
        var price: Int = -1
        val versions = mutableListOf<AppVersion>()

        appInfoFromFile.forEach { infoRow ->
            if (infoRow.contains(Regex("name:.*"))) name = infoRow.substringAfter("name:")
            if (infoRow.contains(Regex("description:.*"))) description = infoRow.substringAfter("description:")
            if (infoRow.contains(Regex("developer:.*"))) developer = infoRow.substringAfter("developer:")
            if (infoRow.contains(Regex("price:.*"))) price = infoRow.substringAfter("price:").toInt()
            if (infoRow.contains(Regex("versions:.*"))) {
                val versionsAsString = infoRow.substringAfter("versions:").split("|")
                versionsAsString.forEach { versionAsString ->
                    versions.add(
                        AppVersion(
                            version = versionAsString.substringBefore(":"),
                            date = Date(versionAsString.substringAfter(":").toLong())
                        )
                    )
                }
            }
        }

        val purchasedFile = File("Hidden files/Purchased.txt")
        val purchased = if (purchasedFile.exists()) {
            purchasedFile.readText().split("\n")
        } else {
            emptyList()
        }

        apps.add(
            AppInfo(
                filesPath = "${appFile.path.replace("\\", "/")}/Files",
                name = name,
                description = description,
                developer = developer,
                price = price,
                isPurchased = purchased.contains(name),
                versions = versions,
                imagesPath = getAllPngFiles("${appFile.path.replace("\\", "/")}/Info/Images")
            )
        )
    }

    return apps
}

private fun getAllPngFiles(path: String): List<String> {
    val allFiles = File(path).listFiles()
    val images = mutableListOf<String>()

    allFiles?.forEach {
        if (it.exists() && it.name.contains(".png")) {
            images.add(it.path)
        }
    }

    return images
}

private fun getAllPng(imagesPath: List<String>): List<ImageBitmap> {
    val images = mutableListOf<ImageBitmap>()

    imagesPath.forEach { path ->
        val imageFile = File(path)

        if (imageFile.name.contains(".png")) {
            getImageBitmap(imageFile)?.let { images.add(it) }
        }
    }

    return images
}