package programs.other.mojang.minecraft.generator

enum class ObjectType {
    Tree, Rock, None;

    companion object {
        fun objectFromInt(value: Int): ObjectType {
            return when (value) {
                0 -> Tree
                1 -> Rock
                else -> None
            }
        }
    }
}