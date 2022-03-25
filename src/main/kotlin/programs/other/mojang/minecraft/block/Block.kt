package programs.other.mojang.minecraft.block

import programs.other.mojang.minecraft.generator.World

data class Block(
    val position: BlockPosition,
    var type: BlockType,
    val chankPosition: Int? = null,
    val world: World? = null
) {
    companion object {
        const val visibleSize = 48
    }
}
