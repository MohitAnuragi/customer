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

class BookingViewModel : ViewModel() {

    private val repository = FirebaseRepository()


    private val _locationsState = MutableStateFlow<LocationsState>(LocationsState.Loading)
    val locationsState: StateFlow<LocationsState> = _locationsState.asStateFlow()

    private val _selectedLocation = MutableStateFlow<Location?>(null)
    val selectedLocation: StateFlow<Location?> = _selectedLocation.asStateFlow()

    private val _bookingState = MutableStateFlow<BookingState>(BookingState.Idle)
    val bookingState: StateFlow<BookingState> = _bookingState.asStateFlow()

    private val _currentBookingId = MutableStateFlow<String?>(null)
    val currentBookingId: StateFlow<String?> = _currentBookingId.asStateFlow()


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


    fun selectLocation(location: Location) {
        _selectedLocation.value = location
    }


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

    private fun observeBookingStatus(bookingId: String) {
        viewModelScope.launch {
            repository.observeBookingStatus(bookingId).collect { status ->
                when (status) {
                    "pending" -> _bookingState.value = BookingState.Pending
                    "accepted" -> {
                        _bookingState.value = BookingState.Accepted
                        delay(2500)
                        resetBookingState()
                    }
                    "rejected" -> {
                        _bookingState.value = BookingState.Rejected
                        delay(2500)
                        resetBookingState()
                    }
                    else -> _bookingState.value = BookingState.Error("Unknown status: $status")
                }
            }
        }
    }

    fun resetBookingState() {
        _bookingState.value = BookingState.Idle
        _currentBookingId.value = null
    }


}

sealed class LocationsState {
    object Loading : LocationsState()
    data class Success(val locations: List<Location>) : LocationsState()
    data class Error(val message: String) : LocationsState()
}


