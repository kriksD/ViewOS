package programs.other.mojang.minecraft.mob

import programs.other.mojang.minecraft.generator.World

class MobManager(
    val world: World
) {
    val mobs = mutableListOf<Mob>()

    fun addMob(mob: Mob) {
        mobs.add(mob)
    }

    fun removeMob(mob: Mob) {
        mobs.remove(mob)
    }

    fun removeMob(id: Int) {
        val mob = mobs.find { it.id == id }
        mob?.let { mobs.remove(it) }
    }
}