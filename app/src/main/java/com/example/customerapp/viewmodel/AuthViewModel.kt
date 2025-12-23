package com.example.customerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.customerapp.model.Customer
import com.example.customerapp.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class AuthViewModel : ViewModel() {

    private val repository = FirebaseRepository()


    private val _authState = MutableStateFlow<AuthState>(AuthState.EmailInput)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _customerId = MutableStateFlow<String?>(null)
    val customerId: StateFlow<String?> = _customerId.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _generatedOtp = MutableStateFlow("")
    val generatedOtp: StateFlow<String> = _generatedOtp.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
        _errorMessage.value = null
    }

    fun requestOtp() {
        val emailValue = _email.value.trim()

        if (emailValue.isBlank() || !emailValue.contains("@")) {
            _errorMessage.value = "Please enter a valid email address"
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading

            repository.generateOTP(emailValue).fold(
                onSuccess = { otp ->
                    _generatedOtp.value = otp
                    _authState.value = AuthState.OtpInput
                    _errorMessage.value = null
                },
                onFailure = { error ->
                    _errorMessage.value = "Failed to send OTP: ${error.message}"
                    _authState.value = AuthState.EmailInput
                }
            )
        }
    }

    fun verifyOtp(enteredOtp: String) {
        if (enteredOtp.isBlank()) {
            _errorMessage.value = "Please enter the OTP"
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading

            repository.verifyOTP(_email.value, enteredOtp).fold(
                onSuccess = { isValid ->
                    if (isValid) {
                        val customerId = generateCustomerId(_email.value)
                        _customerId.value = customerId

                        val customer = Customer(
                            customerId = customerId,
                            email = _email.value
                        )

                        repository.saveCustomer(customer).fold(
                            onSuccess = {
                                _authState.value = AuthState.Authenticated
                                _errorMessage.value = null
                            },
                            onFailure = { error ->
                                _errorMessage.value = "Failed to save customer data: ${error.message}"
                                _authState.value = AuthState.OtpInput
                            }
                        )
                    } else {
                        _errorMessage.value = "Invalid OTP. Please try again."
                        _authState.value = AuthState.OtpInput
                    }
                },
                onFailure = { error ->
                    _errorMessage.value = "Verification failed: ${error.message}"
                    _authState.value = AuthState.OtpInput
                }
            )
        }
    }

    private fun generateCustomerId(email: String): String {
        return "customer_${email.substringBefore("@").replace(".", "_")}_${System.currentTimeMillis()}"
    }

    fun clearError() {
        _errorMessage.value = null
    }


    fun logout() {
        _authState.value = AuthState.EmailInput
        _customerId.value = null
        _email.value = ""
        _generatedOtp.value = ""
        _errorMessage.value = null
    }
}
sealed class AuthState {
    object EmailInput : AuthState()
    object OtpInput : AuthState()
    object Loading : AuthState()
    object Authenticated : AuthState()
}

