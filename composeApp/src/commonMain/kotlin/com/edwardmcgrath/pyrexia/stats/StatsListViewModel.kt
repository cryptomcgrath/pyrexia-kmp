package com.edwardmcgrath.pyrexia.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edwardmcgrath.pyrexia.service.ApiError
import com.edwardmcgrath.pyrexia.service.PyrexiaService
import com.edwardmcgrath.pyrexia.service.Stat
import com.edwardmcgrath.pyrexia.login.LoadingState
import com.edwardmcgrath.pyrexia.service.toApiError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StatsListViewModel(val url: String,
                         val pyrexiaService: PyrexiaService,
                         val statId: Int = 0) : ViewModel() {
    private val _uiState = MutableStateFlow(StatsListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            showLoading()
            try {
                val response = pyrexiaService.getStatList(url)
                _uiState.value = _uiState.value.copy(
                    stats = response.data,
                    loadingState = LoadingState.Idle,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.toApiError(),
                    loadingState = LoadingState.Idle
                )
            }
        }
    }

    private fun showLoading() {
        _uiState.value =
            uiState.value.copy(
                loadingState = LoadingState.Loading,
                error = null
            )
    }

    fun toggleDebugError() {
        _uiState.value =
            uiState.value.copy(
                error = _uiState.value.error?.copy(
                    debug = !(_uiState.value.error?.debug ?: false)
                )
            )
    }

    fun statIncrease(id: Int) {
        viewModelScope.launch {
            val response = pyrexiaService.statIncrease(url, id)
            _uiState.value = uiState.value.copy(
                stats = uiState.value.stats.toMutableList().map {
                    if (it.program_id == id) response.data else it
                }
            )
        }
    }

    fun statDecrease(id: Int) {
        viewModelScope.launch {
            val response = pyrexiaService.statDecrease(url, id)
            _uiState.value = uiState.value.copy(
                stats = uiState.value.stats.toMutableList().map {
                    if (it.program_id == id) response.data else it
                }
            )
        }
    }

    fun refill(controlId: Int, id: Int) {
        viewModelScope.launch {
            val response = pyrexiaService.refill(url, controlId, statId)
            _uiState.value = uiState.value.copy(
                stats = uiState.value.stats.toMutableList().map {
                    if (it.program_id == id) response.data else it
                }
            )
        }
    }
}

data class StatsListUiState(
    val stats: List<Stat> = emptyList(),
    val statId: Int = 0,
    val loadingState: LoadingState = LoadingState.Idle,
    val error: ApiError? = null
)