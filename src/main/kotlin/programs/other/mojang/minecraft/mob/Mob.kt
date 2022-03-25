package programs.other.mojang.minecraft.mob

import programs.other.mojang.minecraft.generator.World

interface Mob {
    var world: World
    var position: MobPosition
    val height: Double
    val width: Double
    val id: Int
    var hp: Int

    fun mobLogic()
}