package com.example.calendarassistant.ui.screens.components.calendarScreenComponents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendarassistant.ui.theme.ButtonBlue
import com.example.calendarassistant.ui.theme.DarkerButtonBlue
import com.example.calendarassistant.ui.theme.DeepBlue
import com.example.calendarassistant.ui.theme.TextWhite


// TODO: Rework into full scale calendar?
//  Need to be able to swap months, year etc
@Composable
fun DatePickSection(
    dates: List<String>,
    startIndex: String
) {
    var selectedDateIndex by remember {
        mutableStateOf(startIndex)
    }
    val listState = rememberLazyListState()
    LaunchedEffect(startIndex) {
        val indexToScrollTo = dates.indexOf(startIndex).coerceAtLeast(1)
        listState.scrollToItem(index = indexToScrollTo + 2, scrollOffset = -listState.layoutInfo.viewportEndOffset / 2)
    }
    
    Box(
        modifier = Modifier
            .padding(16.dp)
            .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(8.dp))
    ) {
        LazyRow(state = listState) {
            items(dates.size) { index ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(start = 12.dp, top = 12.dp, bottom = 12.dp)
                        .width(70.dp)
                        .clickable {
                            selectedDateIndex = index.toString()
                            // TODO: Load information about this date from VM
                        }
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (selectedDateIndex == index.toString()) ButtonBlue
                            else DeepBlue
                        )
                        .padding(15.dp)
                ) {
                    Text(
                        text = dates[index],
                        color = TextWhite,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}