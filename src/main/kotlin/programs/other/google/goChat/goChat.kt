package programs.other.google.goChat

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import os.desktop.SubWindowData
import os.manager.*
import os.properties.OsProperties
import os.time.Date
import os.time.Time
import viewOsAppends.UIs.TextCheckbox
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.io.File


@Composable
fun GoChat(
    data: SubWindowData,
) {
    val chatOpened = remember {
        mutableStateOf(
            if (data.args["chat"] != null) data.args["chat"] as String else ""
        )
    }
    val nickname = remember { mutableStateOf(getNickname()) }
    val color = remember { mutableStateOf(getColor()) }

    val reload = remember { mutableStateOf(true) }
    if (reload.value) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ChatsList(
                nickname = nickname,
                color = color,
                chatOpened = chatOpened,
                data = data
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.DarkGray)
                    .padding(8.dp),
            ) {
                Chat(
                    chatOpened = chatOpened,
                    data = data,
                    modifier = Modifier
                        .weight(9F),
                    nickname = nickname.value,
                    nicknameColor = color.value
                )

                MessageSender(
                    chatOpened = chatOpened,
                    reload = reload,
                    nickname = nickname,
                    data = data,
                )
            }
        }
    } else {
        reload.value = true
    }
}

fun getColor(): Color {
    return Color.Blue
}

private fun getNickname(): String {
    val nicknameFile = File("ViewOS/ProgramData/GoChat/nickname.txt")
    return if (nicknameFile.exists()) nicknameFile.readText() else "unknown"
}

@Composable
private fun ChatsList(
    nickname: MutableState<String>,
    color: MutableState<Color>,
    chatOpened: MutableState<String>,
    data: SubWindowData,
) {
    val favoriteCheck = remember {
        mutableStateOf(
            if (data.args["favorite"] != null) data.args["favorite"] as Boolean else false
        )
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(400.dp)
            .background(Color.DarkGray)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Your nickname:",
            color = Color.LightGray
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            OutlinedTextField(
                value = nickname.value,
                onValueChange = {
                    nickname.value = it
                    File("ViewOS/ProgramData/GoChat/nickname.txt").writeText(nickname.value)
                },
                textStyle = TextStyle(color = Color.LightGray),
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val expandedCPDropdownMenu = remember { mutableStateOf(false) }
                val colors = listOf(
                    Color.Black, Color.DarkGray, Color.Gray,
                    Color.LightGray, Color.White, Color.Blue,
                    Color.Red, Color.Cyan, Color.Magenta,
                    Color.Green, Color.Yellow
                )
                Button(
                    onClick = {
                        expandedCPDropdownMenu.value = true
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = color.value)
                ) {
                    DropdownMenu(
                        expanded = expandedCPDropdownMenu.value,
                        onDismissRequest = {
                            expandedCPDropdownMenu.value = false
                        }
                    ) {
                        colors.forEach {
                            DropdownMenuItem(
                                onClick = {
                                    color.value = it
                                },
                                modifier = Modifier
                                    .background(it)
                            ) {

                            }
                        }
                    }
                }
            }
        }

        TextCheckbox(
            text = "Favorite only",
            textColor = Color.LightGray,
            check = favoriteCheck.value,
            onCheckedChange = {
                favoriteCheck.value = it
            }
        )

        val chatListScroll = rememberScrollState(0)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.DarkGray)
                .padding(8.dp)
                .verticalScroll(chatListScroll),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val chats = getChats()

            chats.forEach { chat ->
                if (!favoriteCheck.value || favoriteCheck.value && chat.isFavorite())
                    ChatListItem(
                        title = chat.name,
                        lastMessage = chat.lastMessage,
                        checkFavorite = chat.isFavorite(),
                        modifier = Modifier
                            .fillMaxWidth(),
                        onCheckFavorite = { value ->
                            chat.setIsFavorite(value)
                        },
                        onClick = {
                            chatOpened.value = chat.name
                            data.args["chat"] = chatOpened.value
                        }
                    )
            }
        }
    }
}

private fun getChats(): List<Chat> {
    val files = File("${InternetManager.internetFolderPath()}/GoChat/Chats").listFiles()
    val chats = mutableListOf<Chat>()

    files?.forEach { file ->
        val propertiesFilePath = "ViewOS/ProgramData/GoChat/Chats/${file.nameWithoutExtension}/properties.txt"
        val propertiesFile = File(propertiesFilePath)
        val properties = if (propertiesFile.exists()) {
            propertiesFile.readText()
        } else {
            File("ViewOS/ProgramData/GoChat/Chats/${file.nameWithoutExtension}").mkdir()
            createPropertiesFile(propertiesFilePath)
            "favorite:false"
        }
        val favoriteFound = Regex("favorite:(false|true)").find(properties)
        val favorite = favoriteFound?.value?.substringAfter(":").toBoolean()

        val lastMessage =
            getMessages("${InternetManager.internetFolderPath()}/GoChat/Chats/${file.nameWithoutExtension}/Messages").last().message

        chats.add(
            Chat(
                name = file.nameWithoutExtension,
                lastMessage = lastMessage,
                isFavorite = favorite
            )
        )
    }

    return chats
}

private fun createPropertiesFile(path: String, favorite: Boolean = false) {
    val propertiesFile = File(path)
    propertiesFile.writeText("favorite:$favorite")
}

@Composable
private fun ChatListItem(
    title: String,
    lastMessage: String,
    iconPath: String = "ViewOS/ProgramData/GoChat/chatIcon.png",
    checkFavorite: Boolean,
    modifier: Modifier = Modifier,
    onCheckFavorite: (value: Boolean) -> Unit,
    onClick: (() -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable {
                onClick?.invoke()
            }
    ) {
        val imgFile = File(iconPath)
        val image = if (imgFile.exists()) {
            org.jetbrains.skia.Image.makeFromEncoded(imgFile.readBytes()).asImageBitmap()
        } else {
            org.jetbrains.skia.Image.makeFromEncoded(
                File("ViewOS/ProgramData/GoChat/chatIcon.png").readBytes()
            ).toComposeImageBitmap()
        }

        Image(
            bitmap = image,
            contentDescription = "icon of chat",
            modifier = Modifier
                .width(80.dp)
        )

        Column(

        ) {
            Text(
                text = title,
                color = Color.LightGray,
                fontSize = 18.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )

            Text(
                text = lastMessage,
                color = Color.LightGray,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )

            TextCheckbox(
                text = "Set as favorite",
                textColor = Color.LightGray,
                check = checkFavorite,
                onCheckedChange = {
                    onCheckFavorite(it)
                }
            )
        }
    }
}

@Composable
private fun Chat(
    chatOpened: MutableState<String>,
    data: SubWindowData,
    modifier: Modifier = Modifier,
    nickname: String,
    nicknameColor: Color
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState(
        if (data.args["chatScroll"] != null) data.args["chatScroll"] as Int else 0
    )
    val clipboard = LocalClipboardManager.current

    val messages = getMessages("${InternetManager.internetFolderPath()}/GoChat/Chats/${chatOpened.value}/Messages")

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (chatOpened.value.isNotEmpty()) {
            items(messages) { message ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = if (nickname == message.nickname) Arrangement.End else Arrangement.Start,
                ) {
                    Column {
                        ChatMessage(
                            message = message.message,
                            nickname = message.nickname,
                            nicknameColor = if (nickname == message.nickname) nicknameColor else Color.Blue,
                            timeWhenSent = message.time,
                            dateWhenSent = message.date,
                            position = if (nickname == message.nickname) MessagePosition.Right else MessagePosition.Left,
                            onClick = {
                                NotificationManager.add("You save the message into clipboard: ${message.message}")
                                clipboard.setText(AnnotatedString(message.message))
                            }
                        )

                        val url = Regex("vgs-[a-zA-Z0-9-]*").find(message.message)
                        if (url != null) {
                            ChatMessage(
                                message = url.value,
                                nickname = "BOT",
                                nicknameColor = if (nickname == message.nickname) nicknameColor else Color.Blue,
                                timeWhenSent = message.time,
                                dateWhenSent = message.date,
                                position = if (nickname == message.nickname) MessagePosition.Right else MessagePosition.Left,
                                onClick = {
                                    val vBrowser = ProgramsCreator.getInstance("VBrowser")
                                    vBrowser.args["url"] = url.value
                                    ProgramsManager.open(vBrowser)
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    coroutineScope.launch {
        listState.scrollToItem(listState.layoutInfo.totalItemsCount)
        data.args["chatScroll"] = listState.firstVisibleItemIndex
    }
}

@Composable
private fun ChatMessage(
    message: String,
    nickname: String,
    nicknameColor: Color,
    timeWhenSent: Time,
    dateWhenSent: Date,
    position: MessagePosition = MessagePosition.Left,
    onClick: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = Color.Black,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(8.dp)
            .clickable {
                if (onClick != null) {
                    onClick()
                }
            },
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = if (position == MessagePosition.Left) Alignment.Start else Alignment.End
    ) {
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(
                8.dp,
                if (position == MessagePosition.Left) Alignment.Start else Alignment.End
            ),
        ) {
            if (position == MessagePosition.Left) {
                Text(
                    text = nickname,
                    color = nicknameColor,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "$dateWhenSent $timeWhenSent",
                    color = Color.LightGray,
                    fontWeight = FontWeight.ExtraLight
                )
            } else {
                Text(
                    text = "$dateWhenSent $timeWhenSent",
                    color = Color.LightGray,
                    fontWeight = FontWeight.ExtraLight
                )
                Text(
                    text = nickname,
                    color = nicknameColor,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Text(
            text = message,
            color = Color.LightGray,
            fontSize = 18.sp,
            textAlign = if (position == MessagePosition.Left) TextAlign.Left else TextAlign.Right
        )
    }
}

private fun getMessages(path: String): List<Message> {
    val files = File(path).listFiles()
    val sortedFiles = files?.sortedBy {
        try {
            it.nameWithoutExtension.toInt()
        } catch (e: Exception) {
            println(e.message)
            null
        }
    }

    val messages = mutableListOf<Message>()
    sortedFiles?.forEach { file ->
        val messageProperties = if (file.exists()) file.readText() else ""
        messages.add(Message.getInstanceOfProperties(messageProperties))
    }

    return messages
}

@Composable
private fun MessageSender(
    chatOpened: MutableState<String>,
    reload: MutableState<Boolean>,
    nickname: MutableState<String>,
    data: SubWindowData,
    modifier: Modifier = Modifier,
) {
    val enterText = remember {
        mutableStateOf(
            if (data.args["enter"] != null) data.args["enter"] as String else ""
        )
    }

    Row(
        modifier = modifier
            .background(Color.DarkGray)
            .fillMaxWidth()
            .height(75.dp)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = enterText.value,
            onValueChange = {
                enterText.value = it
                data.args["enter"] = enterText.value
            },
            textStyle = TextStyle(color = Color.LightGray),
            modifier = Modifier
                .weight(9F)
        )

        Button(
            onClick = {
                sendMessage(
                    chatOpened = chatOpened.value,
                    message = enterText.value,
                    nickname = nickname.value
                )

                enterText.value = ""
                data.args["enter"] = enterText.value
                reload.value = false
            },
            modifier = Modifier
                .weight(1F),
        ) {
            Text(
                text = "send",
                color = Color.LightGray,
            )
        }
    }
}

fun sendMessage(
    chatOpened: String,
    message: String,
    nickname: String,
) {
    val chatFolder = File("${InternetManager.internetFolderPath()}/GoChat/Chats/$chatOpened/Messages")

    val messageFiles = if (chatFolder.exists()) {
        chatFolder.listFiles()?.sortedBy {
            it.nameWithoutExtension.toInt()
        }
    } else null

    if (messageFiles != null) {
        val newMessageFile = File("${chatFolder.path}/${messageFiles.last().nameWithoutExtension.toInt() + 1}.txt")

        newMessageFile.writeText(
            "Message:\"${message}\"\n" +
                    "By:\"${nickname}\"\n" +
                    "Time:${OsProperties.currentTime().toLong()}\n" +
                    "Date:${OsProperties.currentDate().day}\n" +
                    "e"
        )
    }
}