package com.edwardmcgrath.pyrexia

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.edwardmcgrath.pyrexia.login.loginScreen
import com.edwardmcgrath.pyrexia.stats.statInfoScreen
import com.edwardmcgrath.pyrexia.stats.statsListScreen
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = Target.LoginTarget
        ) {
            composable<Target.LoginTarget> {
                loginScreen(deviceUrl, onSuccess = {
                    navController.navigateUp()
                    navController.navigate(Target.StatsTarget(deviceUrl))
                })
            }
            composable<Target.StatsTarget> {
                val args = it.toRoute<Target.StatsTarget>()
                statsListScreen(args.url,
                    onBack = {
                        navController.navigateUp()
                    },
                    goToStat = { id ->
                        navController.navigate(Target.StatInfoTarget(args.url, id))
                    })
            }
            composable<Target.StatInfoTarget> {
                val args = it.toRoute<Target.StatInfoTarget>()
                statInfoScreen(args.url,
                    args.id,
                    onBack = {
                        navController.navigateUp()
                    })
            }
        }
    }
}

private const val deviceUrl = "http://192.168.0.119:8000"

@Composable
fun <T: ViewModel> viewModel(factory: () -> T): T {
    return remember { factory() }
}


