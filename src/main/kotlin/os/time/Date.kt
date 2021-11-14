package os.time

data class Date(var day: Long) {
    override fun toString(): String{
        return "${day}д"
    }
}
