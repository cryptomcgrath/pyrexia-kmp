package com.edwardmcgrath.pyrexia.stats

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edwardmcgrath.pyrexia.service.ApiErrorCard
import com.edwardmcgrath.pyrexia.service.PyrexiaService
import com.edwardmcgrath.pyrexia.viewModel


@Composable
fun statInfoScreen(url: String, id: Int, onBack: () -> Unit) {
    val viewModel: StatsListViewModel = viewModel {
        StatsListViewModel(url, PyrexiaService.instance, id)
    }

    val uiState = viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center) {
        uiState.value.error?.let {
            ApiErrorCard(
                apiError = it,
                onLongPress = {
                    viewModel.toggleDebugError()
                }
            )
        }
        uiState.value.stats.firstOrNull {
            it.program_id == id
        }?.let { stat ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stat.program_name,
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    modifier = Modifier.width(250.dp)
                )
                Spacer(
                    modifier = Modifier.fillMaxWidth().height(10.dp)
                )
                ThermostatView(
                    currentTemp = stat.sensor_value.formatTemp(),
                    setPoint = stat.set_point.formatTemp(),
                    message = "",
                    centerColor = Color.Blue,
                    setPointTickColor = Color.Cyan,
                    bezelWidth = 24.dp,
                    tickGapDegrees = 1.375f,
                    onClickIncrease = {
                        println("onClickIncrease")
                        viewModel.statIncrease(stat.program_id)
                    },
                    onClickDecrease = {
                        println("onClickDecrease")
                        viewModel.statDecrease(stat.program_id)
                    },
                    modifier = Modifier.width(250.dp).height(250.dp)
                )
            }
            Text(
                text = "Mode: ${stat.mode}",
                textAlign = TextAlign.Left,
                fontSize = 14.sp
            )
            Row(
                modifier = Modifier
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val propane = 100 - (stat.total_run.toFloat() / stat.run_capacity.toFloat() * 100f).toInt()
                Text(
                    text = "Propane: ${propane}%",
                    textAlign = TextAlign.Left,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    modifier = Modifier.clickable(enabled=true) {
                        viewModel.refill(stat.control_id, stat.program_id)
                    },
                    text = "refill",
                    textAlign = TextAlign.Left,
                    fontSize = 14.sp,
                    color = Color.Blue
                )
            }


        }
    }
}