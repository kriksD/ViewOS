package programs.other.mojang.minecraft

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import os.desktop.SubWindowData
import programs.other.mojang.minecraft.block.Block
import programs.other.mojang.minecraft.block.BlockPosition
import programs.other.mojang.minecraft.block.BlockType
import programs.other.mojang.minecraft.block.Textures
import programs.other.mojang.minecraft.generator.World
import programs.other.mojang.minecraft.generator.chank.Chank
import programs.other.mojang.minecraft.mob.MobPosition
import programs.other.mojang.minecraft.mob.mobs.Player
import viewOsAppends.fpsCount
import kotlin.math.floor

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Minecraft(
    data: SubWindowData
) {
    val fps = remember { mutableStateOf(0) }
    val world = remember { World(seed = 1) }
    val player = remember { Player(position = MobPosition(5.0, 65.0), world) }

    val coroutineScope = rememberCoroutineScope()

    val screenPosition = remember { mutableStateOf(Offset(0F, -(32F * 48F) - 1200)) }
    val visibleChanks = remember { mutableListOf<Chank>() }

    val tempSelected = remember { mutableStateOf(BlockType.Air) }

    val reload = remember { mutableStateOf(true) }
    if (reload.value) {
        Box(
            modifier = Modifier
                .background(color = Color.Blue)
                .fillMaxSize()
                .clipToBounds()
        ) {
            coroutineScope.launch {
                val from = ((0 - screenPosition.value.x) / Chank.width / Block.visibleSize).toInt() - 1
                val to =
                    (((0 - screenPosition.value.x) + data.width.value) / Chank.width / Block.visibleSize).toInt() + 1

                visibleChanks.clear()
                for (pos in from..to) {
                    visibleChanks.add(world.getChank(pos))
                }
            }

            val requester = FocusRequester()
            Canvas(
                modifier = Modifier
                    .pointerInput(key1 = true) {
                        detectTapGestures(onPress = {
                            requester.requestFocus()
                        })
                    }
                    .onKeyEvent {
                        return@onKeyEvent when (it.key) {
                            Key.W -> {
                                screenPosition.value = Offset(screenPosition.value.x, screenPosition.value.y + 30)
                                player.jump()
                                true
                            }
                            Key.S -> {
                                screenPosition.value = Offset(screenPosition.value.x, screenPosition.value.y - 30)

                                true
                            }
                            Key.A -> {
                                screenPosition.value = Offset(screenPosition.value.x + 30, screenPosition.value.y)
                                player.goLeft()
                                true
                            }
                            Key.D -> {
                                screenPosition.value = Offset(screenPosition.value.x - 30, screenPosition.value.y)
                                player.goRight()
                                true
                            }
                            else -> {
                                false
                            }
                        }
                    }
                    .focusRequester(requester)
                    .focusable()
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consumeAllChanges()

                            screenPosition.value += dragAmount
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures { click ->
                            val x = floor(((click.x - screenPosition.value.x) / Block.visibleSize)).toInt()
                            val y = (Chank.height - ((click.y - screenPosition.value.y) / Block.visibleSize)).toInt() + 1

                            world.setBlock(tempSelected.value, BlockPosition(x, y))
                        }
                    }
                    .offset(screenPosition.value.x.dp, screenPosition.value.y.dp)
                    .fillMaxSize()
            ) {
                visibleChanks.forEach { chank ->
                    chank.forEach { block ->
                        val type = Textures.getTexture(block.type)

                        drawImage(
                            image = type,
                            topLeft = Offset(
                                x = (block.position.x * Block.visibleSize).toFloat(),
                                y = ((Chank.height - block.position.y) * Block.visibleSize).toFloat()
                            ),
                        )
                    }
                }
                drawRect(
                    color = Color.Green,
                    topLeft = Offset(
                        x = (player.position.x * Block.visibleSize).toFloat(),
                        y = ((Chank.height - player.position.y) * Block.visibleSize).toFloat()
                    ),
                    size = Size(
                        width = (player.width * Block.visibleSize).toFloat(),
                        height = (player.height * Block.visibleSize).toFloat()
                    )
                )
            }
            LaunchedEffect(Unit) {
                requester.requestFocus()
            }
            Row {
                BlockType.values().forEach {
                    Button(
                        onClick = {
                            tempSelected.value = it
                        }
                    ) {
                        Text(text = it.name)
                    }
                }
            }
            Text(
                text = fps.value.toString(),
                color = Color.White,
                fontSize = 22.sp,
                modifier = Modifier
                    .clickable { reload.value = false }
            )
        }

    } else {
        reload.value = true
    }

    fps.value = fpsCount()
}