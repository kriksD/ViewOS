package programs.other.firstGame.dungeons2

import java.io.File

data class Statistic(
    var damage: Int,
    var gotDamage: Int,
    var kills: Int,
    var deaths: Int,
    var receivedCoins: Int,
    var receivedWood: Int,
    var receivedStone: Int,
    var receivedIron: Int,
    var receivedCopper: Int,
    var spentCoins: Int,
    var spentWood: Int,
    var spentStone: Int,
    var spentIron: Int,
    var spentCopper: Int,
    var poisonUsed: Int,
) {
    fun saveInFile(file: File) {
        if (file.canWrite()) {
            file.writeText(
                "damage:$damage\n" +
                        "gotDamage:$gotDamage\n" +
                        "kills:$kills\n" +
                        "deaths:$deaths" +
                        "receivedCoins:$receivedCoins\n" +
                        "receivedWood:$receivedWood\n" +
                        "receivedStone:$receivedStone\n" +
                        "receivedIron:$receivedIron\n" +
                        "receivedCopper:$receivedCopper\n" +
                        "spentCoins:$spentCoins\n" +
                        "spentWood:$spentWood\n" +
                        "spentStone:$spentStone\n" +
                        "spentIron:$spentIron\n" +
                        "spentCopper:$spentCopper\n" +
                        "poisonUsed:$poisonUsed\n"
            )

        } else {
            throw Exception("${file.path}: File can't to write!")
        }
    }

    companion object {
        fun getInstanceFromFile(file: File): Statistic {
            if (file.canRead()) {
                val statsText = file.readText()

                return Statistic(
                    Regex("damage:\\n").find(statsText)?.value?.substringAfter(":")?.toInt() ?: 0,
                    Regex("gotDamage:\\n").find(statsText)?.value?.substringAfter(":")?.toInt() ?: 0,
                    Regex("kills:\\n").find(statsText)?.value?.substringAfter(":")?.toInt() ?: 0,
                    Regex("deaths:\\n").find(statsText)?.value?.substringAfter(":")?.toInt() ?: 0,
                    Regex("receivedCoins:\\n").find(statsText)?.value?.substringAfter(":")?.toInt() ?: 0,
                    Regex("receivedWood:\\n").find(statsText)?.value?.substringAfter(":")?.toInt() ?: 0,
                    Regex("receivedStone:\\n").find(statsText)?.value?.substringAfter(":")?.toInt() ?: 0,
                    Regex("receivedIron:\\n").find(statsText)?.value?.substringAfter(":")?.toInt() ?: 0,
                    Regex("receivedCopper:\\n").find(statsText)?.value?.substringAfter(":")?.toInt() ?: 0,
                    Regex("spentCoins:\\n").find(statsText)?.value?.substringAfter(":")?.toInt() ?: 0,
                    Regex("spentWood:\\n").find(statsText)?.value?.substringAfter(":")?.toInt() ?: 0,
                    Regex("spentStone:\\n").find(statsText)?.value?.substringAfter(":")?.toInt() ?: 0,
                    Regex("spentIron:\\n").find(statsText)?.value?.substringAfter(":")?.toInt() ?: 0,
                    Regex("spentCopper:\\n").find(statsText)?.value?.substringAfter(":")?.toInt() ?: 0,
                    Regex("poisonUsed:\\n").find(statsText)?.value?.substringAfter(":")?.toInt() ?: 0,
                )

            } else {
                throw Exception("${file.path}: File can't to read!")
            }
        }
    }
}
