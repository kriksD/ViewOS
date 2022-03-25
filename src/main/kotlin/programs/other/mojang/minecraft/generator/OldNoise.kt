package programs.other.mojang.minecraft.generator

import java.util.*

@Deprecated("Old noise generator")
class OldNoise(rand: Random?, roughness: Float, width: Int, height: Int) {
    /** Source of entropy  */
    private val rand_: Random

    /** Amount of roughness  */
    var roughness_: Float

    /** Plasma fractal grid  */
    private val grid_: List<MutableList<Float>>
    fun initialise() {
        val xh = grid_.size - 1
        val yh: Int = grid_[0].size - 1

        // set the corner points
        grid_[0][0] = rand_.nextFloat() - 0.5f
        grid_[0][yh] = rand_.nextFloat() - 0.5f
        grid_[xh][0] = rand_.nextFloat() - 0.5f
        grid_[xh][yh] = rand_.nextFloat() - 0.5f

        // generate the fractal
        generate(0, 0, xh, yh)
    }

    fun getGrid(): List<List<Float>> {
        return grid_
    }

    // Add a suitable amount of random displacement to a point
    private fun roughen(v: Float, l: Int, h: Int): Float {
        return (v + roughness_ * (rand_.nextGaussian() * (h - l))).toFloat()
    }

    // generate the fractal
    private fun generate(xl: Int, yl: Int, xh: Int, yh: Int) {
        val xm = (xl + xh) / 2
        val ym = (yl + yh) / 2
        if (xl == xm && yl == ym) return
        grid_[xm][yl] = 0.5f * (grid_[xl][yl] + grid_[xh][yl])
        grid_[xm][yh] = 0.5f * (grid_[xl][yh] + grid_[xh][yh])
        grid_[xl][ym] = 0.5f * (grid_[xl][yl] + grid_[xl][yh])
        grid_[xh][ym] = 0.5f * (grid_[xh][yl] + grid_[xh][yh])
        val v = roughen(0.5f * (grid_[xm][yl] + grid_[xm][yh]), xl + yl, yh + xh)
        grid_[xm][ym] = v
        grid_[xm][yl] = roughen(grid_[xm][yl], xl, xh)
        grid_[xm][yh] = roughen(grid_[xm][yh], xl, xh)
        grid_[xl][ym] = roughen(grid_[xl][ym], yl, yh)
        grid_[xh][ym] = roughen(grid_[xh][ym], yl, yh)
        generate(xl, yl, xm, ym)
        generate(xm, yl, xh, ym)
        generate(xl, ym, xm, yh)
        generate(xm, ym, xh, yh)
    }

    /**
     * Dump out as a CSV
     */
    fun printAsCSV() {
        for (i in grid_.indices) {
            for (j in 0 until grid_[0].size) {
                print(grid_[i][j])
                print(",")
            }
            println()
        }
    }

    /**
     * Convert to a Boolean array
     * @return the boolean array
     */
    fun toBooleans(): Array<BooleanArray> {
        val w = grid_.size
        val h: Int = grid_[0].size
        val ret = Array(w) { BooleanArray(h) }
        for (i in 0 until w) {
            for (j in 0 until h) {
                ret[i][j] = grid_[i][j] < 0
            }
        }
        return ret
    }

    init {
        roughness_ = roughness / width
        grid_ = List(width) { MutableList(height) { 0F } }
        rand_ = rand ?: Random()
    }
}