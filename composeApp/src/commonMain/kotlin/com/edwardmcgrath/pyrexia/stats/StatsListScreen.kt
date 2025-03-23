package com.edwardmcgrath.pyrexia.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.edwardmcgrath.pyrexia.service.ApiErrorCard
import com.edwardmcgrath.pyrexia.service.PyrexiaService
import com.edwardmcgrath.pyrexia.viewModel


@Composable
fun statsListScreen(url: String, onBack: () -> Unit, goToStat: (Int) -> Unit) {
    val viewModel: StatsListViewModel = viewModel {
        StatsListViewModel(url, PyrexiaService.instance)
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
        LazyVerticalGrid(
            columns = GridCells.Adaptive(140.dp),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            content = {
                items(uiState.value.stats.size) { idx ->
                    val stat = uiState.value.stats[idx]
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = stat.program_name,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(130.dp)
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
                            bezelWidth = 16.dp,
                            tickGapDegrees = 1.5f,
                            onClickIncrease = {
                                println("onClickIncrease")
                                viewModel.statIncrease(stat.program_id)
                            },
                            onClickDecrease = {
                                println("onClickDecrease")
                                viewModel.statDecrease(stat.program_id)
                            },
                            onClickCenter = {
                                goToStat.invoke(stat.program_id)
                            },
                            modifier = Modifier.width(130.dp).height(130.dp)
                        )
                    }
                }
            }
        )
    }
}

