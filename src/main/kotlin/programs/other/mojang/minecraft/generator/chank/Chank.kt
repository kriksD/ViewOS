package programs.other.mojang.minecraft.generator.chank

import programs.other.mojang.minecraft.block.BlockPosition
import programs.other.mojang.minecraft.block.Block
import programs.other.mojang.minecraft.block.BlockType
import programs.other.mojang.minecraft.generator.World

data class Chank(
    val position: Int,
    val world: World? = null,
    private val blocks: List<List<Block>> = List(height) { y ->
        List(width) { x -> Block(BlockPosition(position * width + x, y), BlockType.Air, position, world) }
    }
) {
    fun setBlock(type: BlockType, position: BlockPosition, miss: Boolean = false) {
        if (position.x in 0 until width && position.y in 0 until height) {
            blocks[position.y][position.x].type = type
        } else {
            if (!miss) {
                throw Exception("Block position out of bounds of chank. $position, Chank: ${this.position}")
            }
        }
    }

    fun replace(type: BlockType, forReplace: BlockType, position: BlockPosition, miss: Boolean = false) {
        if (position.x in 0 until width && position.y in 0 until height) {
            if (blocks[position.y][position.x].type == forReplace) {
                blocks[position.y][position.x].type = type
            }
        } else {
            if (!miss) {
                throw Exception("Block position out of bounds of chank. $position, Chank: ${this.position}")
            }
        }
    }

    fun replace(type: BlockType, forReplace: Collection<BlockType>, position: BlockPosition, miss: Boolean = false) {
        if (position.x in 0 until width && position.y in 0 until height) {
            if (forReplace.contains(blocks[position.y][position.x].type)) {
                blocks[position.y][position.x].type = type
            }
        } else {
            if (!miss) {
                throw Exception("Block position out of bounds of chank. $position, Chank: ${this.position}")
            }
        }
    }

    fun getBlock(position: BlockPosition): Block {
        return if (position.x in 0 until width && position.y in 0 until height) {
            blocks[position.y][position.x]
        } else {
            throw Exception("Block position out of bounds of chank. $position, Chank: ${this.position}")
        }
    }

    fun forEach(action: (block: Block) -> Unit) {
        blocks.forEach { lBlock ->
            lBlock.forEach { block ->
                action(block)
            }
        }
    }

    companion object {
        const val height = 128
        const val width = 16
    }
}
