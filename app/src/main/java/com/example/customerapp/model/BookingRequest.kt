package com.example.customerapp.model

/**
 * Data class representing a booking request
 * Stored in Firebase Realtime Database
 */
data class BookingRequest(
    val bookingId: String = "",
    val customerId: String = "",
    val location: String = "",
    val status: String = "pending", // pending | accepted | rejected
    val timestamp: Long = System.currentTimeMillis()
) {
    // Firebase requires a no-arg constructor
    constructor() : this("", "", "", "pending", 0L)

    fun toMap(): Map<String, Any> {
        return mapOf(
            "customerId" to customerId,
            "location" to location,
            "status" to status,
            "timestamp" to timestamp
        )
    }
}

