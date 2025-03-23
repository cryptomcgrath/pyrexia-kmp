package com.edwardmcgrath.pyrexia.service

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ApiErrorCard(
    apiError: ApiError,
    onButtonClick: () -> Unit = {},
    onLongPress: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    val indication = LocalIndication.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFffb8c1), shape = RoundedCornerShape(8.dp))
            .indication(interactionSource, indication) // Ripple effect
            .verticalScroll(rememberScrollState())
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null, // Avoid duplicating the ripple effect
                onClick = {
                    onButtonClick()
                },
                onLongClick = {
                    onLongPress()
                }
            ).padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (apiError.debug) apiError.debugMessage else apiError.message,
            color = Color.White,
            fontSize = if (apiError.debug) 10.sp else 21.sp,
            modifier = Modifier.padding(4.dp)
        )
    }
}