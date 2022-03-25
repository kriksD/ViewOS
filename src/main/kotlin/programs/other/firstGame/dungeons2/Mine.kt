package programs.other.firstGame.dungeons2

import programs.other.firstGame.dungeons2.resource.Resource

class Mine(
    val resource: Resource,
    private val mineStrategy: MineStrategy,
    var count: Int
) {
    fun mining() {
        resource.add(mineStrategy.mining())
    }
}