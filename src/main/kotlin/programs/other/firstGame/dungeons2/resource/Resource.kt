package programs.other.firstGame.dungeons2.resource

data class Resource(
    val type: ResourceType,
    private var count: Int,
) {
    fun add(n: Int) {
        count += n
    }

    fun remove(n: Int) {
        count -= n
    }
}
