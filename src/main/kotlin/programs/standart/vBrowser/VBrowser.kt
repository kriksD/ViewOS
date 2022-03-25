package programs.standart.vBrowser

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import os.desktop.SubWindowData
import os.manager.InternetManager
import viewOsAppends.emptyImageBitmap
import viewOsAppends.getImageBitmap
import java.io.File

private interface VBBaseView {
    var width: Int
    var height: Int
    var background: Color
}

private open class VBEndRow : VBBaseView {
    final override var width: Int = 0
    final override var height: Int = 0
    final override var background: Color = Color(0, 0, 0)
}

private open class VBView(argsAsString: String) : VBBaseView {

    final override var width = 150
    final override var height = 40
    final override var background = Color.Gray

    init {
        val args = argsAsString.split("|")

        args.forEach { arg ->
            if (arg.contains(Regex("background=rgb\\(\\d{1,3},\\d{1,3},\\d{1,3}\\)"))) {
                val digits = Regex("\\d{1,3}").findAll(arg)
                val rgbValues = mutableListOf<Int>()

                digits.forEach { value ->
                    rgbValues.add(value.value.toInt())
                }

                background = Color(rgbValues[0], rgbValues[1], rgbValues[2])
            } else if (arg.contains(Regex("width=(\\d+|max)"))) {
                val widthAsString = Regex("(\\d+|max)").find(arg)?.value ?: "100"

                width = if (widthAsString == "max") {
                    -1
                } else {
                    widthAsString.toInt()
                }

            } else if (arg.contains(Regex("height=\\d+"))) {
                val heightAsString = Regex("\\d+").find(arg)?.value ?: "100"
                height = heightAsString.toInt()
            }
        }
    }

    override fun toString(): String {
        return "$background $width $height ${hashCode()}"
    }

}

private open class VBText(argsAsString: String) : VBView(argsAsString) {

    var color = Color.DarkGray
    var text = ""
    var fontSize = 14
    var align = Alignment.TopStart

    init {
        val args = argsAsString.split("|")

        args.forEach { arg ->
            if (arg.contains(Regex("text=.*"))) {
                text = arg.substringAfter("text=")

            } else if (arg.contains(Regex("fontSize=\\d+"))) {
                val fontSizeAsString = Regex("\\d+").find(arg)?.value ?: "12"
                fontSize = fontSizeAsString.toInt()

            } else if (arg.contains(Regex("color=rgb\\(\\d{1,3},\\d{1,3},\\d{1,3}\\)"))) {
                val digits = Regex("\\d{1,3}").findAll(arg)
                val rgbValues = mutableListOf<Int>()

                digits.forEach { value ->
                    rgbValues.add(value.value.toInt())
                }

                color = Color(rgbValues[0], rgbValues[1], rgbValues[2])

            } else if (arg.contains(Regex("align=.*"))) {
                align = when (arg.substringAfter("align=")) {
                    "center" -> Alignment.Center
                    "topStart" -> Alignment.TopStart
                    "topCenter" -> Alignment.TopCenter
                    "topEnd" -> Alignment.TopEnd
                    "endCenter" -> Alignment.CenterEnd
                    "bottomEnd" -> Alignment.BottomEnd
                    "bottomCenter" -> Alignment.BottomCenter
                    "bottomStart" -> Alignment.BottomStart
                    "startCenter" -> Alignment.CenterStart
                    else -> Alignment.TopStart
                }
            }
        }

        text = text.replace("\\n", "\n")
    }
}

private open class VBUrlText(argsAsString: String) : VBText(argsAsString) {

    var url = ""

    init {
        val args = argsAsString.split("|")

        args.forEach { arg ->
            if (arg.contains(Regex("url=vgs-.*"))) {
                url = arg.substringAfter("url=")
            }
        }
    }

    override fun toString(): String {
        return "${super.toString()}   url = $url"
    }
}

private open class VBButton(argsAsString: String) : VBView(argsAsString) {

    var color = Color.DarkGray
    var url = ""
    var text = ""
    var fontSize = 14

    init {
        val args = argsAsString.split("|")

        args.forEach { arg ->
            if (arg.contains(Regex("url=vgs-.*"))) {
                url = arg.substringAfter("url=")

            } else if (arg.contains(Regex("color=rgb\\(\\d{1,3},\\d{1,3},\\d{1,3}\\)"))) {
                val digits = Regex("\\d{1,3}").findAll(arg)
                val rgbValues = mutableListOf<Int>()

                digits.forEach { value ->
                    rgbValues.add(value.value.toInt())
                }

                color = Color(rgbValues[0], rgbValues[1], rgbValues[2])

            } else if (arg.contains(Regex("fontSize=\\d+"))) {
                val fontSizeAsString = Regex("\\d+").find(arg)?.value ?: "12"
                fontSize = fontSizeAsString.toInt()

            } else if (arg.contains(Regex("text=.*"))) {
                text = arg.substringAfter("text=")
            }
        }

        text = text.replace("\\n", "\n")
    }

    override fun toString(): String {
        return "${super.toString()}   url = $url"
    }
}

private open class VBImage(argsAsString: String) : VBView(argsAsString) {
    var src = ""

    init {
        val args = argsAsString.split("|")

        args.forEach { arg ->
            if (arg.contains(Regex("src=(ViewOS|Hidden files/Internet)/.*"))) {

                src = arg.substringAfter("src=")
            }
        }
    }
}

@Composable
fun VBrowser(
    data: SubWindowData,
) {

    val url = remember { mutableStateOf(
        if (data.args["url"] != null) data.args["url"].toString() else "vgs-ViewOS"
    ) }
    val pageContent = remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val pageFile = File("${InternetManager.internetFolderPath()}/VBrowser/Sites/${url.value}.vgml")

        if (pageFile.exists()) {
            pageContent.value = pageFile.readText()
        }

        TextField(
            value = url.value,
            onValueChange = {
                url.value = it
                data.args["url"] = url.value
            },
            modifier = Modifier.fillMaxWidth()
        )

        val scrollState = remember { mutableStateOf(0) }

        Box(
            modifier = Modifier.fillMaxSize().verticalScroll(ScrollState(scrollState.value))
        ) {
            ContentArea(
                contentAsString = pageContent.value,
                url = url,
                data = data
            )
        }
    }
}

@Composable
private fun ContentArea(
    contentAsString: String,
    url: MutableState<String>,
    data: SubWindowData,
) {
    val views = contentToListViews(contentAsString)

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (contentAsString.contains(Regex("<vgml1>.*"))) {
            var currentIndexView = 0
            for (endRow in 0..views.filterIsInstance<VBEndRow>().size + 1) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    for (i in currentIndexView until views.size) {
                        currentIndexView++
                        val view: VBBaseView = views[i]
                        if (view is VBEndRow) break

                        val modifier: Modifier = if (view.width == -1) {
                            Modifier
                                .fillMaxWidth()
                                .height(view.height.dp)
                                .background(view.background)
                        } else {
                            Modifier
                                .width(view.width.dp)
                                .height(view.height.dp)
                                .background(view.background)
                        }

                        when (view) {
                            is VBButton -> {
                                Button(
                                    onClick = {
                                        url.value = view.url
                                        data.args["url"] = url.value
                                    },
                                    modifier = modifier,
                                    colors = ButtonDefaults.buttonColors(backgroundColor = view.background)
                                ) {
                                    Text(
                                        text = view.text,
                                        color = view.color,
                                        textAlign = TextAlign.Center,
                                        fontSize = view.fontSize.sp
                                    )
                                }
                            }
                            is VBUrlText -> {
                                Box(
                                    modifier = modifier
                                        .clickable {
                                            url.value = view.url
                                            data.args["url"] = url.value
                                        },
                                    contentAlignment = view.align,
                                ) {
                                    Text(
                                        text = view.text,
                                        color = view.color,
                                        fontSize = view.fontSize.sp,
                                    )
                                }
                            }
                            is VBText -> {
                                Box(
                                    contentAlignment = view.align,
                                    modifier = modifier,
                                ) {
                                    Text(
                                        text = view.text,
                                        color = view.color,
                                        fontSize = view.fontSize.sp,
                                    )
                                }
                            }
                            is VBImage -> {
                                val image = getImageBitmap(File(view.src)) ?: emptyImageBitmap

                                Image(
                                    bitmap = image,
                                    contentDescription = "image",
                                    modifier = modifier
                                )
                            }
                            is VBView -> {
                                Box(
                                    modifier = modifier
                                ) {}
                            }
                        }
                    }
                }
            }
        } else if (contentAsString.contains(Regex("<vgml2>.*"))) {
            Text("vgml2")
        } else {
            Text("Error: VGML version not found")
        }
    }
}

private fun contentToListViews(contentAsString: String): List<VBBaseView> {
    val viewsAsString = contentAsString.replace(Regex("[\n\r\t]"), "").split(";")

    val views = mutableListOf<VBBaseView>()

    viewsAsString.forEach { view ->
        if (view.contains(Regex("View:.*"))) {
            views.add(VBView(view.substringAfter("View:")))

        } else if (view.contains(Regex("EndRow"))) {
            views.add(VBEndRow())

        } else if (view.contains(Regex("UrlText:.*"))) {
            views.add(VBUrlText(view.substringAfter("UrlText:")))

        } else if (view.contains(Regex("Text:.*"))) {
            views.add(VBText(view.substringAfter("Text:")))

        } else if (view.contains(Regex("Button:.*"))) {
            views.add(VBButton(view.substringAfter("Button:")))

        } else if (view.contains(Regex("Image:.*"))) {
            views.add(VBImage(view.substringAfter("Image:")))
        }
    }

    return views
}