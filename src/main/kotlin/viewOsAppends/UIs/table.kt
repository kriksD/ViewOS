package viewOsAppends

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class TableType {
    WITH_COLUMN_TITLE, NORMAL
}

class TableData {
    companion object {
        val minimumWidth = 50.dp
    }
}

@Composable
fun Table(
    modifier: Modifier = Modifier,
    data: Map<String, Collection<Any>>,
    type: TableType = TableType.NORMAL,
    columnWidth: Dp = 400.dp,
    onItemClicked: (titleOfItem: MutableState<String>, columnPositionM: Int, rowPositionM: Int) -> Unit
) {
    Row(modifier = modifier) {
        var c = 0
        var r = 0

        data.forEach { columnData ->
            Column {
                if (type == TableType.WITH_COLUMN_TITLE) {
                    TableColumnTitle(columnData.key)
                }

                val width = remember { mutableStateOf(columnWidth) }

                for (title in columnData.value) {
                    r++

                    TableField(
                        titleOfItem = title.toString(),
                        width = width,
                        columnPosition = c,
                        rowPosition = r,
                        onItemClicked = onItemClicked
                    )
                }
            }
            c++
            r = 0
        }
    }

}

@Composable
fun Table(
    modifier: Modifier = Modifier,
    data: Collection<Collection<Any>>,
    columnWidth: Dp = 400.dp,
    onItemClicked: (titleOfItem: MutableState<String>, columnPositionM: Int, rowPositionM: Int) -> Unit
) {
    Row(modifier = modifier) {
        var c = 0
        var r = 0

        data.forEach { columnData ->
            Column {
                val width = remember { mutableStateOf(columnWidth) }

                for (title in columnData) {
                    TableField(
                        titleOfItem = title.toString(),
                        width = width,
                        columnPosition = c,
                        rowPosition = r,
                        onItemClicked = onItemClicked
                    )

                    r++
                }
            }

            c++
            r = 0
        }
    }

}

@Composable
private fun TableField(
    titleOfItem: String,
    width: MutableState<Dp>,
    columnPosition: Int,
    rowPosition: Int,
    onItemClicked: (titleOfItem: MutableState<String>, columnPositionM: Int, rowPositionM: Int) -> Unit
) {
    val columnPositionM = remember { mutableStateOf(columnPosition) }
    val rowPositionM = remember { mutableStateOf(rowPosition) }
    val title = remember { mutableStateOf(titleOfItem) }

    Button(
        shape = RoundedCornerShape(0.dp),
        border = BorderStroke(1.dp, Color.Black),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Unspecified),
        onClick = {
            onItemClicked(title, columnPositionM.value, rowPositionM.value)
        },
        modifier = Modifier
            .width(width.value)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consumeAllChanges()
                    if (width.value > TableData.minimumWidth) {
                        width.value += dragAmount.x.dp
                    } else {
                        width.value = TableData.minimumWidth + 1.dp
                    }
                }
            }
    ) {
        Text(text = title.value, color = Color.DarkGray, maxLines = 1)
    }
}

@Composable
private fun TableColumnTitle(
    title: String
) {
    Text(
        title,
        color = Color.Black,
        maxLines = 1
    )
}