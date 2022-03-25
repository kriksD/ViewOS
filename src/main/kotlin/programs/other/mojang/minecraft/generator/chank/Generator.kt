package programs.other.mojang.minecraft.generator.chank

import programs.other.mojang.minecraft.generator.World

interface Generator {
    val seed: Int
    fun generateChank(position: Int, world: World? = null): Chank
}