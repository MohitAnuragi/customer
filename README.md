# Customer Booking App - Complete Implementation Guide

## Overview
This is a complete Android Customer App built with **Kotlin** and **Jetpack Compose**, following strict **MVVM architecture** and **clean code principles**. The app simulates a booking system where users can browse locations and make booking requests.

## Tech Stack
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose with Material 3
- **Architecture:** MVVM (Model-View-ViewModel)
- **State Management:** StateFlow / MutableStateFlow
- **Async Operations:** Kotlin Coroutines
- **Navigation:** Navigation Compose
- **Minimum SDK:** 24
- **Target SDK:** 36

## Project Structure

```
app/src/main/java/com/example/customerapp/
├── MainActivity.kt                  # App entry point
├── model/
│   ├── Location.kt                  # Location data class
│   ├── BookingRequest.kt            # Booking request data class
│   └── BookingStatus.kt             # Sealed class for booking states
├── repository/
│   └── BookingRepository.kt         # Data layer with dummy data
├── viewmodel/
│   ├── AuthViewModel.kt             # Authentication logic
│   └── BookingViewModel.kt          # Booking operations logic
└── ui/
    ├── navigation/
    │   ├── Screen.kt                # Navigation routes
    │   └── AppNavigation.kt         # NavHost setup
    ├── screens/
    │   ├── LoginScreen.kt           # Email OTP login flow
    │   ├── HomeScreen.kt            # Locations list (horizontal scroll)
    │   ├── LocationDetailsScreen.kt # Location details with Book button
    │   └── BookingStatusScreen.kt   # Booking status (Pending/Accepted/Rejected)
    └── theme/
        └── ...                      # Material 3 theming
```

## App Flow

### 1. Login Screen (Email OTP)
- User enters email address
- App generates a dummy OTP: **123456**
- OTP is displayed on screen for testing purposes
- User enters OTP to authenticate
- On successful authentication, navigates to Home Screen

### 2. Home Screen (Locations List)
- Displays available locations in a **horizontal scrollable list** (LazyRow)
- Each location card shows:
  - Location name
  - Description
  - "View Details" button
- Click on any card to navigate to Location Details

### 3. Location Details Screen
- Shows selected location information
- Displays a prominent **"Book Now"** button
- Clicking "Book Now":
  - Submits booking request
  - Navigates to Booking Status screen

### 4. Booking Status Screen
- **Initial State:** Shows "Waiting for owner response..." with loading indicator
- **Simulation:** Uses coroutine delay of 2-3 seconds
- **Random Response:** 70% acceptance rate, 30% rejection rate
- **Accepted:** Shows success message with green checkmark
- **Rejected:** Shows rejection message with red X icon
- **Auto-navigation:** After 2.5 seconds, automatically returns to Home Screen

## Key Features

### MVVM Architecture
- **Model Layer:** Data classes (Location, BookingRequest, BookingStatus)
- **View Layer:** Composable UI components
- **ViewModel Layer:** Business logic and state management
- **Repository Layer:** Data operations with simulated delays

### State Management
- Uses **StateFlow** for reactive UI updates
- Sealed classes for type-safe state representation
- Clean separation of concerns

### Navigation
- Type-safe navigation with sealed classes
- Proper back stack management
- Automatic navigation based on state changes

### UI/UX
- Material 3 design components
- Smooth animations for booking status transitions
- Loading states for all async operations
- Error handling with retry options

## How to Run

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 11 or later
- Android SDK with API 36

### Steps
1. **Open Project:**
   ```
   Open Android Studio -> Open -> Select CustomerApp folder
   ```

2. **Sync Gradle:**
   - Android Studio will automatically sync Gradle
   - Wait for dependencies to download

3. **Build Project:**
   ```
   Build -> Make Project
   ```
   Or use command line:
   ```powershell
   .\gradlew.bat assembleDebug
   ```

4. **Run on Emulator/Device:**
   - Select a device/emulator (API 24+)
   - Click the "Run" button (green play icon)
   - Or use:
   ```powershell
   .\gradlew.bat installDebug
   ```

## Testing the App

### Login Flow
1. Enter any email (e.g., `test@example.com`)
2. Click "Continue"
3. See the generated OTP: **123456**
4. Enter OTP: **123456**
5. Click "Verify OTP"

### Booking Flow
1. After login, you'll see the Home screen with locations
2. Swipe horizontally to browse locations
3. Click "View Details" on any location
4. On details screen, click "Book Now"
5. Watch the booking status screen:
   - Initially shows "Waiting for owner response..."
   - After 2-3 seconds, shows Accepted or Rejected
   - After 2.5 more seconds, returns to Home

### Test Different Scenarios
- **Successful Booking:** ~70% chance - green success screen
- **Rejected Booking:** ~30% chance - red rejection screen
- **Multiple Bookings:** Try different locations
- **Navigation:** Use back button on details screen

## Code Highlights

### Clean Code Principles
- ✅ Single Responsibility Principle
- ✅ Dependency Inversion (Repository pattern)
- ✅ Comprehensive documentation
- ✅ Meaningful variable and function names
- ✅ No business logic in UI layer
- ✅ Proper error handling

### No External Dependencies
- ✅ No Firebase or backend required
- ✅ Simulated delays using coroutines
- ✅ Dummy data in Repository
- ✅ Fully functional offline

## Simulated Data

### Locations
The app includes 6 pre-configured locations:
1. Sunset Beach Resort
2. Mountain View Lodge
3. City Center Hotel
4. Lakeside Cabin
5. Desert Oasis Villa
6. Countryside Farmhouse

### Authentication
- Dummy OTP: **123456**
- Email validation: Basic format check
- Simulated network delays

### Booking
- Acceptance rate: 70%
- Rejection rate: 30%
- Response time: 2-3 seconds
- Auto-navigation after: 2.5 seconds

## Troubleshooting

### Build Issues
```powershell
# Clean and rebuild
.\gradlew.bat clean assembleDebug
```

### Gradle Sync Issues
```
File -> Invalidate Caches -> Invalidate and Restart
```

### SDK Issues
Ensure you have API 36 installed via SDK Manager

## Future Enhancements (Not Implemented)
- Real backend integration
- User profile management
- Booking history
- Push notifications
- Payment integration
- Location images
- Search and filters
- Ratings and reviews

## Architecture Diagram

```
┌─────────────┐
│   UI Layer  │  (Composables)
└──────┬──────┘
       │
       │ observes StateFlow
       │
┌──────▼──────┐
│  ViewModel  │  (Business Logic)
└──────┬──────┘
       │
       │ calls
       │
┌──────▼──────┐
│ Repository  │  (Data Layer)
└──────┬──────┘
       │
       │ returns
       │
┌──────▼──────┐
│    Model    │  (Data Classes)
└─────────────┘
```

## Dependencies

All dependencies are managed via `libs.versions.toml`:
- Compose BOM: 2024.09.00
- Navigation Compose: 2.8.5
- Lifecycle ViewModel Compose: 2.10.0
- Material 3: (from Compose BOM)
- Kotlin: 2.0.21

## License
This is a demo project for educational purposes.

## Contact
For questions or issues, please refer to the code documentation within each file.

---
**Built with ❤️ using Jetpack Compose and MVVM Architecture**

