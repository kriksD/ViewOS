package programs.standart.sapper

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import os.desktop.SubWindowData
import viewOsUis.Table
import viewOsUis.TableData
import java.util.*

class SapperData {
    companion object {
        const val size = 10
    }
}

class MyRandom(seed: Long) : Random(seed) {
    var iterations = 0L

    override fun nextInt(bound: Int): Int {
        iterations++
        setSeed(Calendar.getInstance().timeInMillis + iterations)
        return super.nextInt(bound)
    }
}

data class SapperField(val notBomb: Boolean, var clicked: Boolean) {
    override fun toString(): String {
        return if (!clicked) {
            "■"
        } else if (!notBomb) {
            "x"
        } else if (notBomb) {
            ""
        } else {
            "error"
        }
    }
}

@Composable
fun Sapper(
    data: SubWindowData,
) {
    val random = MyRandom(Calendar.getInstance().timeInMillis)
    val arrayFields = remember {
        mutableStateOf(
            if (data.args["fields"] != null) {
                data.args["fields"] as MutableList<MutableList<SapperField>>
            } else {
                MutableList(SapperData.size) {
                    MutableList(SapperData.size) {
                        SapperField(random.nextInt(5) != 0, false)
                    }
                }
            }
        )
    }

    val text = remember { mutableStateOf("Click on a field!") }
    val tableVisible = remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(text.value)

        if (tableVisible.value) {
            Table(
                data = arrayFields.value,
                columnWidth = TableData.minimumWidth,
                onItemClicked = { fText: MutableState<String>, column: Int, row: Int ->
                    arrayFields.value[column][row].clicked = true

                    when (val fieldText = arrayFields.value[column][row].toString()) {
                        "x" -> {
                            text.value = "You loosed!"

                            arrayFields.value.forEach { i ->
                                i.forEach {
                                    it.clicked = true
                                }
                            }

                            tableVisible.value = false

                        }
                        "" -> {
                            var bombCount = 0

                            for (i in (column - 1)..(column + 1)) {
                                for (j in (row - 1)..(row + 1)) {
                                    if (i in 0..9 && j in 0..9) {
                                        if (!arrayFields.value[i][j].notBomb) {
                                            bombCount++
                                        }
                                    }
                                }
                            }

                            var isWin = 0
                            arrayFields.value.forEach { i ->
                                i.forEach {
                                    if (it.clicked || !it.notBomb) {
                                        isWin++
                                    }
                                }
                            }

                            if (isWin >= 100) {
                                text.value = "You win!"

                                arrayFields.value.forEach { i ->
                                    i.forEach {
                                        it.clicked = true
                                    }
                                }

                                tableVisible.value = false
                            }

                            fText.value = bombCount.toString()
                        }
                        else -> {
                            fText.value = fieldText
                        }
                    }

                    data.args["fields"] = arrayFields.value
                }
            )
        } else {
            tableVisible.value = true
        }
    }
}