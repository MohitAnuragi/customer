package com.example.customerapp.model

data class BookingRequest(
    val bookingId: String = "",
    val customerId: String = "",
    val location: String = "",
    val status: String = "pending",
    val timestamp: Long = System.currentTimeMillis()
) {

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

