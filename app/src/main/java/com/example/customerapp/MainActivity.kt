package com.example.customerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.customerapp.auth.navigation.AuthNavigation
import com.example.customerapp.ui.navigation.FigmaAppNavigation
import com.example.customerapp.ui.theme.CustomerAppTheme
import com.example.customerapp.viewmodel.AuthViewModel
import com.example.customerapp.viewmodel.BookingViewModel

/**
 * Main Activity - Entry point with Email OTP authentication
 *
 * Flow:
 * 1. Show Email OTP authentication screens
 * 2. On successful authentication, show main app (Splash → Onboarding → Home)
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CustomerAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Authentication state management
                    var isAuthenticated by remember { mutableStateOf(false) }

                    if (!isAuthenticated) {
                        // Show Email OTP Authentication Flow
                        val authNavController = rememberNavController()
                        AuthNavigation(
                            navController = authNavController,
                            onAuthSuccess = {
                                // User authenticated successfully
                                isAuthenticated = true
                            }
                        )
                    } else {
                        // Show Main App (Splash → Onboarding → Home → Booking)
                        val navController = rememberNavController()
                        val authViewModel: AuthViewModel = viewModel()
                        val bookingViewModel: BookingViewModel = viewModel()

                        FigmaAppNavigation(
                            navController = navController,
                            authViewModel = authViewModel,
                            bookingViewModel = bookingViewModel
                        )
                    }
                }
            }
        }
    }
}
