package com.example.customerapp.auth.model

/**
 * Sealed class representing authentication states
 * Used for type-safe state management in ViewModel
 */
sealed class LoginState {
    /**
     * Initial idle state before any action
     */
    object Idle : LoginState()

    /**
     * Loading state during Firebase operations
     */
    object Loading : LoginState()

    /**
     * Email link sent successfully
     * Waiting for user to verify via email
     */
    object CodeSent : LoginState()

    /**
     * Email link verified successfully
     * User is authenticated
     */
    object Verified : LoginState()

    /**
     * Error state with error message
     * @param message Human-readable error message
     */
    data class Error(val message: String) : LoginState()
}

/**
 * Data class representing OTP input state
 * @param digits Array of 6 digits (0-9 or empty)
 */
data class OtpState(
    val digits: List<String> = List(6) { "" }
) {
    /**
     * Check if all 6 digits are filled
     */
    fun isComplete(): Boolean = digits.all { it.isNotEmpty() }

    /**
     * Get OTP as complete string
     */
    fun getOtp(): String = digits.joinToString("")
}

/**
 * Data class representing resend timer state
 * @param secondsRemaining Seconds until resend is enabled
 */
data class TimerState(
    val secondsRemaining: Int = 60,
    val isEnabled: Boolean = false
) {
    /**
     * Format timer as MM:SS
     */
    fun formattedTime(): String {
        val minutes = secondsRemaining / 60
        val seconds = secondsRemaining % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}

