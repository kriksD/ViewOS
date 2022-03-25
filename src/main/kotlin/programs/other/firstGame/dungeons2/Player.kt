package programs.other.firstGame.dungeons2

import programs.other.firstGame.dungeons2.item.Item
import programs.other.firstGame.dungeons2.item.Poison
import programs.other.firstGame.dungeons2.item.PoisonType
import java.io.File

class Player(
    override val name: String,
    override var health: Int,
    override var armor: Int,
    override var damage: Int,
    override var weaponItem: Item,
    override var armorItem: Item,
    override val poisons: MutableList<Poison>
) : Mob {

    private val statistic = Statistic.getInstanceFromFile(File("")) // don't forget about this

    override fun getDamage(from: Mob): Int {
        val cDamage = from.damage - armor

        health -= cDamage
        statistic.gotDamage += cDamage

        if (from is Player) {
            from.statistic.damage += cDamage
        }

        return cDamage
    }

    override fun setWeapon(weapon: Item) {
        this.weaponItem = weapon
    }

    override fun setArmor(armor: Item) {
        this.armorItem = armor
    }

    override fun addPoisons(poisons: List<Poison>) {
        this.poisons.addAll(poisons)
    }

    override fun removePoisons(poisons: List<Poison>) {
        this.poisons.removeAll(poisons)
    }

    override fun addPoison(poison: Poison) {
        this.poisons.add(poison)
    }

    override fun removePoison(poison: Poison) {
        this.poisons.remove(poison)
    }

    override fun usePoison(type: PoisonType) {
        poisons.find { poison ->
            poison.type == type
        }?.use(this)
    }

    override fun usePoison(poison: Poison) {
        poisons.find { poisonF ->
            poisonF.name == poison.name
        }?.use(this)
    }
}