package com.example.customerapp.repository

import com.example.customerapp.model.BookingRequest
import com.example.customerapp.model.Customer
import com.example.customerapp.model.Location
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseRepository {

    private val database: DatabaseReference = FirebaseDatabase.getInstance(
        "https://customer-app-b1940-default-rtdb.firebaseio.com/"
    ).reference

    suspend fun saveCustomer(customer: Customer): Result<Unit> {
        return try {
            database.child("customers").child(customer.customerId)
                .setValue(customer.toMap())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateOTP(email: String): Result<String> {
        return try {
            val otp = "1234"
            val otpRef = database.child("otps").child(email.replace(".", "_"))
            otpRef.setValue(mapOf(
                "otp" to otp,
                "timestamp" to System.currentTimeMillis(),
                "verified" to false
            )).await()
            Result.success(otp)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyOTP(email: String, enteredOtp: String): Result<Boolean> {
        return try {
            val snapshot = database.child("otps")
                .child(email.replace(".", "_"))
                .get()
                .await()

            val storedOtp = snapshot.child("otp").getValue(String::class.java)
            val isValid = storedOtp == enteredOtp

            if (isValid) {
                snapshot.ref.child("verified").setValue(true).await()
            }

            Result.success(isValid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLocations(): Result<List<Location>> {
        return try {
            val locations = Location.getAvailableLocations()

            val activeSnapshot = database.child("activeLocations").get().await()
            val updatedLocations = locations.map { location ->
                val isActive = activeSnapshot.child(location.id).getValue(Boolean::class.java) ?: true
                location.copy(isActive = isActive)
            }

            Result.success(updatedLocations)
        } catch (e: Exception) {
            Result.success(Location.getAvailableLocations())
        }
    }

    suspend fun createBooking(customerId: String, location: String): Result<String> {
        return try {
            val bookingId = database.child("bookings").push().key ?: throw Exception("Failed to generate booking ID")

            val booking = BookingRequest(
                bookingId = bookingId,
                customerId = customerId,
                location = location,
                status = "pending",
                timestamp = System.currentTimeMillis()
            )

            database.child("bookings").child(bookingId)
                .setValue(booking.toMap())
                .await()

            Result.success(bookingId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeBookingStatus(bookingId: String): Flow<String> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val status = snapshot.child("status").getValue(String::class.java) ?: "pending"
                trySend(status)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        val bookingRef = database.child("bookings").child(bookingId)
        bookingRef.addValueEventListener(listener)

        awaitClose {
            bookingRef.removeEventListener(listener)
        }
    }

    suspend fun getBooking(bookingId: String): Result<BookingRequest> {
        return try {
            val snapshot = database.child("bookings").child(bookingId).get().await()
            val booking = snapshot.getValue(BookingRequest::class.java)
                ?: throw Exception("Booking not found")
            Result.success(booking)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCustomerBookings(customerId: String): Result<List<BookingRequest>> {
        return try {
            val snapshot = database.child("bookings")
                .orderByChild("customerId")
                .equalTo(customerId)
                .get()
                .await()

            val bookings = mutableListOf<BookingRequest>()
            snapshot.children.forEach { child ->
                child.getValue(BookingRequest::class.java)?.let { bookings.add(it) }
            }

            Result.success(bookings.sortedByDescending { it.timestamp })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

