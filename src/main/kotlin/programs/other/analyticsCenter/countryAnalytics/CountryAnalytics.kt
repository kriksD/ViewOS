package programs.other.analyticsCenter.countryAnalytics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import os.desktop.SubWindowData
import java.io.File

private data class CityData(
    val name: String = "Unknown",
    var population: Int = 0,
    val country: String = "Unknown",
)

private data class CountryData(
    val cities: MutableList<CityData>,
    val name: String = "Unknown",
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

@Composable
fun CountryAnalytics(
    programsThatOpen: MutableList<SubWindowData>,
    data: SubWindowData,
) {
    val countryOpened = remember {
        mutableStateOf(
            if (data.args["country"] != null) data.args["country"] as String else ""
        )
    }
    val dataFile = File("ViewOS/ProgramData/Country Analytics/cities.txt")
    val citiesText = Regex(".+\\{.+}").findAll(if (dataFile.exists()) dataFile.readText() else "")

    val cities = remember { mutableListOf<CityData>() }
    val countries = remember {
        mutableListOf(
            CountryData(
                cities = mutableListOf(),
                name = "Human Country"
            )
        )
    }
    citiesText.forEach {
        val cityTextData = it.value

        val name = cityTextData.substringBefore("{")
        val population = Regex("population:\\d+").find(cityTextData)?.value?.substringAfter(":")?.toInt() ?: 0
        val country = Regex("country:.+,").find(cityTextData)?.value?.substringAfter(":")?.replace(",", "") ?: "Unknown"

        val city = CityData(name, population, country)
        cities.add(city)

        countries.find { fCountry ->
            fCountry.name == country
        }?.addCity(city) ?: countries.add(
            CountryData(
                cities = mutableListOf(city),
                name = country
            )
        )
        countries.find { fCountry -> fCountry.name == "Human Country" }?.addCity(city)
    }

    val countryScroll = rememberLazyListState(0)
    val cityScroll = rememberLazyListState(0)

    val reload = remember { mutableStateOf(true) }
    if (reload.value) {
        Row(

        ) {
            LazyColumn(
                state = countryScroll,
            ) {
                countries.forEach {
                    item {
                        DataView(
                            title = it.name,
                            data = mapOf(Pair("Population", it.population())),
                            onClick = {
                                countryOpened.value = it.name
                            }
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .background(Color.DarkGray)
                    .width(5.dp)
                    .fillMaxHeight(),
            ) {}

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

    } else {
        reload.value = true
    }
}

@Composable
fun DataView(
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
            .clickable {
                onClick()
            },
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
