package com.edwardmcgrath.pyrexia.login


import androidx.lifecycle.ViewModel
import com.edwardmcgrath.pyrexia.service.ApiError
import com.edwardmcgrath.pyrexia.service.PyrexiaService
import com.edwardmcgrath.pyrexia.service.toApiError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel(private val url: String, private val pyrexiaService: PyrexiaService): ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun updateEmail(newEmail: String) {
        _uiState.value  = _uiState.value.copy(
            email = newEmail
        )
    }

    fun updatePassword(newPassword: String) {
        _uiState.value = _uiState.value.copy(
            password = newPassword
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

    suspend fun login(): Result<String> {
        _uiState.value = _uiState.value.copy(
            loadingState = LoadingState.Loading,
            error = null
        )
        val result  = try {
            val data = pyrexiaService.login(url, uiState.value.email, uiState.value.password)
            Result.success(data)
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = e.toApiError(),
                loadingState = LoadingState.Idle
            )
            Result.failure(e)
        }
        _uiState.value = _uiState.value.copy(
            loadingState = LoadingState.Idle
        )
        return result
    }
}

data class LoginUiState(
    val loadingState: LoadingState = LoadingState.Idle,
    val email: String = "",
    val password: String = "",
    val error: ApiError? = null
)

enum class LoadingState {
    Loading,
    Idle
}

