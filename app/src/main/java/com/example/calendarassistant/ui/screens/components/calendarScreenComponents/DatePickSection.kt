package com.example.calendarassistant.ui.screens.components.calendarScreenComponents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.calendarassistant.ui.theme.DeepBlue
import com.example.calendarassistant.ui.theme.TealButton
import com.example.calendarassistant.ui.theme.TextWhite
import java.time.LocalDate

@Composable
fun DatePickSection(
    dates: List<String>,
    startIndex: String
) {
    // TODO: Replace year logic with LocalDate.now() logic in backend to calculate last year, current year + 5 years onwards or something
    val years = listOf("2023", "2024", "2025", "2026", "2027", "2028")
    // TODO: Don't know if I should put this somewhere else
    val months =
        listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec")

    Column {
        Row() {
            // Month picker
            ClickableScroller(
                items = months,
                startIndex = "0", // TODO: Change with logic from backend
                verticalScroll = true,
                modifier = Modifier
                    .width(180.dp)
            )
            // Year picker
            ClickableScroller(
                items = years,
                startIndex = "0", // TODO: Change with logic from backend
                verticalScroll = true,
                modifier = Modifier
                    .width(250.dp)
            )
        }
        // Day picker
        ClickableScroller(
            items = dates,
            startIndex = startIndex,
            verticalScroll = false,
            modifier = Modifier
        )
    }
}

@Composable
fun ClickableScroller(
    items: List<String>,
    startIndex: String,
    verticalScroll: Boolean,
    modifier: Modifier
) {
    var selectedDateIndex by remember { mutableStateOf(startIndex) }
    val listState = rememberLazyListState()

    if (!verticalScroll) {
        // Scrolls the list to desired state, only works for the day picker right now
        LaunchedEffect(startIndex) {
            val indexToScrollTo = items.indexOf(startIndex).coerceAtLeast(1)
            listState.scrollToItem(
                index = indexToScrollTo + 2,
                scrollOffset = -listState.layoutInfo.viewportEndOffset / 2
            )
        }
    }

    Box(
        modifier = Modifier
            .padding(16.dp)
            .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(8.dp))
            .then(modifier)
    ) {
        if (!verticalScroll) {
            LazyRow(
                state = listState
            ) {
                itemsIndexed(items) { index, item ->
                    ItemContent(
                        item,
                        index,
                        selectedDateIndex,
                        onSelect = { newIndex ->
                            selectedDateIndex = newIndex
                            // TODO: Call VM to update UI based on the new index
                        },
                        horizontal = true
                    )
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .height(125.dp)
            ) {
                itemsIndexed(items) { index, item ->
                    ItemContent(
                        item,
                        index,
                        selectedDateIndex,
                        onSelect = { newIndex ->
                            selectedDateIndex = newIndex
                            // TODO: Call VM to update UI based on the new index
                        },
                        horizontal = false
                    )
                }
            }
        }
    }
}

@Composable
fun ItemContent(
    item: String,
    index: Int,
    selectedDateIndex: String,
    onSelect: (String) -> Unit,
    horizontal: Boolean
) {
    val horizontalModifier = Modifier
        .padding(start = 6.dp, top = 12.dp, bottom = 12.dp, end = 6.dp)
        .width(70.dp)
        .clickable {
            onSelect(index.toString())
        }
        .clip(RoundedCornerShape(8.dp))
        .background(
            if (selectedDateIndex == index.toString()) ButtonBlue
            else DeepBlue
        )
        .padding(15.dp)

    val verticalModifier = Modifier
        .padding(start = 12.dp, top = 6.dp, bottom = 6.dp, end = 12.dp)
        .fillMaxWidth()
        .clickable {
            onSelect(index.toString())
        }
        .clip(RoundedCornerShape(8.dp))
        .background(
            if (selectedDateIndex == index.toString()) ButtonBlue
            else DeepBlue
        )
        .padding(10.dp)

    Box(
        contentAlignment = Alignment.Center,
        modifier = if (horizontal) horizontalModifier else verticalModifier
    ) {
        Text(
            text = item,
            color = TextWhite,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}