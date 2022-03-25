package programs.other.mojang.minecraft.mob.mobs

import programs.other.mojang.minecraft.block.BlockPosition
import programs.other.mojang.minecraft.block.BlockType
import programs.other.mojang.minecraft.generator.World
import programs.other.mojang.minecraft.mob.Mob
import programs.other.mojang.minecraft.mob.MobPosition
import kotlin.math.floor

class Player(override var position: MobPosition, override var world: World) : Mob {
    override val height: Double = 1.7
    override val width: Double = 0.7
    override val id: Int = hashCode()
    override var hp: Int = 100

    var inAir: Boolean = false

    override fun mobLogic() {

    }

    fun goLeft() {
        if (world.getBlock(BlockPosition(floor(position.x).toInt() - 1, floor(position.y).toInt())).type == BlockType.Air) {
            position.x -= 0.5
        }
    }

    fun goRight() {
        if (world.getBlock(BlockPosition(floor(position.x).toInt() + 1, floor(position.y).toInt() + 2)).type == BlockType.Air) {
            position.x += 0.5
        }
    }

    fun jump() {
        if (!inAir && world.getBlock(BlockPosition(floor(position.x).toInt(), floor(position.y).toInt() + 2)).type == BlockType.Air) {
            inAir = true
            position.y += 1.5
        }
    }
}