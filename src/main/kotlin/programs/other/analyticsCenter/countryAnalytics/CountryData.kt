package programs.other.analyticsCenter.countryAnalytics

data class CountryData(
    val name: String = "Unknown",
    val cities: MutableList<CityData> = mutableListOf(),
) {
    fun population(): Int {
        var tPopulation = 0

        cities.forEach {
            tPopulation += it.population
        }

        return tPopulation
    }

    fun addCity(city: CityData) {
        cities.add(city)
    }

    fun removeCity(city: CityData) {
        cities.remove(city)
    }
}