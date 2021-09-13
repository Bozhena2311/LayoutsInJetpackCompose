package com.example.layoutsinjetpackcompose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.example.layoutsinjetpackcompose.ui.theme.LayoutsInJetpackComposeTheme
import com.google.android.material.chip.Chip
import kotlin.math.max

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LayoutsInJetpackComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    BodyContent()
                }
            }
        }
    }
}

//Как создать модификатор
@Stable
fun Modifier.padding(all: Dp) =
    this.then(
        PaddingModifier(start = all, top = all, end = all, bottom = all, rtlAware = true)
    )

//Детали реализации
private class PaddingModifier(
    val start: Dp = 0.dp,
    val top: Dp = 0.dp,
    val end: Dp = 0.dp,
    val bottom: Dp = 0.dp,
    val rtlAware: Boolean,
) : LayoutModifier {

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {

        val horizontal = start.roundToPx() + end.roundToPx()
        val vertical = top.roundToPx() + bottom.roundToPx()

        val placeable = measurable.measure(constraints.offset(-horizontal, -vertical))

        val width = constraints.constrainWidth(placeable.width + horizontal)
        val height = constraints.constrainHeight(placeable.height + vertical)
        return layout(width, height) {
            if (rtlAware) {
                placeable.placeRelative(start.roundToPx(), top.roundToPx())
            } else {
                placeable.place(start.roundToPx(), top.roundToPx())
            }
        }
    }
}

@Composable
fun BodyContent(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(color = Color.LightGray, shape = RectangleShape)
            .size(200.dp)
            .padding(16.dp)
            .horizontalScroll(rememberScrollState())
    ) {
        StaggeredGrid {
            for (topic in topics) {
                Chip(modifier = Modifier.padding(8.dp), text = topic)
            }
        }
    }
}

fun Modifier.firstBaselineToTop(
    firstBaselineToTop: Dp
) = this.then(
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)

        //Проверка composable элемента на первую базовую линию
        check(placeable[FirstBaseline] != AlignmentLine.Unspecified)
        val firstBaseline = placeable[FirstBaseline]

        //Высота composable элемента с paddingami - первая базовая линия
        val placeableY = firstBaselineToTop.roundToPx() - firstBaseline
        val height = placeable.height + placeableY
        layout(placeable.width, height) {
            //здесь будет размещено composable
            placeable.placeRelative(0, placeableY)
        }
    }
)


@Composable
fun StaggeredGrid(
    modifier: Modifier = Modifier,
    rows: Int = 3,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->

        val rowWidths = IntArray(rows) { 0 }

        val rowHeights = IntArray(rows) { 0 }

        val placeables = measurables.mapIndexed { index, measurable ->

            val placeable = measurable.measure(constraints)

            val row = index % rows

            rowWidths[row] += placeable.width
            rowHeights[row] = max(rowHeights[row], placeable.height)

            placeable
        }

        val width = rowWidths.maxOrNull()
            ?.coerceIn(constraints.minWidth.rangeTo(constraints.maxWidth)) ?: constraints.minWidth

        val height = rowHeights.sumOf { it }
            .coerceIn(constraints.minHeight.rangeTo(constraints.maxHeight))

        val rowY = IntArray(rows) { 0 }
        for (i in 1 until rows) {
            rowY[i] = rowY[i-1] + rowHeights[i-1]
        }

        layout(width, height) {

            val rowX = IntArray(rows) { 0 }

            placeables.forEachIndexed { index, placeable ->
                val row = index % rows
                placeable.placeRelative(
                    x = rowX[row],
                    y = rowY[row]
                )
                rowX[row] += placeable.width
            }
        }
    }
}

@Composable
fun Chip(modifier: Modifier = Modifier, text: String) {
    Card(
        modifier = modifier,
        border = BorderStroke(color = Color.Black, width = Dp.Hairline),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp, 16.dp)
                    .background(color = MaterialTheme.colors.secondary)
            )
            Spacer(Modifier.width(4.dp))
            Text(text = text)
        }
    }
}

val topics = listOf(
    "Arts & Crafts", "Beauty", "Books", "Business", "Comics", "Culinary",
    "Design", "Fashion", "Film", "History", "Maths", "Music", "People", "Philosophy",
    "Religion", "Social sciences", "Technology", "TV", "Writing"
)

@Preview
@Composable
fun Preview() {
    LayoutsInJetpackComposeTheme {
        BodyContent()
    }
}

/*
@Composable
fun BodyContent(modifier: Modifier = Modifier) {
    Row(modifier = modifier.horizontalScroll(rememberScrollState())) {
        StaggeredGrid {
            for (topic in topics) {
                Chip(modifier = Modifier.padding(8.dp), text = topic)
            }
        }
    }
}


@Preview
@Composable
fun BodyContentPreview() {
    LayoutsInJetpackComposeTheme {
        BodyContent()
    }
}*/
/*@Preview
@Composable
fun ChipPreview() {
    LayoutsInJetpackComposeTheme {
        Chip(text = "Hi there")
    }
}*/

/*
@Composable
fun BodyContent(modifier: Modifier = Modifier) {
    MyOwnColumn(modifier.padding(8.dp)) {
        Text("MyOwnColumn")
        Text("places items")
        Text("vertically")
        Text("We've done it by hand!")
    }
}

/*@Composable
fun CustomLayout(
    modifier: Modifier = Modifier,
    // атрибуты собственной разметки
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        //измерьте и расположите здесь дочерние элементы, с учетом логики органичений здесь
    }
}*/

@Composable
fun MyOwnColumn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        //не ограничиваейте дочерние элементы дальше, измеряйте их с учетом заданных constraint
        //список измеренных дочерних элементов
        val placeables = measurables.map { measurable ->
        //Измерьте каждый дочерний элемент
        measurable.measure(constraints)
        }
        //Отслеживайте координаты где мы разместили дочерние элементы
        var yPosition = 0
        //Установите размер layouta как можно больше
        layout(constraints.maxWidth, constraints.maxHeight) {
            //Поместите детей в родительский макет
            placeables.forEach {placeable ->
            //Расположите элементы на экране
                placeable.placeRelative(x = 0, y = yPosition)

                //Запишите y координаты
                yPosition += placeable.height
            }
        }

    }
}



@Composable
fun ImageListItem(index: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {

        Image(
            painter = rememberImagePainter(
                data = "https://developer.android.com/images/brand/Android_Robot.png"
            ),
            contentDescription = "Android Logo",
            modifier = Modifier.size(50.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text("Item #$index", style = MaterialTheme.typography.subtitle1)
    }
}

@Composable
fun ScrollingList() {
    //количество элементов в списке
    val listSize = 100
    //сохраняем состояние скроллинга
    val scrollState = rememberLazyListState()
    //сохраняем область сопрограммы, в которой будет выполняться наш анимированный список
    val coroutineScope = rememberCoroutineScope()

    Row {
        Button(onClick = {
            coroutineScope.launch {
                //0 - индекс первого элемента
                scrollState.animateScrollToItem(0)
            }
        }) {
            Text("Скролл вверх")
        }

        Button(onClick = {
            coroutineScope.launch {
                //размер списка - 1 = индекс последнего элемента списка
                scrollState.animateScrollToItem(listSize - 1)
            }
        }) {
            Text("Скролл вниз")
        }
    }

    LazyColumn(state = scrollState) {
        items(listSize) {
            ImageListItem(it)
        }
    }
}

@Composable
fun SimpleList() {

    val scrollState = rememberScrollState() //Для скроллинга экрана

    Column (Modifier.verticalScroll(scrollState)) {
        repeat(100) {
            Text("Item #$it")
        }
    }
}



@Composable
fun LayoutsCodelab() {
    Scaffold(
        topBar = { //добавление верхней панели приложения
            TopAppBar(
                title = { //добавление заголовка верхней панели приложения
                    Text(text = "LayoutsCodelab")
                },
                actions = { //добавление значка-избранное в верхнюю панель приложения
                    IconButton(onClick = { /* doSomething */}) {
                        Icon(Icons.Filled.Favorite, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        BodyContent(Modifier
            .padding(innerPadding)
            .padding(8.dp)) //вынесение кода в составную функцию
    }                                               //для повторного использования кода и тестируемости
}

/*@Composable
fun BodyContent(modifier: Modifier = Modifier) {
    Column(modifier = Modifier) {
        Text(text = "Hi there!")
        Text(text = "Thanks for going through the Layouts codelab")
    }
}*/

@Composable
fun PhotographerCard(modifier: Modifier = Modifier) {
    Row(modifier
        .padding(8.dp)
        .clip(RoundedCornerShape(4.dp)) //скругление краев
        .background(MaterialTheme.colors.surface) //другой цвет
        .clickable(onClick = { /* Ignoring onClick */ })
        .padding(16.dp)
    ) {
        Surface(
            modifier = Modifier.size(50.dp),
            shape = CircleShape,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
        ) {
            //Images goes here
        }
        Column(
            modifier = Modifier
                .padding(start = 8.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text("Alfred Sisley", fontWeight = FontWeight.Bold)
            // LocalContentAlpha определяет уровень непрозрачности своих дочерних элементов
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text("3 minutes ago", style = MaterialTheme.typography.body2)
            }
        }
    }
}

@Preview
@Composable
fun BodyContent() {
    LayoutsInJetpackComposeTheme {
        BodyContent()
    }
}

@Preview
@Composable
fun TextWithPaddingToBaselinePreview() {
    LayoutsInJetpackComposeTheme {
        Text("Hi there!", Modifier.firstBaselineToTop(32.dp))
    }
}

@Preview
@Composable
fun TextWithNormalPaddingPreview() {
    LayoutsInJetpackComposeTheme {
        Text("Hi there!", Modifier.padding(top = 32.dp))
    }
}

@Preview
@Composable
fun SimpleListPreview() {
    LayoutsInJetpackComposeTheme {
        ScrollingList()
    }
}

@Preview
@Composable
fun LayoutsCodelabPreview() {
    LayoutsInJetpackComposeTheme {
        LayoutsCodelab()
    }
}

@Preview(showBackground = true)
@Composable
fun PhotographerCardPreview() {
    LayoutsInJetpackComposeTheme {
        PhotographerCard()
    }
}*/