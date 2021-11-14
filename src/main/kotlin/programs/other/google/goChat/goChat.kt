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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jetbrains.skia.Bitmap
import os.desktop.SubWindowData
import os.properties.OsProperties
import os.time.Date
import os.time.Time
import viewOsUis.TextCheckbox
import java.io.File
import androidx.compose.ui.unit.dp as dp

private data class Message(
    val message: String,
    val nickname: String,
    val time: Time,
    val date: Date
) {
    companion object {
        fun getInstanceOfMessageProperties(messageProperties: String): Message {
            val newMessageProp = messageProperties.replace("\r", "")
            val message = newMessageProp.substring(
                newMessageProp.indexOf("Message:"),
                newMessageProp.indexOf("\n")
            ).removePrefix("Message:")
            val nickname = newMessageProp.substring(
                newMessageProp.indexOf("By:"),
                newMessageProp.indexOf("\nTime:")
            ).removePrefix("By:")
            val time = Time(
                newMessageProp.substring(
                    newMessageProp.indexOf("Time:"),
                    newMessageProp.indexOf("\nDate:")
                ).removePrefix("Time:").toLong()
            )
            val date = Date(
                newMessageProp.substring(
                    newMessageProp.indexOf("Date:"),
                    newMessageProp.indexOf("\ne")
                ).removePrefix("Date:").toLong()
            )

            return Message(
                message,
                nickname,
                time,
                date
            )
        }
    }
}

@Composable
fun GoChat(
    programsThatOpen: MutableList<SubWindowData>,
    data: SubWindowData,
) {
    val chatOpened = remember {
        mutableStateOf(
            if (data.args["chat"] != null) data.args["chat"] as String else ""
        )
    }
    val nicknameFile = File("ViewOS/ProgramData/GoChat/nickname.txt")
    val nickname = remember { mutableStateOf(if (nicknameFile.exists()) nicknameFile.readText() else "unknown") }
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
                        .weight(9F)
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

@Composable
private fun ChatsList(
    nickname: MutableState<String>,
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
        OutlinedTextField(
            value = nickname.value,
            onValueChange = {
                nickname.value = it
                File("ViewOS/ProgramData/GoChat/nickname.txt").writeText(nickname.value)
            },
            textStyle = TextStyle(color = Color.LightGray),
        )

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
            val chatsList = File("ViewOS/ProgramData/GoChat/Chats").listFiles()

            chatsList?.forEach {
                val propertiesFile = File("${it.path}/properties.txt")
                val properties = if (propertiesFile.exists()) {
                    propertiesFile.readText()
                } else "favorite:false"
                val isFavorite = remember { mutableStateOf(properties.contains("favorite:true")) }

                val chatFolder = File("${it.path}/Messages")
                val messageFiles = if (chatFolder.exists()) {
                    chatFolder.listFiles()?.sortedBy { file ->
                        file.nameWithoutExtension.toInt()
                    }
                } else null

                val lastMessage = if (messageFiles != null) {
                    val mess = messageFiles.last().readText()
                    mess.substring(
                        mess.indexOf("Message:"),
                        mess.indexOf("\n")
                    ).removePrefix("Message:")
                } else "Message isn`t exist :("

                if (!favoriteCheck.value || favoriteCheck.value && isFavorite.value)
                    ChatListItem(
                        title = it.nameWithoutExtension,
                        lastMessage = lastMessage,
                        checkFavorite = isFavorite.value,
                        modifier = Modifier
                            .fillMaxWidth(),
                        onCheckFavorite = { value ->
                            isFavorite.value = value
                            if (propertiesFile.exists()) {
                                propertiesFile.writeText(
                                    properties.replace(
                                        Regex("favorite:(true|false)"), "favorite:${isFavorite.value}"
                                    )
                                )
                            }
                            data.args["favorite"] = favoriteCheck.value
                        },
                        onClick = {
                            chatOpened.value = it.nameWithoutExtension
                            data.args["chat"] = chatOpened.value
                        }
                    )
            }
        }
    }
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
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState(
        if (data.args["chatScroll"] != null) data.args["chatScroll"] as Int else 0
    )

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (chatOpened.value.isNotEmpty()) {
            val chatFolder = File("ViewOS/ProgramData/GoChat/Chats/${chatOpened.value}/Messages")

            val messageFiles = if (chatFolder.exists()) {
                chatFolder.listFiles()?.sortedBy {
                    it.nameWithoutExtension.toInt()
                }
            } else null

            if (messageFiles != null) {
                items(messageFiles) { file ->
                    val messageProperties = file.readText()
                    val message = Message.getInstanceOfMessageProperties(messageProperties)

                    ChatMessage(
                        message = message.message,
                        nickname = message.nickname,
                        timeWhenSent = message.time,
                        dateWhenSent = message.date
                    )
                }
            } else {
                item {
                    Text(
                        text = "Sorry! This chat isn't exist :(",
                        color = Color.Red
                    )
                }
            }
        }

        coroutineScope.launch {
            listState.scrollToItem(listState.layoutInfo.totalItemsCount)
            data.args["chatScroll"] = listState.firstVisibleItemIndex
        }
    }
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
            modifier = Modifier.width(800.dp)
        )

        Button(
            onClick = {
                val chatFolder = File("ViewOS/ProgramData/GoChat/Chats/${chatOpened.value}/Messages")

                val messageFiles = if (chatFolder.exists()) {
                    chatFolder.listFiles()?.sortedBy {
                        it.nameWithoutExtension.toInt()
                    }
                } else null

                if (messageFiles != null) {
                    val newMessageFile =
                        File("${chatFolder.path}/${messageFiles.last().nameWithoutExtension.toInt() + 1}.txt")

                    newMessageFile.writeText(
                        "Message:${enterText.value}\n" +
                                "By:${nickname.value}\n" +
                                "Time:${OsProperties.currentTime().toLong()}\n" +
                                "Date:${OsProperties.currentDate().day}\n" +
                                "e"
                    )

                    enterText.value = ""
                    data.args["enter"] = enterText.value
                    reload.value = false
                }
            },
        ) {
            Text(
                text = "send",
                color = Color.LightGray
            )
        }
    }
}

@Composable
private fun ChatMessage(
    message: String,
    nickname: String,
    timeWhenSent: Time,
    dateWhenSent: Date,
) {
    Column(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = Color.Black,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = nickname,
                color = Color.Blue,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "$dateWhenSent $timeWhenSent",
                color = Color.LightGray,
                fontWeight = FontWeight.ExtraLight
            )
        }

        Text(
            text = message,
            color = Color.LightGray,
            fontSize = 18.sp
        )
    }
}