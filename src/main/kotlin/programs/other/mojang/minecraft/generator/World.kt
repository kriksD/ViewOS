package programs.other.mojang.minecraft.generator

import programs.other.mojang.minecraft.block.BlockPosition
import programs.other.mojang.minecraft.block.Block
import programs.other.mojang.minecraft.block.BlockType
import programs.other.mojang.minecraft.generator.chank.Chank
import programs.other.mojang.minecraft.generator.chank.ChankGenerator
import programs.other.mojang.minecraft.generator.chank.Generator
import kotlin.math.abs

data class World(
    private val chanks: MutableList<Chank> = mutableListOf(),
    val seed: Int,
    val generator: Generator = ChankGenerator(0)
) {
    fun setBlock(type: BlockType, position: BlockPosition) {
        if (position.x >= 0) {
            getChank(position.x / Chank.width).setBlock(type, BlockPosition(position.x % Chank.width, position.y))
        } else if (position.x < 0) {
            getChank(position.x / Chank.width - 1).setBlock(type, BlockPosition(Chank.width - abs(position.x) % Chank.width, position.y))
        }
    }

    fun getBlock(position: BlockPosition): Block {
        return chanks[position.x / Chank.width].getBlock(BlockPosition(position.x % Chank.width, position.y))
    }

    fun replace(type: BlockType, forReplace: BlockType, position: BlockPosition) {
        val block = getBlock(position)
        if (block.type == forReplace) {
            block.type = type
        }
    }

    fun replace(type: BlockType, forReplace: Collection<BlockType>, position: BlockPosition) {
        val block = getBlock(position)
        if (forReplace.contains(block.type)) {
            block.type = type
        }
    }

    fun getChank(position: Int): Chank {
        val chank = chanks.find { it.position == position }

        return if (chank != null) {
            chank
        } else {
            val newChank = generator.generateChank(position, this)
            chanks.add(newChank)
            newChank
        }
    }

    fun addChank(chank: Chank) {
        chanks.add(chank)
    }

    fun forEach(action: (chank: Chank) -> Unit) {
        chanks.forEach { chank ->
            action(chank)
        }
    }
}
