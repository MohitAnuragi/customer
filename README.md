# 📱 Customer App – Booking Agent (Android)

This repository contains the **Customer (User) App** for the *Booking Agent* mini assignment.
The app is developed using **Kotlin** and **Jetpack Compose**, following **MVVM architecture** and clean code practices.

---

## 🚀 Project Overview

The Customer App allows users to:

* Log in using **email OTP–style authentication**
* Browse **locations (Kolkata / Bombay)** using horizontally scrollable cards
* Send a **booking request** for a selected location
* Receive **real-time booking status updates** (Accepted / Rejected) from the Owner App

The app uses **Firebase Realtime Database** to support **instant syncing** between Customer and Owner apps.

---

## ✨ Features Implemented

* Email OTP–like login using **Firebase Email Link Authentication**
* Modern OTP UI with auto-focus input boxes
* Resend OTP functionality with **60-second countdown timer**
* Horizontal location cards using `LazyRow`
* Booking request creation
* **Real-time booking status updates** without screen refresh
* Clean and structured **MVVM architecture**
* UI closely aligned with the provided **Figma design**

---

## 🛠 Tech Stack

* **Language:** Kotlin
* **UI:** Jetpack Compose (Material 3)
* **Architecture:** MVVM
* **State Management:** StateFlow
* **Backend:** Firebase Realtime Database
* **Authentication:** Firebase Email Link (OTP-like)
* **Async:** Kotlin Coroutines

---

## 🔐 Authentication

The app uses **Firebase Email Link Authentication**, which works as an OTP-based, passwordless login system:

* User enters email
* A secure email link is sent
* User verifies login without a password

This approach is secure, simple, and industry-accepted.

---

## 🗂 Firebase Database Structure

```text
customers/
  {customerId}/
    email

bookings/
  {bookingId}/
    customerId
    location
    status       
    timestamp

activeLocations/
  kolkata: true/false
  bombay: true/false
```

---

## 🧭 App Flow

```text
Login
  → Home (Location Cards)
  → Location Details
  → Book Now
  → Waiting for Owner Response
  → Accepted / Rejected
  → Back to Home
```

All updates happen in **real time** using Firebase listeners.

---

## 📐 UI Design

* UI closely follows the provided **Figma design**
* Layout, spacing, and hierarchy are carefully matched
* Built entirely using **Jetpack Compose**

---

## 📁 Project Structure

```text
ui/
  login/
  home/
  details/
  status/

viewmodel/
  AuthViewModel
  BookingViewModel

repository/
  BookingRepository

model/
  Booking
  BookingStatus
  Location
```

---

## ⚠️ Note

* This repository currently includes **only the Customer App**
* The **Owner (Admin) App** is under development due to time constraints
* With additional time, both apps can be completed fully as per requirements

---

## ▶️ How to Run

1. Clone the repository
2. Open the project in **Android Studio**
3. Add `google-services.json` for Firebase
4. Sync Gradle
5. Run on emulator or physical device

---

## 👤 Author

**Mohit Anuragi**
Android Developer (Kotlin | Jetpack Compose)
