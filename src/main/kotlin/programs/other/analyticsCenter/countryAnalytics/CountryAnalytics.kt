package programs.other.analyticsCenter.countryAnalytics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import os.desktop.SubWindowData
import os.manager.InternetManager
import os.properties.OsProperties
import viewOsAppends.Line
import java.io.File
import kotlin.random.Random

@Composable
fun CountryAnalytics(
    data: SubWindowData,
) {
    val countryOpened = remember {
        mutableStateOf(
            if (data.args["country"] != null) data.args["country"] as String else ""
        )
    }

    val cities = remember { getCities() }
    val countries = remember { formationCountries(cities) }

    val countryScroll = rememberLazyListState(0)
    val cityScroll = rememberLazyListState(0)

    Row {
        LazyColumn(
            state = countryScroll,
        ) {
            countries.forEach {
                item {
                    DataView(
                        title = it.name,
                        data = mapOf(Pair("Population", it.population())),
                        onClick = { countryOpened.value = it.name }
                    )
                }
            }
        }

        Line(
            color = Color.DarkGray,
            padding = 8.dp,
        )

        LazyColumn(
            state = cityScroll
        ) {
            val country = countries.find { it.name == countryOpened.value }

            country?.let {
                it.cities.forEach {
                    item {
                        DataView(
                            title = it.name,
                            data = mapOf(
                                Pair("Country", it.country),
                                Pair("Population", it.population),
                            ),
                            onClick = {
                                countryOpened.value = it.name
                            }
                        )
                    }
                }
            }
        }
    }

    val onTimeUpAdded = remember { mutableStateOf(false) }
    if (!onTimeUpAdded.value) {
        createPopulationGenerator(cities)
        onTimeUpAdded.value = true
    }
}

@Composable
private fun DataView(
    title: String,
    data: Map<String, Any>,
    onClick: () -> Unit = {},
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .padding(8.dp)
            .background(Color.DarkGray)
            .padding(8.dp)
            .clickable { onClick() },
    ) {
        Text(title, fontSize = 24.sp)

        data.forEach {
            Text(
                text = "${it.key}: ${it.value}",
                fontSize = 18.sp,
                color = Color.LightGray,
            )
        }
    }
}

fun getCities(): List<CityData> {
    val file = File("${InternetManager.internetFolderPath()}/Country Analytics/cities.txt")
    val foundCities = Regex(".+\\{.+}").findAll(if (file.exists()) file.readText() else "")
    val cities = mutableListOf<CityData>()

    foundCities.forEach { foundCity ->
        val foundCityText = foundCity.value

        val name = foundCityText.substringBefore("{")
        val population = Regex("population:\\d+").find(foundCityText)?.value?.substringAfter(":")?.toInt() ?: 0
        val country =
            Regex("country:.+,").find(foundCityText)?.value?.substringAfter(":")?.replace(",", "") ?: "Unknown"

        cities.add(CityData(name, population, country))
    }

    return cities
}

fun formationCountries(cities: Collection<CityData>): List<CountryData> {
    val countries = mutableListOf(
        CountryData(
            name = "Human Country"
        )
    )

    cities.forEach { city ->
        countries.find { country -> country.name == city.name }?.addCity(city)
            ?: countries.add(CountryData(cities = mutableListOf(city), name = city.country))

        countries.find { country -> country.name == "Human Country" }?.addCity(city)
    }

    return countries
}

fun createPopulationGenerator(cities: Collection<CityData>) {
    OsProperties.addOnTimeUp("populationGenerator") { time, date ->
        if (time.minute == 5) {
            var newCitiesText = ""

            cities.forEach { city ->
                val random = Random(OsProperties.currentTime().toLong() + OsProperties.currentDate().day)

                when (city.population) {
                    in 0..10 -> random.nextInt(2)
                    in 10..100 -> city.population += random.nextInt(-1, 2)
                    in 100..500 -> city.population += random.nextInt(-3, 4)
                    in 500..1000 -> city.population += random.nextInt(-5, 6)
                    in 1000..3000 -> city.population += random.nextInt(-8, 9)
                    in 3000..6000 -> city.population += random.nextInt(-10, 11)
                    in 6000..10000 -> city.population += random.nextInt(-11, 12)
                    else -> city.population += random.nextInt(-12, 13)
                }

                newCitiesText += city.toString() + "\n"

                // Old a population generator. Just I don't want to remove it. Sorry :(
                /*println(sqrt(it.population.toDouble()))
                val addPopulation = it.population.toDouble() * (sqrt(it.population.toDouble()) / 500.0 + 1.0)
                val removePopulation = it.population.toDouble() * (sqrt(it.population.toDouble()) / 800.0 + 1.0)
                val alreadyNewPopulation = Random(OsProperties.currentTime().toLong() + OsProperties.currentDate().day)
                    .nextInt(removePopulation.toInt(), addPopulation.toInt())
                it.population = alreadyNewPopulation*/
            }

            val file = File("${InternetManager.internetFolderPath()}/Country Analytics/cities.txt")
            if (file.exists()) file.writeText(newCitiesText)
        }
    }
}
