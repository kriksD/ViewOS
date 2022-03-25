package programs.other.mojang.minecraft.mob

import programs.other.mojang.minecraft.block.BlockPosition
import kotlin.math.floor

data class MobPosition(
    var x: Double,
    var y: Double
) {
    fun up(n: Double = 1.0): MobPosition {
        return MobPosition(x, y + n)
    }

    fun down(n: Double = 1.0): MobPosition {
        return MobPosition(x, y - n)
    }

    fun left(n: Double = 1.0): MobPosition {
        return MobPosition(x - n, y)
    }

    fun right(n: Double = 1.0): MobPosition {
        return MobPosition(x + n, y)
    }

    fun toBlockPosition(): BlockPosition {
        return BlockPosition(floor(x).toInt(), floor(y).toInt())
    }
}