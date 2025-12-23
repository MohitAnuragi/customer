package com.example.customerapp.model

/**
 * Data class representing a Location entity
 * Locations: Kolkata and Bombay (as per assignment)
 */
data class Location(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val isActive: Boolean = true
) {
    companion object {
        // Fixed locations as per assignment
        val KOLKATA = Location(
            id = "kolkata",
            name = "Kolkata",
            description = "Experience the cultural capital of India with its rich heritage and vibrant atmosphere",
            imageUrl = "kolkata_image",
            isActive = true
        )

        val BOMBAY = Location(
            id = "bombay",
            name = "Bombay",
            description = "The city of dreams offering endless opportunities and coastal beauty",
            imageUrl = "bombay_image",
            isActive = true
        )

        fun getAvailableLocations() = listOf(KOLKATA, BOMBAY)
    }
}

