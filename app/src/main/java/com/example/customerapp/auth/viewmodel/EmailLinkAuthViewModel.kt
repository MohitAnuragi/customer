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


class EmailLinkAuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = EmailLinkAuthRepository(application)

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _otpState = MutableStateFlow(OtpState())
    val otpState: StateFlow<OtpState> = _otpState.asStateFlow()

    // Timer state for resend OTP
    private val _timerState = MutableStateFlow(TimerState(secondsRemaining = 60, isEnabled = false))
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    // Timer job reference
    private var timerJob: Job? = null


    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun isValidEmail(): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return _email.value.matches(emailPattern.toRegex())
    }

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

    fun updateOtpDigit(index: Int, digit: String) {
        if (index !in 0..5) return

        val newDigits = _otpState.value.digits.toMutableList()
        newDigits[index] = digit.take(1) // Only take first character
        _otpState.value = OtpState(newDigits)

        if (_otpState.value.isComplete()) {
            verifyOtp()
        }
    }

    fun clearOtpDigit(index: Int) {
        if (index !in 0..5) return

        val newDigits = _otpState.value.digits.toMutableList()
        newDigits[index] = ""
        _otpState.value = OtpState(newDigits)
    }


    private fun verifyOtp() {
        val otp = _otpState.value.getOtp()

        viewModelScope.launch {
            _loginState.value = LoginState.Loading

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

    private fun startResendTimer() {
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
            _timerState.value = TimerState(secondsRemaining = 0, isEnabled = true)
        }
    }


    private fun stopResendTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun resetToEmailInput() {
        _loginState.value = LoginState.Idle
        _otpState.value = OtpState()
        stopResendTimer()
    }


    override fun onCleared() {
        super.onCleared()
        stopResendTimer()
    }
}

