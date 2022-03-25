package programs.other.firstGame.dungeons2

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import os.desktop.SubWindowData
import programs.other.firstGame.dungeons2.resource.Resource
import programs.other.firstGame.dungeons2.resource.ResourceType
import viewOsAppends.Line
import viewOsAppends.emptyImageBitmap

@Composable
fun Dungeons2(
    data: SubWindowData,
) {
    val resources = remember {
        mutableMapOf(
            Pair("coins", Resource(ResourceType.Coins, 0)),
            Pair("wood", Resource(ResourceType.Wood, 0)),
            Pair("stone", Resource(ResourceType.Stone, 0)),
            Pair("iron", Resource(ResourceType.Iron, 0)),
            Pair("copper", Resource(ResourceType.Copper, 0))
        )
    }

    val shopCheck = remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Tab(
                check = shopCheck.value,
                name = "shop",
                onChoose = {
                    if (!shopCheck.value) {
                        shopCheck.value = true
                    }

                    // all others false
                }
            )
        }

        Line(
            orientation = Orientation.Horizontal,
            padding = 2.dp,
            width = 2.dp,
        )

        if (shopCheck.value) {
            Shop(
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun Tab(
    check: Boolean,
    name: String,
    image: ImageBitmap? = null,
    onChoose: (name: String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .selectable(selected = check) {
                onChoose(name)
            }
            .background(if (check) Color.DarkGray else Color.Gray)
            .padding(8.dp)
    ) {
        Image(
            bitmap = image ?: emptyImageBitmap,
            contentDescription = "tab icon"
        )
        Text(
            text = name,
            fontSize = 28.sp,
            color = Color.LightGray
        )
    }
}

@Composable
fun Shop(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(4.dp)
                .background(Color.DarkGray)
                .fillMaxWidth()
        ) {
            Text(
                text = "Mines",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray,
            )
        }
    }
}

@Composable
fun MineField(
    name: String,
    description: String,
    mine: Mine
) {
    Row {
        Column {
            Text(
                text = name,
                color = Color.LightGray,
                fontSize = 22.sp
            )

            Text(
                text = description,
                color = Color.LightGray,
                fontSize = 18.sp
            )
        }

        Button(
            onClick = {
                mine.count += 1
            }
        ) {
            Text("Buy")
        }
    }
}