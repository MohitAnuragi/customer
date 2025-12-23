package com.example.customerapp.ui.navigation

/**
 * Navigation routes - type-safe navigation
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object LocationDetails : Screen("location_details")
    object BookingStatus : Screen("booking_status")
}

