package com.example.hydrogenmobile.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hydrogenmobile.R
import com.example.hydrogenmobile.models.DataStats
import com.example.hydrogenmobile.utils.DrawGraph
import com.example.hydrogenmobile.utils.DrawRectangle
import com.example.hydrogenmobile.utils.leftBorder
import com.example.hydrogenmobile.utils.bottomBorder
import com.example.hydrogenmobile.utils.rightBorder
import com.example.hydrogenmobile.utils.topBorder

// Data Card
@Composable
fun defaultCard(
    modifier: Modifier = Modifier,
    filterName: String,
    data: DataStats?,
    history: List<Int>
) {
    // CardArea For Graph Panel
    Column (modifier = modifier) {
        Card(modifier = Modifier
            .fillMaxWidth()
            .weight(0.75f)
            .padding(bottom = 5.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            border = BorderStroke(1.dp, Color.LightGray),
            shape = RoundedCornerShape(5.dp),
            //elevation = CardDefaults.cardElevation(
            //    defaultElevation = 1.dp
            //)
        ) {
            Text (text = filterName,
                modifier = Modifier.padding(start = 10.dp, top = 5.dp),
                color = Color.Black
            )
            Box(modifier = Modifier
                .fillMaxSize()
                // .rightBorder(1.dp, Color.LightGray)
                // .topBorder(1.dp, Color.LightGray)
            ) {
                Row() {
                    Text (text = "\nY\nT\nI\nC\nK\nS",
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(0.04f)
                            .padding(0.dp),
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp
                    )
                    Column(modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.96f)
                    ) {
                        defaultGraphPanel(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 10.dp, top = 10.dp)
                                .weight(0.9f)
                                .leftBorder(1.dp, Color.LightGray)
                                .bottomBorder(1.dp, Color.LightGray),
                            data = data ?: DataStats(),
                            FilterName = filterName,
                            history = history
                        )
                        Text (text = "XTICKS",
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.11f)
                                .padding(bottom = 5.dp),
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
        // Card Area For Control Panel
        Card(modifier = Modifier
            .fillMaxWidth()
            .weight(0.25f)
            .padding(top = 5.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            border = BorderStroke(1.dp, Color.LightGray),
            shape = RoundedCornerShape(5.dp)
        ) {
            defaultControlPanel(
                modifier = Modifier
                    .fillMaxHeight(),
                filterName = "${filterName.filter { it.isUpperCase() }}\nCONTROL",
                data = data
            )
        }
    }
}
// Graph Panel
@Composable
fun defaultGraphPanel(
    modifier: Modifier = Modifier,
    data: DataStats,
    FilterName: String,
    history: List<Int>
) {
    DrawGraph(
        modifier = modifier,
        FilterName = FilterName,
        data = data.current,
        MaxY = data.max + 100,
        MinY = data.min - 100,
        xScale = 100,
        history
    )
}

// Control Panel
@Composable
fun defaultControlPanel(modifier: Modifier = Modifier, filterName: String, data: DataStats?) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ControlHeader(filterName)
        ControlValueComponents(
            "Average",
            data?.average ?: 0,
            modifier = Modifier
                .padding(top = 10.dp)
                .weight(1f),
            null
        )
        ControlValueComponents(
            "MIN",
            data?.min ?: 0,
            modifier = Modifier
                .padding(top = 10.dp)
                .weight(1f),
            null
        )
        ControlValueComponents(
            "MAX",
            data?.max ?: 0,
            modifier = Modifier
                .padding(top = 10.dp)
                .weight(1f),
            null
        )
        ControlValueComponents(
            "Peak-Peak",
            data?.peakToPeak ?: 0,
            modifier = Modifier
                .padding(top = 10.dp)
                .weight(1f),
            GetThresholdByName(filterName)?.maxPeakToPeak
        )
        ControlValueComponents(
            "ST DEV",
            data?.stDev ?: 0,
            modifier = Modifier
                .padding(top = 10.dp)
                .weight(1f),
            GetThresholdByName(filterName)?.maxStDev
        )
        Column(modifier = Modifier
            .padding(top = 10.dp)
            .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = { /* 클릭 시 실행할 동작 */ },
                modifier = Modifier
                    .weight(0.6f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.func_on_off_btn),
                    contentDescription = "Filter ON/OFF Button"
                )
            }
            Text (text = "GRAPH",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f),
                color = Color.Black,
                textAlign = TextAlign.Center,
                fontSize = 12.sp
            )
        }
        Column(modifier = Modifier
            .padding(top = 10.dp, end = 10.dp)
            .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = { /* 클릭 시 실행할 동작 */ },
                modifier = Modifier
                    .weight(0.6f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.func_on_off_btn),
                    contentDescription = "Graph Logging Button"
                )
            }
            Text (text = "ON/OFF",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f),
                color = Color.Black,
                textAlign = TextAlign.Center,
                fontSize = 12.sp
            )
        }
    }
}

// COMPONENTS IN CONTROL CARD
@Composable
fun ControlHeader(header: String) {
    Card (
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier
            .padding(start = 10.dp)
            //.rightBorder(1.dp, Color.LightGray),
    ) {
        Text (text = header,
            modifier = Modifier
                .padding(end = 10.dp),
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}
@Composable
fun ControlValueComponents(
    header: String,
    data: Int,
    modifier: Modifier = Modifier,
    errorThreshold: Int?
) {
    Column(modifier = modifier) {
        Text (text = header,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f)
                .wrapContentHeight(Alignment.CenterVertically),
            color = Color.Black,
            textAlign = TextAlign.Center,
            fontSize = 14.sp
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.1f)
                .padding(start = 5.dp, end = 5.dp, top = 2.dp, bottom = 2.dp)
        ) {
            DrawRectangle(
                modifier = Modifier.fillMaxSize(),
                data = data,
                errorThreshold
            )
        }

        Text (text = data.toString(),
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f),
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}

data class ErrorThreshold (
    val maxPeakToPeak: Int = Int.MAX_VALUE,
    val maxStDev: Int = Int.MAX_VALUE,
)

fun GetThresholdByName(FilterName:String): ErrorThreshold? {
    return when {
        FilterName.contains("RAW") -> ErrorThreshold(
            maxPeakToPeak = 1000,
            maxStDev = 150,
        )
        FilterName.contains("SAF") -> ErrorThreshold(
            maxPeakToPeak = 300,
            maxStDev = 50,
        )
        FilterName.contains("LPF") -> ErrorThreshold(
            maxPeakToPeak = 150,
            maxStDev = 18,
        )
        FilterName.contains("MAF") -> ErrorThreshold(
            maxPeakToPeak = 130,
            maxStDev = 15,
        )
        else -> ErrorThreshold()
    }
}