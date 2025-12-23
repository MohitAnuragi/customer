package com.example.customerapp.model

/**
 * Sealed class representing booking status states
 * Used for type-safe state management in ViewModels
 */
sealed class BookingState {
    object Idle : BookingState()
    object Pending : BookingState()
    object Accepted : BookingState()
    object Rejected : BookingState()
    data class Error(val message: String) : BookingState()
}

/**
 * Customer data class for Firebase
 */
data class Customer(
    val customerId: String = "",
    val email: String = ""
) {
    constructor() : this("", "")

    fun toMap(): Map<String, Any> {
        return mapOf(
            "email" to email
        )
    }
}

