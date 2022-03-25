package programs.other.mojang.minecraft.generator.chank

import programs.other.mojang.minecraft.block.BlockPosition
import programs.other.mojang.minecraft.block.BlockType
import programs.other.mojang.minecraft.generator.Noise
import programs.other.mojang.minecraft.generator.World

class ChankGenerator(override val seed: Int) : Generator {

    override fun generateChank(position: Int, world: World?): Chank {
        val newChank = Chank(position = position, world = world)
        val generator = Noise(seed)

        val mountains = generator.smooth1d(generator.gen1d(Chank.width, 8, 80, 100, position), 53)
        val plains = generator.gen1d(Chank.width, 4, 58, 68, position)
        val generated = generator.smooth1d(generator.unit1d(plains, mountains), 65, 3)

        val newCaves = generator.smooth1d(generator.gen1d(Chank.width, 4, 5, 40, position), 25)
        val cavesWider = generator.gen1d(Chank.width, 4, 1, 4, position)

        val newCaves2 = generator.smooth1d(generator.gen1d(Chank.width, 4, 30, 60, position), 45)

        val coal = generator.gen2dtfBool(Chank.width, Chank.height, 20, 200, position)
        val iron = generator.gen2dtfBool(Chank.width, Chank.height, 5, 100, position)

        val objects = generator.gen1d(Chank.width, 1, 0, 1000, position)

        for (x in 0 until Chank.width) {
            for (y in 0 until Chank.height) {
                val newNoise = generated[x]

                /*if (!caves[x][y] || !caves2[x][y] || !caves3[x][y]) {
                    newChank.setBlock(BlockType.Air, BlockPosition(x, y))

                }*/
                if (
                    y in newCaves[x]..(5 + newCaves[x] + cavesWider[x]) ||
                    y in newCaves2[x]..(3 + newCaves2[x])
                ) {
                    newChank.setBlock(BlockType.Air, BlockPosition(x, y))

                } else if (y < newNoise - 4) {
                    newChank.setBlock(BlockType.Stone, BlockPosition(x, y))

                } else if (y < newNoise) {
                    newChank.setBlock(BlockType.Dirt, BlockPosition(x, y))

                } else if (y == newNoise) {
                    newChank.setBlock(BlockType.Grass, BlockPosition(x, y))

                } else {
                    newChank.setBlock(BlockType.Air, BlockPosition(x, y))
                }

                if (y > 35 && !coal[x][y]) {
                    newChank.replace(BlockType.Coal, BlockType.Stone, BlockPosition(x, y))

                } else if (y < 45 && !iron[x][y]) {
                    newChank.replace(BlockType.Iron, BlockType.Stone, BlockPosition(x, y))

                }
            }
        }

        for (x in 0 until Chank.width) {
            val y = generated[x]

            when (objects[x]) {
                in 0..100 -> if (newChank.getBlock(BlockPosition(x, y)).type == BlockType.Grass) genTree(newChank, BlockPosition(x, y).up())
                in 101..150 -> genRock(newChank, BlockPosition(x, y).up())
            }
        }

        return newChank
    }

    private fun genTree(chank: Chank, position: BlockPosition) {
        chank.replace(BlockType.Tree, listOf(BlockType.Air, BlockType.Stone), position, true)
        chank.replace(BlockType.Tree, listOf(BlockType.Air, BlockType.Stone), position.up(), true)
        chank.replace(BlockType.Tree, listOf(BlockType.Air, BlockType.Stone), position.up(2), true)
        chank.replace(BlockType.Tree, listOf(BlockType.Air, BlockType.Stone), position.up(3), true)
        chank.replace(BlockType.Tree, listOf(BlockType.Air, BlockType.Stone), position.up(4), true)

        chank.replace(BlockType.Leaves, listOf(BlockType.Air, BlockType.Stone), position.up(2).left(), true)
        chank.replace(BlockType.Leaves, listOf(BlockType.Air, BlockType.Stone), position.up(2).left(2), true)
        chank.replace(BlockType.Leaves, listOf(BlockType.Air, BlockType.Stone), position.up(2).right(), true)
        chank.replace(BlockType.Leaves, listOf(BlockType.Air, BlockType.Stone), position.up(2).right(2), true)

        chank.replace(BlockType.Leaves, listOf(BlockType.Air, BlockType.Stone), position.up(3).left(), true)
        chank.replace(BlockType.Leaves, listOf(BlockType.Air, BlockType.Stone), position.up(3).left(2), true)
        chank.replace(BlockType.Leaves, listOf(BlockType.Air, BlockType.Stone), position.up(3).right(), true)
        chank.replace(BlockType.Leaves, listOf(BlockType.Air, BlockType.Stone), position.up(3).right(2), true)

        chank.replace(BlockType.Leaves, listOf(BlockType.Air, BlockType.Stone), position.up(4).left(), true)
        chank.replace(BlockType.Leaves, listOf(BlockType.Air, BlockType.Stone), position.up(4).right(), true)

        chank.replace(BlockType.Leaves, listOf(BlockType.Air, BlockType.Stone), position.up(5).left(), true)
        chank.replace(BlockType.Leaves, listOf(BlockType.Air, BlockType.Stone), position.up(5), true)
        chank.replace(BlockType.Leaves, listOf(BlockType.Air, BlockType.Stone), position.up(5).right(), true)

    }

    private fun genRock(chank: Chank, position: BlockPosition) {
        chank.setBlock(BlockType.Stone, position, true)
        chank.setBlock(BlockType.Stone, BlockPosition(position.x, position.y + 1), true)
        chank.setBlock(BlockType.Stone, BlockPosition(position.x - 1, position.y), true)
        chank.setBlock(BlockType.Stone, BlockPosition(position.x + 1, position.y), true)
    }
}