package programs.other.analyticsCenter.countryAnalytics

data class CityData(
    val name: String = "Unknown",
    var population: Int = 0,
    val country: String = "Unknown",
) {
    override fun toString(): String {
        return "$name{country:$country,population:$population}"
    }
}