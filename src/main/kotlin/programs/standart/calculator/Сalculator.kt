package programs.standart.calculator

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.notkamui.keval.keval
import os.desktop.SubWindowData

@Composable
fun Calculator(
    programsThatOpen: MutableList<SubWindowData>,
    data: SubWindowData,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val exp = remember { mutableStateOf(
            if (data.args["exp"] != null) data.args["exp"].toString() else ""
        ) }
        val output = remember { mutableStateOf(
            try {
                val result = exp.value.keval()
                result.toString()
            } catch (e: Exception) {
                e.message.toString()
            }
        ) }

        TextField(
            value = exp.value,
            onValueChange = {
                exp.value = it

                try {
                    val result = exp.value.keval()
                    output.value = result.toString()
                } catch (e: Exception) {
                    output.value = e.message.toString()
                }

                data.args["exp"] = exp.value
            },
            modifier = Modifier
                .fillMaxWidth(),
        )

        Text(
            output.value,
            color = Color.LightGray,
            fontSize = 22.sp,
            modifier = Modifier
                .fillMaxWidth(),
        )
    }
}