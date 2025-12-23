package com.example.customerapp.auth.repository

import android.content.Context
import android.util.Log
import kotlinx.coroutines.delay


class EmailLinkAuthRepository(private val context: Context) {

    companion object {
        private const val TAG = "EmailLinkAuthRepo"
        private const val DEMO_OTP = "123456"
    }


    suspend fun sendSignInLink(email: String): Result<Unit> {
        return try {
            Log.d(TAG, "Sending verification code to: $email")
            delay(800) // Simulate network delay
            storeEmail(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun verifyOtp(otp: String): Result<Unit> {
        return try {
            Log.d(TAG, "Verifying OTP")
            delay(500) // Simulate verification

            if (otp == DEMO_OTP) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Invalid OTP. Please try again."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun storeEmail(email: String) {
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("email", email)
            .apply()
    }
}

