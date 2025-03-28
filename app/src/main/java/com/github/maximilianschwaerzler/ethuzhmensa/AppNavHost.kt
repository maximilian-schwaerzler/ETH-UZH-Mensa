package com.github.maximilianschwaerzler.ethuzhmensa

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.github.maximilianschwaerzler.ethuzhmensa.data.MensaDetailScreenViewModel
import com.github.maximilianschwaerzler.ethuzhmensa.data.OverviewScreenViewModel
import com.github.maximilianschwaerzler.ethuzhmensa.ui.MensaDetailScreen
import com.github.maximilianschwaerzler.ethuzhmensa.ui.OverviewScreen
import com.github.maximilianschwaerzler.ethuzhmensa.ui.SettingsScreen
import com.github.maximilianschwaerzler.ethuzhmensa.ui.SplashScreen
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
object SplashScreen

@Serializable
object OverviewScreen

@Serializable
object SettingsScreen

@Serializable
data class MensaDetailScreen(
    val facilityId: Int,
    val date: Long
)

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController, SplashScreen, modifier) {
        composable<SplashScreen> {
            SplashScreen {
                navController.navigate(
                    OverviewScreen,
                    NavOptions.Builder().setPopUpTo(SplashScreen, true).build()
                )
            }
        }

        composable<OverviewScreen> {
            val viewModel: OverviewScreenViewModel = hiltViewModel()
            val isLoading = viewModel.isLoading.collectAsStateWithLifecycle()
            val facilitiesWithOffers = viewModel.facilitiesWithOffers.collectAsStateWithLifecycle()

            OverviewScreen(
                isLoading = isLoading.value,
                facilitiesWithOffers = facilitiesWithOffers.value,
                onRefresh = viewModel::onPullToRefresh,
                onSettingsNavigate = {
                    navController.navigate(SettingsScreen)
                },
                onDetailScreenNavigate = { facilityId, date ->
                    navController.navigate(
                        MensaDetailScreen(facilityId, date.toEpochDay())
                    )
                }
            )
        }

        composable<SettingsScreen> {
            SettingsScreen(onNavigateUp = navController::popBackStack)
        }

        composable<MensaDetailScreen> {
            val viewModel: MensaDetailScreenViewModel = hiltViewModel()
            val mensaDetailScreenRoute = it.toRoute<MensaDetailScreen>()
            viewModel.loadFacilityAndMenus(
                mensaDetailScreenRoute.facilityId,
                mensaDetailScreenRoute.date.let { LocalDate.ofEpochDay(it) }
            )
            val facility = viewModel.facility.collectAsStateWithLifecycle()
            val menus = viewModel.menus.collectAsStateWithLifecycle()
            MensaDetailScreen(
                facility.value,
                menus.value,
                onNavigateUp = { navController.popBackStack() })
        }
    }
}