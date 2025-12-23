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
                    var isAuthenticated by remember { mutableStateOf(false) }

                    if (!isAuthenticated) {

                        val authNavController = rememberNavController()
                        AuthNavigation(
                            navController = authNavController,
                            onAuthSuccess = {
                                isAuthenticated = true
                            }
                        )
                    } else {
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
