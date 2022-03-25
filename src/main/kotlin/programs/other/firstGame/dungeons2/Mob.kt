package programs.other.firstGame.dungeons2

import programs.other.firstGame.dungeons2.item.Item
import programs.other.firstGame.dungeons2.item.Poison
import programs.other.firstGame.dungeons2.item.PoisonType

interface Mob {
    val name: String
    var health: Int
    var armor: Int
    var damage: Int
    var weaponItem: Item
    var armorItem: Item
    val poisons: MutableList<Poison>

    fun getDamage(from: Mob): Int

    fun setWeapon(weapon: Item)
    fun setArmor(armor: Item)

    fun addPoisons(poisons: List<Poison>)
    fun removePoisons(poisons: List<Poison>)
    fun addPoison(poison: Poison)
    fun removePoison(poison: Poison)

    fun usePoison(type: PoisonType)
    fun usePoison(poison: Poison)
}