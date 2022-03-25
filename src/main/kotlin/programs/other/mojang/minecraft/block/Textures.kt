package programs.other.mojang.minecraft.block

import androidx.compose.ui.graphics.ImageBitmap
import viewOsAppends.emptyImageBitmap
import viewOsAppends.getImageBitmap

object Textures {
    private val textures = mapOf(
        Pair(BlockType.Air, getImageBitmap("ViewOS/ProgramData/Minecraft/Textures/air.png") ?: emptyImageBitmap),
        Pair(BlockType.Stone, getImageBitmap("ViewOS/ProgramData/Minecraft/Textures/stone.png") ?: emptyImageBitmap),
        Pair(BlockType.Dirt, getImageBitmap("ViewOS/ProgramData/Minecraft/Textures/dirt.png") ?: emptyImageBitmap),
        Pair(BlockType.Grass, getImageBitmap("ViewOS/ProgramData/Minecraft/Textures/grass.png") ?: emptyImageBitmap),
        Pair(BlockType.Tree, getImageBitmap("ViewOS/ProgramData/Minecraft/Textures/tree.png") ?: emptyImageBitmap),
        Pair(BlockType.Leaves, getImageBitmap("ViewOS/ProgramData/Minecraft/Textures/leaves.png") ?: emptyImageBitmap),
        Pair(BlockType.Coal, getImageBitmap("ViewOS/ProgramData/Minecraft/Textures/coal.png") ?: emptyImageBitmap),
        Pair(BlockType.Iron, getImageBitmap("ViewOS/ProgramData/Minecraft/Textures/iron.png") ?: emptyImageBitmap),
    )

    fun getTexture(type: BlockType): ImageBitmap {
        return textures[type] ?: emptyImageBitmap
    }
}