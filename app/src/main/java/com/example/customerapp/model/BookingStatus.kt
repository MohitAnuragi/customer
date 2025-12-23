package com.example.customerapp.model

sealed class BookingState {
    object Idle : BookingState()
    object Pending : BookingState()
    object Accepted : BookingState()
    object Rejected : BookingState()
    data class Error(val message: String) : BookingState()
}

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

