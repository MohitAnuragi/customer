package com.example.customerapp.auth.model

sealed class LoginState {

    object Idle : LoginState()

     object Loading : LoginState()


    object CodeSent : LoginState()


    object Verified : LoginState()


    data class Error(val message: String) : LoginState()
}

data class OtpState(
    val digits: List<String> = List(6) { "" }
) {

    fun isComplete(): Boolean = digits.all { it.isNotEmpty() }


    fun getOtp(): String = digits.joinToString("")
}


data class TimerState(
    val secondsRemaining: Int = 60,
    val isEnabled: Boolean = false
) {

    fun formattedTime(): String {
        val minutes = secondsRemaining / 60
        val seconds = secondsRemaining % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}

