package programs.other.firstGame.dungeons2.item

import programs.other.firstGame.dungeons2.Mob

interface Poison : Item {
    val type: PoisonType
    fun use(mob: Mob)
}