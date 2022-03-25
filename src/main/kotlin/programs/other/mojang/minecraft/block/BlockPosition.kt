package programs.other.mojang.minecraft.block

import programs.other.mojang.minecraft.mob.MobPosition

data class BlockPosition(
    var x: Int,
    var y: Int
) {
    fun up(n: Int = 1): BlockPosition {
        return BlockPosition(x, y + n)
    }

    fun down(n: Int = 1): BlockPosition {
        return BlockPosition(x, y - n)
    }

    fun left(n: Int = 1): BlockPosition {
        return BlockPosition(x - n, y)
    }

    fun right(n: Int = 1): BlockPosition {
        return BlockPosition(x + n, y)
    }

    fun toMobPosition(): MobPosition {
        return MobPosition(x.toDouble(), y.toDouble())
    }
}
