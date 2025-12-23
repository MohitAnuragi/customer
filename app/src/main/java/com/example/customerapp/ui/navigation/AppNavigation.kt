package com.example.customerapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.customerapp.ui.screens.BookingStatusScreen
import com.example.customerapp.ui.screens.HomeScreen
import com.example.customerapp.ui.screens.LocationDetailsScreen
import com.example.customerapp.ui.screens.LoginScreen
import com.example.customerapp.viewmodel.AuthState
import com.example.customerapp.viewmodel.AuthViewModel
import com.example.customerapp.viewmodel.BookingViewModel

/**
 * Main navigation graph with Firebase integration
 * Passes customerId through navigation flow
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel(),
    bookingViewModel: BookingViewModel = viewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val customerId by authViewModel.customerId.collectAsState()

    val startDestination = if (authState is AuthState.Authenticated) {
        Screen.Home.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Login Screen
        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Home Screen
        composable(Screen.Home.route) {
            HomeScreen(
                bookingViewModel = bookingViewModel,
                onLocationClick = { location ->
                    bookingViewModel.selectLocation(location)
                    navController.navigate(Screen.LocationDetails.route)
                }
            )
        }

        // Location Details Screen
        composable(Screen.LocationDetails.route) {
            LocationDetailsScreen(
                bookingViewModel = bookingViewModel,
                customerId = customerId ?: "",
                onBookNowClick = {
                    navController.navigate(Screen.BookingStatus.route)
                },
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }

        // Booking Status Screen
        composable(Screen.BookingStatus.route) {
            BookingStatusScreen(
                bookingViewModel = bookingViewModel,
                onNavigateBack = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

