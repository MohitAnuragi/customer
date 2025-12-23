package com.example.customerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.customerapp.model.BookingState
import com.example.customerapp.model.Location
import com.example.customerapp.repository.FirebaseRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * BookingViewModel - Handles booking operations with Firebase real-time updates
 * Following MVVM architecture
 */
class BookingViewModel : ViewModel() {

    private val repository = FirebaseRepository()

    // Locations state
    private val _locationsState = MutableStateFlow<LocationsState>(LocationsState.Loading)
    val locationsState: StateFlow<LocationsState> = _locationsState.asStateFlow()

    // Selected location
    private val _selectedLocation = MutableStateFlow<Location?>(null)
    val selectedLocation: StateFlow<Location?> = _selectedLocation.asStateFlow()

    // Booking state - uses sealed class as per assignment
    private val _bookingState = MutableStateFlow<BookingState>(BookingState.Idle)
    val bookingState: StateFlow<BookingState> = _bookingState.asStateFlow()

    // Current booking ID for real-time listening
    private val _currentBookingId = MutableStateFlow<String?>(null)
    val currentBookingId: StateFlow<String?> = _currentBookingId.asStateFlow()

    /**
     * Load locations from Firebase
     */
    fun loadLocations() {
        viewModelScope.launch {
            _locationsState.value = LocationsState.Loading

            repository.getLocations().fold(
                onSuccess = { locations ->
                    _locationsState.value = LocationsState.Success(locations)
                },
                onFailure = { error ->
                    _locationsState.value = LocationsState.Error(error.message ?: "Failed to load locations")
                }
            )
        }
    }

    /**
     * Select a location
     */
    fun selectLocation(location: Location) {
        _selectedLocation.value = location
    }

    /**
     * Submit booking to Firebase and listen for real-time updates
     */
    fun submitBooking(customerId: String) {
        val location = _selectedLocation.value ?: return

        viewModelScope.launch {
            _bookingState.value = BookingState.Pending

            repository.createBooking(customerId, location.id).fold(
                onSuccess = { bookingId ->
                    _currentBookingId.value = bookingId

                    // Start listening to booking status in REAL-TIME
                    observeBookingStatus(bookingId)
                },
                onFailure = { error ->
                    _bookingState.value = BookingState.Error(error.message ?: "Failed to create booking")
                }
            )
        }
    }

    /**
     * Observe booking status changes in REAL-TIME using Firebase listeners
     * NO POLLING - pure real-time updates
     */
    private fun observeBookingStatus(bookingId: String) {
        viewModelScope.launch {
            repository.observeBookingStatus(bookingId).collect { status ->
                when (status) {
                    "pending" -> _bookingState.value = BookingState.Pending
                    "accepted" -> {
                        _bookingState.value = BookingState.Accepted
                        // After showing result, auto-navigate back to home
                        delay(2500)
                        resetBookingState()
                    }
                    "rejected" -> {
                        _bookingState.value = BookingState.Rejected
                        // After showing result, auto-navigate back to home
                        delay(2500)
                        resetBookingState()
                    }
                    else -> _bookingState.value = BookingState.Error("Unknown status: $status")
                }
            }
        }
    }

    /**
     * Reset booking state to idle
     */
    fun resetBookingState() {
        _bookingState.value = BookingState.Idle
        _currentBookingId.value = null
    }

    /**
     * Clear selected location
     */
    fun clearSelectedLocation() {
        _selectedLocation.value = null
    }
}

/**
 * Sealed class for locations list state
 */
sealed class LocationsState {
    object Loading : LocationsState()
    data class Success(val locations: List<Location>) : LocationsState()
    data class Error(val message: String) : LocationsState()
}


