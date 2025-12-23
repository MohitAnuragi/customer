package com.example.customerapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.customerapp.model.Location
import com.example.customerapp.viewmodel.BookingViewModel
import com.example.customerapp.viewmodel.LocationsState

/**
 * Home Screen - Displays location cards in horizontal scroll (LazyRow)
 * Matches Figma design with Kolkata and Bombay cards
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    bookingViewModel: BookingViewModel,
    onLocationClick: (Location) -> Unit
) {
    val locationsState by bookingViewModel.locationsState.collectAsState()

    // Load locations on first composition
    LaunchedEffect(Unit) {
        bookingViewModel.loadLocations()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Choose Location",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            when (val state = locationsState) {
                is LocationsState.Loading -> {
                    LoadingContent()
                }
                is LocationsState.Success -> {
                    LocationCardsContent(
                        locations = state.locations,
                        onLocationClick = onLocationClick
                    )
                }
                is LocationsState.Error -> {
                    ErrorContent(
                        message = state.message,
                        onRetry = { bookingViewModel.loadLocations() }
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = message, color = MaterialTheme.colorScheme.error)
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

/**
 * Location cards in horizontal scroll - matches Figma design
 */
@Composable
private fun LocationCardsContent(
    locations: List<Location>,
    onLocationClick: (Location) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 24.dp)
    ) {
        Text(
            text = "Available Locations",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            color = Color(0xFF333333)
        )

        Text(
            text = "Select your preferred location",
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
            color = Color(0xFF666666)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Horizontal scrollable location cards (LazyRow)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(locations) { location ->
                LocationCard(
                    location = location,
                    onClick = { onLocationClick(location) }
                )
            }
        }
    }
}

/**
 * Individual location card - Figma-style design
 */
@Composable
private fun LocationCard(
    location: Location,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(220.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Location image placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when (location.id) {
                            "kolkata" -> Color(0xFF4CAF50)
                            "bombay" -> Color(0xFF2196F3)
                            else -> Color(0xFF9E9E9E)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = location.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Location description
            Text(
                text = location.description,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = Color(0xFF666666),
                maxLines = 2
            )

            // View button
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Select Location", fontSize = 14.sp)
            }
        }
    }
}

