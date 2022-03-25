package viewOsAppends

import java.util.*

class MyRandom(private val seed: Long) : Random(seed) {
    private var iterations = 0L

    override fun nextInt(bound: Int): Int {
        iterations++
        super.setSeed(seed + iterations)
        return super.nextInt(bound)
    }

    override fun nextBoolean(): Boolean {
        iterations++
        super.setSeed(seed + iterations)
        return super.nextBoolean()
    }
}