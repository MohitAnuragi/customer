package com.example.customerapp.auth.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.customerapp.auth.model.LoginState
import com.example.customerapp.auth.model.OtpState
import com.example.customerapp.auth.model.TimerState
import com.example.customerapp.auth.repository.EmailLinkAuthRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel handling Email Link OTP Authentication
 *
 * This implements a modern OTP-like login flow using Firebase Email Link Authentication.
 * The email link behaves as a passwordless OTP verification system.
 *
 * Key Features:
 * - Email validation
 * - 6-digit OTP input with auto-focus
 * - 60-second resend timer
 * - Real-time state management
 * - Clean MVVM architecture
 */
class EmailLinkAuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = EmailLinkAuthRepository(application)

    // Login state management
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    // Email state
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    // OTP state (6 digits)
    private val _otpState = MutableStateFlow(OtpState())
    val otpState: StateFlow<OtpState> = _otpState.asStateFlow()

    // Timer state for resend OTP
    private val _timerState = MutableStateFlow(TimerState(secondsRemaining = 60, isEnabled = false))
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    // Timer job reference
    private var timerJob: Job? = null

    /**
     * Update email value
     * @param newEmail New email address
     */
    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    /**
     * Validate email format
     * @return true if email is valid
     */
    fun isValidEmail(): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return _email.value.matches(emailPattern.toRegex())
    }

    /**
     * Send email link (OTP)
     * This triggers Firebase to send an email with verification link
     * Starts the 60-second countdown timer
     */
    fun sendOtp() {
        if (!isValidEmail()) {
            _loginState.value = LoginState.Error("Please enter a valid email address")
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            repository.sendSignInLink(_email.value).fold(
                onSuccess = {
                    _loginState.value = LoginState.CodeSent
                    startResendTimer() // Start 60-second countdown
                },
                onFailure = { error ->
                    _loginState.value = LoginState.Error(
                        error.message ?: "Failed to send verification email"
                    )
                }
            )
        }
    }

    /**
     * Update OTP digit at specific position
     * Automatically manages 6-digit OTP input
     *
     * @param index Position (0-5)
     * @param digit Single digit (0-9) or empty
     */
    fun updateOtpDigit(index: Int, digit: String) {
        if (index !in 0..5) return

        val newDigits = _otpState.value.digits.toMutableList()
        newDigits[index] = digit.take(1) // Only take first character
        _otpState.value = OtpState(newDigits)

        // Auto-verify when all 6 digits are filled
        if (_otpState.value.isComplete()) {
            verifyOtp()
        }
    }

    /**
     * Clear OTP digit at specific position
     * Used for backspace/delete functionality
     *
     * @param index Position (0-5)
     */
    fun clearOtpDigit(index: Int) {
        if (index !in 0..5) return

        val newDigits = _otpState.value.digits.toMutableList()
        newDigits[index] = ""
        _otpState.value = OtpState(newDigits)
    }

    /**
     * Verify OTP (email link)
     * This simulates OTP verification
     * In production, this would verify the actual email link
     */
    private fun verifyOtp() {
        val otp = _otpState.value.getOtp()

        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            // Small delay for UX (show loading state)
            delay(500)

            repository.verifyOtp(otp).fold(
                onSuccess = {
                    _loginState.value = LoginState.Verified
                    stopResendTimer() // Stop timer on successful verification
                },
                onFailure = { error ->
                    _loginState.value = LoginState.Error(
                        error.message ?: "Invalid OTP. Please try again."
                    )
                    // Clear OTP on error
                    _otpState.value = OtpState()
                }
            )
        }
    }

    /**
     * Resend OTP (email link)
     * Only works when timer reaches 0
     * Restarts the 60-second countdown
     */
    fun resendOtp() {
        if (!_timerState.value.isEnabled) return // Prevent resend if timer not expired

        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            repository.sendSignInLink(_email.value).fold(
                onSuccess = {
                    _loginState.value = LoginState.CodeSent
                    startResendTimer() // Restart timer
                },
                onFailure = { error ->
                    _loginState.value = LoginState.Error(
                        error.message ?: "Failed to resend verification email"
                    )
                }
            )
        }
    }

    /**
     * Start 60-second countdown timer
     * Timer runs in background using coroutines
     * Updates UI every second
     * Enables resend button when timer reaches 0
     */
    private fun startResendTimer() {
        // Cancel existing timer if any
        stopResendTimer()

        _timerState.value = TimerState(secondsRemaining = 60, isEnabled = false)

        timerJob = viewModelScope.launch {
            for (seconds in 59 downTo 0) {
                delay(1000) // Wait 1 second
                _timerState.value = TimerState(
                    secondsRemaining = seconds,
                    isEnabled = false
                )
            }
            // Enable resend button when timer reaches 0
            _timerState.value = TimerState(secondsRemaining = 0, isEnabled = true)
        }
    }

    /**
     * Stop countdown timer
     * Called on successful verification or cleanup
     */
    private fun stopResendTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    /**
     * Reset to initial state
     * Used when navigating back to email input
     */
    fun resetToEmailInput() {
        _loginState.value = LoginState.Idle
        _otpState.value = OtpState()
        stopResendTimer()
    }

    /**
     * Clear error state
     */
    fun clearError() {
        if (_loginState.value is LoginState.Error) {
            _loginState.value = LoginState.CodeSent
        }
    }

    /**
     * Cleanup when ViewModel is destroyed
     */
    override fun onCleared() {
        super.onCleared()
        stopResendTimer()
    }
}

