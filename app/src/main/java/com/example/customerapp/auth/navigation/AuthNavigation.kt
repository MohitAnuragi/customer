package com.example.customerapp.auth.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.customerapp.auth.ui.EmailInputScreen
import com.example.customerapp.auth.ui.OtpVerificationScreen
import com.example.customerapp.auth.viewmodel.EmailLinkAuthViewModel

/**
 * Navigation routes for authentication flow
 */
sealed class AuthRoute(val route: String) {
    object EmailInput : AuthRoute("email_input")
    object OtpVerification : AuthRoute("otp_verification")
}

/**
 * Authentication navigation graph
 * Handles email input → OTP verification flow
 *
 * @param navController Navigation controller
 * @param onAuthSuccess Callback when authentication is successful
 */
@Composable
fun AuthNavigation(
    navController: NavHostController,
    onAuthSuccess: () -> Unit
) {
    // Single ViewModel instance shared across auth screens
    val authViewModel: EmailLinkAuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = AuthRoute.EmailInput.route
    ) {
        // Email Input Screen
        composable(AuthRoute.EmailInput.route) {
            EmailInputScreen(
                viewModel = authViewModel,
                onCodeSent = {
                    navController.navigate(AuthRoute.OtpVerification.route)
                }
            )
        }

        // OTP Verification Screen
        composable(AuthRoute.OtpVerification.route) {
            OtpVerificationScreen(
                viewModel = authViewModel,
                onVerified = {
                    // Navigate to main app
                    onAuthSuccess()
                },
                onBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}

