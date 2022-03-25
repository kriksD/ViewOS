package viewOsAppends.UIs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxColors
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class CheckboxTextPosition {
    LEFT, RIGHT
}

@Composable
fun TextCheckbox(
    text: String,
    check: Boolean,
    textFontSize: TextUnit = 16.sp,
    textColor: Color = Color.Black,
    checkboxColors: CheckboxColors = CheckboxDefaults.colors(),
    textPosition: CheckboxTextPosition = CheckboxTextPosition.RIGHT,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .clickable(
                onClick = {
                    onCheckedChange(!check)
                }
            )
    ) {
        if (textPosition == CheckboxTextPosition.LEFT) {
            Text(
                text = text,
                color = textColor,
                fontSize = textFontSize
            )
        }

        Checkbox(
            checked = check,
            onCheckedChange = onCheckedChange,
            colors = checkboxColors
        )

        if (textPosition == CheckboxTextPosition.RIGHT) {
            Text(
                text = text,
                color = textColor,
                fontSize = textFontSize
            )
        }
    }
}