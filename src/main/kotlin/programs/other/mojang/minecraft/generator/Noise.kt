package programs.other.mojang.minecraft.generator

import programs.other.mojang.minecraft.block.BlockPosition
import viewOsAppends.MyRandom
import kotlin.math.*
import kotlin.random.Random

class Noise(
    val seed: Int
) {
    fun smooth1d(values: List<Int>, line: Int, strength: Int = 2): List<Int> {
        val newValues = mutableListOf<Int>()
        newValues.addAll(values)

        for (n in 0 until newValues.size) {
            newValues[n] += (line - newValues[n]) / strength
        }

        return newValues
    }

    fun gen1d(length: Int, step: Int = 1, from: Int, to: Int, numberChank: Int = 0): List<Int> {
        val newSeed = seed + numberChank * length
        val values = MutableList(length) { 0 }

        var j = 1
        var previous = Random(newSeed).nextInt(from, to)
        var next = Random(newSeed + 1).nextInt(from, to)

        for (i in 0 until length) {
            if (j == step) {
                values[i] = next
                previous = next
                next = Random(newSeed + i).nextInt(from, to)

                j = 1
            } else {
                val up = (previous.toFloat() - next.toFloat()) / step.toFloat() * j.toFloat()
                values[i] = next + floor(up).toInt()

                j++
            }
        }

        return values
    }

    fun unit1d(values1: List<Int>, values2: List<Int>): List<Int> {
        return if (values1.size == values2.size) {
            val newValues = MutableList(values1.size) { 0 }

            for (i in 0 until newValues.size) {
                newValues[i] = values1[i] + (values1[i] - values2[i]) / 2
            }

            newValues
        } else {
            values1
        }
    }

    fun gen2dtfBool(width: Int, height: Int, wide: Int = 0, rare: Int = 1, numberChank: Int = 0): List<List<Boolean>> {
        val newSeed = seed + numberChank * width
        val rand = MyRandom(newSeed.toLong())

        val grid = List(width) { MutableList(height) { true } }

        for (x in 0 until width) {
            for (y in 0 until height) {
                val value = rand.nextInt(rare) != 0
                grid[x][y] = value

                if (wide > 0 && !value) {
                    /*val points = getCircle(x, y, wide)
                    points.forEach { point ->
                        if (point.x in 0 until width && point.y in 0 until height) {
                            grid[point.x][point.y] = false
                        }
                    }*/

                    var previous = BlockPosition(x, y)
                    for (n in 0 until wide) {
                        if (previous.x in 0 until width && previous.y in 0 until height) {
                            grid[previous.x][previous.y] = false
                            previous = BlockPosition(previous.x + rand.nextInt(3) - 1, previous.y + rand.nextInt(3) - 1)
                        }
                    }
                }
            }
        }

        return grid
    }

    private fun getCircle(x: Int, y: Int, r: Int): List<BlockPosition> {
        val points = mutableListOf<BlockPosition>()

        var x1: Double
        var y1: Double

        val minAngle = acos(1.0 - 1.0 / r)

        var angle = 0.0
        while (angle <= 360) {
            x1 = r * cos(angle)
            y1 = r * sin(angle)
            points.add(BlockPosition((x + x1).toInt(), (y + y1).toInt()))

            angle += minAngle
        }

        return points
    }
}