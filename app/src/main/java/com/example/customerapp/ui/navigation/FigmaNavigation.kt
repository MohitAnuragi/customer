package com.example.customerapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.customerapp.ui.screens.*
import com.example.customerapp.viewmodel.AuthState
import com.example.customerapp.viewmodel.AuthViewModel
import com.example.customerapp.viewmodel.BookingViewModel

/**
 * Main navigation with Figma-matched screens
 */
@Composable
fun FigmaAppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel(),
    bookingViewModel: BookingViewModel = viewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val email by authViewModel.email.collectAsState()
    val generatedOtp by authViewModel.generatedOtp.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val customerId by authViewModel.customerId.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // 1. Splash Screen
        composable("splash") {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate("onboarding") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        // 2. Onboarding Screen
        composable("onboarding") {
            OnboardingScreen(
                onGetStarted = {
                    navController.navigate("signin")
                }
            )
        }

        // 3. Sign In Screen
        composable("signin") {
            SignInScreen(
                email = email,
                onEmailChange = authViewModel::updateEmail,
                onSignIn = {
                    authViewModel.requestOtp()
                    navController.navigate("verification")
                },
                errorMessage = errorMessage,
                onBack = {
                    navController.navigateUp()
                }
            )
        }

        // 4. Verification Screen
        composable("verification") {
            VerificationScreen(
                email = email,
                generatedOtp = generatedOtp,
                onVerify = { otp ->
                    authViewModel.verifyOtp(otp)
                },
                errorMessage = errorMessage,
                onBack = {
                    navController.navigateUp()
                },
                onResendCode = {
                    authViewModel.requestOtp()
                }
            )
        }

        // 5. Home Screen (navigate after authentication)
        composable("home") {
            FigmaHomeScreen(
                bookingViewModel = bookingViewModel,
                onLocationClick = { location ->
                    bookingViewModel.selectLocation(location)
                    navController.navigate(Screen.LocationDetails.route)
                }
            )
        }

        // Location Details Screen (new Figma design)
        composable(Screen.LocationDetails.route) {
            DetailScreen(
                bookingViewModel = bookingViewModel,
                customerId = customerId ?: "",
                onBookNow = {
                    navController.navigate(Screen.BookingStatus.route)
                },
                onBack = {
                    navController.navigateUp()
                }
            )
        }

        // Booking Status Screen
        composable(Screen.BookingStatus.route) {
            BookingStatusScreen(
                bookingViewModel = bookingViewModel,
                onNavigateBack = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }

    // Navigate to home when authenticated
    if (authState is AuthState.Authenticated) {
        navController.navigate("home") {
            popUpTo("verification") { inclusive = true }
        }
    }
}

