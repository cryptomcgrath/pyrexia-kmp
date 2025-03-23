package com.edwardmcgrath.pyrexia.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edwardmcgrath.pyrexia.service.ApiErrorCard
import com.edwardmcgrath.pyrexia.service.PyrexiaService
import com.edwardmcgrath.pyrexia.viewModel
import kotlinx.coroutines.launch

@Composable
fun loginScreen(url: String, onSuccess: () -> Unit) {
    val viewModel: LoginViewModel = viewModel {
        LoginViewModel(url, PyrexiaService.instance)
    }
    val uiState = viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    fun beginLogin() {
        if (uiState.value.loadingState == LoadingState.Idle) {
            scope.launch {
                val result = viewModel.login()
                if (result.isSuccess) {
                    onSuccess.invoke()
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center)
    {
        Text(
            text = "Login",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp)
        )

        uiState.value.error?.let {
            ApiErrorCard(
                apiError = it,
                onLongPress = {
                    viewModel.toggleDebugError()
                }
            )
        }

        OutlinedTextField(
            value = uiState.value.email,
            onValueChange = { newValue -> viewModel.updateEmail(newValue) },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = uiState.value.password,
            onValueChange = { newValue -> viewModel.updatePassword(newValue) },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    beginLogin()
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                beginLogin()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.value.loadingState == LoadingState.Idle) {
                Text("Login")
            } else {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                    strokeWidth = 2.dp
                )
            }
        }
    }
}
